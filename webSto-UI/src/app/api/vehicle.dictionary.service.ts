import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {DocumentCollection} from 'ngx-jsonapi';
import {VehicleDictionaryResource, VehicleDictionaryResourceService} from '../model/resource/vehicle-dictionary.resource.service';

@Injectable()
export class VehicleDictionaryService {

  constructor(private vehicleDictionaryResourceService: VehicleDictionaryResourceService) {
    vehicleDictionaryResourceService.register();
  }

  findByName(search: string): Observable<DocumentCollection<VehicleDictionaryResource>> {
    return this.vehicleDictionaryResourceService.all({
      beforepath: `external`,
      remotefilter: {
        name: search
      }
    });
  }

}
