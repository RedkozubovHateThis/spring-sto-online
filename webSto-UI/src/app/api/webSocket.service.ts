import {Injectable} from '@angular/core';
import {UserService} from './user.service';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import {Subject, Subscription} from 'rxjs';
import {ToastrService} from 'ngx-toastr';
import {ChatMessageResponse} from '../model/postgres/chatMessageResponse';

@Injectable()
export class WebSocketService {

  constructor(private userService: UserService, private toastrService: ToastrService) { }
  private baseUrl = 'http://localhost:8181/ws';
  public clientIsConnected: Subject<Client> = new Subject<Client>();
  public client: Client;

  connect() {
    const me = this;

    const token = this.userService.getToken();
    if ( token == null ) return;

    const client = new Client({
      debug(str) {
        console.log(str);
      },
      webSocketFactory() {
        return new SockJS('http://localhost:8181/secured/ws/?access_token=' + JSON.parse(localStorage.getItem('token')).access_token);
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
      console.log('Broker reported error: ' + frame.headers.message);
      console.log('Additional details: ' + frame.body);
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

    this.client.subscribe('/topic/message/' + this.userService.currentUser.id, message => {
      const chatMessage = JSON.parse( message.body ) as ChatMessageResponse;

      if ( chatMessage.uploadFileUuid != null )
        me.toastrService.success( chatMessage.uploadFileName, `${chatMessage.fromFio} отправил(а) файл:` );
      else
        me.toastrService.success( chatMessage.messageText, `${chatMessage.fromFio} написал(а):` );
    });

  }

}
