import {ActivatedRoute, Params, Router} from '@angular/router';
import { OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import {Pageable} from '../model/pageable';

export abstract class Pagination implements OnInit {

  private queryParamsSub: Subscription;

  protected filter: PageFilterInterface;
  protected abstract routeName: string;
  protected totalPages: number = 0;
  protected size: number = 10;
  protected offset: number = -10;
  protected pagesInfo: number[] = [];

  protected constructor(protected route: ActivatedRoute, protected router: Router) { }

  ngOnInit() {
    this.queryParamsSub = this.route.queryParams.subscribe( queryParams => {
      this.filter.prepareFilter(queryParams);
      this.calculatePagination();
      this.requestData();
    } );
  }

  abstract requestData();

  calculatePagination() {
    this.offset = ( this.filter.page * this.size ) - this.size;
  }

  setPageData(data: Pageable<any>) {
    this.totalPages = data.totalPages;
    const pageInfo = this.filter.page + 1;

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

  goToPage(page: number, event) {
    event.preventDefault();
    this.filter.page = page;
    this.router.navigate([this.routeName], { queryParams: this.filter });
  }

  applyFilter() {
    this.filter.page = 0;
    this.router.navigate([this.routeName], { queryParams: this.filter });
  }

}
