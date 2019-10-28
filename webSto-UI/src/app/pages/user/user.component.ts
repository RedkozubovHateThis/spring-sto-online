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

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserComponent extends ModelTransfer<User, number> implements OnInit {

  private isLoading: boolean = false;
  private clientResponse: ClientResponse;
  private organizationResponse: OrganizationResponse;
  private isADLoading: boolean = false;
  private title: string = "Данные пользователя";
  private showBack: boolean = true;
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

      if ( this.model.client )
        this.requestClient();
      else if ( this.model.serviceLeader )
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
      this.isADLoading = false;
    }, () => {
      this.isADLoading = false;
    } );
  }

  onTransferComplete() {
    if ( this.model.client )
      this.requestClient();
    else if ( this.model.serviceLeader )
      this.requestOrganization();
  }

  private navigate(user: User) {
    this.userService.setTransferModel( user );
    this.router.navigate(['/users', user.id, 'edit']);
  }

  private approve(user: User, approved: boolean) {
    user.isApproved = approved;
    this.userService.saveUser(user);
  }

}
