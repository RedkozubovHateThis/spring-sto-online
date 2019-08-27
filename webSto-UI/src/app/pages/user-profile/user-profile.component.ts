import { Component, OnInit } from '@angular/core';
import {UserService} from "../../api/user.service";
import { FormsModule } from '@angular/forms';
import {ClientResponse} from "../../model/firebird/clientResponse";
import {ClientResponseService} from "../../api/clientResponse.service";
import {User} from "../../model/postgres/auth/user";
import { Router } from '@angular/router';

@Component({
  selector: 'app-user-profile',
  templateUrl: './../user/user.component.html',
  styleUrls: ['./user-profile.component.scss']
})
export class UserProfileComponent implements OnInit {

  private model: User;
  private clientResponse:ClientResponse;
  private isClientLoading:boolean = false;
  private title: string = "Профиль";

  constructor(private userService:UserService, private clientResponseService:ClientResponseService,
              private router: Router) { }

  ngOnInit() {

    if ( this.userService.currentUser == null ) {

      let subscription = this.userService.currentUserIsLoaded.subscribe( () => {
        this.ngOnInit();
        subscription.unsubscribe();
      } );

      return;

    }

    this.model = this.userService.currentUser;
    if ( this.userService.currentUser.clientId == null ) return;

    this.isClientLoading = true;

    this.clientResponseService.getOne(this.userService.currentUser.clientId).subscribe( data => {
      this.clientResponse = data as ClientResponse;
      this.isClientLoading = false;
    }, () => {
      this.isClientLoading = false;
    } );

  }

  private navigate(user: User) {
    this.userService.setTransferModel( user );
    this.router.navigate(['/users', user.id, 'edit']);
  }

}
