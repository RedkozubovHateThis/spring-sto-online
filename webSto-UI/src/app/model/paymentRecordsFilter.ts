import {Params} from '@angular/router';
import {PageFilter} from './pageFilter';

export class PaymentRecordsFilter extends PageFilter {

  paymentType: string;
  paymentState: string;
  userId: string;
  fromDate: string;
  toDate: string;
  sort: string;
  direction: string;

  constructor() {
    super();
    this.paymentType = null;
    this.paymentState = null;
    this.userId = null;
    this.fromDate = null;
    this.toDate = null;
    this.sort = 'createDate';
    this.direction = 'desc';
  }

  setFilterProperties(queryParams: Params) {
    if ( queryParams.sort ) this.sort = queryParams.sort;
    if ( queryParams.direction ) this.direction = queryParams.direction;

    if ( queryParams.paymentState ) this.paymentState = queryParams.paymentState;
    // else this.paymentState = null;

    if ( queryParams.paymentType ) this.paymentType = queryParams.paymentType;
    // else this.paymentType = null;

    if ( queryParams.userId ) this.userId = queryParams.userId;
    // else this.userId = null;

    if ( queryParams.fromDate ) this.fromDate = queryParams.fromDate;
    // else this.fromDate = null;

    if ( queryParams.toDate ) this.toDate = queryParams.toDate;
    // else this.toDate = null;
  }

}

