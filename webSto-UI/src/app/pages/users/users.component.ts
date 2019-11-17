import { Component } from '@angular/core';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {Pageable} from "../../model/pageable";
import { Subscription } from 'rxjs';
import {Pagination} from "../pagination";
import {User} from "../../model/postgres/auth/user";
import {UserService} from "../../api/user.service";
import {UsersFilter} from '../../model/usersFilter';
import {UserController} from '../../controller/user.controller';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss']
})
export class UsersComponent extends Pagination {

  private isLoading: boolean = false;
  protected routeName = '/users';

  constructor(private userService: UserService, protected route: ActivatedRoute, protected router: Router,
              protected userController: UserController) {
    super(route, router, userController);
  }

  requestData() {
    this.isLoading = true;
    this.userService.getAll(this.userController.filter).subscribe( data => {
      this.userController.all = data as Pageable<User>;

      this.isLoading = false;
    }, error => {
      this.isLoading = false;
    } );
  }

  private navigate(user: User) {
    this.userService.setTransferModel( user );
    this.router.navigate(['/users', user.id]);
  }

}
