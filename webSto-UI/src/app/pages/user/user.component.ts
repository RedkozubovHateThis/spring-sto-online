import { Component, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import {UserService} from "../../api/user.service";
import {User} from "../../model/auth/user";

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserComponent implements OnInit {

  private routeSub:Subscription;
  private model:User;
  private id:number;
  isLoading:boolean = false;

  constructor(private userService:UserService, private route:ActivatedRoute) { }

  ngOnInit() {

    if ( this.userService.exchangingModel != null ) {
      this.model = this.userService.exchangingModel;
      this.userService.exchangingModel = null;
    }
    else {
      this.routeSub = this.route.params.subscribe(params => {
        this.id = params['id'];
      });

      this.requestData();
    }

  }

  ngOnDestroy() {
    if ( this.routeSub != null )
      this.routeSub.unsubscribe();
  }

  private requestData() {
    this.isLoading = true;
    this.userService.getOne(this.id).subscribe( data => {
      this.model = data as User;
      this.isLoading = false;
    }, error => {
      this.isLoading = false;
    } );
  }

}
