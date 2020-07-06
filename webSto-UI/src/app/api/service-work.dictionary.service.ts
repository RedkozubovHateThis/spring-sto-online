import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {DocumentCollection} from 'ngx-jsonapi';
import {VehicleDictionaryResource, VehicleDictionaryResourceService} from '../model/resource/vehicle-dictionary.resource.service';
import {
  ServiceWorkDictionaryResource,
  ServiceWorkDictionaryResourceService
} from '../model/resource/service-work-dictionary.resource.service';
import {environment} from '../../environments/environment';

@Injectable()
export class ServiceWorkDictionaryService {

  constructor(private serviceWorkDictionaryResourceService: ServiceWorkDictionaryResourceService) {
    serviceWorkDictionaryResourceService.register();
  }

  findByName(search: string): Observable<DocumentCollection<ServiceWorkDictionaryResource>> {
    return this.serviceWorkDictionaryResourceService.all({
      beforepath: `${environment.getBeforeUrl()}/external`,
      remotefilter: {
        name: search
      }
    });
  }

}
