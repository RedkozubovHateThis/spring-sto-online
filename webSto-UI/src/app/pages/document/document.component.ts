import {Component, OnInit} from '@angular/core';
import {DocumentResponse} from '../../model/firebird/documentResponse';
import {DocumentResponseService} from '../../api/documentResponse.service';
import {ActivatedRoute} from '@angular/router';
import {ModelTransfer} from '../model.transfer';
import {ServiceWorkResponseService} from '../../api/serviceWorkResponse.service';
import {UserService} from '../../api/user.service';
import {HttpClient} from '@angular/common/http';
import {ToastrService} from 'ngx-toastr';
import {Location} from '@angular/common';
import {UserResponse} from '../../model/firebird/userResponse';
import {PaymentService} from '../../api/payment.service';
import {ManagerResponse} from '../../model/firebird/managerResponse';
import {OrganizationResponseService} from '../../api/organizationResponse.service';
import {ServiceWorkResponse} from '../../model/firebird/serviceWorkResponse';

@Component({
  selector: 'app-documents',
  templateUrl: './document.component.html',
  styleUrls: ['./document.component.scss']
})
export class DocumentComponent extends ModelTransfer<DocumentResponse, number> implements OnInit {

  private isLoading: boolean = false;
  private firebirdUsers: UserResponse[] = [];
  private managers: ManagerResponse[] = [];
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
              private location: Location, private paymentService: PaymentService, private organizationResponseService: OrganizationResponseService) {
    super(documentResponseService, route);
  }

  requestData() {
    this.isLoading = true;
    this.documentResponseService.getOne(this.id).subscribe( data => {
      this.model = data as DocumentResponse;
      this.isLoading = false;
      this.findManagers();
    }, error => {
      this.isLoading = false;
    } );
    this.findFirebirdUsers();
  }

  onTransferComplete() {
    this.findManagers();
    this.findFirebirdUsers();
  }

  findFirebirdUsers() {
    this.documentResponseService.getFirebirdUsers().subscribe( data => {
      this.firebirdUsers = data as UserResponse[];
    } );
  }

  findManagers() {
    this.organizationResponseService.getAllManagers(this.model.organizationId).subscribe( managers => {
      this.managers = managers;
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

  updatePriceView(serviceWork: ServiceWorkResponse) {

    const count = serviceWork.quantity || 1;

    if ( serviceWork.byPrice ) {
      serviceWork.total = serviceWork.price * count;
    }
    else {
      serviceWork.total = serviceWork.priceNorm * serviceWork.timeValue * count;
    }
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
        this.toastrService.error('Ошибка изменения статуса заказ-наряда! Возможно, заказ-наряд открыт в системе АвтоДилер.', 'Внимание!');
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
        this.toastrService.error('Ошибка изменения оформителя заказ-наряда! Возможно, заказ-наряд открыт в системе АвтоДилер.', 'Внимание!');
      } );
  }

  updateManager(documentResponse, managerId) {

    if ( documentResponse == null || managerId == null ) return;

    this.isUpdating = true;

    this.serviceWorkResponseService.updateManager(documentResponse.id, documentResponse.documentOutHeaderId, managerId)
      .subscribe( data => {
          this.model = data as DocumentResponse;
          this.isUpdating = false;
          this.toastrService.success('Менеджер заказ-наряда успешно изменен(-а)');
      }, () => {
        this.isUpdating = false;
        this.toastrService.error('Ошибка изменения менеджера заказ-наряда! Возможно, заказ-наряд открыт в системе АвтоДилер.', 'Внимание!');
      } );
  }
}
