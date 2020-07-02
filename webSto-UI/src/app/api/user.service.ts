import {Injectable, Output} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable, Subject} from 'rxjs';
import {User} from '../model/postgres/auth/user';
import {Router} from '@angular/router';
import {TransferService} from './transfer.service';
import {environment} from '../../environments/environment';
import {ToastrService} from 'ngx-toastr';
import {UsersFilter} from '../model/usersFilter';
import {RestService} from './rest.service';
import {UserResource, UserResourceService} from '../model/resource/user.resource.service';
import {UserRoleResourceService} from '../model/resource/user-role.resource.service';
import {SubscriptionResourceService} from '../model/resource/subscription.resource.service';
import {SubscriptionTypeResourceService} from '../model/resource/subscription-type.resource.service';
import {DocumentCollection} from 'ngx-jsonapi';
import {ProfileResourceService} from '../model/resource/profile.resource.service';

@Injectable()
export class UserService implements TransferService<UserResource>, RestService<UserResource> {

  constructor(private http: HttpClient, private router: Router, private toastrService: ToastrService,
              private userResourceService: UserResourceService, private userRoleResourceService: UserRoleResourceService,
              private subscriptionResourceService: SubscriptionResourceService,
              private subscriptionTypeResourceService: SubscriptionTypeResourceService,
              private profileResourceService: ProfileResourceService) {
    userResourceService.register();
  }
  currentUser: UserResource;
  isSaving = false;
  private transferModel: UserResource;

  @Output()
  public currentUserIsLoaded: Subject<UserResource> = new Subject<UserResource>();
  @Output()
  public currentUserIsLoggedOut: Subject<void> = new Subject<void>();

  login(loginPayload) {
    const headers = {
      Authorization: 'Basic ' + btoa('spring-security-oauth2-read-write-client:spring-security-oauth2-read-write-client-password1234'),
      'Content-type': 'application/x-www-form-urlencoded'
    };
    return this.http.post(`${this.getApiUrl()}oauth/token`, loginPayload, {headers});
  }

  restore(restoreData: HttpParams) {
    const headers = {
      Authorization: 'Basic ' + btoa('spring-security-oauth2-read-write-client:spring-security-oauth2-read-write-client-password1234'),
      'Content-type': 'application/x-www-form-urlencoded'
    };

    return this.http.post(`${this.getApiUrl()}oauth/restore`, restoreData, {headers});
  }

  restorePassword(restoreData: HttpParams) {
    const headers = {
      Authorization: 'Basic ' + btoa('spring-security-oauth2-read-write-client:spring-security-oauth2-read-write-client-password1234'),
      'Content-type': 'application/x-www-form-urlencoded'
    };

    return this.http.post(`${this.getApiUrl()}oauth/restore/password`, restoreData, {headers});
  }

  requestOpenReport(uuid: string) {
    const headers = {
      Authorization: 'Basic ' + btoa('spring-security-oauth2-read-write-client:spring-security-oauth2-read-write-client-password1234'),
      'Content-type': 'application/x-www-form-urlencoded'
    };
    const params = {
      uuid
    };
    return this.http.get(`${this.getApiUrl()}open/report/compiled`, {headers, params, responseType: 'blob'});
  }

  logout() {
    localStorage.setItem('isAuthenticated', 'false');
    this.currentUser = null;
    localStorage.setItem( 'token', null );
    localStorage.removeItem( 'demoDomain' );
    this.currentUserIsLoggedOut.next();
    this.router.navigate(['/login']);
  }

  getUsername(): string {
    if ( this.currentUser != null ) {
      if ( this.currentUser.attributes.fio != null )
        return this.currentUser.attributes.fio;
      // else if ( this.currentUser.attributes.phone != null )
      //   return this.currentUser.attributes.phone;
      // else if ( this.currentUser.attributes.email != null )
      //   return this.currentUser.attributes.email;
      else
        return this.currentUser.attributes.username;
    }
    else
      return null;
  }

  getModelUsername(model: UserResource): string {
    if ( model != null ) {
      if ( model.attributes.fio != null )
        return model.attributes.fio;
      // else if ( model.attributes.phone != null )
      //   return model.attributes.phone;
      // else if ( model.attributes.email != null )
      //   return model.attributes.email;
      else
        return model.attributes.username;
    }
    else
      return null;
  }

  isTokenExists(): boolean {
    return localStorage.getItem('token') != null && localStorage.getItem('token') !== 'null';
  }

  getToken(): string {
    if ( this.isTokenExists() ) {
        return JSON.parse(localStorage.getItem('token')).access_token;
    } else {
      this.logout();
      return null;
    }
  }

  isDemoDomain(): boolean {
    return localStorage.getItem('demoDomain') != null && localStorage.getItem('demoDomain') !== null
  }

  getApiUrl(): string {
    if ( localStorage.getItem('demoDomain') != null && localStorage.getItem('demoDomain') !== null ) {
      return environment.demoUrl;
    }
    else
      return environment.apiUrl;
  }

  getWsUrl(): string {
    if ( localStorage.getItem('demoDomain') != null && localStorage.getItem('demoDomain') !== null ) {
      return environment.wsdUrl;
    }
    else
      return environment.wsUrl;
  }

  isAuthenticated(): boolean {
    const isAuthenticated = localStorage.getItem('isAuthenticated') as unknown;

    return isAuthenticated != null && isAuthenticated as boolean;
  }

  getHeaders() {

    if ( this.isTokenExists() ) {
      return {
        Authorization: 'Bearer ' + JSON.parse(localStorage.getItem('token')).access_token
      };
    } else {
      this.logout();
      return null;
    }

  }

  authenticate() {

    this.userResourceService.get('currentUser', {
      beforepath: 'external'
    }).subscribe((result) => {
      this.setCurrentUserData( result );
      localStorage.setItem('isAuthenticated', 'true');

      const redirectUrl: string = localStorage.getItem('redirectUrl');

      if ( redirectUrl != null && redirectUrl.length > 0 ) {
        this.router.navigate([redirectUrl]);
        localStorage.removeItem('redirectUrl');
      }
      this.router.navigate(['/documents']);
    });

  }

  getCurrentUser() {

    const headers = this.getHeaders();

    console.log('REQUESTING CURRENT USER!');
    this.userResourceService.get('currentUser', {
      beforepath: 'external'
    }).subscribe((result) => {
      this.setCurrentUserData( result );
      this.currentUserIsLoaded.next( this.currentUser );
    });

  }

  private setCurrentUserData(user: UserResource) {
    this.currentUser = user;
  }

  createUser(user: User, selectedRole: string) {
    return this.http.post(`${this.getApiUrl()}oauth/register/${selectedRole}`, user);
  }

  delete(user: UserResource) {
    const headers = this.getHeaders();

    return this.http.delete(`${this.getApiUrl()}/users/${user.id}`, {headers});
  }

  createDemoUser() {
    return this.http.get(`${this.getApiUrl()}oauth/demo/register`);
  }

  saveUser(user: UserResource, message: string) {

    const headers = this.getHeaders();
    this.isSaving = true;

    const profile = user.relationships.profile.data;

    profile.save().subscribe( (savedProfile) => {
      user.save().subscribe(savedUser => {
        this.isSaving = false;

        if ( this.currentUser != null && this.currentUser.id === user.id )
          this.setCurrentUserData( user );

        this.isSaving = false;
        this.toastrService.success(message);
      }, () => {
        this.isSaving = false;
        this.toastrService.error('Ошибка сохранения пользователя!', 'Внимание!');
      });
    }, () => {
      this.isSaving = false;
      this.toastrService.error('Ошибка сохранения пользователя!', 'Внимание!');
    } );
  }

  getAll(filter: UsersFilter): Observable<DocumentCollection<UserResource>> {
    const params = {
      role: filter.role != null ? filter.role : '',
      isAutoRegistered: filter.isAutoRegistered != null ? filter.isAutoRegistered : '',
      phone: filter.phone != null ? filter.phone : '',
      email: filter.email != null ? filter.email : '',
      inn: filter.inn != null ? filter.inn : '',
      fio: filter.fio != null ? filter.fio : ''
    };

    return this.userResourceService.all({
      beforepath: 'external',
      sort: [`${filter.direction === 'desc' ? '-' : ''}${filter.sort}`],
      page: {number: filter.page, size: filter.size},
      remotefilter: params
    });
  }

  getOne(id: string): Observable<UserResource> {
    return this.userResourceService.get(id.toString());
  }

  getTransferModel() {
    return this.transferModel;
  }

  setTransferModel(user: UserResource) {
    this.transferModel = user;
  }

  resetTransferModel() {
    this.transferModel = null;
  }

  isNotClient(): boolean {
    return this.currentUser != null && ( this.currentUser.attributes.userAdmin || this.currentUser.attributes.userServiceLeader );
  }

  isClient(): boolean {
    return this.currentUser != null && this.currentUser.attributes.userClient;
  }

  isServiceLeader(): boolean {
    return this.currentUser != null && this.currentUser.attributes.userServiceLeader;
  }

  isAdmin(): boolean {
    return this.currentUser != null && this.currentUser.attributes.userAdmin;
  }

  isSameUser(model: UserResource) {
    return this.currentUser != null && model != null && this.currentUser.id === model.id;
  }

}
