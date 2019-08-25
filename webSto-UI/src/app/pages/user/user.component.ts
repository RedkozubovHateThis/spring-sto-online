import { Component, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import {UserService} from "../../api/user.service";
import {User} from "../../model/postgres/auth/user";
import {ModelTransfer} from "../model.transfer";

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserComponent extends ModelTransfer<User, number> implements OnInit {
  isLoading:boolean = false;

  constructor(private userService:UserService, protected route:ActivatedRoute) {
    super(userService, route);
  }

  requestData() {
    this.isLoading = true;
    this.userService.getOne(this.id).subscribe( data => {
      this.model = data as User;
      this.isLoading = false;
    }, error => {
      this.isLoading = false;
    } );
  }

}
