import { Component, OnInit } from '@angular/core';
import {DocumentResponse} from "../../model/firebird/documentResponse";
import {DocumentResponseService} from "../../api/documentResponse.service";
import { ActivatedRoute, Router } from '@angular/router';
import {ModelTransfer} from "../model.transfer";
import {ServiceWorkResponseService} from "../../api/ServiceWorkResponse.service";
import {UserService} from "../../api/user.service";
import {HttpClient} from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import {Location} from '@angular/common';

@Component({
  selector: 'app-documents',
  templateUrl: './document.component.html',
  styleUrls: ['./document.component.scss']
})
export class DocumentComponent extends ModelTransfer<DocumentResponse, number> implements OnInit {

  private isDownloading: boolean = false;
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

  constructor(private documentResponseService: DocumentResponseService, protected route: ActivatedRoute, private toastrService: ToastrService,
              private serviceWorkResponseService: ServiceWorkResponseService, private userService: UserService, private httpClient: HttpClient,
              private location: Location) {
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

  updateCost(documentResponse, serviceGoodsAddon, cost) {

    if ( documentResponse == null || serviceGoodsAddon == null || cost == null ) return;

    this.isUpdating = true;

    this.serviceWorkResponseService.updateCost(documentResponse.id, serviceGoodsAddon.id, cost)
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

  downloadReport(reportType: string, reportName: string) {
    const headers = this.userService.getHeaders();

    this.isDownloading = true;
    this.httpClient.get(`http://localhost:8181/secured/reports/${this.model.id}/${reportType}`,
      {headers, responseType: 'blob'} ).subscribe( blob => {

      this.isDownloading = false;

      const data = window.URL.createObjectURL(blob);

      const link = document.createElement('a');
      link.href = data;
      link.download = `${reportName} № ${this.model.documentNumber}.pdf`;
      link.dispatchEvent(new MouseEvent('click', { bubbles: true, cancelable: true, view: window }));

      setTimeout( () => {
        window.URL.revokeObjectURL(data);
        link.remove();
      }, 100 );

    }, error => {
      this.isDownloading = false;
      if ( error.status === 404 )
        this.toastrService.error('Необходимые данные не найдены!', 'Внимание!');
      else
        this.toastrService.error('Ошибка формирования заказ-наряда!', 'Внимание!');
    } );
  }

}
