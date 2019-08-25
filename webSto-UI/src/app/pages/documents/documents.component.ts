import { Component, OnInit } from '@angular/core';
import {FirebirdResponse} from "../../model/firebirdResponse";
import {FirebirdResponseService} from "../../api/firebirdResponse.service";
import { ActivatedRoute, Router } from '@angular/router';
import {ModelTransfer} from "../model.transfer";

@Component({
  selector: 'app-documents',
  templateUrl: './documents.component.html',
  styleUrls: ['./documents.component.scss']
})
export class DocumentsComponent extends ModelTransfer<FirebirdResponse, number> implements OnInit {

  isLoading:boolean = false;

  constructor(private firebirdResponseService:FirebirdResponseService, protected route:ActivatedRoute) {
    super(firebirdResponseService, route);
  }

  requestData() {
    this.isLoading = true;
    this.firebirdResponseService.getOne(this.id).subscribe( data => {
      this.model = data as FirebirdResponse;
      this.isLoading = false;
    }, error => {
      this.isLoading = false;
    } );
  }

}
