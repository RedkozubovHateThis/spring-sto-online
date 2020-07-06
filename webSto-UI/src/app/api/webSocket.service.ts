import {Injectable} from '@angular/core';
import {UserService} from './user.service';
import {Client} from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import {Subject, Subscription} from 'rxjs';
import {ToastrService} from 'ngx-toastr';
import {EventMessageService} from './event-message.service';
import {EventMessageResponse} from '../model/postgres/eventMessageResponse';
import {DatePipe} from '@angular/common';
import {Router} from '@angular/router';
import {environment} from '../../environments/environment';

@Injectable()
export class WebSocketService {

  constructor(private userService: UserService, private toastrService: ToastrService, private router: Router,
              private eventMessageResponseService: EventMessageService, private datePipe: DatePipe) { }

  public clientIsConnected: Subject<Client> = new Subject<Client>();
  public client: Client;
  public opponentId: number;

  connect() {
    const me = this;

    const token = this.userService.getToken();
    if ( token == null ) return;

    const client = new Client({
      // debug(str) {
      //       //   console.log(str);
      //       // },
      webSocketFactory() {
        return new SockJS(
          `${environment.getWsUrl()}?access_token=${JSON.parse(localStorage.getItem('token')).access_token}`
        );
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000
    });

    client.onConnect = function(frame) {
      me.client = client;
      me.clientIsConnected.next(client);
      me.subscribeOnToastMessages();
    };

    client.onStompError = function(frame) {
      console.error('Broker reported error: ' + frame.headers.message);
      console.error('Additional details: ' + frame.body);
    };

    client.activate();
  }

  private subscribeOnToastMessages() {

    if ( this.userService.currentUser == null ) {
      const subscription: Subscription = this.userService.currentUserIsLoaded.subscribe( currentUser => {

        this.subscribeOnToastMessages();
        subscription.unsubscribe();

      } );

      return;
    }

    const me = this;

    this.eventMessageResponseService.getLast5();
    this.client.subscribe('/topic/event/' + this.userService.currentUser.id, message => {
      const eventMessage: EventMessageResponse = JSON.parse( message.body );
      this.eventMessageResponseService.addMessage();
      me.buildMessage( eventMessage );
    });

  }

  private buildMessage(eventMessage: EventMessageResponse) {
    let messageText = '';

    if ( eventMessage.messageType === 'DOCUMENT_CHANGE' )
      messageText = `Пользователь ${eventMessage.fromFio} изменил документ ${eventMessage.documentName}: ${eventMessage.additionalInformation}`;
    else if ( eventMessage.messageType === 'MODERATOR_REPLACEMENT' )
      messageText = `Модератор ${eventMessage.fromFio} назначил вас своим замещающим`;
    else if ( eventMessage.messageType === 'USER_REGISTER' )
      messageText = `Пользователь ${eventMessage.fromFio} зарегистрировался на сайте`;
    else if ( eventMessage.messageType === 'USER_AUTODEALER' )
      messageText = `Пользователь ${eventMessage.fromFio} совершил привязку к системе АвтоДилер`;
    else if ( eventMessage.messageType === 'USER_APPROVE' )
      messageText = `Модератор ${eventMessage.fromFio} подтвердил привязку к системе АвтоДилер`;
    else if ( eventMessage.messageType === 'USER_REJECT' )
      messageText = `Модератор ${eventMessage.fromFio} отменил привязку к системе АвтоДилер`;
    else
      return;

    const bubble = this.toastrService.info(messageText, this.datePipe.transform(eventMessage.messageDate, 'dd.MM.yyyy HH:mm:ss'));
    bubble.onTap.subscribe(() => {
      this.router.navigate(['/event-messages']);
    });
  }

}
