import {Component, OnInit} from '@angular/core';
import {Location} from '@angular/common';
import {ActivatedRoute, Router} from '@angular/router';
import {UserService} from '../../api/user.service';
import {User} from '../../model/postgres/auth/user';
import {ModelTransfer} from '../model.transfer';
import {PaymentService} from '../../api/payment.service';
import {SubscriptionTypeResponse} from '../../model/payment/subscriptionTypeResponse';
import {ToastrService} from 'ngx-toastr';
import {UserResource} from '../../model/resource/user.resource.service';
import {DocumentCollection} from 'ngx-jsonapi';
import {SubscriptionTypeResource} from '../../model/resource/subscription-type.resource.service';

@Component({
  selector: 'app-user-edit',
  templateUrl: './user-edit.component.html',
  styleUrls: ['./user-edit.component.scss']
})
export class UserEditComponent extends ModelTransfer<UserResource, string> implements OnInit {

  private isLoading = false;

  private subscriptionTypes: DocumentCollection<SubscriptionTypeResource>;
  private isTypesLoading = false;
  private vinNumber: string;

  constructor(private userService: UserService, protected route: ActivatedRoute, private location: Location,
              private router: Router, private toastrService: ToastrService,
              private paymentService: PaymentService) {
    super(userService, route);
  }

  requestData() {
    this.isLoading = true;
    this.userService.getOne(this.id).subscribe( data => {
      this.model = data;
      this.isLoading = false;
    }, error => {
      this.isLoading = false;
    } );

    this.requestAllSubscriptionTypes();
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

  onTransferComplete() {
    this.requestAllSubscriptionTypes();
  }

}
