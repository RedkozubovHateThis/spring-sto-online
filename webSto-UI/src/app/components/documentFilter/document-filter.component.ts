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

@Component({
  selector: 'app-document-filter',
  templateUrl: './document-filter.component.html',
  styleUrls: ['./document-filter.component.scss'],
})
export class DocumentFilterComponent implements OnInit {

  constructor(private organizationResponseService: OrganizationResponseService, private userService: UserService) {}

  @Input()
  private filter: DocumentsFilter;
  @Input()
  private document: DocumentResponse;
  @Output()
  private onChange: EventEmitter<any> = new EventEmitter();
  private states = [
    {
      name: 'Черновик',
      id: 2
    },
    {
      name: 'Оформлен',
      id: 4
    }
  ];
  private datePickerConfig = {
    locale: 'ru',
    firstDayOfWeek: 'mo',
    showGoToCurrent: true,
    format: 'DD.MM.YYYY',
    monthFormat: 'MM, YYYY'
  };
  private fromDate: moment.Moment;
  private toDate: moment.Moment;
  private organizations: OrganizationResponse[] = [];
  private clients: ClientResponse[] = [];

  ngOnInit(): void {
    this.organizationResponseService.getAll().subscribe( response => {
      this.organizations = response as OrganizationResponse[];
    } );
    this.organizationResponseService.getAllClients().subscribe( response => {
      this.clients = response as ClientResponse[];
    } );
    if ( this.filter.fromDate )
      this.fromDate = moment(this.filter.fromDate, 'DD.MM.YYYY');
    if ( this.filter.toDate )
      this.toDate = moment(this.filter.toDate, 'DD.MM.YYYY');
  }

  setFromDate() {
    this.filter.fromDate = this.fromDate.format('DD.MM.YYYY');
    this.emitChange();
  }

  setToDate() {
    this.filter.toDate = this.toDate.format('DD.MM.YYYY');
    this.emitChange();
  }

  emitChange() {
    this.onChange.emit();
  }

  resetFilters() {
    this.filter.state = null;
    this.filter.organization = null;
    this.filter.vehicle = null;
    this.filter.client = null;
    this.filter.vinNumber = null;
    this.filter.fromDate = null;
    this.filter.toDate = null;
    this.fromDate = null;
    this.toDate = null;
    this.emitChange();
  }

  resetSort() {
    this.filter.sort = 'dateStart';
    this.filter.direction = 'desc';
    this.emitChange();
  }

}
