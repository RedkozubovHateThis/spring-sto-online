import { Component, OnInit } from '@angular/core';
import {FirebirdResponse} from "../../model/firebirdResponse";
import {FirebirdResponseService} from "../../api/firebirdResponse.service";

@Component({
  selector: 'app-tables',
  templateUrl: './tables.component.html',
  styleUrls: ['./tables.component.scss']
})
export class TablesComponent implements OnInit {

  public all:FirebirdResponse[];
  firebirdResponseService:FirebirdResponseService;
  isLoading:boolean = false;

  constructor(firebirdResponseService:FirebirdResponseService) {
    this.firebirdResponseService = firebirdResponseService;
  }

  ngOnInit() {
    this.requestData();
  }

  private requestData() {
    this.isLoading = true;
    this.firebirdResponseService.getAll().subscribe( data => {
      this.all = data as FirebirdResponse[];
      this.isLoading = false;
    }, error => {
      this.isLoading = false;
    } );
  }

}
