import { Component, OnInit } from '@angular/core';
import {DocumentResponse} from "../../model/firebird/documentResponse";
import {DocumentResponseService} from "../../api/documentResponse.service";
import { ActivatedRoute, Router } from '@angular/router';
import {ModelTransfer} from "../model.transfer";

@Component({
  selector: 'app-documents',
  templateUrl: './document.component.html',
  styleUrls: ['./document.component.scss']
})
export class DocumentComponent extends ModelTransfer<DocumentResponse, number> implements OnInit {

  isLoading:boolean = false;

  constructor(private documentResponseService:DocumentResponseService, protected route:ActivatedRoute) {
    super(documentResponseService, route);
  }

  requestData() {
    this.isLoading = true;
    this.documentResponseService.getOne(this.id).subscribe( data => {
      this.model = data as DocumentResponse;
      this.isLoading = false;
    }, error => {
      this.isLoading = false;
    } );
  }

}
