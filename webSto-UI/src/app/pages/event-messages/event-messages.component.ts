import {Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Pagination} from '../pagination';
import {EventMessageService} from '../../api/event-message.service';
import {EventMessageController} from '../../controller/event-message.controller';

@Component({
  selector: 'app-event-messages',
  templateUrl: './event-messages.component.html',
  styleUrls: ['./event-messages.component.scss']
})
export class EventMessagesComponent extends Pagination {

  private isLoading: boolean = false;
  protected routeName = '/event-messages';

  constructor(private eventMessageResponseService: EventMessageService, protected route: ActivatedRoute, protected router: Router,
              protected eventMessageController: EventMessageController) {
    super(route, router, eventMessageController);
  }

  requestData() {
    this.isLoading = true;
    this.eventMessageResponseService.getAll(this.eventMessageController.filter).subscribe(data => {
      this.eventMessageController.all = data;

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
