import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {UserService} from '../../api/user.service';
import {UsersFilter} from '../../model/usersFilter';

@Component({
  selector: 'app-user-filter',
  templateUrl: './user-filter.component.html',
  styleUrls: ['./user-filter.component.scss'],
})
export class UserFilterComponent implements OnInit {

  constructor(private userService: UserService) {}

  @Input()
  private filter: UsersFilter;
  @Output()
  private onChange: EventEmitter<any> = new EventEmitter();

  ngOnInit(): void { }

  emitChange() {
    this.onChange.emit();
  }

  resetFilters() {
    this.filter.role = null;
    this.filter.isApproved = null;
    this.filter.isAutoRegistered = null;
    this.filter.phone = null;
    this.filter.email = null;
    this.filter.fio = null;
    this.emitChange();
  }

  resetSort() {
    this.filter.sort = 'lastName';
    this.filter.direction = 'asc';
    this.emitChange();
  }

}
