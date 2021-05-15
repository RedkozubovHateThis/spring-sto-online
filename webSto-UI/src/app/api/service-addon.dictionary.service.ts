import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {DocumentCollection} from 'ngx-jsonapi';
import {
  ServiceAddonDictionaryResource,
  ServiceAddonDictionaryResourceService
} from '../model/resource/service-addon-dictionary.resource.service';
import {environment} from '../../environments/environment';
import {DictionariesFilter} from '../model/dictionariesFilter';
import {IDataCollection} from 'ngx-jsonapi/interfaces/data-collection';
import {HttpClient} from '@angular/common/http';
import {IDocumentResource} from 'ngx-jsonapi/interfaces/data-object';
import {RestService} from './rest.service';

@Injectable()
export class ServiceAddonDictionaryService implements RestService<ServiceAddonDictionaryResource> {

  constructor(private serviceAddonDictionaryResourceService: ServiceAddonDictionaryResourceService, private http: HttpClient) {
    serviceAddonDictionaryResourceService.register();
  }

  getAll(filter: DictionariesFilter): Observable<DocumentCollection<ServiceAddonDictionaryResource>> {
    const params = {
      name: filter.name != null ? filter.name : ''
    };
    return this.serviceAddonDictionaryResourceService.all({
      beforepath: `${environment.getBeforeUrl()}/external`,
      sort: [`${filter.direction === 'desc' ? '-' : ''}${filter.sort}`],
      page: { number: filter.page, size: filter.size },
      remotefilter: params
    });
  }

  findByName(search: string): Observable<DocumentCollection<ServiceAddonDictionaryResource>> {
    return new Observable<DocumentCollection<ServiceAddonDictionaryResource>>( (subscriber) => {
      this.http.get<IDataCollection>(`${environment.getApiUrl()}external/serviceAddonDictionaries/search`, { params: { name: search } }).subscribe( (data) => {
        const collection = this.serviceAddonDictionaryResourceService.newCollection();
        collection.fill( data );
        subscriber.next( collection );
        subscriber.complete();
      }, (error) => {
        subscriber.error( error );
        subscriber.complete();
      } );
    } );
  }

  save(model: ServiceAddonDictionaryResource): Observable<ServiceAddonDictionaryResource> {
    return new Observable<ServiceAddonDictionaryResource>( (subscriber) => {
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

  delete(model: ServiceAddonDictionaryResource): Observable<void> {
    return this.serviceAddonDictionaryResourceService.delete(model.id, {
      beforepath: environment.getBeforeUrl()
    });
  }

}
