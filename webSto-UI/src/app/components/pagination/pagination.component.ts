import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {Pageable} from '../../model/pageable';

@Component({
  selector: 'app-pagination',
  templateUrl: './pagination.component.html',
  styleUrls: ['./pagination.component.scss']
})
export class PaginationComponent implements OnInit, OnChanges {

  @Input()
  private totalPages: number;
  @Input()
  private isFirst: boolean;
  @Input()
  private isLast: boolean;
  @Input()
  private filter: PageFilterInterface;
  @Output()
  private onPageChange: EventEmitter<number> = new EventEmitter();
  private pagesInfo: number[] = [];

  constructor() { }

  ngOnInit() {
    this.setPageData();
  }

  private setPageData() {
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

  private goToPage(page: number, event) {
    event.preventDefault();
    this.onPageChange.emit(page);
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.setPageData();
  }
}
