import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {UserService} from '../../api/user.service';
import {AdEntityService} from '../../api/ad-entity.service';
import {AdEntityResource, AdEntityResourceService} from '../../model/resource/ad-entity.resource.service';
import {WebSocketService} from '../../api/webSocket.service';
import {StompSubscription} from '@stomp/stompjs';
import {Subscription} from 'rxjs';
import {IDocumentResource} from 'ngx-jsonapi/interfaces/data-object';

@Component({
  selector: 'tr[app-ad-row]',
  templateUrl: './ad-row.component.html',
  styleUrls: ['./ad-row.component.scss'],
})
export class AdRowComponent implements OnInit, OnDestroy {
  constructor(private userService: UserService, private adEntityService: AdEntityService,
              private adEntityResourceService: AdEntityResourceService,
              private webSocketService: WebSocketService) {}

  private currentAd: AdEntityResource = null;

  private subscription: StompSubscription;
  private onConnect: Subscription;

  ngOnInit(): void {
    this.adEntityService.getCurrent().subscribe( (current) => {
      if ( current && !current.is_new )
        this.currentAd = current;
    } );

    this.onConnect = this.webSocketService.clientIsConnected.subscribe( client => {
      this.subscribe();
    } );

    this.subscribe();
  }

  subscribe() {

    if ( !this.webSocketService.client ) {
      const subscription: Subscription = this.webSocketService.clientIsConnected.subscribe( client => {
        this.subscribe();
        subscription.unsubscribe();
      } );

      return;
    }

    this.subscription = this.webSocketService.client.subscribe('/topic/ad', message => {
      const data: IDocumentResource = JSON.parse( message.body );
      const adEntity: AdEntityResource = this.adEntityResourceService.new();
      adEntity.fill(data);

      this.currentAd = adEntity;
    });

    this.subscription = this.webSocketService.client.subscribe('/topic/ad/clear', message => {
      this.currentAd = null;
    });

  }

  unsubscribe() {
    if ( this.subscription )
      this.subscription.unsubscribe();
  }

  ngOnDestroy(): void {
    this.unsubscribe();
  }

  formatPhone(phone): string {
    if ( !phone ) return '';

    const match: RegExpMatchArray = phone.match(/^8(\d{3})(\d{3})(\d{2})(\d{2})$/);

    if (match)
      return '+7 (' + match[1] + ') ' + match[2] + '-' + match[3] + '-' + match[4];

    return phone;
  }
}
