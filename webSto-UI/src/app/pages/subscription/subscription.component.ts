import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {PaymentService} from '../../api/payment.service';
import {ToastrService} from 'ngx-toastr';
import {PaymentResponse} from '../../model/payment/paymentResponse';
import * as moment from 'moment';
import {SubscriptionTypeResponse} from '../../model/payment/subscriptionTypeResponse';
import {UserService} from '../../api/user.service';
import {SubscriptionResponse} from '../../model/payment/subscriptionResponse';

@Component({
  selector: 'app-subscription',
  templateUrl: './subscription.component.html',
  styleUrls: ['./subscription.component.scss']
})
export class SubscriptionComponent implements OnInit {

  private isTypesLoading: boolean = false;
  private isLoading: boolean = false;
  private subscriptionTypes: SubscriptionTypeResponse[] = [];
  private subscriptions: SubscriptionResponse[] = [];
  private selectedSubscription: SubscriptionResponse;
  private documentsCount: number = null;
  private cost: number;
  private showAddon: boolean = false;

  constructor(private userService: UserService, private paymentService: PaymentService, private toastrService: ToastrService) { }

  ngOnInit() {
    this.requestAllSubscriptionTypes();
    this.requestAllSubscriptions();
  }

  requestAllSubscriptionTypes() {
    this.isTypesLoading = true;

    this.paymentService.getAllSubscriptionTypes().subscribe(subscriptionTypes => {
      this.subscriptionTypes = subscriptionTypes;
      this.isTypesLoading = false;
    }, () => {
      this.isTypesLoading = false;
    } );
  }

  requestAllSubscriptions() {
    this.isLoading = true;

    this.paymentService.getAllSubscriptions().subscribe(subscriptions => {
      this.subscriptions = subscriptions;

      if ( this.subscriptions.length > 0 ) {
        if ( this.selectedSubscription != null )
          this.selectedSubscription = this.subscriptions.find( subscription => subscription.id === this.selectedSubscription.id );
        else
          this.selectedSubscription = this.subscriptions[ this.subscriptions.length - 1 ];
      }

      this.isLoading = false;
    }, () => {
      this.isLoading = false;
    } );
  }

  buySubscription(subscriptionType: string) {
    this.paymentService.isSubscriptionLoading = true;

    this.paymentService.buySubscription( subscriptionType ).subscribe( subscription => {
      this.paymentService.isSubscriptionLoading = false;

      this.toastrService.success(`Тариф "${subscription.name}" успешно оформлен!`);
      this.requestAllSubscriptionTypes();
      this.requestAllSubscriptions();
      this.userService.getCurrentUser();
    }, error => {
      this.paymentService.isSubscriptionLoading = false;

      if ( error.error.responseText )
        this.toastrService.error( error.error.responseText, 'Внимание!' );
      else
        this.toastrService.error( 'Ошибка оформления тарифа!', 'Внимание!' );
    } );
  }

  buySubscriptionAddon() {
    if ( this.selectedSubscription == null || this.documentsCount == null || this.documentsCount === 0 ||
      this.userService.currentUser.balance < this.cost ) return;

    this.isLoading = true;

    this.paymentService.buySubscriptionAddon( this.selectedSubscription.id, this.documentsCount ).subscribe( () => {
      this.isLoading = false;

      this.toastrService.success('Тариф успешно дополнен!');
      this.requestAllSubscriptions();
      this.userService.getCurrentUser();
    }, error => {
      this.isLoading = false;

      if ( error.error.responseText )
        this.toastrService.error( error.error.responseText, 'Внимание!' );
      else
        this.toastrService.error( 'Ошибка дополнения тарифа!', 'Внимание!' );
    } );
  }

  updateRenewalSubscription(subscription: SubscriptionTypeResponse) {
    this.paymentService.isSubscriptionLoading = true;

    const isSame = this.paymentService.currentSubscription.renewalType === subscription.type;

    this.paymentService.updateRenewalSubscription( subscription.type ).subscribe( () => {
      this.paymentService.isSubscriptionLoading = false;

      if ( isSame )
        this.toastrService.success(`Тариф "${subscription.name}" успешно отменен от продления!`);
      else
        this.toastrService.success(`Тариф "${subscription.name}" успешно оформлен на продление!`);
      this.paymentService.requestCurrentSubscription();
    }, error => {
      this.paymentService.isSubscriptionLoading = false;

      if ( error.error.responseText )
        this.toastrService.error( error.error.responseText, 'Внимание!' );
      else
        this.toastrService.error( 'Ошибка продления тарифа!', 'Внимание!' );
    } );
  }

  isBuyAvailable(): boolean {
    if ( this.paymentService.currentSubscription == null )
      return true;
    else {
      const now: Date = new Date();

      return this.paymentService.currentSubscription.endDate < now;
    }
  }

  calculateCost() {

    if ( this.documentsCount == null ) {
      this.cost = null;
      return;
    }

    if ( this.documentsCount < 0 ) this.documentsCount = 0;
    if ( !Number.isInteger(this.documentsCount) ) this.documentsCount = Math.floor( this.documentsCount );

    if ( this.selectedSubscription == null )
      this.cost = null;
    else
      this.cost = this.documentsCount * this.selectedSubscription.documentCost;
  }

  toggleAddon() {
    this.showAddon = true;
  }

}
