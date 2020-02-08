import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {UserService} from '../../api/user.service';
import {User} from '../../model/postgres/auth/user';
import {ModelTransfer} from '../model.transfer';
import {ClientResponse} from '../../model/firebird/clientResponse';
import {ClientResponseService} from '../../api/clientResponse.service';
import {OrganizationResponse} from '../../model/firebird/organizationResponse';
import {OrganizationResponseService} from '../../api/organizationResponse.service';
import {Location} from '@angular/common';
import {Shops} from '../../variables/shops';
import {ManagerResponse} from '../../model/firebird/managerResponse';
import {PaymentService} from '../../api/payment.service';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserComponent extends ModelTransfer<User, number> implements OnInit {

  private isLoading: boolean = false;
  private clientResponse: ClientResponse;
  private organizationResponse: OrganizationResponse;
  private managerResponse: ManagerResponse;
  private isADLoading: boolean = false;
  private title: string = "Данные пользователя";
  private showBack: boolean = true;
  private shops: ShopInterface[] = [];

  constructor(private userService: UserService, protected route: ActivatedRoute, private location: Location,
              private clientResponseService: ClientResponseService, private router: Router, private toastrService: ToastrService,
              private organizationResponseService: OrganizationResponseService, private paymentService: PaymentService) {
    super(userService, route);
    this.shops = Shops.shops;
  }

  requestData() {
    this.isLoading = true;
    this.userService.getOne(this.id).subscribe( data => {
      this.model = data as User;
      this.isLoading = false;

      if ( this.model.userClient )
        this.requestClient();
      else if ( this.model.userServiceLeaderOrFreelancer )
        this.requestOrganization();

    }, error => {
      this.isLoading = false;
    } );
  }

  containsShop(user: User, shopId: number): boolean {
    if ( user == null || user.partShops == null ) return false;
    return this.model.partShops.includes(shopId);
  }

  requestClient() {
    if ( this.model.clientId == null ) return;

    this.isADLoading = true;

    this.clientResponseService.getOne(this.model.clientId).subscribe( data => {
      this.clientResponse = data as ClientResponse;
      this.isADLoading = false;
    }, () => {
      this.isADLoading = false;
    } );
  }

  requestOrganization() {
    if ( this.model.organizationId == null ) return;

    this.isADLoading = true;

    this.organizationResponseService.getOne(this.model.organizationId).subscribe( data => {
      this.organizationResponse = data as OrganizationResponse;

      if ( this.model.userFreelancer )
        this.requestManager();
      else
        this.isADLoading = false;
    }, () => {
      this.isADLoading = false;
    } );
  }

  requestManager() {
    if ( this.model.managerId == null ) return;

    this.organizationResponseService.getOneManager( this.model.managerId ).subscribe( manager => {
      this.managerResponse = manager;
      this.isADLoading = false;
    }, () => {
      this.isADLoading = false;
    } );
  }

  onTransferComplete() {
    if ( this.model.userClient )
      this.requestClient();
    else if ( this.model.userServiceLeaderOrFreelancer )
      this.requestOrganization();
  }

  private navigate(user: User) {
    this.userService.setTransferModel( user );
    this.router.navigate(['/users', user.id, 'edit']);
  }

  private approve(user: User, approved: boolean) {
    user.isApproved = approved;
    this.userService.saveUser(user, `Пользователь успешно ${approved ? 'подвтержден' : 'отменен'}!`);
  }

  private removeLink(user: User) {
    user.isApproved = false;
    user.clientId = null;
    user.organizationId = null;
    this.clientResponse = null;
    this.organizationResponse = null;
    this.userService.saveUser(user, `Привязка успешно удалена!`);
  }

  private giftSubscription() {
    this.isLoading = true;

    this.paymentService.giftSubscription(this.model.id).subscribe( subscriptionResponse => {
      this.isLoading = false;
      this.toastrService.success('Тариф успешно выдан!');
    }, error => {
      this.isLoading = false;

      if ( error.error.responseText )
        this.toastrService.error(error.error.responseText, 'Внимание!');
      else
        this.toastrService.error('Ошибка выдачи тарифа!', 'Внимание!');
    } );
  }

}
