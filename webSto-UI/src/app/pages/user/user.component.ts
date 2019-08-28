import { Component, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import {UserService} from "../../api/user.service";
import {User} from "../../model/postgres/auth/user";
import {ModelTransfer} from "../model.transfer";
import {ClientResponse} from "../../model/firebird/clientResponse";
import {ClientResponseService} from "../../api/clientResponse.service";
import {DocumentResponse} from "../../model/firebird/documentResponse";

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserComponent extends ModelTransfer<User, number> implements OnInit {

  private isLoading: boolean = false;
  private clientResponse: ClientResponse;
  private isClientLoading: boolean = false;
  private title: string = "Данные пользователя";

  constructor(private userService: UserService, protected route: ActivatedRoute,
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

  requestClient() {
    if ( this.model.clientId == null ) return;

    this.isClientLoading = true;

    this.clientResponseService.getOne(this.model.clientId).subscribe( data => {
      this.clientResponse = data as ClientResponse;
      this.isClientLoading = false;
    }, () => {
      this.isClientLoading = false;
    } );
  }

  onTransferComplete() {
    this.requestClient();
  }

  private navigate(user: User) {
    this.userService.setTransferModel( user );
    this.router.navigate(['/users', user.id, 'edit']);
  }

}
