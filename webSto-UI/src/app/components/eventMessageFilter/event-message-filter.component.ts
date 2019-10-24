import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {UserService} from '../../api/user.service';
import {OrganizationResponseService} from '../../api/organizationResponse.service';
import {OrganizationResponse} from '../../model/firebird/organizationResponse';
import {VehicleResponse} from '../../model/firebird/vehicleResponse';
import {DocumentsFilter} from '../../model/documentsFilter';
import {Router} from '@angular/router';
import {User} from '../../model/postgres/auth/user';
import {EventMessagesFilter} from '../../model/eventMessagesFilter';
import {DocumentResponse} from '../../model/firebird/documentResponse';
import {DocumentResponseService} from '../../api/documentResponse.service';

@Component({
  selector: 'app-event-message-filter',
  templateUrl: './event-message-filter.component.html',
  styleUrls: ['./event-message-filter.component.scss'],
})
export class EventMessageFilterComponent implements OnInit {

  constructor(private userService: UserService, private documentResponseService: DocumentResponseService) {}

  @Input()
  private filter: EventMessagesFilter;
  @Output()
  private onChange: EventEmitter<any> = new EventEmitter();
  private messageTypes = [
    {
      name: 'Назначение замещающего',
      id: 'MODERATOR_REPLACEMENT'
    },
    {
      name: 'Изменение документа',
      id: 'DOCUMENT_CHANGE'
    }
  ];
  private usersFrom: User[] = [];
  private usersTo: User[] = [];
  private documents: DocumentResponse[] = [];

  ngOnInit(): void {
    this.userService.getEventMessageFromUsers().subscribe( response => {
      this.usersFrom = response as User[];
    } );
    this.userService.getEventMessageToUsers().subscribe( response => {
      this.usersTo = response as User[];
    } );
    this.documentResponseService.getDocumentsByEventMessage().subscribe( response => {
      this.documents = response as DocumentResponse[];
    } );
  }

  emitChange() {
    this.onChange.emit();
  }

  resetFilters() {
    this.filter.messageType = null;
    this.filter.fromId = null;
    this.filter.toId = null;
    this.filter.documentId = null;
    this.emitChange();
  }

  resetSort() {
    this.filter.sort = 'messageDate';
    this.filter.direction = 'asc';
    this.emitChange();
  }

}
