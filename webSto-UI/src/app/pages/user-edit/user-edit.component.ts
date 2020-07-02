import {Component, OnInit} from '@angular/core';
import {Location} from '@angular/common';
import {ActivatedRoute, Router} from '@angular/router';
import {UserService} from '../../api/user.service';
import {ModelTransfer} from '../model.transfer';
import {PaymentService} from '../../api/payment.service';
import {ToastrService} from 'ngx-toastr';
import {UserResource} from '../../model/resource/user.resource.service';
import {DocumentCollection} from 'ngx-jsonapi';
import {SubscriptionTypeResource} from '../../model/resource/subscription-type.resource.service';
import {ProfileResourceService} from '../../model/resource/profile.resource.service';
import {UserRoleResource, UserRoleResourceService} from '../../model/resource/user-role.resource.service';

@Component({
  selector: 'app-user-edit',
  templateUrl: './user-edit.component.html',
  styleUrls: ['./user-edit.component.scss']
})
export class UserEditComponent extends ModelTransfer<UserResource, string> implements OnInit {

  private isLoading = false;
  private roles: DocumentCollection<UserRoleResource> = new DocumentCollection<UserRoleResource>();
  private selectedRole: UserRoleResource;
  private password = '';
  private rePassword = '';

  private subscriptionTypes: DocumentCollection<SubscriptionTypeResource>;
  private isTypesLoading = false;

  constructor(private userService: UserService, protected route: ActivatedRoute, private location: Location,
              private router: Router, private toastrService: ToastrService, private userRoleResourceService: UserRoleResourceService,
              private paymentService: PaymentService, private profileResourceService: ProfileResourceService) {
    super(userService, route);
  }

  requestData() {
    this.isLoading = true;
    this.userService.getOne(this.id).subscribe( data => {
      this.model = data;
      this.isLoading = false;
      this.checkRelations();
    }, error => {
      this.isLoading = false;
    } );

    this.requestAllSubscriptionTypes();
  }

  checkRelations() {
    if ( !this.model.relationships.profile.data )
      this.model.addRelationship( this.profileResourceService.new(), 'profile' );
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
    this.checkRelations();
    this.requestAllSubscriptionTypes();
  }

  setRole() {}

  save() {
    this.userService.saveUser( this.model, 'Пользователь успешно сохранен!' );
  }

}
