import {Injectable} from '@angular/core';
import {PaginationController} from './pagination.controller';
import {Pageable} from '../model/pageable';
import {User} from '../model/postgres/auth/user';
import {UsersFilter} from '../model/usersFilter';

@Injectable()
export class UserController extends PaginationController {

  filter: UsersFilter = new UsersFilter();
  all: Pageable<User>;

  constructor() {
    super();
  }

}
