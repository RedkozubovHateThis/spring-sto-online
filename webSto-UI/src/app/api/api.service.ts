import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {User} from "../model/auth/user";
import {Router} from "@angular/router";

@Injectable()
export class ApiService {

  constructor(private http: HttpClient, private router: Router) { }
  private baseUrl: string = 'http://localhost:8181/';
  currentUser: User;
  isAuthenticated: boolean = false;
  isSaving:boolean = false;

  login(loginPayload) {
    const headers = {
      'Authorization': 'Basic ' + btoa('spring-security-oauth2-read-write-client:spring-security-oauth2-read-write-client-password1234'),
      'Content-type': 'application/x-www-form-urlencoded'
    };
    return this.http.post('http://localhost:8181/' + 'oauth/token', loginPayload, {headers});
  }

  logout() {
    this.router.navigate(['login']);
    this.isAuthenticated = false;
    this.currentUser = null;
    localStorage.setItem( 'currentUser', null );
    sessionStorage.setItem( "token", null );
  }

  getUsername() {
    if ( this.currentUser != null )
      return this.currentUser.fio != null ? this.currentUser.fio : this.currentUser.username;
    else
      return undefined;
  }

  getHeaders() {

    if ( sessionStorage.getItem("token") != null ) {
      return {
        'Authorization': 'Bearer ' + JSON.parse(sessionStorage.getItem("token")).access_token
      };
    }
    else {
      this.logout();
      return undefined;
    }

  }

  getCurrentUser() {

    const headers = this.getHeaders();

    return this.http.get( this.baseUrl + 'secured/users/currentUser', {headers} ).subscribe( data => {

      this.setCurrentUserData( data as User );
      this.isAuthenticated = true;

      this.router.navigate(['dashboard']);

    } );

  }

  private setCurrentUserData(user:User) {
    this.currentUser = user;
    localStorage.setItem( 'currentUser', JSON.stringify(this.currentUser) );
  }

  getUserFromStorage() {

    if ( this.currentUser == null ) {

      let storageUser:string = localStorage.getItem("currentUser");

      if ( storageUser != null ) {
        this.currentUser = JSON.parse( storageUser ) as User;
        this.isAuthenticated = true;
      }
      else
        this.logout();

    }

  }

  createUser(user: User) {
    return this.http.post(this.baseUrl + 'oauth/register', user);
  }

  saveUser(user: User, updateCurrentUserData:boolean) {

    const headers = this.getHeaders();
    this.isSaving = true;

    return this.http.put( this.baseUrl + `secured/users/${user.id}`, user,{headers} ).subscribe( data => {

      if ( updateCurrentUserData )
        this.setCurrentUserData( data as User );

      this.isSaving = false;

    }, error => {
      this.isSaving = false;
    } );

  }

}
