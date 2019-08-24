import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {User} from "../model/auth/user";
import {Router} from "@angular/router";
import { UserService } from './user.service';
import {FirebirdResponse} from "../model/firebirdResponse";

@Injectable()
export class FirebirdResponseService {

  constructor(private http: HttpClient, private router: Router, private userService:UserService) { }
  baseUrl: string = 'http://localhost:8181/';
  exchangingModel:FirebirdResponse;

  getLast5() {
    return this.getFirebirdResponse(0, 5, -5);
  }

  getAll(page:number, size:number, offset:number) {
    return this.getFirebirdResponse(page, size, offset);
  }

  getOne(id) {
    const headers = this.userService.getHeaders();

    return this.http.get( this.baseUrl + 'secured/documents/' + id, {headers} );
  }

  getFirebirdResponse(page:number, size:number, offset:number) {

    const headers = this.userService.getHeaders();

    return this.http.get( `${this.baseUrl}secured/documents/findAll?sort=dateStart,desc&size=${size}&page=${page}&offset=${offset}`, {headers} );

  }
}
