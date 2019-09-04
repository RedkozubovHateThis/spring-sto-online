import {Injectable, Input} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {User} from "../model/postgres/auth/user";
import {Router} from "@angular/router";
import { UserService } from './user.service';
import {DocumentResponse} from "../model/firebird/documentResponse";
import {TransferService} from "./transfer.service";

@Injectable()
export class DocumentResponseService implements TransferService<DocumentResponse> {

  constructor(private http: HttpClient, private router: Router, private userService: UserService) { }
  private baseUrl: string = 'http://localhost:8181/';
  private transferModel: DocumentResponse;

  getLast5() {
    return this.getDocumentResponse(0, 5, -5);
  }

  getAll(page:number, size:number, offset:number) {
    return this.getDocumentResponse(page, size, offset);
  }

  getOne(id) {
    const headers = this.userService.getHeaders();

    return this.http.get( this.baseUrl + 'secured/documents/' + id, {headers} );
  }

  getDocumentResponse(page:number, size:number, offset:number) {

    const headers = this.userService.getHeaders();

    return this.http.get( `${this.baseUrl}secured/documents/findAll?sort=dateStart,desc&size=${size}&page=${page}&offset=${offset}`, {headers} );

  }

  getDocumentsCount() {

    const headers = this.userService.getHeaders();

    return this.http.get( `${this.baseUrl}secured/documents/count`, {headers} );

  }

  getDocumentsCountByState(state: number) {

    const headers = this.userService.getHeaders();

    return this.http.get( `${this.baseUrl}secured/documents/count/state?state=${state}`, {headers} );

  }

  getTransferModel() {
    return this.transferModel;
  }

  setTransferModel(documentResponse: DocumentResponse) {
    this.transferModel = documentResponse;
  }

  resetTransferModel() {
    this.transferModel = null;
  }

}
