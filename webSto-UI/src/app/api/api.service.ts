import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {User} from "../model/user";
import {Router} from "@angular/router";

@Injectable()
export class ApiService {

  constructor(private http: HttpClient, private router: Router) { }
  baseUrl: string = 'http://localhost:8181/';
  currentUser: User;
  isAuthenticated: boolean = false;

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
  }

  getUsername() {
    if ( this.currentUser != null )
      return this.currentUser.username;
    else
      return undefined;
  }

  getHeaders() {
    return {
      'Authorization': 'Bearer ' + JSON.parse(window.sessionStorage.getItem("token")).access_token
    };
  }

  getCurrentUser() {

    const headers = this.getHeaders();

    this.http.get( this.baseUrl + 'secured/users/currentUser', {headers} ).subscribe( data => {

      this.currentUser = data as User;
      this.isAuthenticated = true;
      localStorage.setItem( 'currentUser', JSON.stringify(this.currentUser) );

      this.router.navigate(['dashboard']);

    } );

  }

  getUserFromStorage() {

    if ( this.currentUser == null ) {

      let storageUser:string = localStorage.getItem("currentUser");

      if ( storageUser != null ) {
        this.currentUser = JSON.parse( storageUser ) as User;
        this.isAuthenticated = true;
      }

    }

  }

  getUsers() {
    return this.http.get(this.baseUrl + 'user_oath2?access_token=' + JSON.parse(window.sessionStorage.getItem('token')).access_token);
  }

  getUserById(id: number) {
    return this.http.get(this.baseUrl + 'user_oath2/' + id + '?access_token=' + JSON.parse(window.sessionStorage.getItem('token')).access_token);
  }

  createUser(user: User){
    return this.http.post(this.baseUrl + 'oauth/register', user);
  }

  updateUser(user: User) {
    return this.http.put(this.baseUrl + 'user_oath2/' + user.id + '?access_token=' + JSON.parse(window.sessionStorage.getItem('token')).access_token, user);
  }

  deleteUser(id: number){
    return this.http.delete(this.baseUrl + 'user_oath2/' + id + '?access_token=' + JSON.parse(window.sessionStorage.getItem('token')).access_token);
  }
}
