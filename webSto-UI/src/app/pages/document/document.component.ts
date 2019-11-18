import {Component, OnInit, ViewChild} from '@angular/core';
import {DocumentResponse} from "../../model/firebird/documentResponse";
import {DocumentResponseService} from "../../api/documentResponse.service";
import { ActivatedRoute, Router } from '@angular/router';
import {ModelTransfer} from "../model.transfer";
import {ServiceWorkResponseService} from "../../api/serviceWorkResponse.service";
import {UserService} from "../../api/user.service";
import {HttpClient} from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import {Location} from '@angular/common';
import {UserResponse} from '../../model/firebird/userResponse';

@Component({
  selector: 'app-documents',
  templateUrl: './document.component.html',
  styleUrls: ['./document.component.scss']
})
export class DocumentComponent extends ModelTransfer<DocumentResponse, number> implements OnInit {

  private isLoading: boolean = false;
  private firebirdUsers: UserResponse[] = [];
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
    this.findFirebirdUsers();
  }

  onTransferComplete() {
    this.findFirebirdUsers();
  }

  findFirebirdUsers() {
    this.documentResponseService.getFirebirdUsers().subscribe( data => {
      this.firebirdUsers = data as UserResponse[];
    } );
  }

  updatePrice(documentResponse, serviceWork, price) {

    if ( documentResponse == null || serviceWork == null || price == null ) return;

    this.isUpdating = true;

    this.serviceWorkResponseService.updatePrice(documentResponse.id, serviceWork.id, serviceWork.byPrice, price)
      .subscribe( data => {
          this.model = data as DocumentResponse;
          this.isUpdating = false;
        this.toastrService.success('Стоимость работы успешно изменена');
      }, () => {
        this.isUpdating = false;
        this.toastrService.error('Ошибка изменения стоимости работы!');
      } );
  }

  updateCost(documentResponse, serviceGoodsAddon, cost) {

    if ( documentResponse == null || serviceGoodsAddon == null || cost == null ) return;

    this.isUpdating = true;

    this.serviceWorkResponseService.updateCost(documentResponse.id, serviceGoodsAddon.id, cost)
      .subscribe( data => {
          this.model = data as DocumentResponse;
          this.isUpdating = false;
        this.toastrService.success('Стоимость товара успешно изменена');
      }, () => {
        this.isUpdating = false;
        this.toastrService.error('Ошибка изменения стоимости товара!');
      } );
  }

  updateState(documentResponse, state) {

    if ( documentResponse == null || state == null ) return;

    this.isUpdating = true;

    this.serviceWorkResponseService.updateState(documentResponse.id, documentResponse.documentOutHeaderId, state)
      .subscribe( data => {
          this.model = data as DocumentResponse;
          this.isUpdating = false;
          this.toastrService.success('Статус заказ-наряда успешно изменен');
      }, () => {
        this.isUpdating = false;
        this.toastrService.error('Ошибка изменения статуса заказ-наряда!', 'Внимание!');
      } );
  }

  updateUser(documentResponse, userId) {

    if ( documentResponse == null || userId == null ) return;

    this.isUpdating = true;

    this.serviceWorkResponseService.updateUser(documentResponse.id, documentResponse.documentOutHeaderId, userId)
      .subscribe( data => {
          this.model = data as DocumentResponse;
          this.isUpdating = false;
          this.toastrService.success('Оформитель заказ-наряда успешно изменен(-а)');
      }, () => {
        this.isUpdating = false;
        this.toastrService.error('Ошибка изменения оформителя заказ-наряда!', 'Внимание!');
      } );
  }
}
