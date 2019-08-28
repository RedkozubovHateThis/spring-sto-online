import { Component, OnInit } from '@angular/core';
import {UserService} from "../../api/user.service";
import { FormsModule } from '@angular/forms';
import {ClientResponse} from "../../model/firebird/clientResponse";
import {ClientResponseService} from "../../api/clientResponse.service";
import {User} from "../../model/postgres/auth/user";
import { Router } from '@angular/router';
import {OrganizationResponseService} from "../../api/organizationResponse.service";
import {OrganizationResponse} from "../../model/firebird/organizationResponse";

@Component({
  selector: 'app-user-profile',
  templateUrl: './../user/user.component.html',
  styleUrls: ['./user-profile.component.scss']
})
export class UserProfileComponent implements OnInit {

  private model: User;
  private clientResponse:ClientResponse;
  private organizationResponse: OrganizationResponse;
  private isADLoading:boolean = false;
  private title: string = "Профиль";

  constructor(private userService:UserService, private clientResponseService:ClientResponseService,
              private router: Router, private organizationResponseService: OrganizationResponseService) { }

  ngOnInit() {

    if ( this.userService.currentUser == null ) {

      let subscription = this.userService.currentUserIsLoaded.subscribe( () => {
        this.ngOnInit();
        subscription.unsubscribe();
      } );

      return;

    }

    this.model = this.userService.currentUser;

    if ( this.model.client )
      this.requestClient();
    else if ( this.model.serviceLeader )
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

}
