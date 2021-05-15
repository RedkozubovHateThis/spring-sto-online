import {Injectable} from '@angular/core';
import {Autoregister, DocumentCollection, Resource, Service} from 'ngx-jsonapi';
import {ServiceDocumentResource} from './service-document.resource.service';
import {VehicleMileageResource} from './vehicle-mileage.resource.service';

export class ServiceAddonDictionaryResource extends Resource {
  public attributes = {
    initial: false,
    name: null,
    deleted: false
  };

  public relationships = {};
}

@Injectable()
@Autoregister()
export class ServiceAddonDictionaryResourceService extends Service<ServiceAddonDictionaryResource> {
  public resource = ServiceAddonDictionaryResource;
  public type = 'serviceAddonDictionary';
  public path = 'serviceAddonDictionaries';
}
