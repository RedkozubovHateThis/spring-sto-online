import {Params} from '@angular/router';
import {PageFilter} from './pageFilter';

export class EventMessagesFilter extends PageFilter {

  messageType: string;
  fromId: number;
  toId: number;
  documentId: number;
  sort: string;
  direction: string;

  constructor() {
    super();
    this.messageType = null;
    this.fromId = null;
    this.toId = null;
    this.documentId = null;
    this.sort = 'messageDate';
    this.direction = 'desc';
  }

  setFilterProperties(queryParams: Params) {
    if ( queryParams.sort ) this.sort = queryParams.sort;
    if ( queryParams.direction ) this.direction = queryParams.direction;

    if ( queryParams.messageType ) this.messageType = queryParams.messageType;
    // else this.messageType = null;

    if ( queryParams.fromId ) this.fromId = parseInt(queryParams.fromId, 10);
    // else this.fromId = null;

    if ( queryParams.toId ) this.toId = parseInt(queryParams.toId, 10);
    // else this.toId = null;

    if ( queryParams.documentId ) this.documentId = parseInt(queryParams.documentId, 10);
    // else this.documentId = null;
  }

}

