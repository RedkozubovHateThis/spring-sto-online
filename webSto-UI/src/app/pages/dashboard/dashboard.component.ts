import { Component, OnInit } from '@angular/core';
import {DocumentResponse} from "../../model/firebird/documentResponse";
import {DocumentResponseService} from "../../api/documentResponse.service";
import {Pageable} from "../../model/pageable";
import { Router } from '@angular/router';
import {ChatMessageResponseService} from '../../api/chatMessageResponse.service';
import {UserService} from '../../api/user.service';
import {DocumentResponseController} from '../../controller/document-response.controller';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {

  private isLoading: boolean = false;

  constructor(private documentResponseService: DocumentResponseService, private router: Router, private userService: UserService,
              private documentResponseController: DocumentResponseController) { }

  ngOnInit() {
    this.requestData();
  }

  private requestData() {
    this.isLoading = true;
    this.documentResponseService.getLast5().subscribe( data => {
      this.documentResponseController.last5 = data as Pageable<DocumentResponse>;
      this.isLoading = false;
    }, error => {
      this.isLoading = false;
    } );
  }

  private navigate(documentResponse: DocumentResponse) {
    this.documentResponseService.setTransferModel( documentResponse );
    this.router.navigate(['/documents', documentResponse.id]);
  }

}
