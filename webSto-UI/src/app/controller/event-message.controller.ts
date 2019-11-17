import {Injectable} from '@angular/core';
import {PaginationController} from './pagination.controller';
import {Pageable} from '../model/pageable';
import {EventMessageResponse} from '../model/postgres/eventMessageResponse';
import {EventMessagesFilter} from '../model/eventMessagesFilter';

@Injectable()
export class EventMessageController extends PaginationController {

  filter: EventMessagesFilter = new EventMessagesFilter();
  all: Pageable<EventMessageResponse>;

  constructor() {
    super();
  }

}
