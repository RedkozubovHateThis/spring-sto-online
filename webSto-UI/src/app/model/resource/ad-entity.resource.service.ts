import {Injectable} from '@angular/core';
import {Autoregister, DocumentCollection, DocumentResource, IAttributes, Resource, Service} from 'ngx-jsonapi';
import {ServiceDocumentResource} from './service-document.resource.service';
import {VehicleResource} from './vehicle.resource.service';
import {UserResource} from './user.resource.service';
import {IRelationships} from 'ngx-jsonapi/interfaces/relationship';
import {VehicleMileageResource} from './vehicle-mileage.resource.service';
import {ProfileResource} from './profile.resource.service';

export interface IAdEntityAttributes extends IAttributes {
  name: string;
  description: string;
  phone: string;
  email: string;
  url: string;
  createDate: string;
  current: boolean;
  sideOffer: boolean;
  active: boolean;
  deleted: boolean;
  addedBy: string;
  addedById: string;
}

export interface IAdEntityRelationships extends IRelationships {
  serviceLeader: DocumentResource<UserResource>;
  sideOfferServiceLeader: DocumentResource<UserResource>;
}

export class AdEntityResource extends Resource {
  public attributes: IAdEntityAttributes = {
    name: null,
    description: null,
    phone: null,
    email: null,
    url: null,
    createDate: null,
    current: false,
    sideOffer: false,
    active: true,
    deleted: false,
    addedBy: null,
    addedById: null
  };

  get activeStatus(): string {
    if ( this.attributes.active == null ) return 'Не указано';
    return this.attributes.active ? 'Да' : 'Нет';
  }

  get sideOfferStatus(): string {
    if ( this.attributes.sideOffer == null ) return 'Не указано';
    return this.attributes.sideOffer ? 'Да' : 'Нет';
  }

  get url(): string {
    if ( !this.attributes.url ) return undefined;

    if ( !this.attributes.url.startsWith('http') )
      return `http://${this.attributes.url}`;
  }

  public relationships: IAdEntityRelationships = {
    serviceLeader: new DocumentResource<UserResource>(),
    sideOfferServiceLeader: new DocumentResource<UserResource>()
  };
}

@Injectable()
@Autoregister()
export class AdEntityResourceService extends Service<AdEntityResource> {
  public resource = AdEntityResource;
  public type = 'adEntity';
  public path = 'adEntities';
}
