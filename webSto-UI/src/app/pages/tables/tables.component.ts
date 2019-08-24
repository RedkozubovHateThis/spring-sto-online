import { Component } from '@angular/core';
import {FirebirdResponse} from "../../model/firebirdResponse";
import {FirebirdResponseService} from "../../api/firebirdResponse.service";
import { ActivatedRoute, Router } from '@angular/router';
import {Pageable} from "../../model/Pageable";
import { Subscription } from 'rxjs';
import {Pagination} from "../pagination";

@Component({
  selector: 'app-tables',
  templateUrl: './tables.component.html',
  styleUrls: ['./tables.component.scss']
})
export class TablesComponent extends Pagination {

  private all:Pageable<FirebirdResponse>;
  private isLoading:boolean = false;

  constructor(private firebirdResponseService:FirebirdResponseService, protected route:ActivatedRoute, private router:Router) {
    super(route);
  }

  requestData() {
    this.isLoading = true;
    this.firebirdResponseService.getAll(this.page, this.size, this.offset).subscribe( data => {
      this.all = data as Pageable<FirebirdResponse>;
      this.setPageData(this.all);

      this.isLoading = false;
    }, error => {
      this.isLoading = false;
    } );
  }

  private navigate(firebirdResponse:FirebirdResponse) {
    this.firebirdResponseService.exchangingModel = firebirdResponse;
    this.router.navigate(['/documents', firebirdResponse.id]);
  }

}
