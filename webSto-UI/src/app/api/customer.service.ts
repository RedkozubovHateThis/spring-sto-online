import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
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
import {CustomerResource, CustomerResourceService} from '../model/resource/customer.resource.service';
import {environment} from '../../environments/environment';

@Injectable()
export class CustomerService {

  constructor(private customerResourceService: CustomerResourceService, private http: HttpClient, private userService: UserService) {
    customerResourceService.register();
  }

  findByPhoneOrEmail(search: string): Observable<DocumentCollection<CustomerResource>> {
    return this.customerResourceService.all({
      beforepath: `${environment.getBeforeUrl()}/external`,
      remotefilter: {
        phone: search,
        email: search,
        fio: search,
        inn: search
      }
    });
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

}
