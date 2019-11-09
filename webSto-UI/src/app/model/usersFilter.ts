import {Params} from '@angular/router';

export class UsersFilter implements PageFilterInterface {

  page: number;
  role: string;
  isApproved: string;
  isAutoRegistered: string;
  phone: string;
  email: string;
  fio: string;
  sort: string;
  direction: string;

  constructor() {
    this.page = 0;
    this.role = null;
    this.isApproved = null;
    this.isAutoRegistered = null;
    this.phone = null;
    this.email = null;
    this.fio = null;
    this.sort = 'lastName';
    this.direction = 'asc';
  }

  prepareFilter(queryParams: Params) {
    if ( queryParams.sort ) this.sort = queryParams.sort;
    if ( queryParams.direction ) this.direction = queryParams.direction;

    if ( queryParams.page != null ) this.page = parseInt(queryParams.page, 10);
    else this.page = 0;

    if ( queryParams.role ) this.role = queryParams.role;
    else this.role = null;

    if ( queryParams.isApproved ) this.isApproved = queryParams.isApproved;
    else this.isApproved = null;

    if ( queryParams.isAutoRegistered ) this.isAutoRegistered = queryParams.isAutoRegistered;
    else this.isAutoRegistered = null;

    if ( queryParams.phone ) this.phone = queryParams.phone;
    else this.phone = null;

    if ( queryParams.email ) this.email = queryParams.email;
    else this.email = null;

    if ( queryParams.fio ) this.fio = queryParams.fio;
    else this.fio = null;
  }

}

