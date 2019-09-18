import {Component, OnInit, ElementRef, Input} from '@angular/core';
import {DocumentResponseService} from "../../api/documentResponse.service";
import {UserService} from '../../api/user.service';
import {User} from '../../model/postgres/auth/user';
import {Subscription} from 'rxjs';
import {StompSubscription} from '@stomp/stompjs';
import {ChatMessageResponse} from '../../model/postgres/chatMessageResponse';
import {WebSocketService} from '../../api/webSocket.service';

@Component({
  selector: 'app-infobar',
  templateUrl: './infobar.component.html',
  styleUrls: ['./infobar.component.scss'],
})
export class InfobarComponent implements OnInit {

  constructor(private documentResponseService: DocumentResponseService, private userService: UserService,
              private webSocketService: WebSocketService) {}

  private documentsCount: number = null;
  private documentsCountLoading: boolean = false;
  private documents2Count: number = null;
  private documents2CountLoading: boolean = false;
  private documents4Count: number = null;
  private documents4CountLoading: boolean = false;
  private usersCount: number = null;
  private usersCountLoading: boolean = false;
  private usersNotApprovedCount: number = null;
  private usersNotApprovedCountLoading: boolean = false;
  private currentUser: User;
  private subscription: StompSubscription;
  private onConnect: Subscription;

  ngOnInit(): void {

    if ( this.userService.currentUser == null ) {
      const subscription: Subscription = this.userService.currentUserIsLoaded.subscribe( currentUser => {
        this.ngOnInit();
        subscription.unsubscribe();
      } );

      return;
    }

    this.currentUser = this.userService.currentUser;

    this.getDocumentsCount();
    this.getUsersCount();
    this.getUsersNotApprovedCount();
    this.getDocumentsCountByState();

    this.onConnect = this.webSocketService.clientIsConnected.subscribe( client => {
      this.subscribe();
    } );

    this.subscribe();
  }

  subscribe() {

    if ( !this.webSocketService.client ) { return; }

    const me = this;

    this.subscription = this.webSocketService.client.subscribe('/topic/counters/' + this.userService.currentUser.id, message => {
      me.getDocumentsCount();
      me.getUsersCount();
      me.getUsersNotApprovedCount();
      me.getDocumentsCountByState();
    });

  }

  getDocumentsCount() {
    this.documentsCountLoading = true;

    this.documentResponseService.getDocumentsCount().subscribe( result => {
      this.documentsCount = result as number;
      this.documentsCountLoading = false;
    }, () => {
      this.documentsCountLoading = false;
    } );
  }

  getDocumentsCountByState() {
    this.documents2CountLoading = true;
    this.documents4CountLoading = true;

    this.documentResponseService.getDocumentsCountByState(2).subscribe( result => {
      this.documents2Count = result as number;
      this.documents2CountLoading = false;
    }, () => {
      this.documents2CountLoading = false;
    } );

    this.documentResponseService.getDocumentsCountByState(4).subscribe( result => {
      this.documents4Count = result as number;
      this.documents4CountLoading = false;
    }, () => {
      this.documents4CountLoading = false;
    } );
  }

  getUsersCount() {
    this.usersCountLoading = true;

    this.userService.getUsersCount(false).subscribe( result => {
      this.usersCount = result as number;
      this.usersCountLoading = false;
    }, () => {
      this.usersCountLoading = false;
    } );
  }

  getUsersNotApprovedCount() {
    this.usersNotApprovedCountLoading = true;

    this.userService.getUsersCount(true).subscribe( result => {
      this.usersNotApprovedCount = result as number;
      this.usersNotApprovedCountLoading = false;
    }, () => {
      this.usersNotApprovedCountLoading = false;
    } );
  }

}
