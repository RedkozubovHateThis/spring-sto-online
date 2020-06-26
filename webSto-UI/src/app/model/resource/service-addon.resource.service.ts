import {Injectable} from '@angular/core';
import {Autoregister, DocumentResource, Resource, Service} from 'ngx-jsonapi';
import {ServiceDocumentResource} from './service-document.resource.service';

export class ServiceAddonResource extends Resource {
  public attributes = {
    count: null,
    cost: null,
    name: null,
    number: null,
    deleted: false
  };

  public relationships = {
    serviceDocument: new DocumentResource<ServiceDocumentResource>()
  };
}

@Injectable()
@Autoregister()
export class ServiceAddonResourceService extends Service<ServiceAddonResource> {
  public resource = ServiceAddonResource;
  public type = 'serviceAddon';
  public path = 'serviceAddons';
}
