import {Params} from '@angular/router';

export abstract class PageFilter implements PageFilterInterface {

  page: number;
  size: number;
  offset: number;

  protected constructor() {
    this.page = 1;
    this.size = 10;
    this.offset = -10;
  }

  prepareFilter(queryParams: Params) {
    if ( queryParams.page != null ) this.page = parseInt(queryParams.page, 10);
    // else this.page = 0;
    if ( queryParams.size != null ) this.size = parseInt(queryParams.size, 10);
    // else this.size = 10;
    if ( queryParams.offset != null ) this.offset = parseInt(queryParams.offset, 10);
    // else this.offset = -10;

    this.setFilterProperties(queryParams);
  }

  calculatePagination() {
    this.offset = ( this.page * this.size ) - this.size;
  }

  abstract setFilterProperties(queryParams: Params);

}

