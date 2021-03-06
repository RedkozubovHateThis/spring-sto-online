import {Injectable} from '@angular/core';
import {PaginationController} from './pagination.controller';
import {EventMessagesFilter} from '../model/eventMessagesFilter';
import {DocumentCollection} from 'ngx-jsonapi';
import {EventMessageResource} from '../model/resource/event-message.resource.service';

@Injectable()
export class EventMessageController extends PaginationController {

  filter: EventMessagesFilter = new EventMessagesFilter();
  all: DocumentCollection<EventMessageResource>;

  constructor() {
    super();
  }

}
