import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {UserService} from '../../api/user.service';
import {OrganizationResponseService} from '../../api/organizationResponse.service';
import {OrganizationResponse} from '../../model/firebird/organizationResponse';
import {VehicleResponse} from '../../model/firebird/vehicleResponse';
import {DocumentsFilter} from '../../model/documentsFilter';
import {Router} from '@angular/router';
import {DocumentResponse} from '../../model/firebird/documentResponse';
import {ClientResponse} from '../../model/firebird/clientResponse';
import * as moment from 'moment';
import {Observable} from 'rxjs';
import {debounceTime, distinctUntilChanged, map} from 'rxjs/operators';
import {UsersFilter} from '../../model/usersFilter';
import {UserRole} from '../../model/postgres/auth/userRole';

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
    console.log(this.filter);
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