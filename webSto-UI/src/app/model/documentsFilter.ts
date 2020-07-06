import {Params} from '@angular/router';
import {PageFilter} from './pageFilter';

export class DocumentsFilter extends PageFilter {

  state: string;
  paidState: string;
  organization: string;
  vehicle: string;
  vinNumber: string;
  client: string;
  sort: string;
  direction: string;
  fromDate: string;
  toDate: string;

  constructor() {
    super();
    this.state = null;
    this.paidState = null;
    this.organization = null;
    this.vehicle = null;
    this.client = null;
    this.vinNumber = null;
    this.fromDate = null;
    this.toDate = null;
    this.sort = 'startDate';
    this.direction = 'desc';
  }

  setFilterProperties(queryParams: Params) {
    if ( queryParams.sort ) this.sort = queryParams.sort;
    if ( queryParams.direction ) this.direction = queryParams.direction;

    if ( queryParams.state ) this.state = queryParams.state;
    // else this.state = null;

    if ( queryParams.paidState ) this.paidState = queryParams.paidState;
    // else this.state = null;

    if ( queryParams.organization ) this.organization = queryParams.organization;
    // else this.organization = null;

    if ( queryParams.client ) this.client = queryParams.client;
    // else this.client = null;

    if ( queryParams.vehicle ) this.vehicle = queryParams.vehicle;
    // else this.vehicle = null;

    if ( queryParams.vinNumber ) this.vinNumber = queryParams.vinNumber;
    // else this.vinNumber = null;

    if ( queryParams.fromDate ) this.fromDate = queryParams.fromDate;
    // else this.fromDate = null;

    if ( queryParams.toDate ) this.toDate = queryParams.toDate;
    // else this.toDate = null;
  }

}

