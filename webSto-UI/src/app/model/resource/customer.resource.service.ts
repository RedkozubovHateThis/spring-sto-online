import {Injectable} from '@angular/core';
import {Autoregister, DocumentCollection, DocumentResource, Resource, Service, IAttributes} from 'ngx-jsonapi';
import {ProfileResource} from './profile.resource.service';
import { IRelationships } from 'ngx-jsonapi/interfaces/relationship';
import {UserResource} from './user.resource.service';

export interface ICustomerAttributes extends IAttributes {
  name: string;
  address: string;
  phone: string;
  email: string;
  inn: string;
  deleted: boolean;
}

export interface ICustomerRelationships extends IRelationships {
  createdBy: DocumentResource<ProfileResource>;
}

export class CustomerResource extends Resource {
  public attributes: ICustomerAttributes = {
    name: null,
    address: null,
    phone: null,
    email: null,
    inn: null,
    deleted: false
  };

  public relationships: ICustomerRelationships = {
    createdBy: new DocumentResource<ProfileResource>()
  };

  public isCreatedByUser(user: UserResource): boolean {
    if ( user.isAdmin() ) return true;
    if ( !user.relationships.profile.data ) return false;
    if ( !this.relationships.createdBy.data ) return false;

    return user.relationships.profile.data.id === this.relationships.createdBy.data.id;
  }
}

@Injectable()
@Autoregister()
export class CustomerResourceService extends Service<CustomerResource> {
  public resource = CustomerResource;
  public type = 'customer';
  public path = 'customers';
}
