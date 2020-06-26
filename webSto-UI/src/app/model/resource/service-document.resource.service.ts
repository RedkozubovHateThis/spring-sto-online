import {Injectable} from '@angular/core';
import {Autoregister, DocumentCollection, DocumentResource, Resource, Service} from 'ngx-jsonapi';
import {UserResource} from './user.resource.service';
import {ServiceWorkResource} from './service-work.resource.service';
import {ServiceAddonResource} from './service-addon.resource.service';
import {VehicleResource} from './vehicle.resource.service';
import {VehicleMileageResource} from './vehicle-mileage.resource.service';

export class ServiceDocumentResource extends Resource {
  public attributes = {
    number: null,
    startDate: null,
    endDate: null,
    status: null,
    deleted: false,
    cost: null,
    reason: null
  };

  public relationships = {
    serviceWorks: new DocumentCollection<ServiceWorkResource>(),
    serviceAddons: new DocumentCollection<ServiceAddonResource>(),
    executor: new DocumentResource<UserResource>(),
    client: new DocumentResource<UserResource>(),
    vehicle: new DocumentResource<VehicleResource>(),
    vehicleMileage: new DocumentResource<VehicleMileageResource>(),
  };
}

@Injectable()
@Autoregister()
export class ServiceDocumentResourceService extends Service<ServiceDocumentResource> {
  public resource = ServiceDocumentResource;
  public type = 'serviceDocument';
  public path = 'serviceDocuments';
}
