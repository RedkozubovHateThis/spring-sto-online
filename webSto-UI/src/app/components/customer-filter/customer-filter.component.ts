import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {UserService} from '../../api/user.service';
import {CustomersFilter} from '../../model/customersFilter';

@Component({
  selector: 'app-customer-filter',
  templateUrl: './customer-filter.component.html',
  styleUrls: ['./customer-filter.component.scss'],
})
export class CustomerFilterComponent implements OnInit {

  constructor(private userService: UserService) {}

  @Input()
  private filter: CustomersFilter;
  @Output()
  private onChange: EventEmitter<any> = new EventEmitter();

  ngOnInit(): void { }

  emitChange() {
    this.onChange.emit();
  }

  resetFilters() {
    this.filter.phone = null;
    this.filter.email = null;
    this.filter.inn = null;
    this.filter.name = null;
    this.emitChange();
  }

  resetSort() {
    this.filter.sort = 'name';
    this.filter.direction = 'asc';
    this.emitChange();
  }

}
