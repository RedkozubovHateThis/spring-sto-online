import {Injectable} from '@angular/core';
import {Autoregister, DocumentCollection, Resource, Service} from 'ngx-jsonapi';
import {ServiceDocumentResource} from './service-document.resource.service';
import {VehicleMileageResource} from './vehicle-mileage.resource.service';

export class VehicleResource extends Resource {
  public attributes = {
    modelName: null,
    vinNumber: null,
    regNumber: null,
    year: null,
    deleted: false
  };

  public relationships = {
    vehicleMileages: new DocumentCollection<VehicleMileageResource>(),
    documents: new DocumentCollection<ServiceDocumentResource>()
  };
}

@Injectable()
@Autoregister()
export class VehicleResourceService extends Service<VehicleResource> {
  public resource = VehicleResource;
  public type = 'vehicle';
  public path = 'vehicles';
}
