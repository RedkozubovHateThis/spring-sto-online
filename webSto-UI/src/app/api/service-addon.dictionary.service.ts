import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {DocumentCollection} from 'ngx-jsonapi';
import {VehicleDictionaryResource, VehicleDictionaryResourceService} from '../model/resource/vehicle-dictionary.resource.service';
import {ServiceWorkDictionaryResourceService} from '../model/resource/service-work-dictionary.resource.service';
import {
  ServiceAddonDictionaryResource,
  ServiceAddonDictionaryResourceService
} from '../model/resource/service-addon-dictionary.resource.service';
import {environment} from '../../environments/environment';

@Injectable()
export class ServiceAddonDictionaryService {

  constructor(private serviceAddonDictionaryResourceService: ServiceAddonDictionaryResourceService) {
    serviceAddonDictionaryResourceService.register();
  }

  findByName(search: string): Observable<DocumentCollection<ServiceAddonDictionaryResource>> {
    return this.serviceAddonDictionaryResourceService.all({
      beforepath: `${environment.getBeforeUrl()}/external`,
      remotefilter: {
        name: search
      }
    });
  }

}
