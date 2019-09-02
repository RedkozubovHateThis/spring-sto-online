import { Component, OnInit } from '@angular/core';
import {DocumentResponse} from "../../model/firebird/documentResponse";
import {DocumentResponseService} from "../../api/documentResponse.service";
import { ActivatedRoute, Router } from '@angular/router';
import {ModelTransfer} from "../model.transfer";
import {ServiceWorkResponseService} from "../../api/ServiceWorkResponse.service";
import {UserService} from "../../api/user.service";

@Component({
  selector: 'app-documents',
  templateUrl: './document.component.html',
  styleUrls: ['./document.component.scss']
})
export class DocumentComponent extends ModelTransfer<DocumentResponse, number> implements OnInit {

  private isLoading: boolean = false;
  private price: number;
  private isUpdating: boolean = false;
  private states = [
    {
      name: 'Черновик',
      id: 2
    },
    {
      name: 'Оформлен',
      id: 4
    }
  ];

  constructor(private documentResponseService:DocumentResponseService, protected route:ActivatedRoute,
              private serviceWorkResponseService: ServiceWorkResponseService, private userService: UserService) {
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

  onTransferComplete() {}

  updatePrice(documentResponse, serviceWork, price) {

    if ( documentResponse == null || serviceWork == null || price == null ) return;

    this.isUpdating = true;

    this.serviceWorkResponseService.updatePrice(documentResponse.id, serviceWork.id, serviceWork.byPrice, price)
      .subscribe( data => {
          this.model = data as DocumentResponse;
          this.isUpdating = false;
      }, () => {
        this.isUpdating = false;
      } );
  }

  updateState(documentResponse, state) {

    if ( documentResponse == null || state == null ) return;

    this.isUpdating = true;

    this.serviceWorkResponseService.updateState(documentResponse.id, documentResponse.documentOutHeaderId, state)
      .subscribe( data => {
          this.model = data as DocumentResponse;
          this.isUpdating = false;
      }, () => {
        this.isUpdating = false;
      } );
  }

}
