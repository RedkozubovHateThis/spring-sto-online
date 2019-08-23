import { Component, OnInit } from '@angular/core';
import {FirebirdResponse} from "../../model/firebirdResponse";
import {FirebirdResponseService} from "../../api/firebirdResponse.service";
import { Subscription } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-tables',
  templateUrl: './documents.component.html',
  styleUrls: ['./documents.component.scss']
})
export class DocumentsComponent implements OnInit {

  private routeSub:Subscription;
  private response:FirebirdResponse;
  private id:number;
  isLoading:boolean = false;

  constructor(private firebirdResponseService:FirebirdResponseService, private route:ActivatedRoute) { }

  ngOnInit() {

    if ( this.firebirdResponseService.exchangingModel != null ) {
      this.response = this.firebirdResponseService.exchangingModel;
      this.firebirdResponseService.exchangingModel = null;
    }
    else {
      this.routeSub = this.route.params.subscribe(params => {
        this.id = params['id'];
      });

      this.requestData();
    }

  }

  ngOnDestroy() {
    if ( this.routeSub != null )
      this.routeSub.unsubscribe();
  }

  private requestData() {
    this.isLoading = true;
    this.firebirdResponseService.getOne(this.id).subscribe( data => {
      this.response = data as FirebirdResponse;
      this.isLoading = false;
    }, error => {
      this.isLoading = false;
    } );
  }

}
