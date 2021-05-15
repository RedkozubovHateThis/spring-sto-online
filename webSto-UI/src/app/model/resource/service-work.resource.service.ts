import {Injectable} from '@angular/core';
import {Autoregister, DocumentResource, Resource, Service} from 'ngx-jsonapi';
import {ServiceDocumentResource} from './service-document.resource.service';

export class ServiceWorkResource extends Resource {
  public attributes = {
    count: null,
    priceNorm: null,
    price: null,
    byPrice: false,
    timeValue: null,
    name: null,
    number: null,
    deleted: false
  };

  public relationships = {
    document: new DocumentResource<ServiceDocumentResource>()
  };
}

@Injectable()
@Autoregister()
export class ServiceWorkResourceService extends Service<ServiceWorkResource> {
  public resource = ServiceWorkResource;
  public type = 'serviceWork';
  public path = 'serviceWorks';
}
