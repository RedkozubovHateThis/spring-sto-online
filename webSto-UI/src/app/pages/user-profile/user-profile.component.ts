import { Component, OnInit } from '@angular/core';
import {UserService} from "../../api/user.service";
import { FormsModule } from '@angular/forms';
import {ClientResponse} from "../../model/firebird/clientResponse";
import {ClientResponseService} from "../../api/clientResponse.service";
import {User} from "../../model/postgres/auth/user";
import { Router } from '@angular/router';
import {OrganizationResponseService} from "../../api/organizationResponse.service";
import {OrganizationResponse} from "../../model/firebird/organizationResponse";
import {HttpClient} from '@angular/common/http';
import {ToastrService} from 'ngx-toastr';
import {Shops} from '../../variables/shops';
import {ManagerResponse} from '../../model/firebird/managerResponse';
import {VehicleResponse} from '../../model/firebird/vehicleResponse';

@Component({
  selector: 'app-user-profile',
  templateUrl: './../user/user.component.html',
  styleUrls: ['./../user/user.component.scss']
})
export class UserProfileComponent implements OnInit {

  private isLoading: boolean = false;
  private model: User;
  private clientResponse: ClientResponse;
  private organizationResponse: OrganizationResponse;
  private managerResponse: ManagerResponse;
  private isADLoading: boolean = false;
  private title: string = "Профиль";
  private showBack: boolean = false;
  private shops: ShopInterface[] = [];
  private vehicles: VehicleResponse[] = [];

  constructor(private userService: UserService, private clientResponseService: ClientResponseService,
              private router: Router, private organizationResponseService: OrganizationResponseService, private httpClient: HttpClient,
              private toastrService: ToastrService) {
    this.shops = Shops.shops;
  }

  ngOnInit() {

    if ( this.userService.currentUser == null ) {

      let subscription = this.userService.currentUserIsLoaded.subscribe( () => {
        this.ngOnInit();
        subscription.unsubscribe();
      } );

      return;

    }

    this.model = this.userService.currentUser;

    if ( this.model.userClient ) {
      this.requestClient();
      this.requestVehicles();
    }
    else if ( this.model.userServiceLeaderOrFreelancer )
      this.requestOrganization();

  }

  private navigate(user: User) {
    this.userService.setTransferModel( user );
    this.router.navigate(['/users', user.id, 'edit']);
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

  requestVehicles() {
    this.isLoading = true;

    this.userService.getVehicles(this.model.id).subscribe( data => {
      this.vehicles = data;
      this.isLoading = false;
    }, () => {
      this.isLoading = false;
    } );
  }

  getVehicle(vinNumber: string): VehicleResponse {
    if ( this.vehicles.length === 0 ) return null;

    return this.vehicles.find( vehicle => vehicle.vinNumber.toLowerCase() === vinNumber.toLowerCase() );
  }

  containsShop(user: User, shopId: number): boolean {
    if ( user == null || user.partShops == null ) return false;
    return this.model.partShops.includes(shopId);
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

}
