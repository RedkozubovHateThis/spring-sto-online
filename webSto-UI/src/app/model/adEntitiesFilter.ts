import {Params} from '@angular/router';
import {PageFilter} from './pageFilter';

export class AdEntitiesFilter extends PageFilter {

  name: string;
  phone: string;
  email: string;
  sideOffer: boolean;
  active: boolean;
  userId: string;
  sort: string;
  direction: string;

  constructor() {
    super();
    this.name = null;
    this.phone = null;
    this.email = null;
    this.sideOffer = null;
    this.userId = null;
    this.active = null;
    this.sort = 'name';
    this.direction = 'asc';
  }

  setFilterProperties(queryParams: Params) {
    if ( queryParams.sort ) this.sort = queryParams.sort;
    if ( queryParams.direction ) this.direction = queryParams.direction;

    if ( queryParams.name ) this.name = queryParams.name;
    // else this.name = null;

    if ( queryParams.phone ) this.phone = queryParams.phone;
    // else this.phone = null;

    if ( queryParams.sideOffer ) this.sideOffer = queryParams.sideOffer === 'true';
    // else this.email = null;

    if ( queryParams.userId ) this.userId = queryParams.userId;
    // else this.userId = null;

    if ( queryParams.active ) this.active = queryParams.active === 'true';
    // else this.email = null;
  }

}

