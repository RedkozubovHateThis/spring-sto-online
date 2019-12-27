import {Component, OnInit, ElementRef, Input} from '@angular/core';
import {UserService} from '../../api/user.service';
import {User} from '../../model/postgres/auth/user';
import {Subscription} from 'rxjs';
import {StompSubscription} from '@stomp/stompjs';
import {ChatMessageResponse} from '../../model/postgres/chatMessageResponse';
import {WebSocketService} from '../../api/webSocket.service';
import {InfobarService} from '../../api/infobar.service';
import {ClientInfo} from '../../model/info/clientInfo';
import {ServiceLeaderInfo} from '../../model/info/serviceLeaderInfo';
import {ModeratorInfo} from '../../model/info/moderatorInfo';
import {DocumentResponseController} from '../../controller/document-response.controller';

@Component({
  selector: 'app-infobar',
  templateUrl: './infobar.component.html',
  styleUrls: ['./infobar.component.scss'],
})
export class InfobarComponent implements OnInit {

  constructor(private infobarService: InfobarService, private userService: UserService,
              private webSocketService: WebSocketService, private documentResponseController: DocumentResponseController) {}

  private currentUser: User;
  private subscription: StompSubscription;
  private onConnect: Subscription;

  private clientInfo: ClientInfo;
  private serviceLeaderInfo: ServiceLeaderInfo;
  private moderatorInfo: ModeratorInfo;
  private isLoading: boolean = false;

  private readonly daysRemainsWarn = 1000 * 60 * 60 * 24 * 3;

  ngOnInit(): void {

    if ( this.userService.currentUser == null ) {
      const subscription: Subscription = this.userService.currentUserIsLoaded.subscribe( currentUser => {
        this.ngOnInit();
        subscription.unsubscribe();
      } );

      return;
    }

    this.currentUser = this.userService.currentUser;

    this.getData();

    this.onConnect = this.webSocketService.clientIsConnected.subscribe( client => {
      this.subscribe();
    } );

    this.subscribe();

    if ( this.currentUser.userModerator || this.currentUser.userAdmin )
      this.documentResponseController.organizationChange.subscribe( () => {
        this.getModeratorData();
      } );
  }

  subscribe() {

    if ( !this.webSocketService.client ) { return; }

    const me = this;

    this.subscription = this.webSocketService.client.subscribe('/topic/counters/' + this.userService.currentUser.id, message => {
      me.getData();
    });

  }

  private getData() {
    if ( this.currentUser == null ) return;

    if ( this.currentUser.userClient )
      this.getClientData();
    else if ( this.currentUser.userServiceLeader )
      this.getServiceLeaderData();
    else if ( this.currentUser.userModerator || this.currentUser.userAdmin )
      this.getModeratorData();
  }

  private getClientData() {
    this.isLoading = true;
    this.infobarService.getClientInfo().subscribe( data => {
      this.clientInfo = data as ClientInfo;
      this.isLoading = false;
    }, () => {
      this.isLoading = false;
    } );
  }

  private getServiceLeaderData() {
    this.isLoading = true;
    this.infobarService.getServiceLeaderInfo().subscribe( data => {
      this.serviceLeaderInfo = data as ServiceLeaderInfo;
      this.isLoading = false;
    }, () => {
      this.isLoading = false;
    } );
  }

  private getModeratorData() {
    this.isLoading = true;
    this.infobarService.getModeratorInfo( this.documentResponseController.filter.organization ).subscribe( data => {
      this.moderatorInfo = data as ModeratorInfo;
      this.isLoading = false;
    }, () => {
      this.isLoading = false;
    } );
  }

  private isLessThen3DaysRemains(endDate: any): boolean {
    const now: number = new Date().getTime();
    return endDate != null && endDate > now && now - endDate <= this.daysRemainsWarn;
  }

  private isExpired(endDate: any): boolean {
    const now: number = new Date().getTime();
    return endDate != null && endDate < now;
  }

}
