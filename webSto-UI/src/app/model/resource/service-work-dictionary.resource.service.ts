import {Injectable} from '@angular/core';
import {Autoregister, DocumentCollection, Resource, Service} from 'ngx-jsonapi';
import {ServiceDocumentResource} from './service-document.resource.service';
import {VehicleMileageResource} from './vehicle-mileage.resource.service';

export class ServiceWorkDictionaryResource extends Resource {
  public attributes = {
    initial: false,
    name: null,
    deleted: false
  };

  public relationships = {};
}

@Injectable()
@Autoregister()
export class ServiceWorkDictionaryResourceService extends Service<ServiceWorkDictionaryResource> {
  public resource = ServiceWorkDictionaryResource;
  public type = 'serviceWorkDictionary';
  public path = 'serviceWorkDictionaries';
}
