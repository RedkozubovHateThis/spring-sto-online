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

@Component({
  selector: 'app-user-edit',
  templateUrl: './userEdit.component.html',
  styleUrls: ['./userEdit.component.scss']
})
export class UserEditComponent extends ModelTransfer<User, number> implements OnInit {

  private isLoading: boolean = false;

  private vinNumber: string;
  private clientResponse: ClientResponse;
  private organizationResponse: OrganizationResponse;
  private isADLoading: boolean = false;
  private replaceModerators: User[];
  private moderators: User[];
  private shops: ShopInterface[] = [];

  constructor(private userService: UserService, protected route: ActivatedRoute, private location: Location,
              private clientResponseService: ClientResponseService, private router: Router,
              private organizationResponseService: OrganizationResponseService) {
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

    }, error => {
      this.isLoading = false;
    } );

    this.requestReplacementModerators();
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

    if ( this.vinNumber == null || this.vinNumber.length == 0 ) return;

    this.isADLoading = true;

    this.clientResponseService.getOneByVin(this.vinNumber).subscribe( data => {
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

    this.vinNumber = null;
    user.clientId = this.clientResponse.id;
    this.userService.saveUser( user, 'Автомобиль успешно привязан! Ожидайте подтверждения данных модератором!' );
  }

  linkOrganization(user: User) {
    if ( this.organizationResponse == null ) return;

    user.organizationId = this.organizationResponse.id;
    this.userService.saveUser( user, 'Организация успешно привязана! Ожидайте подтверждения данных модератором!' );
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

    this.requestReplacementModerators();
  }

  private removeLink(user: User) {
    user.isApproved = false;
    user.clientId = null;
    user.organizationId = null;
    this.clientResponse = null;
    this.organizationResponse = null;
    this.userService.saveUser(user, `Привязка успешно удалена!`);
  }

}
