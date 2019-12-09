import {Component, OnInit, OnDestroy} from '@angular/core';
import {UserService} from '../../api/user.service';
import {Subscription} from 'rxjs';
import {ChatMessageResponse} from '../../model/postgres/chatMessageResponse';
import {ChatMessageResponseService} from '../../api/chatMessageResponse.service';
import {StompSubscription} from '@stomp/stompjs';
import {WebSocketService} from '../../api/webSocket.service';
import $ from 'jquery';
import {OpponentResponse} from '../../model/postgres/opponentResponse';
import {FileItem, FileUploader, ParsedResponseHeaders} from 'ng2-file-upload';
import {UploadFileResponse} from '../../model/postgres/uploadFileResponse';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.scss'],
})
export class ChatComponent implements OnInit, OnDestroy {

  constructor(private userService: UserService, private chatMessageResponseService: ChatMessageResponseService,
              private webSocketService: WebSocketService, private toastrService: ToastrService) {}

  private isOpponentsLoading = false;
  private isMessageSaving = false;
  private isMessagesLoading = false;
  private opponents: OpponentResponse[] = [];
  private messages: ChatMessageResponse[] = [];
  private selectedOpponent: OpponentResponse = null;
  private chatMessage = '';
  private subscription: StompSubscription;
  private onConnect: Subscription;
  private observer: MutationObserver = null;
  private uploader: FileUploader;

  ngOnInit() {

    if ( this.userService.currentUser == null ) {
      const subscription: Subscription = this.userService.currentUserIsLoaded.subscribe( currentUser => {

        subscription.unsubscribe();
        this.ngOnInit();

      } );

      return;
    }

    this.uploader = new FileUploader({
      url: `${this.userService.getApiUrl()}secured/files/upload`,
      autoUpload: true,
      authTokenHeader: 'Authorization',
      authToken: `Bearer ${this.userService.getToken()}`
    });

    this.uploader.onCompleteItem = ( item: FileItem, response: string, status: number, headers: ParsedResponseHeaders ) => {
      const index = this.uploader.queue.indexOf(item);
      if (index !== -1) this.uploader.queue.splice(index, 1);

      if ( status === 200 ) {
        const uploadFileResponse: UploadFileResponse = JSON.parse( response ) as UploadFileResponse;
        this.sendMessage( uploadFileResponse.id );
      }
      else {
        this.toastrService.error('Ошибка отправки сообщения!', 'Внимание!');
      }


    };

    this.findOpponents();

    this.onConnect = this.webSocketService.clientIsConnected.subscribe( client => {
      this.subscribe();
    } );

    this.subscribe();

    this.setObserver();

  }

  ngOnDestroy() {
    if ( this.onConnect ) {
      this.onConnect.unsubscribe();
    }
    if ( this.subscription ) {
      this.subscription.unsubscribe();
    }

    this.removeObserver();
  }

  showUpload() {
    $('#upload').click();
  }

  findOpponents() {

    this.isOpponentsLoading = true;

    this.userService.getOpponents().subscribe( opponents => {
      this.opponents = opponents as OpponentResponse[];

      if ( this.opponents.length > 0 && this.webSocketService.opponentId != null ) {
        const filtered = this.opponents.filter( opponent => {
          return opponent.id === this.webSocketService.opponentId;
        } );
        if ( filtered.length > 0 ) {
          this.selectedOpponent = filtered[0];
          this.findMessages();
        }
        this.webSocketService.opponentId = null;
      }

      this.isOpponentsLoading = false;
    }, error => {
      this.isOpponentsLoading = false;

      if ( error.status === 404 ) {
        this.toastrService.info('Собеседники не найдены...');
      }
    } );

  }

  selectOpponent(opponent) {
    this.selectedOpponent = opponent;
    this.findMessages();
  }

  subscribe() {

    if ( !this.webSocketService.client ) { return; }

    const me = this;

    this.subscription = this.webSocketService.client.subscribe('/topic/message/' + this.userService.currentUser.id, message => {
      const chatMessage = JSON.parse( message.body ) as ChatMessageResponse;

      me.updateLastMessage(chatMessage);

      if ( me.selectedOpponent != null && me.selectedOpponent.id === chatMessage.fromId ) {
        me.messages.push(chatMessage);
      }
    });

  }

  setObserver() {
    const me = this;
    const target = document.getElementById('scrollable');

    this.observer = new MutationObserver(mutations => {
      me.scrollDown();
    });

    const config = { attributes: true, childList: true, characterData: true };

    this.observer.observe(target, config);
  }

  scrollDown() {
    const scrollable = document.getElementById('scrollable');
    if ( scrollable ) { scrollable.scrollTop = scrollable.scrollHeight; }
  }

  removeObserver() {
    if ( this.observer ) {
      this.observer.disconnect();
    }
  }

  sendMessage(uploadFileId: number) {

    if ( this.selectedOpponent == null
      || ( uploadFileId == null && ( this.chatMessage == null || this.chatMessage.length === 0 ) ) ) return;

    this.isMessageSaving = true;

    this.chatMessageResponseService.saveMessage( this.selectedOpponent, this.chatMessage, uploadFileId ).subscribe( savedMessage => {
      this.chatMessage = '';
      this.messages.push(savedMessage as ChatMessageResponse);
      this.updateLastMessage(savedMessage as ChatMessageResponse);
      this.isMessageSaving = false;
    }, () => {
      this.isMessageSaving = false;
    } );

  }

  updateLastMessage(chatMessage: ChatMessageResponse) {
    const filtered = this.opponents.find( opponent => {
      return opponent.id === chatMessage.toId || opponent.id === chatMessage.fromId;
    } );

    if ( filtered ) {
      filtered.lastMessageText = chatMessage.messageText;
      filtered.lastMessageFromId = chatMessage.fromId;
      filtered.lastMessageType = chatMessage.chatMessageType;
      filtered.uploadFileName = chatMessage.uploadFileName;

      filtered.lastMessageDocumentNumber = chatMessage.documentNumber;
      filtered.lastMessageServiceWorkName = chatMessage.serviceWorkName;
      filtered.lastMessageServiceGoodsAddonName = chatMessage.serviceGoodsAddonName;
      filtered.lastMessageClientGoodsOutName = chatMessage.clientGoodsOutName;
    }
    else
      this.findOpponents();
  }

  findMessages() {

    if ( this.selectedOpponent == null ) {
      this.messages = [];
      return;
    }

    this.isMessagesLoading = true;

    this.chatMessageResponseService.getMessages( this.selectedOpponent.id ).subscribe( messages => {
      this.messages = messages as ChatMessageResponse[];
      this.isMessagesLoading = false;
    }, error => {
      this.messages = [];
      this.isMessagesLoading = false;

      if ( error.status === 404 ) {
        this.toastrService.info('Сообщения не найдены...');
      }
    } );

  }

  isShared(message: ChatMessageResponse): boolean {
    return message.chatMessageType === 'DOCUMENT' || message.chatMessageType === 'SERVICE_WORK' || message.chatMessageType === 'SERVICE_GOODS_ADDON'
      || message.chatMessageType === 'CLIENT_GOODS_OUT';
  }

}
