import {Params} from '@angular/router';
import {PageFilter} from './pageFilter';

export class ProfilesFilter extends PageFilter {

  phone: string;
  email: string;
  fio: string;
  inn: string;
  address: string;
  sort: string;
  direction: string;

  constructor() {
    super();
    this.phone = null;
    this.email = null;
    this.fio = null;
    this.inn = null;
    this.address = null;
    this.sort = 'name';
    this.direction = 'asc';
  }

  setFilterProperties(queryParams: Params) {
    if ( queryParams.sort ) this.sort = queryParams.sort;
    if ( queryParams.direction ) this.direction = queryParams.direction;

    if ( queryParams.phone ) this.phone = queryParams.phone;
    // else this.phone = null;

    if ( queryParams.email ) this.email = queryParams.email;
    // else this.email = null;

    if ( queryParams.inn ) this.inn = queryParams.inn;
    // else this.inn = null;

    if ( queryParams.fio ) this.fio = queryParams.fio;
    // else this.fio = null;

    if ( queryParams.address ) this.address = queryParams.address;
    // else this.fio = null;
  }

}

