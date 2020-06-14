import {ActivatedRoute, Router} from '@angular/router';
import {OnInit} from '@angular/core';
import {Subscription} from 'rxjs';
import {PaginationController} from '../controller/pagination.controller';

export abstract class Pagination implements OnInit {

  private queryParamsSub: Subscription;

  protected abstract routeName: string;

  protected constructor(protected route: ActivatedRoute, protected router: Router, protected paginationController: PaginationController) { }

  ngOnInit() {
    this.queryParamsSub = this.route.queryParams.subscribe( queryParams => {
      this.paginationController.filter.prepareFilter(queryParams);
      this.paginationController.filter.calculatePagination();
      this.requestData();
    } );
  }

  abstract requestData();

  goToPage(page: number) {
    this.paginationController.filter.page = page;
    this.router.navigate([this.routeName], { queryParams: this.paginationController.filter });
  }

  setSize(size: number) {
    this.paginationController.filter.size = size;
    this.paginationController.filter.page = 0;
    this.paginationController.filter.calculatePagination();
    this.router.navigate([this.routeName], { queryParams: this.paginationController.filter });
  }

  applyFilter() {
    this.paginationController.filter.page = 0;
    this.router.navigate([this.routeName], { queryParams: this.paginationController.filter });
  }

}
