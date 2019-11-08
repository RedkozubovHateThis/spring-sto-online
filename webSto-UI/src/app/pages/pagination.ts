import {ActivatedRoute, Params, Router} from '@angular/router';
import { OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import {Pageable} from '../model/pageable';

export abstract class Pagination implements OnInit {

  private queryParamsSub: Subscription;

  protected filter: PageFilterInterface;
  protected abstract routeName: string;
  protected size: number = 10;
  protected offset: number = -10;

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

  goToPage(page: number) {
    this.filter.page = page;
    this.router.navigate([this.routeName], { queryParams: this.filter });
  }

  applyFilter() {
    this.filter.page = 0;
    this.router.navigate([this.routeName], { queryParams: this.filter });
  }

}
