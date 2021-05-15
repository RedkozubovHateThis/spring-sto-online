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
import {ProfileResource, ProfileResourceService} from '../../model/resource/profile.resource.service';
import {UserRoleResource, UserRoleResourceService} from '../../model/resource/user-role.resource.service';
import {AdEntityResource, AdEntityResourceService} from '../../model/resource/ad-entity.resource.service';
import {CustomerResource} from '../../model/resource/customer.resource.service';
import {VehicleResource} from '../../model/resource/vehicle.resource.service';
import {VehicleMileageResource} from '../../model/resource/vehicle-mileage.resource.service';
import {AdEntityService} from '../../api/ad-entity.service';

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

  private adEntities: DocumentCollection<AdEntityResource> = new DocumentCollection<AdEntityResource>();

  constructor(private userService: UserService, protected route: ActivatedRoute, private location: Location,
              private router: Router, private toastrService: ToastrService, private userRoleResourceService: UserRoleResourceService,
              private paymentService: PaymentService, private profileResourceService: ProfileResourceService,
              private adEntityResourceService: AdEntityResourceService, private adEntityService: AdEntityService) {
    super(userService, route);
  }

  requestData() {
    this.isLoading = true;
    this.userService.getOne(this.id).subscribe( data => {
      this.model = data;
      this.isLoading = false;
      this.checkRelations();
      this.requestSideOffers();
    }, error => {
      this.isLoading = false;
    } );

    this.requestAllSubscriptionTypes();
  }

  checkRelations() {
    if ( !this.model.relationships.profile.data )
      this.model.addRelationship( this.profileResourceService.new(), 'profile' );
    if ( !this.model.relationships.adEntity.data )
      this.newAdEntity();
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

  requestSideOffers() {
    this.adEntityService.getAllByUser(this.model.id).subscribe( (adEntities) => {
      this.adEntities = adEntities;
    } );
  }

  onTransferComplete() {
    this.checkRelations();
    this.requestAllSubscriptionTypes();
  }

  setRole() {}

  save() {
    if ( this.checkData() )
      this.userService.saveUser( this.model, 'Пользователь успешно сохранен!', this.saveAdEntity );

    if ( this.adEntities.data.length > 0 )
      this.adEntities.data.forEach( (adEntity) => {
        this.adEntityService.save(adEntity).subscribe( (saved) => { } );
      } );
  }

  newAdEntity() {
    const adEntity: AdEntityResource = this.adEntityResourceService.new();
    adEntity.attributes.sideOffer = false;
    this.model.addRelationship( adEntity, 'adEntity' );
  }

  checkData(): boolean {
    if ( this.saveAdEntity ) {
      const adEntity: AdEntityResource = this.model.relationships.adEntity.data;

      if ( !adEntity.attributes.phone || adEntity.attributes.phone.length === 0 ) {
        this.toastrService.error('Не указан телефон рекламного объявления!', 'Внимание!');
        return false;
      }
      else if ( !adEntity.attributes.description || adEntity.attributes.description.length === 0 ) {
        this.toastrService.error('Не указано описание рекламного объявления!', 'Внимание!');
        return false;
      }
    }

    return true;
  }

  get saveAdEntity(): boolean {
    const adEntity: AdEntityResource = this.model.relationships.adEntity.data;
    return this.model.isServiceLeaderOrFreelancer() && this.model.isAdSubscriptionActive()
      && adEntity && adEntity.type && Object.keys(adEntity.attributes).length > 0;
  }

}
