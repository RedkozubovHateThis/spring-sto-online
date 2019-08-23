import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {User} from "../model/auth/user";
import {Router} from "@angular/router";
import { ApiService } from './api.service';

@Injectable()
export class FirebirdResponseService {

  constructor(private http: HttpClient, private router: Router, private apiService:ApiService) { }
  baseUrl: string = 'http://localhost:8181/';

  getLast5() {
    return this.getFirebirdResponse(0, 5, -5);
  }

  getAll(page:number, size:number, offset:number) {
    return this.getFirebirdResponse(page, size, offset);
  }

  getOne(id) {
    const headers = this.apiService.getHeaders();

    return this.http.get( this.baseUrl + 'secured/documents/' + id, {headers} );
  }

  getFirebirdResponse(page:number, size:number, offset:number) {

    const headers = this.apiService.getHeaders();

    return this.http.get( `${this.baseUrl}secured/documents/findAll?sort=dateStart,desc&size=${size}&page=${page}&offset=${offset}`, {headers} );

  }
}
