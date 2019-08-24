import { ActivatedRoute } from '@angular/router';
import { OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import {Pageable} from "../model/pageable";

export abstract class Pagination implements OnInit {

  private queryParamsSub:Subscription;

  page:number = 0;
  totalPages:number = 0;
  size:number = 10;
  offset:number = -10;
  pagesInfo:number[] = [];

  protected constructor(protected route:ActivatedRoute) { }

  ngOnInit() {
    this.queryParamsSub = this.route.queryParams.subscribe( queryParams => {
      let qpPage:number = queryParams['page'];

      if ( qpPage != null ) this.page = qpPage - 1;
      this.calculatePagination();
      this.requestData();
    } );
  }

  abstract requestData();

  calculatePagination() {
    this.offset = ( this.page * this.size ) - this.size;
  }

  setPageData(data:Pageable<any>) {
    this.totalPages = data.totalPages;

    if ( this.totalPages != null ) {

      this.pagesInfo = [];
      for ( let p:number = 1; p <= this.totalPages; p++ ) {
        this.pagesInfo.push(p);
      }

    }
  }

}
