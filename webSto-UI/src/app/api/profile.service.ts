import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Router} from '@angular/router';
import {TransferService} from './transfer.service';
import {ToastrService} from 'ngx-toastr';
import {RestService} from './rest.service';
import {ServiceDocumentResource, ServiceDocumentResourceService} from '../model/resource/service-document.resource.service';
import {ServiceWorkResource, ServiceWorkResourceService} from '../model/resource/service-work.resource.service';
import {ServiceAddonResource, ServiceAddonResourceService} from '../model/resource/service-addon.resource.service';
import {VehicleResource, VehicleResourceService} from '../model/resource/vehicle.resource.service';
import {VehicleMileageResource, VehicleMileageResourceService} from '../model/resource/vehicle-mileage.resource.service';
import {DocumentsFilter} from '../model/documentsFilter';
import {Observable} from 'rxjs';
import {DocumentCollection, DocumentResource} from 'ngx-jsonapi';
import {UserService} from './user.service';
import {IDataCollection} from 'ngx-jsonapi/interfaces/data-collection';
import {IDocumentResource} from 'ngx-jsonapi/interfaces/data-object';
import {subscribeOn} from 'rxjs/operators';
import {ProfileResource, ProfileResourceService} from '../model/resource/profile.resource.service';
import {environment} from '../../environments/environment';
import {ProfilesFilter} from '../model/profilesFilter';
import {ServiceAddonDictionaryResource} from '../model/resource/service-addon-dictionary.resource.service';

@Injectable()
export class ProfileService implements RestService<ProfileResource> {

  constructor(private profileResourceService: ProfileResourceService, private http: HttpClient, private userService: UserService) {
    profileResourceService.register();
  }

  findByPhoneOrEmail(search: string): Observable<DocumentCollection<ProfileResource>> {
    const params = {
      search
    };

    return new Observable<DocumentCollection<ProfileResource>>( (subscriber) => {
      this.http.get<IDataCollection>(`${environment.getApiUrl()}external/profiles/search`, {params}).subscribe( (data) => {
        const collection = this.profileResourceService.newCollection();
        collection.fill( data );
        subscriber.next( collection );
        subscriber.complete();
      }, (error) => {
        subscriber.error( error );
        subscriber.complete();
      } );
    } );
  }

  getAll(filter: ProfilesFilter): Observable<DocumentCollection<ProfileResource>> {
    return this.profileResourceService.all({
      beforepath: `${environment.getBeforeUrl()}/external`,
      sort: [`${filter.direction === 'desc' ? '-' : ''}${filter.sort}`],
      page: { number: filter.page, size: filter.size },
      remotefilter: {
        phone: filter.phone != null ? filter.phone : '',
        email: filter.email != null ? filter.email : '',
        fio: filter.fio != null ? filter.fio : '',
        inn: filter.inn != null ? filter.inn : '',
        address: filter.address != null ? filter.address : ''
      }
    });
  }

  register(profile: ProfileResource, roleName: string): Observable<any> {
    return new Observable<any>( (subscriber) => {
      this.http.put<any>(
        `${environment.getApiUrl()}external/profiles/register?profileId=${profile.id}&roleName=${roleName}`, {}
        )
        .subscribe( () => {
          subscriber.next();
          subscriber.complete();
        }, (error) => {
          subscriber.error( error );
          subscriber.complete();
        } );
    } );
  }

  save(model: ProfileResource): Observable<ProfileResource> {
    return new Observable<ProfileResource>( (subscriber) => {
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

  saveClientProfile(serviceDocument: ServiceDocumentResource, clientRegister: boolean): Observable<ProfileResource> {
    return new Observable<ProfileResource>( (subscriber) => {
      const client: ProfileResource = serviceDocument.relationships.client.data;
      client.attributes.autoRegister = clientRegister;

      client.save({ beforepath: environment.getBeforeUrl() }).subscribe( (saved: IDocumentResource) => {
        // client.fill( saved );
        subscriber.next(client);
        subscriber.complete();
      }, ( error ) => {
        subscriber.error( error );
        subscriber.complete();
      } );
    } );
  }

  saveExecutorProfile(serviceDocument: ServiceDocumentResource): Observable<ProfileResource> {
    return new Observable<ProfileResource>( (subscriber) => {
      const executor: ProfileResource = serviceDocument.relationships.executor.data;

      executor.save({ beforepath: environment.getBeforeUrl() }).subscribe( (saved: IDocumentResource) => {
        // executor.fill( saved );
        subscriber.next(executor);
        subscriber.complete();
      }, ( error ) => {
        subscriber.error( error );
        subscriber.complete();
      } );
    } );
  }

  getAllClients(): Observable<DocumentCollection<ProfileResource>> {
    return new Observable<DocumentCollection<ProfileResource>>( (subscriber) => {
      this.http.get<IDataCollection>(`${environment.getApiUrl()}external/profiles/clients`).subscribe( (data) => {
        const collection = this.profileResourceService.newCollection();
        collection.fill( data );
        subscriber.next( collection );
        subscriber.complete();
      }, (error) => {
        subscriber.error( error );
        subscriber.complete();
      } );
    } );
  }

  getAllExecutors(): Observable<DocumentCollection<ProfileResource>> {
    return new Observable<DocumentCollection<ProfileResource>>( (subscriber) => {
      this.http.get<IDataCollection>(`${environment.getApiUrl()}external/profiles/executors`).subscribe( (data) => {
        const collection = this.profileResourceService.newCollection();
        collection.fill( data );
        subscriber.next( collection );
        subscriber.complete();
      }, (error) => {
        subscriber.error( error );
        subscriber.complete();
      } );
    } );
  }

  delete(model: ProfileResource): Observable<void> {
    return this.profileResourceService.delete(model.id, {
      beforepath: environment.getBeforeUrl()
    });
  }

}
