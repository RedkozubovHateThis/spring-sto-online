import {Params} from '@angular/router';
import {PageFilter} from './pageFilter';

export class CustomersFilter extends PageFilter {

  name: string;
  inn: string;
  phone: string;
  email: number;
  sort: string;
  direction: string;

  constructor() {
    super();
    this.name = null;
    this.inn = null;
    this.phone = null;
    this.email = null;
    this.sort = 'name';
    this.direction = 'asc';
  }

  setFilterProperties(queryParams: Params) {
    if ( queryParams.sort ) this.sort = queryParams.sort;
    if ( queryParams.direction ) this.direction = queryParams.direction;

    if ( queryParams.name ) this.name = queryParams.name;
    if ( queryParams.inn ) this.inn = queryParams.inn;
    if ( queryParams.phone ) this.phone = queryParams.phone;
    if ( queryParams.email ) this.email = queryParams.email;
  }

}

