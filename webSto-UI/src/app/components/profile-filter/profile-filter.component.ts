import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {UserService} from '../../api/user.service';
import {UsersFilter} from '../../model/usersFilter';
import {ProfilesFilter} from '../../model/profilesFilter';

@Component({
  selector: 'app-profile-filter',
  templateUrl: './profile-filter.component.html',
  styleUrls: ['./profile-filter.component.scss'],
})
export class ProfileFilterComponent implements OnInit {

  constructor(private userService: UserService) {}

  @Input()
  private filter: ProfilesFilter;
  @Output()
  private onChange: EventEmitter<any> = new EventEmitter();

  ngOnInit(): void { }

  emitChange() {
    this.onChange.emit();
  }

  resetFilters() {
    this.filter.phone = null;
    this.filter.email = null;
    this.filter.fio = null;
    this.filter.inn = null;
    this.filter.address = null;
    this.emitChange();
  }

  resetSort() {
    this.filter.sort = 'name';
    this.filter.direction = 'asc';
    this.emitChange();
  }

}
