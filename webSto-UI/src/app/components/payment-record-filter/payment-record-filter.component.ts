import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {UserService} from '../../api/user.service';
import * as moment from 'moment';
import {DocumentCollection} from 'ngx-jsonapi';
import {PaymentRecordsFilter} from '../../model/paymentRecordsFilter';
import {states, types} from '../../model/resource/payment-record.resource.service';
import {UserResource} from '../../model/resource/user.resource.service';

@Component({
  selector: 'app-payment-record-filter',
  templateUrl: './payment-record-filter.component.html',
  styleUrls: ['./payment-record-filter.component.scss'],
})
export class PaymentRecordFilterComponent implements OnInit {

  constructor(private userService: UserService) {}

  @Input()
  private filter: PaymentRecordsFilter;
  @Output()
  private onChange: EventEmitter<any> = new EventEmitter();
  private paymentStates = states;
  private paymentTypes = types;
  private datePickerConfig = {
    locale: 'ru',
    firstDayOfWeek: 'mo',
    // showGoToCurrent: true,
    format: 'DD.MM.YYYY',
    // monthFormat: 'MM, YYYY'
  };
  private fromDate: moment.Moment;
  private toDate: moment.Moment;
  private users: DocumentCollection<UserResource> = new DocumentCollection<UserResource>();

  ngOnInit(): void {
    this.userService.getAllServiceLeadersAndFreelancers().subscribe( (response) => {
      this.users = response;
    } );
    if ( this.filter.fromDate )
      this.fromDate = moment(this.filter.fromDate, 'DD.MM.YYYY');
    if ( this.filter.toDate )
      this.toDate = moment(this.filter.toDate, 'DD.MM.YYYY');
  }

  setFromDate(e) {
    this.filter.fromDate = e;
    this.emitChange();
  }

  setToDate(e) {
    this.filter.toDate = e;
    this.emitChange();
  }

  emitChange() {
    this.onChange.emit();
  }

  resetFilters() {
    this.filter.paymentState = null;
    this.filter.paymentType = null;
    this.filter.userId = null;
    this.filter.fromDate = null;
    this.filter.toDate = null;
    this.fromDate = null;
    this.toDate = null;
    this.emitChange();
  }

  resetSort() {
    this.filter.sort = 'startDate';
    this.filter.direction = 'desc';
    this.emitChange();
  }

}
