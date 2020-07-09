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

  public prepareRecord = () => {
    this.parseValue('cost');
  }

  private parseValue = (fieldName: string): void => {
    const field = this.attributes[fieldName];
    if ( field && typeof field === 'string' && field.includes(',') ) {
      try {
        this.attributes[fieldName] = parseFloat(field.replace(',', '\.'));
      } catch (e) {
        console.error(e);
      }
    }
  }

  public relationships = {
    document: new DocumentResource<ServiceDocumentResource>()
  };
}

@Injectable()
@Autoregister()
export class ServiceAddonResourceService extends Service<ServiceAddonResource> {
  public resource = ServiceAddonResource;
  public type = 'serviceAddon';
  public path = 'serviceAddons';
}
