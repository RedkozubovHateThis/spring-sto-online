import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {DocumentCollection} from 'ngx-jsonapi';
import {VehicleDictionaryResource, VehicleDictionaryResourceService} from '../model/resource/vehicle-dictionary.resource.service';
import {environment} from '../../environments/environment';
import {RestService} from './rest.service';
import {
  ServiceWorkDictionaryResource,
  ServiceWorkDictionaryResourceService
} from '../model/resource/service-work-dictionary.resource.service';
import {IDocumentResource} from 'ngx-jsonapi/interfaces/data-object';
import {DictionariesFilter} from '../model/dictionariesFilter';
import {IDataCollection} from 'ngx-jsonapi/interfaces/data-collection';
import {HttpClient} from '@angular/common/http';
import {AdEntityResource, AdEntityResourceService} from '../model/resource/ad-entity.resource.service';
import {AdEntitiesFilter} from '../model/adEntitiesFilter';

@Injectable()
export class AdEntityService implements RestService<AdEntityResource> {

  constructor(private adEntityResourceService: AdEntityResourceService, private http: HttpClient) {
    adEntityResourceService.register();
  }

  getAll(filter: AdEntitiesFilter): Observable<DocumentCollection<AdEntityResource>> {
    const params = {
      name: filter.name != null ? filter.name : '',
      phone: filter.phone != null ? filter.phone : '',
      email: filter.email != null ? filter.email : '',
      active: filter.active != null ? filter.active : '',
      sideOffer: filter.sideOffer != null ? filter.sideOffer : ''
    };
    return this.adEntityResourceService.all({
      beforepath: `${environment.getBeforeUrl()}/external`,
      sort: [`${filter.direction === 'desc' ? '-' : ''}${filter.sort}`],
      page: { number: filter.page, size: filter.size },
      remotefilter: params
    });
  }

  getCurrent(): Observable<AdEntityResource> {
      return this.adEntityResourceService.get('current', {
        beforepath: `${environment.getBeforeUrl()}/external`
      });
  }

  save(model: AdEntityResource): Observable<AdEntityResource> {
    return new Observable<AdEntityResource>( (subscriber) => {
      model.save({ beforepath: environment.getBeforeUrl() }).subscribe( (data: IDocumentResource) => {
        // model.fill(data);
        subscriber.next(model);
        subscriber.complete();
      }, ( error ) => {
        subscriber.error( error );
        subscriber.complete();
      } );
    } );
  }

  delete(model: AdEntityResource): Observable<void> {
    return this.adEntityResourceService.delete(model.id, {
      beforepath: environment.getBeforeUrl()
    });
  }

}
