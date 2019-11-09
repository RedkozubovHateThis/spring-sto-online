import {Params} from '@angular/router';

export class EventMessagesFilter {

  page: number;
  messageType: string;
  fromId: number;
  toId: number;
  documentId: number;
  sort: string;
  direction: string;

  constructor() {
    this.page = 0;
    this.messageType = null;
    this.fromId = null;
    this.toId = null;
    this.documentId = null;
    this.sort = 'messageDate';
    this.direction = 'desc';
  }

  prepareFilter(queryParams: Params) {
    if ( queryParams.sort ) this.sort = queryParams.sort;
    if ( queryParams.direction ) this.direction = queryParams.direction;

    if ( queryParams.page != null ) this.page = parseInt(queryParams.page, 10);
    else this.page = 0;

    if ( queryParams.messageType ) this.messageType = queryParams.messageType;
    else this.messageType = null;

    if ( queryParams.fromId ) this.fromId = parseInt(queryParams.fromId, 10);
    else this.fromId = null;

    if ( queryParams.toId ) this.toId = parseInt(queryParams.toId, 10);
    else this.toId = null;

    if ( queryParams.documentId ) this.documentId = parseInt(queryParams.documentId, 10);
    else this.documentId = null;
  }

}

