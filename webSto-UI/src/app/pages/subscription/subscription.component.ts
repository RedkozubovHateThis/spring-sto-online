import {Component, OnInit} from '@angular/core';
import {PaymentService} from '../../api/payment.service';
import {ToastrService} from 'ngx-toastr';
import {SubscriptionTypeResponse} from '../../model/payment/subscriptionTypeResponse';
import {UserService} from '../../api/user.service';
import {SubscriptionResponse} from '../../model/payment/subscriptionResponse';
import {SubscriptionTypeResource} from '../../model/resource/subscription-type.resource.service';

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

  buySubscription(subscriptionTypeId: number) {
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

  buySubscriptionAddon() {
    if ( this.selectedSubscription == null || this.documentsCount == null || this.documentsCount === 0 ||
      this.userService.currentUser.attributes.balance < this.cost ) return;

    this.isLoading = true;

    this.paymentService.buySubscriptionAddon( this.selectedSubscription.id, this.documentsCount ).subscribe( () => {
      this.isLoading = false;

      this.toastrService.success('Тариф успешно дополнен!');
      this.requestAllSubscriptions();
      this.userService.getCurrentUser();

      this.showAddon = false;
      this.documentsCount = null;
      this.cost = null;
    }, error => {
      this.isLoading = false;

      if ( error.error.responseText )
        this.toastrService.error( error.error.responseText, 'Внимание!' );
      else
        this.toastrService.error( 'Ошибка дополнения тарифа!', 'Внимание!' );
    } );
  }

  updateRenewalSubscription(subscription: SubscriptionTypeResource) {
    this.paymentService.isSubscriptionLoading = true;

    const isSame = this.userService.currentUser.relationships.currentSubscription.data.id === subscription.id;

    this.paymentService.updateRenewalSubscription( subscription.id ).subscribe( () => {
      this.paymentService.isSubscriptionLoading = false;

      if ( isSame )
        this.toastrService.success(`Тариф "${subscription.attributes.name}" успешно отменен как тариф по умолчанию!`);
      else
        this.toastrService.success(`Тариф "${subscription.attributes.name}" успешно установлен как тариф по умолчанию!`);
      this.userService.getCurrentUser();
    }, error => {
      this.paymentService.isSubscriptionLoading = false;

      if ( error.error.responseText )
        this.toastrService.error( error.error.responseText, 'Внимание!' );
      else
        this.toastrService.error( 'Ошибка установки тарифа по умолчанию!', 'Внимание!' );
    } );
  }

  updateSubscription(subscriptionType: SubscriptionTypeResponse) {
    if ( !this.userService.isAdmin() ) return;

    this.isTypesLoading = true;

    this.paymentService.updateSubscription( subscriptionType ).subscribe( () => {
      this.isTypesLoading = false;
      this.requestAllSubscriptionTypes();
      this.toastrService.success( `Тариф "${subscriptionType.name}" успешно изменен!` );
    }, error => {
      this.isTypesLoading = false;

      if ( error.error.responseText )
        this.toastrService.error( error.error.responseText, 'Внимание!' );
      else
        this.toastrService.error( 'Ошибка изменения тарифа!', 'Внимание!' );
    } )
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

  calculateSubscriptionCost(subscriptionType: SubscriptionTypeResponse) {

    if ( subscriptionType.isFree ) return;

    if ( subscriptionType.documentsCount == null || subscriptionType.documentCost == null ) {
      subscriptionType.cost = null;
      return;
    }

    if ( subscriptionType.documentsCount < 0 ) subscriptionType.documentsCount = 0;
    if ( !Number.isInteger(subscriptionType.documentsCount) )
      subscriptionType.documentsCount = Math.floor( subscriptionType.documentsCount );

    if ( subscriptionType.documentCost < 0 ) subscriptionType.documentCost = 0;
    if ( !Number.isInteger(subscriptionType.documentCost) )
      subscriptionType.documentCost = Math.floor( subscriptionType.documentCost );

    subscriptionType.cost = subscriptionType.documentsCount * subscriptionType.documentCost;
  }

  isNotValid(subscriptionType: SubscriptionTypeResponse) {

    const isFreeOrCostValid = subscriptionType.isFree || ( subscriptionType.cost > 0 && subscriptionType.documentCost > 0 );
    const isDocumentsValid = subscriptionType.documentsCount > 0;
    const isDaysValid = subscriptionType.durationDays > 0;

    return !isFreeOrCostValid || !isDocumentsValid || !isDaysValid;

  }

  toggleAddon() {
    this.showAddon = true;
  }

}
