import {AfterViewInit, Component, OnInit} from '@angular/core';
import {UserService} from '../../api/user.service';
import {Subscription} from 'rxjs';
import {StompSubscription} from '@stomp/stompjs';
import {WebSocketService} from '../../api/webSocket.service';
import {InfobarService} from '../../api/infobar.service';
import {ClientInfo} from '../../model/info/clientInfo';
import {ServiceLeaderInfo} from '../../model/info/serviceLeaderInfo';
import {DocumentController} from '../../controller/document.controller';
import {UserResource} from '../../model/resource/user.resource.service';
import {UserRoleResource} from '../../model/resource/user-role.resource.service';

@Component({
  selector: 'app-infobar',
  templateUrl: './infobar.component.html',
  styleUrls: ['./infobar.component.scss'],
})
export class InfobarComponent implements OnInit, AfterViewInit {

  constructor(private infobarService: InfobarService, private userService: UserService,
              private webSocketService: WebSocketService, private documentController: DocumentController) {}

  private subscription: StompSubscription;
  private onConnect: Subscription;

  private clientInfo: ClientInfo;
  private serviceLeaderInfo: ServiceLeaderInfo;
  private isLoading = false;

  private readonly daysRemainsWarn = 1000 * 60 * 60 * 24 * 3;

  ngAfterViewInit() {
    setTimeout( () => {
      // tslint:disable-next-line:only-arrow-functions
      (function(w, d, n, s, t) {
        w[n] = w[n] || [];
        // tslint:disable-next-line:only-arrow-functions
        w[n].push(function() {
          // @ts-ignore
          Ya.Context.AdvManager.render({
            blockId: "R-A-624145-1",
            renderTo: "yandex_rtb_R-A-624145-1",
            async: true
          });
        });
        t = d.getElementsByTagName("script")[0];
        s = d.createElement("script");
        s.type = "text/javascript";
        s.src = "//an.yandex.ru/system/context.js";
        s.async = true;
        t.parentNode.insertBefore(s, t);
      })(window, window.document, "yandexContextAsyncCallbacks");
    }, 2000 );
  }

  ngOnInit(): void {
    if ( this.userService.currentUser == null ) {
      const subscription: Subscription = this.userService.currentUserIsLoaded.subscribe( currentUser => {
        this.ngOnInit();
        subscription.unsubscribe();
      } );

      return;
    }

    this.getData();

    this.onConnect = this.webSocketService.clientIsConnected.subscribe( client => {
      this.subscribe();
    } );

    this.subscribe();

    if ( this.userService.currentUser.isAdmin() )
      this.documentController.organizationChange.subscribe( () => {
        this.getAdminData();
      } );
  }

  subscribe() {

    if ( !this.webSocketService.client ) return;

    const me = this;

    this.subscription = this.webSocketService.client.subscribe('/topic/counters/' + this.userService.currentUser.id, message => {
      me.getData();
    });

  }

  private getData() {
    if ( this.userService.currentUser == null ) return;

    if ( this.userService.currentUser.isClient() )
      this.getClientData();
    else if ( this.userService.currentUser.isServiceLeaderOrFreelancer() )
      this.getServiceLeaderData();
    else if ( this.userService.currentUser.isAdmin() )
      this.getAdminData();
  }

  private getClientData() {
    this.isLoading = true;
    this.infobarService.getClientInfo().subscribe( (data) => {
      this.clientInfo = data;
      this.isLoading = false;
    }, () => {
      this.isLoading = false;
    } );
  }

  private getServiceLeaderData() {
    this.isLoading = true;
    this.infobarService.getServiceLeaderInfo().subscribe( (data) => {
      this.serviceLeaderInfo = data as ServiceLeaderInfo;
      this.isLoading = false;
    }, () => {
      this.isLoading = false;
    } );
  }

  private getAdminData() {
    this.isLoading = true;
    this.infobarService.getAdminInfo( this.documentController.filter.organization ).subscribe( (data) => {
      this.serviceLeaderInfo = data;
      this.isLoading = false;
    }, () => {
      this.isLoading = false;
    } );
  }

  private isLessThen3DocumentsRemains(documentsRemains: any): boolean {
    return documentsRemains < 3;
  }

  private isDocumentsExpired(documentsRemains: any): boolean {
    return documentsRemains === 0;
  }

  private isLessThen3DaysRemains(endDate: any): boolean {
    const now: number = new Date().getTime();
    return endDate != null && endDate > now && endDate - now <= this.daysRemainsWarn;
  }

  private isExpired(endDate: any): boolean {
    const now: number = new Date().getTime();
    return endDate != null && endDate < now;
  }

}
