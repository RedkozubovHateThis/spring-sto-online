import {Params} from '@angular/router';
import {PageFilter} from './pageFilter';

export class VehiclesFilter extends PageFilter {

  modelName: string;
  vinNumber: string;
  regNumber: string;
  year: number;
  sort: string;
  direction: string;

  constructor() {
    super();
    this.modelName = null;
    this.vinNumber = null;
    this.regNumber = null;
    this.year = null;
    this.sort = 'modelName';
    this.direction = 'asc';
  }

  setFilterProperties(queryParams: Params) {
    if ( queryParams.sort ) this.sort = queryParams.sort;
    if ( queryParams.direction ) this.direction = queryParams.direction;

    if ( queryParams.modelName ) this.modelName = queryParams.modelName;
    if ( queryParams.vinNumber ) this.vinNumber = queryParams.vinNumber;
    if ( queryParams.regNumber ) this.regNumber = queryParams.regNumber;
    if ( queryParams.year ) this.year = parseInt(queryParams.year, 10);
  }

}

