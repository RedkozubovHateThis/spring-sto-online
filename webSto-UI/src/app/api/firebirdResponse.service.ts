import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {User} from "../model/user";
import {Router} from "@angular/router";

@Injectable()
export class FirebirdResponseService {

  constructor(private http: HttpClient, private router: Router) { }
  baseUrl: string = 'http://localhost:8181/';

  getHeaders() {
    return {
      'Authorization': 'Bearer ' + JSON.parse(window.sessionStorage.getItem("token")).access_token
    };
  }

  getLast5() {
    return this.getFirebirdResponse(5);
  }

  getAll() {
    return this.getFirebirdResponse(1000);
  }

  getFirebirdResponse(size:number) {

    const headers = this.getHeaders();

    return this.http.get( this.baseUrl + 'secured/documents/findAll?sort=dateStart,desc&size=' + size, {headers} );

  }
}
