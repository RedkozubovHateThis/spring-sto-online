import {Injectable, Input, Output, EventEmitter} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable, Subject} from "rxjs";
import {User} from "../model/postgres/auth/user";
import {Router} from "@angular/router";
import {TransferService} from "./transfer.service";

@Injectable()
export class UserService implements TransferService<User> {

  constructor(private http: HttpClient, private router: Router) { }
  private baseUrl: string = 'http://localhost:8181/';
  currentUser: User;
  isSaving: boolean = false;
  private transferModel: User;

  @Output()
  public currentUserIsLoaded: Subject<User> = new Subject<User>();

  login(loginPayload) {
    const headers = {
      'Authorization': 'Basic ' + btoa('spring-security-oauth2-read-write-client:spring-security-oauth2-read-write-client-password1234'),
      'Content-type': 'application/x-www-form-urlencoded'
    };
    return this.http.post('http://localhost:8181/' + 'oauth/token', loginPayload, {headers});
  }

  logout() {
    localStorage.setItem("isAuthenticated", "false");
    this.currentUser = null;
    localStorage.setItem( "token", null );
    this.router.navigate(['login']);
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
    return localStorage.getItem("token") != null;
  }

  isAuthenticated(): boolean {
    let isAuthenticated = localStorage.getItem('isAuthenticated') as unknown;

    return isAuthenticated != null && isAuthenticated as boolean;
  }

  getHeaders() {

    if ( this.isTokenExists() ) {
      return {
        'Authorization': 'Bearer ' + JSON.parse(localStorage.getItem("token")).access_token
      };
    }
    else {
      this.logout();
      return undefined;
    }

  }

  authenticate() {

    const headers = this.getHeaders();

    this.http.get( this.baseUrl + 'secured/users/currentUser', {headers} ).subscribe( data => {

      this.setCurrentUserData( data as User );
      localStorage.setItem("isAuthenticated", "true");

      this.router.navigate(['dashboard']);

    } );

  }

  getCurrentUser() {

    const headers = this.getHeaders();

    this.http.get( this.baseUrl + 'secured/users/currentUser', {headers} ).subscribe( data => {
      this.setCurrentUserData( data as User );
      this.currentUserIsLoaded.next( this.currentUser );
    } );

  }

  private setCurrentUserData(user:User) {
    this.currentUser = user;
  }

  createUser(user: User) {
    return this.http.post(this.baseUrl + 'oauth/register', user);
  }

  saveUser(user: User) {

    const headers = this.getHeaders();
    this.isSaving = true;

    return this.http.put( this.baseUrl + `secured/users/${user.id}`, user,{headers} ).subscribe( data => {

      let user: User = data as User;

      if ( this.currentUser != null && this.currentUser.id === user.id )
        this.setCurrentUserData( data as User );

      this.isSaving = false;

    }, error => {
      this.isSaving = false;
    } );

  }

  getAll(page: number, size: number, offset: number) {
    const headers = this.getHeaders();

    return this.http.get( `${this.baseUrl}secured/users/findAll?sort=lastName&size=${size}&page=${page}&offset=${offset}`, {headers} );
  }

  getOne(id:number) {
    const headers = this.getHeaders();

    return this.http.get( `${this.baseUrl}secured/users/${id}`, {headers} );
  }

  getUsersCount(notApprovedOnly: boolean) {
    const headers = this.getHeaders();

    return this.http.get( `${this.baseUrl}secured/users/count?notApprovedOnly=${notApprovedOnly}`, {headers} );
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

}
