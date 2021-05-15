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

@Injectable()
export class VehicleDictionaryService implements RestService<VehicleDictionaryResource> {

  constructor(private vehicleDictionaryResourceService: VehicleDictionaryResourceService, private http: HttpClient) {
    vehicleDictionaryResourceService.register();
  }

  getAll(filter: DictionariesFilter): Observable<DocumentCollection<ServiceWorkDictionaryResource>> {
    const params = {
      name: filter.name != null ? filter.name : ''
    };
    return this.vehicleDictionaryResourceService.all({
      beforepath: `${environment.getBeforeUrl()}/external`,
      sort: [`${filter.direction === 'desc' ? '-' : ''}${filter.sort}`],
      page: { number: filter.page, size: filter.size },
      remotefilter: params
    });
  }

  findByName(search: string): Observable<DocumentCollection<ServiceWorkDictionaryResource>> {
    return new Observable<DocumentCollection<ServiceWorkDictionaryResource>>( (subscriber) => {
      this.http.get<IDataCollection>(`${environment.getApiUrl()}external/vehicleDictionaries/search`, { params: { name: search } }).subscribe( (data) => {
        const collection = this.vehicleDictionaryResourceService.newCollection();
        collection.fill( data );
        subscriber.next( collection );
        subscriber.complete();
      }, (error) => {
        subscriber.error( error );
        subscriber.complete();
      } );
    } );
  }

  save(model: VehicleDictionaryResource): Observable<VehicleDictionaryResource> {
    return new Observable<VehicleDictionaryResource>( (subscriber) => {
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

  delete(model: VehicleDictionaryResource): Observable<void> {
    return this.vehicleDictionaryResourceService.delete(model.id, {
      beforepath: environment.getBeforeUrl()
    });
  }

}
