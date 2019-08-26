import { Component, OnInit } from '@angular/core';
import {UserService} from "../../api/user.service";
import { FormsModule } from '@angular/forms';
import {ClientResponse} from "../../model/firebird/clientResponse";
import {ClientResponseService} from "../../api/clientResponse.service";
import {User} from "../../model/postgres/auth/user";

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss']
})
export class UserProfileComponent implements OnInit {

  private vinNumber:string;
  private clientResponse:ClientResponse;
  private isClientLoading:boolean = false;

  constructor(private userService:UserService, private clientResponseService:ClientResponseService) { }

  ngOnInit() {

    if ( this.userService.currentUser == null ) {

      let subscription = this.userService.currentUserIsLoaded.subscribe( () => {
        this.ngOnInit();
        subscription.unsubscribe();
      } );

      return;

    }

    if ( this.userService.currentUser.clientId == null ) return;

    this.isClientLoading = true;

    this.clientResponseService.getOne(this.userService.currentUser.clientId).subscribe( data => {
      this.clientResponse = data as ClientResponse;
      this.isClientLoading = false;
    }, () => {
      this.isClientLoading = false;
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
    this.userService.currentUser.clientId = this.clientResponse.id;
    this.userService.saveUser( user, true );
  }

}
