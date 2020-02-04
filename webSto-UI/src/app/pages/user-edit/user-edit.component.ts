import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { Subscription } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import {UserService} from "../../api/user.service";
import {User} from "../../model/postgres/auth/user";
import {ModelTransfer} from "../model.transfer";
import {ClientResponse} from "../../model/firebird/clientResponse";
import {ClientResponseService} from "../../api/clientResponse.service";
import {OrganizationResponseService} from "../../api/organizationResponse.service";
import {OrganizationResponse} from "../../model/firebird/organizationResponse";
import { Shops } from './../../variables/shops';
import {PaymentService} from '../../api/payment.service';
import {SubscriptionTypeResponse} from '../../model/payment/subscriptionTypeResponse';
import {ManagerResponse} from '../../model/firebird/managerResponse';

@Component({
  selector: 'app-user-edit',
  templateUrl: './user-edit.component.html',
  styleUrls: ['./user-edit.component.scss']
})
export class UserEditComponent extends ModelTransfer<User, number> implements OnInit {

  private isLoading: boolean = false;

  private clientResponse: ClientResponse;
  private organizationResponse: OrganizationResponse;
  private managerResponse: ManagerResponse;
  private isADLoading: boolean = false;
  private replaceModerators: User[];
  private moderators: User[];
  private shops: ShopInterface[] = [];
  private subscriptionTypes: SubscriptionTypeResponse[] = [];
  private organizations: OrganizationResponse[] = [];
  private managers: ManagerResponse[] = [];
  private isTypesLoading: boolean = false;

  constructor(private userService: UserService, protected route: ActivatedRoute, private location: Location,
              private clientResponseService: ClientResponseService, private router: Router,
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
      else if ( this.model.userServiceLeader )
        this.requestOrganization();

      if ( this.model.userFreelancer ) {
        this.requestOrganizations();
        if ( this.model.organizationId != null )
          this.requestManagers( this.model.organizationId, false );
      }

    }, error => {
      this.isLoading = false;
    } );

    this.requestReplacementModerators();
    this.requestAllSubscriptionTypes();
  }

  requestAllSubscriptionTypes() {
    this.isTypesLoading = true;

    this.paymentService.getAllSubscriptionTypes().subscribe(subscriptionTypes => {
      this.subscriptionTypes = subscriptionTypes;
      this.isTypesLoading = false;
    }, () => {
      this.isTypesLoading = false;
    } );
  }

  manageShop(user: User, shopId: number) {
    if ( user == null ) return;

    if ( user.partShops == null )
      user.partShops = [];

    const index = user.partShops.indexOf( shopId );
    if ( index > -1 )
      user.partShops.splice(index, 1);
    else
      user.partShops.push(shopId);
  }

  containsShop(user: User, shopId: number): boolean {
    if ( user == null || user.partShops == null ) return false;
    return this.model.partShops.includes(shopId);
  }

  findClientByVin() {

    if ( this.model.vin == null || this.model.vin.length == 0 ) return;

    this.isADLoading = true;

    this.clientResponseService.getOneByVin(this.model.vin).subscribe( data => {
      this.clientResponse = data as ClientResponse;
      this.isADLoading = false;
    }, () => {
      this.isADLoading = false;
    } );

  }

  findOrganizationByInn() {

    if ( this.model.inn == null || this.model.inn.length == 0 ) return;

    this.isADLoading = true;

    this.organizationResponseService.getOneByInn(this.model.inn).subscribe(data => {
      this.organizationResponse = data as OrganizationResponse;
      this.isADLoading = false;
    }, () => {
      this.isADLoading = false;
    } );

  }

  linkClient(user: User) {
    if ( this.clientResponse == null ) return;

    user.clientId = this.clientResponse.id;
    this.userService.saveUser( user, 'Автомобиль успешно привязан! Ожидайте подтверждения данных модератором!' );
  }

  linkOrganization(user: User) {
    if ( this.organizationResponse == null ) return;

    user.organizationId = this.organizationResponse.id;
    this.userService.saveUser( user, 'Организация успешно привязана! Ожидайте подтверждения данных модератором!' );
  }

  linkManager(user: User) {
    if ( this.model.organizationId == null || this.model.managerId == null ) return;
    this.userService.saveUser( user, 'Менеджер успешно привязан! Ожидайте подтверждения данных модератором!' );
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
      this.isADLoading = false;
    }, () => {
      this.isADLoading = false;
    } );
  }

  requestOrganizations() {
    this.organizationResponseService.getAll().subscribe( data => {
      this.organizations = data as OrganizationResponse[];
    }, () => {
    } );
  }

  requestManagers(organizationId: number, resetManager: boolean) {

    if ( resetManager ) {
      this.model.organizationId = organizationId;
      this.model.managerId = null;
    }

    this.organizationResponseService.getAllManagers( organizationId ).subscribe( managers => {
      this.managers = managers;
    }, () => {
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

  requestReplacementModerators() {
    this.userService.getReplacementModerators().subscribe( response => {
      this.replaceModerators = response as User[];
    } );
    this.userService.getModerators().subscribe( moderators => {
      this.moderators = moderators as User[];
    } );
  }

  onTransferComplete() {
    if ( this.model.userClient )
      this.requestClient();
    else if ( this.model.userServiceLeader )
      this.requestOrganization();

    if ( this.model.userFreelancer ) {
      this.requestOrganizations();
      if ( this.model.organizationId != null )
        this.requestManagers( this.model.organizationId, false );
    }

    this.requestReplacementModerators();
    this.requestAllSubscriptionTypes();
  }

  private removeLink(user: User) {
    user.isApproved = false;
    user.clientId = null;
    user.organizationId = null;
    user.managerId = null;
    this.clientResponse = null;
    this.organizationResponse = null;
    this.managerResponse = null;
    this.userService.saveUser(user, `Привязка успешно удалена!`);
  }

}
