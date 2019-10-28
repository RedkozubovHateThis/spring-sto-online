import { Component } from '@angular/core';
import {DocumentResponse} from '../../model/firebird/documentResponse';
import {DocumentResponseService} from '../../api/documentResponse.service';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {Pageable} from '../../model/pageable';
import {Pagination} from '../pagination';
import { DocumentsFilter } from 'src/app/model/documentsFilter';
import {EventMessageResponseService} from '../../api/eventMessageResponse.service';
import {EventMessageResponse} from '../../model/postgres/eventMessageResponse';
import {EventMessagesFilter} from '../../model/eventMessagesFilter';

@Component({
  selector: 'app-event-messages',
  templateUrl: './event-messages.component.html',
  styleUrls: ['./event-messages.component.scss']
})
export class EventMessagesComponent extends Pagination {

  private all: Pageable<EventMessageResponse>;
  private isLoading: boolean = false;
  private filter: EventMessagesFilter = new EventMessagesFilter();

  constructor(private eventMessageResponseService: EventMessageResponseService, protected route: ActivatedRoute, private router: Router) {
    super(route);
  }

  requestData() {
    this.isLoading = true;
    this.eventMessageResponseService.getAll(this.page, this.size, this.offset, this.filter).subscribe(data => {
      this.all = data as Pageable<EventMessageResponse>;
      this.setPageData(this.all);

      this.isLoading = false;
    }, error => {
      this.isLoading = false;
    } );
  }

  prepareFilter(queryParams: Params) {
    if ( queryParams.sort ) this.filter.sort = queryParams.sort;
    if ( queryParams.direction ) this.filter.direction = queryParams.direction;

    if ( queryParams.messageType ) this.filter.messageType = queryParams.messageType;
    else this.filter.messageType = null;

    if ( queryParams.fromId ) this.filter.fromId = parseInt(queryParams.fromId, 10);
    else this.filter.fromId = null;

    if ( queryParams.toId ) this.filter.toId = parseInt(queryParams.toId, 10);
    else this.filter.toId = null;

    if ( queryParams.documentId ) this.filter.documentId = parseInt(queryParams.documentId, 10);
    else this.filter.documentId = null;
  }

  refresh() {
    this.router.navigate(['event-messages'], { queryParams: this.filter });
  }

  getMessageType(messageType: string) {
    switch (messageType) {
      case 'MODERATOR_REPLACEMENT': return 'Назначение замещающего';
      case 'DOCUMENT_CHANGE': return 'Изменение документа';
    }
  }

}
