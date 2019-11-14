import {ActivatedRoute, Params, Router} from '@angular/router';
import { OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import {Pageable} from '../model/pageable';

export abstract class Pagination implements OnInit {

  private queryParamsSub: Subscription;

  protected filter: PageFilterInterface;
  protected abstract routeName: string;

  protected constructor(protected route: ActivatedRoute, protected router: Router) { }

  ngOnInit() {
    this.queryParamsSub = this.route.queryParams.subscribe( queryParams => {
      this.filter.prepareFilter(queryParams);
      this.filter.calculatePagination();
      this.requestData();
    } );
  }

  abstract requestData();

  goToPage(page: number) {
    this.filter.page = page;
    this.router.navigate([this.routeName], { queryParams: this.filter });
  }

  setSize(size: number) {
    this.filter.size = size;
    this.filter.page = 0;
    this.filter.calculatePagination();
    this.router.navigate([this.routeName], { queryParams: this.filter });
  }

  applyFilter() {
    this.filter.page = 0;
    this.router.navigate([this.routeName], { queryParams: this.filter });
  }

}
