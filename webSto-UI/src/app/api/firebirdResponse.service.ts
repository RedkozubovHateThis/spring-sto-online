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
    return this.getFirebirdResponse(0, 5, -5);
  }

  getAll(page:number, size:number, offset:number) {
    return this.getFirebirdResponse(page, size, offset);
  }

  getOne(id) {
    const headers = this.getHeaders();

    return this.http.get( this.baseUrl + 'secured/documents/' + id, {headers} );
  }

  getFirebirdResponse(page:number, size:number, offset:number) {

    const headers = this.getHeaders();

    return this.http.get( `${this.baseUrl}secured/documents/findAll?sort=dateStart,desc&size=${size}&page=${page}&offset=${offset}`, {headers} );

  }
}
