import {Injectable, Input, Output, EventEmitter} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, Subject} from 'rxjs';
import {User} from '../model/postgres/auth/user';
import {Router} from '@angular/router';
import {TransferService} from './transfer.service';
import {environment} from '../../environments/environment';
import {ChatMessageResponseService} from './chatMessageResponse.service';
import {ToastrService} from 'ngx-toastr';
import {UsersFilter} from '../model/usersFilter';

@Injectable()
export class UserService implements TransferService<User> {

  constructor(private http: HttpClient, private router: Router, private toastrService: ToastrService) { }
  currentUser: User;
  isSaving = false;
  private transferModel: User;

  @Output()
  public currentUserIsLoaded: Subject<User> = new Subject<User>();

  login(loginPayload) {
    const headers = {
      Authorization: 'Basic ' + btoa('spring-security-oauth2-read-write-client:spring-security-oauth2-read-write-client-password1234'),
      'Content-type': 'application/x-www-form-urlencoded'
    };
    return this.http.post(`${this.getApiUrl()}oauth/token`, loginPayload, {headers});
  }

  logout() {
    localStorage.setItem('isAuthenticated', 'false');
    this.currentUser = null;
    localStorage.setItem( 'token', null );
    localStorage.removeItem( 'demoDomain' );
    this.router.navigate(['/login']);
  }

  getUsername(): string {
    if ( this.currentUser != null ) {
      if ( this.currentUser.fio != null )
        return this.currentUser.fio;
      else if ( this.currentUser.phone != null )
        return this.currentUser.phone;
      else if ( this.currentUser.email != null )
        return this.currentUser.email;
      else
        return this.currentUser.username;
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

  getApiUrl(): string {
    if ( localStorage.getItem('demoDomain') != null && localStorage.getItem('demoDomain') !== null ) {
      console.warn('DEMO DOMAIN IS ACTIVE');
      return environment.demoUrl;
    }
    else
      return environment.apiUrl;
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

    const headers = this.getHeaders();

    this.http.get( this.getApiUrl() + 'secured/users/currentUser', {headers} ).subscribe( data => {

      this.setCurrentUserData( data as User );
      localStorage.setItem('isAuthenticated', 'true');

      const redirectUrl: string = localStorage.getItem('redirectUrl');

      console.log(redirectUrl);

      if ( redirectUrl != null && redirectUrl.length > 0 ) {
        this.router.navigate([redirectUrl]);
        localStorage.removeItem('redirectUrl');
      }
      else if ( this.currentUser.client )
        this.router.navigate(['/documents']);
      else
        this.router.navigate(['/dashboard']);

    } );

  }

  getCurrentUser() {

    const headers = this.getHeaders();

    this.http.get( this.getApiUrl() + 'secured/users/currentUser', {headers} ).subscribe( data => {
      this.setCurrentUserData( data as User );
      this.currentUserIsLoaded.next( this.currentUser );
    } );

  }

  private setCurrentUserData(user: User) {
    this.currentUser = user;
  }

  createUser(user: User, selectedRole: string) {
    return this.http.post(`${this.getApiUrl()}oauth/register/${selectedRole}`, user);
  }

  createDemoUser() {
    return this.http.get(`${this.getApiUrl()}oauth/demo/register`);
  }

  saveUser(user: User, message: string) {

    const headers = this.getHeaders();
    this.isSaving = true;

    return this.http.put( this.getApiUrl() + `secured/users/${user.id}`, user,{headers} ).subscribe( data => {

      let user: User = data as User;

      if ( this.currentUser != null && this.currentUser.id === user.id )
        this.setCurrentUserData( data as User );

      this.isSaving = false;
      this.toastrService.success(message);

    }, error => {
      this.isSaving = false;

      if ( error.status === 400 || error.status === 404 )
        this.toastrService.error(error.error, 'Внимание!');
      else
        this.toastrService.error('Ошибка сохранения пользователя!', 'Внимание!');

    } );

  }

  getAll(size: number, offset: number, filter: UsersFilter) {
    const headers = this.getHeaders();

    const params = {
      sort: `${filter.sort},${filter.direction}`,
      page: filter.page.toString(),
      size: size.toString(),
      offset: offset.toString(),
      role: filter.role != null ? filter.role : '',
      isApproved: filter.isApproved != null ? filter.isApproved : '',
      isAutoRegistered: filter.isAutoRegistered != null ? filter.isAutoRegistered : '',
      phone: filter.phone != null ? filter.phone : '',
      email: filter.email != null ? filter.email : '',
      fio: filter.fio != null ? filter.fio : ''
    };

    return this.http.get( `${this.getApiUrl()}secured/users/findAll`, {headers, params} );
  }

  getReplacementModerators() {
    const headers = this.getHeaders();

    return this.http.get( `${this.getApiUrl()}secured/users/findReplacementModerators`, {headers} );
  }

  getOne(id: number) {
    const headers = this.getHeaders();

    return this.http.get( `${this.getApiUrl()}secured/users/${id}`, {headers} );
  }

  getUsersCount(notApprovedOnly: boolean) {
    const headers = this.getHeaders();

    return this.http.get( `${this.getApiUrl()}secured/users/count?notApprovedOnly=${notApprovedOnly}`, {headers} );
  }

  getOpponents() {
    const headers = this.getHeaders();

    return this.http.get( `${this.getApiUrl()}secured/chat/opponents`, {headers} );
  }

  getEventMessageFromUsers() {
    const headers = this.getHeaders();

    return this.http.get( `${this.getApiUrl()}secured/users/eventMessages/fromUsers`, {headers} );
  }

  getEventMessageToUsers() {
    const headers = this.getHeaders();

    return this.http.get( `${this.getApiUrl()}secured/users/eventMessages/toUsers`, {headers} );
  }

  getTransferModel() {
    return this.transferModel;
  }

  setTransferModel(user: User) {
    this.transferModel = user;
  }

  resetTransferModel() {
    this.transferModel = null;
  }

  isNotClient(): boolean {
    return this.currentUser != null && ( this.currentUser.admin || this.currentUser.serviceLeader || this.currentUser.moderator );
  }

  isClient(): boolean {
    return this.currentUser != null && this.currentUser.client;
  }

  isModerator(): boolean {
    return this.currentUser != null && this.currentUser.moderator;
  }

  isServiceLeader(): boolean {
    return this.currentUser != null && this.currentUser.serviceLeader;
  }

  isAdmin(): boolean {
    return this.currentUser != null && this.currentUser.admin;
  }

  isModeratorOrAdmin(): boolean {
    return this.currentUser != null && ( this.currentUser.admin || this.currentUser.moderator );
  }

  isClientOrServiceLeader(): boolean {
    return this.currentUser != null && ( this.currentUser.client || this.currentUser.serviceLeader );
  }

  isSameUser(model: User) {
    return this.currentUser != null && model != null && this.currentUser.id === model.id;
  }

}
