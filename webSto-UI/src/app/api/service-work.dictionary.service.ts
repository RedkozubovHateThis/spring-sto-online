import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {DocumentCollection} from 'ngx-jsonapi';
import {
  ServiceWorkDictionaryResource,
  ServiceWorkDictionaryResourceService
} from '../model/resource/service-work-dictionary.resource.service';
import {environment} from '../../environments/environment';
import {DictionariesFilter} from '../model/dictionariesFilter';
import {RestService} from './rest.service';
import {ProfileResource, ProfileResourceService} from '../model/resource/profile.resource.service';
import {IDataCollection} from 'ngx-jsonapi/interfaces/data-collection';
import {HttpClient} from '@angular/common/http';
import {ServiceDocumentResource} from '../model/resource/service-document.resource.service';
import {IDocumentResource} from 'ngx-jsonapi/interfaces/data-object';

@Injectable()
export class ServiceWorkDictionaryService implements RestService<ServiceWorkDictionaryResource> {

  constructor(private serviceWorkDictionaryResourceService: ServiceWorkDictionaryResourceService, private http: HttpClient) {
    serviceWorkDictionaryResourceService.register();
  }

  getAll(filter: DictionariesFilter): Observable<DocumentCollection<ServiceWorkDictionaryResource>> {
    const params = {
      name: filter.name != null ? filter.name : ''
    };
    return this.serviceWorkDictionaryResourceService.all({
      beforepath: `${environment.getBeforeUrl()}/external`,
      sort: [`${filter.direction === 'desc' ? '-' : ''}${filter.sort}`],
      page: { number: filter.page, size: filter.size },
      remotefilter: params
    });
  }

  findByName(search: string): Observable<DocumentCollection<ServiceWorkDictionaryResource>> {
    return new Observable<DocumentCollection<ServiceWorkDictionaryResource>>( (subscriber) => {
      this.http.get<IDataCollection>(`${environment.getApiUrl()}external/serviceWorkDictionaries/search`, { params: { name: search } }).subscribe( (data) => {
        const collection = this.serviceWorkDictionaryResourceService.newCollection();
        collection.fill( data );
        subscriber.next( collection );
        subscriber.complete();
      }, (error) => {
        subscriber.error( error );
        subscriber.complete();
      } );
    } );
  }

  save(model: ServiceWorkDictionaryResource): Observable<ServiceWorkDictionaryResource> {
    return new Observable<ServiceWorkDictionaryResource>( (subscriber) => {
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

  delete(model: ServiceWorkDictionaryResource): Observable<void> {
    return this.serviceWorkDictionaryResourceService.delete(model.id, {
      beforepath: environment.getBeforeUrl()
    });
  }

}
