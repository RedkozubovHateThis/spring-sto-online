import {Params} from '@angular/router';
import {PageFilter} from './pageFilter';

export class DictionariesFilter extends PageFilter {

  name: string;
  sort: string;
  direction: string;

  constructor() {
    super();
    this.name = null;
    this.sort = 'name';
    this.direction = 'asc';
  }

  setFilterProperties(queryParams: Params) {
    if ( queryParams.sort ) this.sort = queryParams.sort;
    if ( queryParams.direction ) this.direction = queryParams.direction;

    if ( queryParams.name ) this.name = queryParams.name;
    // else this.role = null;
  }

}

