import {Params} from '@angular/router';
import {PageFilter} from './pageFilter';

export class UsersFilter extends PageFilter {

  role: string;
  isAutoRegistered: string;
  phone: string;
  email: string;
  fio: string;
  inn: string;
  sort: string;
  direction: string;

  constructor() {
    super();
    this.role = null;
    this.isAutoRegistered = null;
    this.phone = null;
    this.email = null;
    this.fio = null;
    this.inn = null;
    this.sort = 'lastName';
    this.direction = 'asc';
  }

  setFilterProperties(queryParams: Params) {
    if ( queryParams.sort ) this.sort = queryParams.sort;
    if ( queryParams.direction ) this.direction = queryParams.direction;

    if ( queryParams.role ) this.role = queryParams.role;
    // else this.role = null;

    if ( queryParams.isAutoRegistered ) this.isAutoRegistered = queryParams.isAutoRegistered;
    // else this.isAutoRegistered = null;

    if ( queryParams.phone ) this.phone = queryParams.phone;
    // else this.phone = null;

    if ( queryParams.email ) this.email = queryParams.email;
    // else this.email = null;

    if ( queryParams.inn ) this.inn = queryParams.inn;
    // else this.inn = null;

    if ( queryParams.fio ) this.fio = queryParams.fio;
    // else this.fio = null;
  }

}

