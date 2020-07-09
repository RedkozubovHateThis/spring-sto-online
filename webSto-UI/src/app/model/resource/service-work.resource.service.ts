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

  public prepareRecord = () => {
    this.parseValue('priceNorm');
    this.parseValue('price');
    this.parseValue('timeValue');
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
export class ServiceWorkResourceService extends Service<ServiceWorkResource> {
  public resource = ServiceWorkResource;
  public type = 'serviceWork';
  public path = 'serviceWorks';
}
