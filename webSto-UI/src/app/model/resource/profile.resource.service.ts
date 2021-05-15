import {Injectable} from '@angular/core';
import {Autoregister, DocumentCollection, DocumentResource, Resource, Service, IAttributes} from 'ngx-jsonapi';
import {UserResource} from './user.resource.service';
import { IRelationships } from 'ngx-jsonapi/interfaces/relationship';

export interface IProfileAttributes extends IAttributes {
  name: string;
  address: string;
  phone: string;
  email: string;
  inn: string;
  deleted: boolean;
  autoRegister: boolean;
  byFio: boolean;
  firstName: string;
  middleName: string;
  lastName: string;
}

export interface IProfileRelationships extends IRelationships {
  user: DocumentResource<UserResource>;
  createdBy: DocumentResource<ProfileResource>;
}

export class ProfileResource extends Resource {
  public attributes: IProfileAttributes = {
    name: null,
    address: null,
    phone: null,
    email: null,
    inn: null,
    deleted: false,
    autoRegister: false,
    byFio: false,
    firstName: null,
    middleName: null,
    lastName: null
  };

  public relationships: IProfileRelationships = {
    user: new DocumentResource<UserResource>(),
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
export class ProfileResourceService extends Service<ProfileResource> {
  public resource = ProfileResource;
  public type = 'profile';
  public path = 'profiles';
}
