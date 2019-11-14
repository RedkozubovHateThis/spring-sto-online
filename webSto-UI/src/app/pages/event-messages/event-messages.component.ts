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
  protected filter: EventMessagesFilter = new EventMessagesFilter();
  protected routeName = '/event-messages';

  constructor(private eventMessageResponseService: EventMessageResponseService, protected route: ActivatedRoute, protected router: Router) {
    super(route, router);
  }

  requestData() {
    this.isLoading = true;
    this.eventMessageResponseService.getAll(this.filter).subscribe(data => {
      this.all = data as Pageable<EventMessageResponse>;

      this.isLoading = false;
    }, error => {
      this.isLoading = false;
    } );
  }

  getMessageType(messageType: string) {
    switch (messageType) {
      case 'MODERATOR_REPLACEMENT': return 'Назначение замещающего';
      case 'DOCUMENT_CHANGE': return 'Изменение документа';
      case 'USER_REGISTER': return 'Регистрация пользователя';
      case 'USER_AUTODEALER': return 'Привязка к системе АвтоДилер';
      case 'USER_APPROVE': return 'Подтверждение привязки к системе АвтоДилер';
      case 'USER_REJECT': return 'Отмена привязки к системе АвтоДилер';
    }
  }

}
