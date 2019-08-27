import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { Subscription } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import {UserService} from "../../api/user.service";
import {User} from "../../model/postgres/auth/user";
import {ModelTransfer} from "../model.transfer";
import {ClientResponse} from "../../model/firebird/clientResponse";
import {ClientResponseService} from "../../api/clientResponse.service";

@Component({
  selector: 'app-user-edit',
  templateUrl: './userEdit.component.html',
  styleUrls: ['./userEdit.component.scss']
})
export class UserEditComponent extends ModelTransfer<User, number> implements OnInit {

  private isLoading: boolean = false;

  private vinNumber: string;
  private clientResponse: ClientResponse;
  private isClientLoading: boolean = false;

  constructor(private userService: UserService, protected route: ActivatedRoute, private location: Location,
              private clientResponseService: ClientResponseService, private router: Router) {
    super(userService, route);
  }

  requestData() {
    this.isLoading = true;
    this.userService.getOne(this.id).subscribe( data => {
      this.model = data as User;
      this.isLoading = false;
      this.requestClient();
    }, error => {
      this.isLoading = false;
    } );
  }

  findClientByVin() {

    if ( this.vinNumber == null || this.vinNumber.length == 0 ) return;

    this.isClientLoading = true;

    this.clientResponseService.getOneByVin(this.vinNumber).subscribe( data => {
      this.clientResponse = data as ClientResponse;
      this.isClientLoading = false;
    }, () => {
      this.isClientLoading = false;
    } );

  }

  linkClient(user:User) {
    if ( this.clientResponse == null ) return;

    this.vinNumber = null;
    user.clientId = this.clientResponse.id;
    this.userService.saveUser( user );
  }

  requestClient() {
    if ( this.model.clientId == null ) return;

    this.isClientLoading = true;

    this.clientResponseService.getOne(this.userService.currentUser.clientId).subscribe( data => {
      this.clientResponse = data as ClientResponse;
      this.isClientLoading = false;
    }, () => {
      this.isClientLoading = false;
    } );
  }

  onTransferComplete() {
    this.requestClient();
  }

}
