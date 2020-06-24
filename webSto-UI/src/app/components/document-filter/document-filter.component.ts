// import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
// import {UserService} from '../../api/user.service';
// import {OrganizationResponseService} from '../../api/organizationResponse.service';
// import {OrganizationResponse} from '../../model/firebird/organizationResponse';
// import {DocumentsFilter} from '../../model/documentsFilter';
// import {DocumentResponse} from '../../model/firebird/documentResponse';
// import {ClientResponse} from '../../model/firebird/clientResponse';
// import * as moment from 'moment';
// import {Observable} from 'rxjs';
// import {debounceTime, distinctUntilChanged, map} from 'rxjs/operators';
//
// @Component({
//   selector: 'app-document-filter',
//   templateUrl: './document-filter.component.html',
//   styleUrls: ['./document-filter.component.scss'],
// })
// export class DocumentFilterComponent implements OnInit {
//
//   constructor(private organizationResponseService: OrganizationResponseService, private userService: UserService) {}
//
//   @Input()
//   private filter: DocumentsFilter;
//   @Input()
//   private document: DocumentResponse;
//   @Input()
//   private disabled: boolean;
//   @Output()
//   private onChange: EventEmitter<any> = new EventEmitter();
//   @Output()
//   private onOrganizationChange: EventEmitter<void> = new EventEmitter();
//   private states = [
//     {
//       name: 'Черновик',
//       id: 2
//     },
//     {
//       name: 'Оформлен',
//       id: 4
//     }
//   ];
//   private datePickerConfig = {
//     locale: 'ru',
//     firstDayOfWeek: 'mo',
//     // showGoToCurrent: true,
//     format: 'DD.MM.YYYY',
//     // monthFormat: 'MM, YYYY'
//   };
//   private fromDate: moment.Moment;
//   private toDate: moment.Moment;
//   private selectedClient: ClientResponse;
//   private organizations: OrganizationResponse[] = [];
//   private clients: ClientResponse[] = [];
//
//   ngOnInit(): void {
//     this.organizationResponseService.getAll().subscribe( response => {
//       this.organizations = response as OrganizationResponse[];
//     } );
//     this.organizationResponseService.getAllClients().subscribe( response => {
//       this.clients = response as ClientResponse[];
//
//       if ( this.filter.client != null ) {
//         this.selectedClient = this.clients.find( client => {
//           return client.id === this.filter.client;
//         } );
//       }
//     } );
//     if ( this.filter.fromDate )
//       this.fromDate = moment(this.filter.fromDate, 'DD.MM.YYYY');
//     if ( this.filter.toDate )
//       this.toDate = moment(this.filter.toDate, 'DD.MM.YYYY');
//   }
//
//   setFromDate(e) {
//     this.filter.fromDate = e;
//     this.emitChange();
//   }
//
//   setToDate(e) {
//     this.filter.toDate = e;
//     this.emitChange();
//   }
//
//   setClient(e) {
//     if ( e == null ) {
//       this.filter.client = null;
//       this.selectedClient = null;
//     }
//     else
//       this.filter.client = e.item.id;
//     this.emitChange();
//   }
//
//   formatter = (result: ClientResponse) => result.shortName;
//
//   search = (text$: Observable<string>) =>
//     text$.pipe(
//       debounceTime(750),
//       distinctUntilChanged(),
//       map(term => term === '' ? []
//         : this.clients.filter(client => client.shortName.toLowerCase().indexOf(term.toLowerCase()) > -1).slice(0, 10))
//     )
//
//   emitChange() {
//     this.onChange.emit();
//   }
//
//   organizationChange() {
//     this.onOrganizationChange.emit();
//     this.emitChange();
//   }
//
//   resetFilters() {
//     this.filter.state = null;
//     this.filter.organization = null;
//     this.filter.vehicle = null;
//     this.filter.client = null;
//     this.filter.vinNumber = null;
//     this.filter.fromDate = null;
//     this.filter.toDate = null;
//     this.fromDate = null;
//     this.toDate = null;
//     this.selectedClient = null;
//     this.emitChange();
//   }
//
//   resetSort() {
//     this.filter.sort = 'dateStart';
//     this.filter.direction = 'desc';
//     this.emitChange();
//   }
//
// }
