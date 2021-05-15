import {Injectable} from '@angular/core';
import {Autoregister, DocumentResource, Resource, Service} from 'ngx-jsonapi';
import {ServiceDocumentResource} from './service-document.resource.service';
import {VehicleResource} from './vehicle.resource.service';

export class VehicleMileageResource extends Resource {
  public attributes = {
    mileage: null,
    deleted: false
  };

  public relationships = {
    vehicle: new DocumentResource<VehicleResource>(),
    document: new DocumentResource<ServiceDocumentResource>()
  };
}

@Injectable()
@Autoregister()
export class VehicleMileageResourceService extends Service<VehicleMileageResource> {
  public resource = VehicleMileageResource;
  public type = 'vehicleMileage';
  public path = 'vehicleMileages';
}
