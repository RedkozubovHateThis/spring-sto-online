import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {RestService} from './rest.service';
import {ServiceDocumentResource} from '../model/resource/service-document.resource.service';
import {Observable} from 'rxjs';
import {DocumentCollection} from 'ngx-jsonapi';
import {UserService} from './user.service';
import {IDataCollection} from 'ngx-jsonapi/interfaces/data-collection';
import {IDocumentResource} from 'ngx-jsonapi/interfaces/data-object';
import {CustomerResource, CustomerResourceService} from '../model/resource/customer.resource.service';
import {environment} from '../../environments/environment';
import {CustomersFilter} from '../model/customersFilter';

@Injectable()
export class CustomerService implements RestService<CustomerResource> {

  constructor(private customerResourceService: CustomerResourceService, private http: HttpClient, private userService: UserService) {
    customerResourceService.register();
  }

  getAll(filter: CustomersFilter): Observable<DocumentCollection<CustomerResource>> {
    const params = {
      name: filter.name != null ? filter.name : '',
      inn: filter.inn != null ? filter.inn : '',
      phone: filter.phone != null ? filter.phone : '',
      email: filter.email != null ? filter.email : '',
    };
    return this.customerResourceService.all({
      beforepath: `${environment.getBeforeUrl()}/external`,
      sort: [`${filter.direction === 'desc' ? '-' : ''}${filter.sort}`],
      page: { number: filter.page, size: filter.size },
      remotefilter: params
    });
  }

  findByPhoneOrEmail(search: string): Observable<DocumentCollection<CustomerResource>> {
    return new Observable<DocumentCollection<CustomerResource>>( (subscriber) => {
      this.http.get<IDataCollection>(`${environment.getApiUrl()}external/customers/search`, { params: { search } }).subscribe( (data) => {
        const collection = this.customerResourceService.newCollection();
        collection.fill( data );
        subscriber.next( collection );
        subscriber.complete();
      }, (error) => {
        subscriber.error( error );
        subscriber.complete();
      } );
    } );
  }

  save(model: CustomerResource): Observable<CustomerResource> {
    return new Observable<CustomerResource>( (subscriber) => {
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

  saveCustomer(serviceDocument: ServiceDocumentResource): Observable<CustomerResource> {
    return new Observable<CustomerResource>( (subscriber) => {
      const customer: CustomerResource = serviceDocument.relationships.customer.data;
      if ( serviceDocument.attributes.clientIsCustomer ) {
        subscriber.next(customer);
        subscriber.complete();
        return;
      }

      customer.save({ beforepath: environment.getBeforeUrl() }).subscribe( (saved: IDocumentResource) => {
        subscriber.next(customer);
        subscriber.complete();
      }, ( error ) => {
        subscriber.error( error );
        subscriber.complete();
      } );
    } );
  }

  delete(model: CustomerResource): Observable<void> {
    return this.customerResourceService.delete(model.id, {
      beforepath: environment.getBeforeUrl()
    });
  }

}
