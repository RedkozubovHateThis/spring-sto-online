import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {UserService} from './user.service';
import {EventMessagesFilter} from '../model/eventMessagesFilter';
import {EventMessageResource, EventMessageResourceService} from '../model/resource/event-message.resource.service';
import {UserResourceService} from '../model/resource/user.resource.service';

@Injectable()
export class EventMessageService {

  constructor(private http: HttpClient, private router: Router, private userService: UserService,
              private eventMessageResourceService: EventMessageResourceService, private userResourceService: UserResourceService) {
    eventMessageResourceService.register();
  }

  public isLoading: boolean = false;
  public messages: Array<EventMessageResource> = [];

  getLast5() {

    this.isLoading = true;
    this.getMessages('messageDate', 'desc', 0, 5, -5, null, null, null, null)
      .subscribe( response => {

      this.isLoading = false;
      this.messages = response.data;

    }, error => {
      this.isLoading = false;
    } );

  }

  getAll(filter: EventMessagesFilter) {
    return this.getMessages(filter.sort, filter.direction, filter.page, filter.size, filter.offset, filter.messageType, filter.fromId,
      filter.toId, filter.documentId);
  }

  private getMessages(sort: string, direction: string, page: number, size: number, offset: number, messageType: string, fromId: number, toId: number,
                      documentId: number) {
    const headers = this.userService.getHeaders();
    const params = {
      messageTypes: messageType != null ? messageType : '',
      fromIds: fromId != null ? fromId.toString() : '',
      toIds: toId != null ? toId.toString() : '',
      documentIds: documentId != null ? documentId.toString() : ''
    };

    return this.eventMessageResourceService.all({
      beforepath: 'external',
      sort: [`${direction === 'desc' ? '-' : ''}${sort}`],
      page: {number: page, size},
      remotefilter: params
    });
  }

  addMessage() {
    this.getLast5();
  }

}
