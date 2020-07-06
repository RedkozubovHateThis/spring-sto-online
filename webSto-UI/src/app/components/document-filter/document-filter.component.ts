import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {UserService} from '../../api/user.service';
import {DocumentsFilter} from '../../model/documentsFilter';
import * as moment from 'moment';
import {Observable} from 'rxjs';
import {debounceTime, distinctUntilChanged, map} from 'rxjs/operators';
import {ServiceDocumentResource} from '../../model/resource/service-document.resource.service';
import {ProfileResource} from '../../model/resource/profile.resource.service';
import {DocumentCollection} from 'ngx-jsonapi';
import {ProfileService} from '../../api/profile.service';

@Component({
  selector: 'app-document-filter',
  templateUrl: './document-filter.component.html',
  styleUrls: ['./document-filter.component.scss'],
})
export class DocumentFilterComponent implements OnInit {

  constructor(private userService: UserService, private profileService: ProfileService) {}

  @Input()
  private filter: DocumentsFilter;
  @Input()
  private document: ServiceDocumentResource;
  @Input()
  private disabled: boolean;
  @Output()
  private onChange: EventEmitter<any> = new EventEmitter();
  @Output()
  private onOrganizationChange: EventEmitter<void> = new EventEmitter();
  private states = [
    {
      name: 'В работе',
      id: 'CREATED'
    },
    {
      name: 'Оформлен',
      id: 'COMPLETED'
    }
  ];
  private datePickerConfig = {
    locale: 'ru',
    firstDayOfWeek: 'mo',
    // showGoToCurrent: true,
    format: 'DD.MM.YYYY',
    // monthFormat: 'MM, YYYY'
  };
  private fromDate: moment.Moment;
  private toDate: moment.Moment;
  private selectedClient: ProfileResource;
  private clients: DocumentCollection<ProfileResource> = new DocumentCollection<ProfileResource>();
  private executors: DocumentCollection<ProfileResource> = new DocumentCollection<ProfileResource>();

  ngOnInit(): void {
    this.profileService.getAllExecutors().subscribe( (response) => {
      this.executors = response;
    } );
    this.profileService.getAllClients().subscribe( (response) => {
      this.clients = response;

      if ( this.filter.client != null ) {
        this.selectedClient = this.clients.data.find( (client) => {
          return client.id === this.filter.client;
        } );
      }
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

  setClient(e) {
    if ( e == null ) {
      this.filter.client = null;
      this.selectedClient = null;
    }
    else
      this.filter.client = e.item.id;
    this.emitChange();
  }

  formatter = (result: ProfileResource) => result.attributes.name;

  search = (text$: Observable<string>) =>
    text$.pipe(
      debounceTime(750),
      distinctUntilChanged(),
      map(term => term === '' ? []
        : this.clients.data.filter(client => client.attributes.name.toLowerCase().indexOf(term.toLowerCase()) > -1).slice(0, 10))
    )

  emitChange() {
    this.onChange.emit();
  }

  organizationChange() {
    this.onOrganizationChange.emit();
    this.emitChange();
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
    this.selectedClient = null;
    this.emitChange();
  }

  resetSort() {
    this.filter.sort = 'startDate';
    this.filter.direction = 'desc';
    this.emitChange();
  }

}
