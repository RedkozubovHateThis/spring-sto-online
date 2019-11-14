import {Injectable, Input} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Router} from '@angular/router';
import { UserService } from './user.service';
import {DocumentResponse} from '../model/firebird/documentResponse';
import {TransferService} from './transfer.service';
import {DocumentsFilter} from '../model/documentsFilter';

@Injectable()
export class DocumentResponseService implements TransferService<DocumentResponse> {

  constructor(private http: HttpClient, private router: Router, private userService: UserService) { }
  private transferModel: DocumentResponse;

  getLast5() {
    return this.getDocumentResponse('dateStart,desc', 0, 5, -5, null, null, null,
      null, null, null, null);
  }

  getAll(filter: DocumentsFilter) {
    return this.getDocumentResponse(`${filter.sort},${filter.direction}`, filter.page, filter.size, filter.offset, filter.state, filter.organization,
      filter.vehicle, filter.client, filter.vinNumber, filter.fromDate, filter.toDate);
  }

  getOne(id) {
    const headers = this.userService.getHeaders();

    return this.http.get( this.userService.getApiUrl() + 'secured/documents/' + id, {headers} );
  }

  getDocumentResponse(sort: string, page: number, size: number, offset: number, state: number, organization: number, vehicle: string,
                      client: number, vinNumber: string, fromDate: string, toDate: string) {

    const headers = this.userService.getHeaders();
    const params = {
      sort,
      page: page.toString(),
      size: size.toString(),
      offset: offset.toString(),
      states: state != null ? state.toString() : '',
      organizations: organization != null ? organization.toString() : '',
      clients: client != null ? client.toString() : '',
      vehicle: vehicle != null ? vehicle : '',
      vinNumber: vinNumber != null ? vinNumber : '',
      fromDate: fromDate != null ? fromDate : '',
      toDate: toDate != null ? toDate : '',
    };

    return this.http.get( `${this.userService.getApiUrl()}secured/documents/findAll`, {headers, params} );

  }

  getDocumentsCount() {

    const headers = this.userService.getHeaders();

    return this.http.get( `${this.userService.getApiUrl()}secured/documents/count`, {headers} );

  }

  getDocumentsCountByState(state: number) {

    const headers = this.userService.getHeaders();

    return this.http.get( `${this.userService.getApiUrl()}secured/documents/count/state?state=${state}`, {headers} );

  }

  getDocumentsByEventMessage() {

    const headers = this.userService.getHeaders();

    return this.http.get( `${this.userService.getApiUrl()}secured/documents/eventMessages/findAll`, {headers} );

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
