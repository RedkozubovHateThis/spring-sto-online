import {Injectable} from '@angular/core';
import {Autoregister, DocumentCollection, Resource, Service, IAttributes, DocumentResource} from 'ngx-jsonapi';
import {ServiceDocumentResource} from './service-document.resource.service';
import {VehicleMileageResource} from './vehicle-mileage.resource.service';
import { IRelationships } from 'ngx-jsonapi/interfaces/relationship';
import {ProfileResource} from './profile.resource.service';
import {UserResource} from './user.resource.service';

export interface IVehicleAttributes extends IAttributes {
  modelName: string;
  vinNumber: string;
  regNumber: string;
  year: number;
  deleted: boolean;
}

export interface IVehicleRelationships extends IRelationships {
  vehicleMileages: DocumentCollection<VehicleMileageResource>;
  documents: DocumentCollection<ServiceDocumentResource>;
  createdBy: DocumentResource<ProfileResource>;
}

export class VehicleResource extends Resource {
  public attributes: IVehicleAttributes = {
    modelName: null,
    vinNumber: null,
    regNumber: null,
    year: null,
    deleted: false
  };

  public relationships: IVehicleRelationships = {
    vehicleMileages: new DocumentCollection<VehicleMileageResource>(),
    documents: new DocumentCollection<ServiceDocumentResource>(),
    createdBy: new DocumentResource<ProfileResource>()
  };

  public isCreatedByUser(user: UserResource): boolean {
    if ( !user.relationships.profile.data ) return false;
    if ( !this.relationships.createdBy.data ) return false;

    return user.relationships.profile.data.id === this.relationships.createdBy.data.id;
  }
}

@Injectable()
@Autoregister()
export class VehicleResourceService extends Service<VehicleResource> {
  public resource = VehicleResource;
  public type = 'vehicle';
  public path = 'vehicles';
}
