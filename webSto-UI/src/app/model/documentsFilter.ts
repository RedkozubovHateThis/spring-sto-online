import {Params} from '@angular/router';

export class DocumentsFilter {

  page: number;
  state: number;
  organization: number;
  vehicle: string;
  vinNumber: string;
  client: number;
  sort: string;
  direction: string;
  fromDate: string;
  toDate: string;

  constructor() {
    this.page = 0;
    this.state = null;
    this.organization = null;
    this.vehicle = null;
    this.client = null;
    this.vinNumber = null;
    this.fromDate = null;
    this.toDate = null;
    this.sort = 'dateStart';
    this.direction = 'desc';
  }

  prepareFilter(queryParams: Params) {
    if ( queryParams.sort ) this.sort = queryParams.sort;
    if ( queryParams.direction ) this.direction = queryParams.direction;

    if ( queryParams.page != null ) this.page = parseInt(queryParams.page, 10);
    else this.page = 0;

    if ( queryParams.state ) this.state = parseInt(queryParams.state, 10);
    else this.state = null;

    if ( queryParams.organization ) this.organization = parseInt(queryParams.organization, 10);
    else this.organization = null;

    if ( queryParams.client ) this.client = parseInt(queryParams.client, 10);
    else this.client = null;

    if ( queryParams.vehicle ) this.vehicle = queryParams.vehicle;
    else this.vehicle = null;

    if ( queryParams.vinNumber ) this.vinNumber = queryParams.vinNumber;
    else this.vinNumber = null;

    if ( queryParams.fromDate ) this.fromDate = queryParams.fromDate;
    else this.fromDate = null;

    if ( queryParams.toDate ) this.toDate = queryParams.toDate;
    else this.toDate = null;
  }

}

