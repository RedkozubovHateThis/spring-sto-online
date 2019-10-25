import { Component } from '@angular/core';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {Pageable} from "../../model/Pageable";
import { Subscription } from 'rxjs';
import {Pagination} from "../pagination";
import {User} from "../../model/postgres/auth/user";
import {UserService} from "../../api/user.service";

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss']
})
export class UsersComponent extends Pagination {

  private all:Pageable<User>;
  private isLoading:boolean = false;

  constructor(private userService:UserService, protected route:ActivatedRoute, private router:Router) {
    super(route);
  }

  requestData() {
    this.isLoading = true;
    this.userService.getAll(this.page, this.size, this.offset).subscribe( data => {
      this.all = data as Pageable<User>;
      this.setPageData(this.all);

      this.isLoading = false;
    }, error => {
      this.isLoading = false;
    } );
  }

  private navigate(user:User) {
    this.userService.setTransferModel( user );
    this.router.navigate(['/users', user.id]);
  }

  prepareFilter(queryParams: Params) {}

  refresh() {
    this.requestData();
  }

}
