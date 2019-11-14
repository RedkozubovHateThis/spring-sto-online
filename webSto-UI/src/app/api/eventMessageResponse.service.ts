import {Injectable, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import { UserService } from './user.service';
import {User} from '../model/postgres/auth/user';
import {OpponentResponse} from '../model/postgres/opponentResponse';
import {EventMessageResponse} from '../model/postgres/eventMessageResponse';
import {Pageable} from '../model/pageable';
import {DocumentsFilter} from '../model/documentsFilter';
import {EventMessagesFilter} from '../model/eventMessagesFilter';

@Injectable()
export class EventMessageResponseService {

  constructor(private http: HttpClient, private router: Router, private userService: UserService) { }

  public isLoading: boolean = false;
  public messages: EventMessageResponse[] = [];

  getLast5() {

    this.isLoading = true;
    this.getMessages('messageDate,desc', 0, 5, -5, null, null, null, null)
      .subscribe( response => {

      this.isLoading = false;
      const pageable = response as Pageable<EventMessageResponse>;
      this.messages = pageable.content;

    }, error => {
      this.isLoading = false;
    } );

  }

  getAll(filter: EventMessagesFilter) {
    return this.getMessages(`${filter.sort},${filter.direction}`, filter.page, filter.size, filter.offset, filter.messageType, filter.fromId,
      filter.toId, filter.documentId);
  }

  private getMessages(sort: string, page: number, size: number, offset: number, messageType: string, fromId: number, toId: number,
                      documentId: number) {
    const headers = this.userService.getHeaders();
    const params = {
      sort,
      page: page.toString(),
      size: size.toString(),
      offset: offset.toString(),
      messageTypes: messageType != null ? messageType : '',
      fromIds: fromId != null ? fromId.toString() : '',
      toIds: toId != null ? toId.toString() : '',
      documentIds: documentId != null ? documentId.toString() : ''
    };

    return this.http.get( `${this.userService.getApiUrl()}secured/eventMessages/findAll`, {headers, params} );
  }

  addMessage(eventMessage: EventMessageResponse) {
    if ( this.messages != null ) {
      this.messages.unshift(eventMessage);
    }
  }

}
