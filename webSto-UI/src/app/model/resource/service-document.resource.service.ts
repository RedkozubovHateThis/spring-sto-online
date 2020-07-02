import {Injectable} from '@angular/core';
import {Autoregister, DocumentCollection, DocumentResource, Resource, Service} from 'ngx-jsonapi';
import {UserResource} from './user.resource.service';
import {ServiceWorkResource} from './service-work.resource.service';
import {ServiceAddonResource} from './service-addon.resource.service';
import {VehicleResource} from './vehicle.resource.service';
import {VehicleMileageResource} from './vehicle-mileage.resource.service';
import {ProfileResource} from './profile.resource.service';

const statuses: IStatus[] = [
    {
      name: 'Черновик',
      id: 'CREATED'
    },
    {
      name: 'Оформлен',
      id: 'COMPLETED'
    }
];

export class ServiceDocumentResource extends Resource {
  public attributes = {
    number: null,
    startDate: new Date().getTime(),
    endDate: null,
    status: 'CREATED',
    deleted: false,
    cost: null,
    reason: null
  };

  public relationships = {
    serviceWorks: new DocumentCollection<ServiceWorkResource>(),
    serviceAddons: new DocumentCollection<ServiceAddonResource>(),
    executor: new DocumentResource<ProfileResource>(),
    client: new DocumentResource<ProfileResource>(),
    vehicle: new DocumentResource<VehicleResource>(),
    vehicleMileage: new DocumentResource<VehicleMileageResource>(),
  };

  public statusRus = (): string => {
    const { status } = this.attributes;

    if ( !status ) return '';
    const currentStatus = statuses.find( (eachStatus) => eachStatus.id === status );
    if ( currentStatus )
      return currentStatus.name;

    return '';
  }

  public getStatuses = (): IStatus[] => {
    return statuses;
  }
}

@Injectable()
@Autoregister()
export class ServiceDocumentResourceService extends Service<ServiceDocumentResource> {
  public resource = ServiceDocumentResource;
  public type = 'serviceDocument';
  public path = 'serviceDocuments';
}
