import {Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Pageable} from '../../model/pageable';
import {Pagination} from '../pagination';
import {User} from '../../model/postgres/auth/user';
import {UserService} from '../../api/user.service';
import {UserController} from '../../controller/user.controller';
import {UserResource} from '../../model/resource/user.resource.service';

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
      this.userController.all = data;

      this.isLoading = false;
    }, error => {
      this.isLoading = false;
    } );
  }

  private navigate(user: UserResource) {
    this.userService.setTransferModel( user );
    this.router.navigate(['/users', user.id]);
  }

}
