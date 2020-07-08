import {Injectable} from '@angular/core';
import {Autoregister, DocumentCollection, DocumentResource, Resource, Service} from 'ngx-jsonapi';
import {UserRoleResource} from './user-role.resource.service';
import {SubscriptionResource} from './subscription.resource.service';
import {UserResource} from './user.resource.service';

export class CustomerResource extends Resource {
  public attributes = {
    name: null,
    address: null,
    phone: null,
    email: null,
    inn: null,
    deleted: false
  };

  public relationships = {
  };
}

@Injectable()
@Autoregister()
export class CustomerResourceService extends Service<CustomerResource> {
  public resource = CustomerResource;
  public type = 'customer';
  public path = 'customers';
}
