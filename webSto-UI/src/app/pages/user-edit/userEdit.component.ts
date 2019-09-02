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

@Component({
  selector: 'app-user-edit',
  templateUrl: './userEdit.component.html',
  styleUrls: ['./userEdit.component.scss']
})
export class UserEditComponent extends ModelTransfer<User, number> implements OnInit {

  private isLoading: boolean = false;

  private vinNumber: string;
  private inn: string;
  private clientResponse: ClientResponse;
  private organizationResponse: OrganizationResponse;
  private isADLoading: boolean = false;

  constructor(private userService: UserService, protected route: ActivatedRoute, private location: Location,
              private clientResponseService: ClientResponseService, private router: Router,
              private organizationResponseService: OrganizationResponseService) {
    super(userService, route);
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

    if ( this.inn == null || this.inn.length == 0 ) return;

    this.isADLoading = true;

    this.organizationResponseService.getOneByInn(this.inn).subscribe(data => {
      this.organizationResponse = data as OrganizationResponse;
      this.isADLoading = false;
    }, () => {
      this.isADLoading = false;
    } );

  }

  linkClient(user:User) {
    if ( this.clientResponse == null ) return;

    this.vinNumber = null;
    user.clientId = this.clientResponse.id;
    this.userService.saveUser( user );
  }

  linkOrganization(user:User) {
    if ( this.organizationResponse == null ) return;

    this.inn = null;
    user.organizationId = this.organizationResponse.id;
    this.userService.saveUser( user );
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

}