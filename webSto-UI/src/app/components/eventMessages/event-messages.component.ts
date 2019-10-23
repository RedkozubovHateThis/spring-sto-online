import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {UserService} from '../../api/user.service';
import {HttpClient} from '@angular/common/http';
import {DomSanitizer} from '@angular/platform-browser';
import {ChatMessageResponse} from '../../model/postgres/chatMessageResponse';
import {ToastrService} from 'ngx-toastr';
import {EventMessageResponse} from '../../model/postgres/eventMessageResponse';
import {EventMessageResponseService} from '../../api/eventMessageResponse.service';
import {WebSocketService} from '../../api/webSocket.service';
import {StompSubscription} from '@stomp/stompjs';
import {Subscription} from 'rxjs';
import {DatePipe} from '@angular/common';

@Component({
  selector: 'app-event-messages',
  templateUrl: './event-messages.component.html',
  styleUrls: ['./event-messages.component.scss'],
})
export class EventMessagesComponent implements OnInit {

  constructor(private userService: UserService, private toastrService: ToastrService, private datePipe: DatePipe,
              private eventMessageResponseService: EventMessageResponseService, private webSocketService: WebSocketService) {}

  private isLoading: boolean = false;
  private messages: EventMessageResponse[] = [];
  private subscription: StompSubscription;
  private onConnect: Subscription;

  ngOnInit(): void {
    this.getMessages();

    this.onConnect = this.webSocketService.clientIsConnected.subscribe( client => {
      this.subscribe();
    } );

    this.subscribe();
  }

  private getMessages() {
    this.isLoading = true;
    this.eventMessageResponseService.getMessages().subscribe( response => {

        this.isLoading = false;
        this.messages = response as EventMessageResponse[];

    }, error => {
      this.isLoading = false;
    } );
  }

  private subscribe() {
    if ( !this.webSocketService.client ) return;

    const me = this;

    this.subscription = this.webSocketService.client.subscribe('/topic/event/' + this.userService.currentUser.id, message => {
      const eventMessage: EventMessageResponse = JSON.parse( message.body );
      this.messages.unshift( eventMessage );
      me.buildMessage( eventMessage );
    });
  }

  private buildMessage(eventMessage: EventMessageResponse) {
    let messageText = '';

    if ( eventMessage.messageType === 'DOCUMENT_CHANGE' )
      messageText = `Пользователь ${eventMessage.fromFio} изменил документ ${eventMessage.documentName}: ${eventMessage.additionalInformation}`;
    else if ( eventMessage.messageType === 'MODERATOR_REPLACEMENT' )
      messageText = `Модератор ${eventMessage.fromFio} назначил вас своим замещающим`;
    else
      return;

    this.toastrService.info(messageText, this.datePipe.transform(eventMessage.messageDate, 'dd.MM.yyyy HH:mm:ss'));
  }

}
