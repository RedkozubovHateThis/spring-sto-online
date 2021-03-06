import {Injectable, Output} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable, Subject} from 'rxjs';
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
import {RegisterModel} from '../model/postgres/registerModel';
import {AdEntityResourceService} from '../model/resource/ad-entity.resource.service';
import {PaymentRecordResource} from '../model/resource/payment-record.resource.service';
import {IDataCollection} from 'ngx-jsonapi/interfaces/data-collection';

@Injectable()
export class UserService implements TransferService<UserResource>, RestService<UserResource> {

  constructor(private http: HttpClient, private router: Router, private toastrService: ToastrService,
              private userResourceService: UserResourceService, private userRoleResourceService: UserRoleResourceService,
              private subscriptionResourceService: SubscriptionResourceService,
              private subscriptionTypeResourceService: SubscriptionTypeResourceService,
              private profileResourceService: ProfileResourceService,
              private adEntityResourceService: AdEntityResourceService) {
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
    return this.http.post(`${environment.getApiUrl()}oauth/token`, loginPayload, {headers});
  }

  restore(restoreData: HttpParams) {
    const headers = {
      Authorization: 'Basic ' + btoa('spring-security-oauth2-read-write-client:spring-security-oauth2-read-write-client-password1234'),
      'Content-type': 'application/x-www-form-urlencoded'
    };

    return this.http.post(`${environment.getApiUrl()}oauth/restore`, restoreData, {headers});
  }

  restorePassword(restoreData: HttpParams) {
    const headers = {
      Authorization: 'Basic ' + btoa('spring-security-oauth2-read-write-client:spring-security-oauth2-read-write-client-password1234'),
      'Content-type': 'application/x-www-form-urlencoded'
    };

    return this.http.post(`${environment.getApiUrl()}oauth/restore/password`, restoreData, {headers});
  }

  requestOpenReport(uuid: string) {
    const headers = {
      Authorization: 'Basic ' + btoa('spring-security-oauth2-read-write-client:spring-security-oauth2-read-write-client-password1234'),
      'Content-type': 'application/x-www-form-urlencoded'
    };
    const params = {
      uuid
    };
    return this.http.get(`${environment.getApiUrl()}open/report/compiled`, {headers, params, responseType: 'blob'});
  }

  logout(redirect = true) {
    localStorage.setItem('isAuthenticated', 'false');
    this.currentUser = null;
    localStorage.setItem( 'token', null );
    localStorage.removeItem( 'demoDomain' );
    this.currentUserIsLoggedOut.next();
    try {
      window.indexedDB.deleteDatabase('dexie_data_provider');
    } catch (e) {
      console.error(e);
    }
    if ( redirect )
      this.router.navigate(['/login']);
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

  isAuthenticated(): boolean {
    const isAuthenticated = localStorage.getItem('isAuthenticated') as unknown;

    return isAuthenticated != null && isAuthenticated as boolean;
  }

  authenticate() {

    this.userResourceService.get('currentUser', {
      beforepath: `${environment.getBeforeUrl()}/external`
    }).subscribe((result) => {
      // TODO: подумать над этим костылем
      setTimeout(() => {
        this.setCurrentUserData( result );

        localStorage.setItem('isAuthenticated', 'true');

        const redirectUrl: string = localStorage.getItem('redirectUrl');

        if ( redirectUrl != null && redirectUrl.length > 0 ) {
          this.router.navigate([redirectUrl]);
          localStorage.removeItem('redirectUrl');
        }
        this.router.navigate(['/documents']);
      }, 500);
    });

  }

  getCurrentUser() {
    this.userResourceService.get('currentUser', {
      beforepath: `${environment.getBeforeUrl()}/external`,
      include: ['roles']
    }).subscribe((result) => {
      // TODO: подумать над этим костылем
      setTimeout(() => {
        this.setCurrentUserData( result );
        this.currentUserIsLoaded.next( this.currentUser );
      }, 500);
    });

  }

  private setCurrentUserData(user: UserResource) {
    this.currentUser = user;
  }

  createUser(user: RegisterModel, selectedRole: string) {
    return this.http.post(`${environment.getApiUrl()}oauth/register/${selectedRole}`, user);
  }

  delete(user: UserResource): Observable<void> {
    return this.userResourceService.delete( user.id, { beforepath: environment.getBeforeUrl() } );
  }

  createDemoUser() {
    return this.http.get(`${environment.getApiUrl()}oauth/demo/register`);
  }

  saveUser(user: UserResource, message: string, saveAdEntity = false) {
    this.isSaving = true;
    const isNew = user.is_new;

    const profile = user.relationships.profile.data;
    const adEntity = user.relationships.adEntity.data;

    if ( saveAdEntity ) {
      profile.save({ beforepath: environment.getBeforeUrl() }).subscribe( (savedProfile) => {
        adEntity.save( { beforepath: environment.getBeforeUrl() } ).subscribe( (savedAdEntity) => {
          user.save({ beforepath: environment.getBeforeUrl() }).subscribe((savedUser) => {
            this.isSaving = false;

            if ( this.currentUser != null && this.currentUser.id === user.id )
              this.setCurrentUserData( user );

            this.isSaving = false;
            this.toastrService.success(message);
            if ( isNew )
              this.router.navigate(['/users', user.id, 'edit']);
          }, (error) => {
            this.isSaving = false;
            this.showError(error, 'Ошибка сохранения пользователя');
          });
        }, (error) => {
          this.isSaving = false;
          this.showError(error, 'Ошибка сохранения рекламного объявления');
        } );
      }, (error) => {
        this.isSaving = false;
        this.showError(error, 'Ошибка сохранения пользователя');
      } );
    }
    else {
      profile.save({ beforepath: environment.getBeforeUrl() }).subscribe( (savedProfile) => {
        user.save({ beforepath: environment.getBeforeUrl() }).subscribe((savedUser) => {
          this.isSaving = false;

          if ( this.currentUser != null && this.currentUser.id === user.id )
            this.setCurrentUserData( user );

          this.isSaving = false;
          this.toastrService.success(message);
          if ( isNew )
            this.router.navigate(['/users', user.id, 'edit']);
        }, (error) => {
          this.isSaving = false;
          this.showError(error, 'Ошибка сохранения пользователя');
        });
      }, (error) => {
        this.isSaving = false;
        this.showError(error, 'Ошибка сохранения пользователя');
      } );
    }
  }

  showError(error: any, defaultMessage: string) {
    if ( error.errors && Array.isArray( error.errors ) )
      this.toastrService.error( `${defaultMessage}: ${error.errors[0].detail}`, 'Внимание!' );
    else
      this.toastrService.error( `${defaultMessage}!`, 'Внимание!' );
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
      beforepath: `${environment.getBeforeUrl()}/external`,
      sort: [`${filter.direction === 'desc' ? '-' : ''}${filter.sort}`],
      page: {number: filter.page, size: filter.size},
      remotefilter: params
    });
  }

  getAllServiceLeadersAndFreelancers(): Observable<DocumentCollection<UserResource>> {
    return new Observable<DocumentCollection<UserResource>>( (subscriber) => {
      this.http.get<IDataCollection>(
        `${environment.getApiUrl()}external/users/serviceLeaders`
      )
        .subscribe( (raw: IDataCollection) => {
          const collection: DocumentCollection<UserResource> = this.userResourceService.newCollection();
          collection.fill(raw);
          subscriber.next( collection );
          subscriber.complete();
        }, (error) => {
          subscriber.error( error );
          subscriber.complete();
        } );
    } );
  }

  getOne(id: string): Observable<UserResource> {
    return this.userResourceService.get(id, { beforepath: environment.getBeforeUrl() });
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
    return this.currentUser != null && ( this.currentUser.isAdmin() || this.currentUser.isServiceLeaderOrFreelancer() );
  }

  isClient(): boolean {
    return this.currentUser != null && this.currentUser.isClient();
  }

  isServiceLeader(): boolean {
    return this.currentUser != null && this.currentUser.isServiceLeader();
  }

  isServiceLeaderOrFreelancer(): boolean {
    return this.currentUser != null && this.currentUser.isServiceLeaderOrFreelancer();
  }

  isAdmin(): boolean {
    return this.currentUser != null && this.currentUser.isAdmin();
  }

  isSameUser(model: UserResource) {
    return this.currentUser != null && model != null && this.currentUser.id === model.id;
  }

}
