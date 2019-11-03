import {ActivatedRoute, Params} from '@angular/router';
import { OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import {Pageable} from '../model/pageable';

export abstract class Pagination implements OnInit {

  private queryParamsSub: Subscription;

  page: number = 0;
  totalPages: number = 0;
  size: number = 10;
  offset: number = -10;
  pagesInfo: number[] = [];

  protected constructor(protected route: ActivatedRoute) { }

  ngOnInit() {
    this.queryParamsSub = this.route.queryParams.subscribe( queryParams => {
      const qpPage: number = queryParams.page;

      if ( qpPage != null ) this.page = qpPage - 1;
      else this.page = 0;
      this.calculatePagination();
      this.prepareFilter(queryParams);
      this.requestData();
    } );
  }

  abstract requestData();
  abstract prepareFilter(queryParams: Params);

  calculatePagination() {
    this.offset = ( this.page * this.size ) - this.size;
  }

  setPageData(data: Pageable<any>) {
    this.totalPages = data.totalPages;
    const pageInfo = this.page + 1;

    if ( this.totalPages != null ) {

      this.pagesInfo = [];
      // for ( let p:number = 1; p <= this.totalPages; p++ ) {
      //   this.pagesInfo.push(p);
      // }

      if (this.totalPages <= 6) {
        for (let p: number = 1; p <= this.totalPages; p++) {
          this.pagesInfo.push(p);
        }
      }
      else {
        // Always print first page button
        this.pagesInfo.push(1);

        // Print "..." only if currentPage is > 3
        // if (pageInfo > 3) {
        //   this.pagesInfo.push(null);
        // }

        // special case where last page is selected...
        if (pageInfo === this.totalPages) {
          this.pagesInfo.push(pageInfo - 2);
        }

        // Print previous number button if currentPage > 2
        if (pageInfo > 2) {
          this.pagesInfo.push(pageInfo - 1);
        }

        // Print current page number button as long as it not the first or last page
        if (pageInfo !== 1 && pageInfo !== this.totalPages) {
          this.pagesInfo.push(pageInfo);
        }

        // print next number button if currentPage < lastPage - 1
        if (pageInfo < this.totalPages - 1) {
          this.pagesInfo.push(pageInfo + 1);
        }

        // special case where first page is selected...
        if (pageInfo === 1) {
          this.pagesInfo.push(pageInfo + 2);
        }

        // print "..." if currentPage is < lastPage -2
        // if (pageInfo < this.totalPages - 2) {
        //   this.pagesInfo.push(null);
        // }

        // Always print last page button if there is more than 1 page
        this.pagesInfo.push(this.totalPages);
      }

    }
  }

}
