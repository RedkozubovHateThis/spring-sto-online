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

  private isLoading: boolean = false;
  private subscriptions: SubscriptionTypeResponse[] = [];

  constructor(private userService: UserService, private paymentService: PaymentService, private toastrService: ToastrService) { }

  ngOnInit() {
    this.requestAllSubscriptions();
  }

  requestAllSubscriptions() {
    this.isLoading = true;

    this.paymentService.getAllSubscriptions().subscribe( subscriptions => {
      this.subscriptions = subscriptions;
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

}
