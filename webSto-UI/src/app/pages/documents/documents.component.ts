import { Component } from '@angular/core';
import {DocumentResponse} from "../../model/firebird/documentResponse";
import {DocumentResponseService} from "../../api/documentResponse.service";
import { ActivatedRoute, Router } from '@angular/router';
import {Pageable} from "../../model/Pageable";
import { Subscription } from 'rxjs';
import {Pagination} from "../pagination";

@Component({
  selector: 'app-tables',
  templateUrl: './documents.component.html',
  styleUrls: ['./documents.component.scss']
})
export class DocumentsComponent extends Pagination {

  private all:Pageable<DocumentResponse>;
  private isLoading:boolean = false;

  constructor(private documentResponseService:DocumentResponseService, protected route:ActivatedRoute, private router:Router) {
    super(route);
  }

  requestData() {
    this.isLoading = true;
    this.documentResponseService.getAll(this.page, this.size, this.offset).subscribe( data => {
      this.all = data as Pageable<DocumentResponse>;
      this.setPageData(this.all);

      this.isLoading = false;
    }, error => {
      this.isLoading = false;
    } );
  }

  private navigate(documentResponse:DocumentResponse) {
    this.documentResponseService.exchangingModel = documentResponse;
    this.router.navigate(['/documents', documentResponse.id]);
  }

}
