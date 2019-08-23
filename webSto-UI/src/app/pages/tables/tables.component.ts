import { Component, OnInit } from '@angular/core';
import {FirebirdResponse} from "../../model/firebirdResponse";
import {FirebirdResponseService} from "../../api/firebirdResponse.service";
import { ActivatedRoute, Router } from '@angular/router';
import {Pageable} from "../../model/Pageable";
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-tables',
  templateUrl: './tables.component.html',
  styleUrls: ['./tables.component.scss']
})
export class TablesComponent implements OnInit {

  private routeSub:Subscription;
  private all:Pageable<FirebirdResponse>;
  private isLoading:boolean = false;
  private page:number = 0;
  private totalPages:number = 0;
  private size:number = 10;
  private offset:number = -10;
  private pagesInfo:number[] = [];

  constructor(private firebirdResponseService:FirebirdResponseService, private route:ActivatedRoute, private router:Router) {  }

  ngOnInit() {
    this.routeSub = this.route.queryParams.subscribe( queryParams => {
      let qpPage:number = queryParams['page'];

      if ( qpPage != null ) this.page = qpPage - 1;
      this.calculatePagination();
      this.requestData();
    } );
  }

  ngOnDestroy() {
    if ( this.routeSub != null )
      this.routeSub.unsubscribe();
  }

  private requestData() {
    this.isLoading = true;
    this.firebirdResponseService.getAll(this.page, this.size, this.offset).subscribe( data => {
      this.all = data as Pageable<FirebirdResponse>;
      this.totalPages = this.all.totalPages;

      if ( this.totalPages != null ) {

        this.pagesInfo = [];
        for ( let p:number = 1; p <= this.totalPages; p++ ) {
          this.pagesInfo.push(p);
        }

      }

      this.isLoading = false;
    }, error => {
      this.isLoading = false;
    } );
  }

  private calculatePagination() {
      this.offset = ( this.page * this.size ) - this.size;
  }

  private navigate(firebirdResponse:FirebirdResponse) {
    this.firebirdResponseService.exchangingModel = firebirdResponse;
    this.router.navigate(['/documents', firebirdResponse.id]);
  }

}
