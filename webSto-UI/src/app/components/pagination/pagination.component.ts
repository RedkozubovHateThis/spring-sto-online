import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';

@Component({
  selector: 'app-pagination',
  templateUrl: './pagination.component.html',
  styleUrls: ['./pagination.component.scss']
})
export class PaginationComponent implements OnInit, OnChanges {

  @Input()
  private totalRecords: number;
  private isFirst = false;
  private isLast = false;
  @Input()
  private page: number;
  @Input()
  private size: number;
  @Input()
  private disabled: boolean = false;
  @Output()
  private onPageChange: EventEmitter<number> = new EventEmitter();
  @Output()
  private onSizeChange: EventEmitter<number> = new EventEmitter();
  private pagesInfo: number[] = [];

  constructor() { }

  ngOnInit() {
    this.setPageData();
  }

  private setPageData() {
    const pageInfo = this.page;

    if ( this.totalRecords != null ) {
      const totalPages = Math.ceil( this.totalRecords / this.size );
      this.isFirst = pageInfo === 1;
      this.isLast = pageInfo === totalPages;

      this.pagesInfo = [];
      // for ( let p:number = 1; p <= totalPages; p++ ) {
      //   this.pagesInfo.push(p);
      // }

      if (totalPages <= 6) {
        for (let p: number = 1; p <= totalPages; p++) {
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
        if (pageInfo === totalPages) {
          this.pagesInfo.push(pageInfo - 2);
        }

        // Print previous number button if currentPage > 2
        if (pageInfo > 2) {
          this.pagesInfo.push(pageInfo - 1);
        }

        // Print current page number button as long as it not the first or last page
        if (pageInfo !== 1 && pageInfo !== totalPages) {
          this.pagesInfo.push(pageInfo);
        }

        // print next number button if currentPage < lastPage - 1
        if (pageInfo < totalPages - 1) {
          this.pagesInfo.push(pageInfo + 1);
        }

        // special case where first page is selected...
        if (pageInfo === 1) {
          this.pagesInfo.push(pageInfo + 2);
        }

        // print "..." if currentPage is < lastPage -2
        // if (pageInfo < totalPages - 2) {
        //   this.pagesInfo.push(null);
        // }

        // Always print last page button if there is more than 1 page
        this.pagesInfo.push(totalPages);
      }

    }
  }

  private goToPage(page: number, event) {
    if ( this.disabled ) return;

    event.preventDefault();
    this.onPageChange.emit(page);
  }

  private setSize(size: number, event) {
    if ( this.disabled ) return;

    event.preventDefault();
    this.onSizeChange.emit(size);
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.setPageData();
  }
}
