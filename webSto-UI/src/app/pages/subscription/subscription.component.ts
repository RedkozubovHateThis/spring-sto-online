import {Component, OnInit} from '@angular/core';
import {PaymentService} from '../../api/payment.service';
import {ToastrService} from 'ngx-toastr';
import {UserService} from '../../api/user.service';
import {SubscriptionTypeResource} from '../../model/resource/subscription-type.resource.service';
import {DocumentCollection} from 'ngx-jsonapi';
import {SubscriptionResource} from '../../model/resource/subscription.resource.service';

@Component({
  selector: 'app-subscription',
  templateUrl: './subscription.component.html',
  styleUrls: ['./subscription.component.scss']
})
export class SubscriptionComponent implements OnInit {

  private isTypesLoading = false;
  private isLoading = false;
  private subscriptionTypes: DocumentCollection<SubscriptionTypeResource>;
  private subscriptions: DocumentCollection<SubscriptionResource>;
  private selectedSubscription: SubscriptionResource;
  private documentsCount: number = null;
  private cost: number;
  private showAddon = false;

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

      if ( this.subscriptions.data.length > 0 )
        if ( this.selectedSubscription != null )
          this.selectedSubscription = this.subscriptions.data.find( subscription => subscription.id === this.selectedSubscription.id );
        else
          this.selectedSubscription = this.subscriptions.data[ this.subscriptions.data.length - 1 ];

      this.isLoading = false;
    }, () => {
      this.isLoading = false;
    } );
  }

  buySubscription(subscriptionTypeId: string) {
    this.paymentService.isSubscriptionLoading = true;

    this.paymentService.buySubscription( subscriptionTypeId ).subscribe( subscription => {
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

  unsubscribe(subscription: SubscriptionResource) {
    this.paymentService.isSubscriptionLoading = true;

    this.paymentService.unsubscribeSubscription( subscription.id ).subscribe( () => {
      this.paymentService.isSubscriptionLoading = false;

      this.toastrService.success(`Вы успешно отписались от тарифа "${subscription.attributes.name}"!`);
      this.userService.getCurrentUser();
    }, error => {
      this.paymentService.isSubscriptionLoading = false;

      if ( error.error.responseText )
        this.toastrService.error( error.error.responseText, 'Внимание!' );
      else
        this.toastrService.error( 'Ошибка отписки от тарифа!', 'Внимание!' );
    } );
  }

  updateSubscription(subscriptionType: SubscriptionTypeResource) {
    if ( !this.userService.isAdmin() ) return;

    this.isTypesLoading = true;

    this.paymentService.updateSubscription( subscriptionType ).subscribe( () => {
      this.isTypesLoading = false;
      this.requestAllSubscriptionTypes();
      this.toastrService.success( `Тариф "${subscriptionType.attributes.name}" успешно изменен!` );
    }, error => {
      this.isTypesLoading = false;

      if ( error.error.responseText )
        this.toastrService.error( error.error.responseText, 'Внимание!' );
      else
        this.toastrService.error( 'Ошибка изменения тарифа!', 'Внимание!' );
    } );
  }

  isBuyAvailable(): boolean {
    if ( this.userService.currentUser.relationships.currentSubscription.loaded )
      return true;
    else {
      const now: Date = new Date();

      return this.userService.currentUser.relationships.currentSubscription.data.attributes.endDate < now;
    }
  }

  isNotValid(subscriptionType: SubscriptionTypeResource) {

    const isCostValid = subscriptionType.attributes.cost > 0;
    const isDaysValid = subscriptionType.attributes.durationDays > 0;

    return !isCostValid || !isDaysValid;

  }

  toggleAddon() {
    this.showAddon = true;
  }

}
