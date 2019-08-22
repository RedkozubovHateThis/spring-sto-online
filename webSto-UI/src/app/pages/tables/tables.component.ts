import { Component, OnInit } from '@angular/core';
import {FirebirdResponse} from "../../model/firebirdResponse";
import {FirebirdResponseService} from "../../api/firebirdResponse.service";
import { ActivatedRoute } from '@angular/router';
import {Pageable} from "../../model/Pageable";

@Component({
  selector: 'app-tables',
  templateUrl: './tables.component.html',
  styleUrls: ['./tables.component.scss']
})
export class TablesComponent implements OnInit {

  private all:Pageable<FirebirdResponse>;
  private firebirdResponseService:FirebirdResponseService;
  private isLoading:boolean = false;
  private page:number = 0;
  private totalPages:number = 0;
  private size:number = 10;
  private offset:number = -10;
  private route:ActivatedRoute;
  private pagesInfo:number[] = [];

  constructor(firebirdResponseService:FirebirdResponseService, route:ActivatedRoute) {
    this.firebirdResponseService = firebirdResponseService;
    this.route = route;
  }

  ngOnInit() {
    this.route.queryParams.subscribe( queryParams => {
      let qpPage:number = queryParams['page'];

      if ( qpPage != null ) this.page = qpPage - 1;
      this.calculatePagination();
      this.requestData();
    } );

    // this.requestData();
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

}
