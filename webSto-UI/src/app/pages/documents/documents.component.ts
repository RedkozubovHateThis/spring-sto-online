import { Component, OnInit } from '@angular/core';
import {FirebirdResponse} from "../../model/firebirdResponse";
import {FirebirdResponseService} from "../../api/firebirdResponse.service";
import { Subscription } from 'rxjs';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-tables',
  templateUrl: './documents.component.html',
  styleUrls: ['./documents.component.scss']
})
export class DocumentsComponent implements OnInit {

  private routeSub:Subscription;
  private route:ActivatedRoute;
  private response:FirebirdResponse;
  private id:number;
  firebirdResponseService:FirebirdResponseService;
  isLoading:boolean = false;

  constructor(firebirdResponseService:FirebirdResponseService, route:ActivatedRoute) {
    this.firebirdResponseService = firebirdResponseService;
    this.route = route;
  }

  ngOnInit() {

    this.routeSub = this.route.params.subscribe(params => {
      console.log(params['id']); //log the value of id
      this.id = params['id'];
    });

    this.requestData();
  }

  ngOnDestroy() {
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
