import {Injectable} from '@angular/core';
import {Autoregister, DocumentCollection, DocumentResource, Resource, Service} from 'ngx-jsonapi';
import {UserResource} from './user.resource.service';
import {ServiceWorkResource} from './service-work.resource.service';
import {ServiceAddonResource} from './service-addon.resource.service';
import {VehicleResource} from './vehicle.resource.service';
import {VehicleMileageResource} from './vehicle-mileage.resource.service';
import {ProfileResource} from './profile.resource.service';
import {CustomerResource} from './customer.resource.service';
import IStatus from '../IStatus';

const statuses: IStatus[] = [
    {
      name: 'Заявка',
      id: 'CREATED',
      style: 'danger'
    },
    {
      name: 'В работе',
      id: 'IN_WORK',
      style: 'warning'
    },
    {
      name: 'Оформлен',
      id: 'COMPLETED',
      style: 'success'
    }
];
const paidStatuses: IStatus[] = [
    {
      name: 'Не оплачен',
      id: 'NOT_PAID',
      style: 'danger'
    },
    {
      name: 'Оплачен',
      id: 'PAID',
      style: 'success'
    }
];

export class ServiceDocumentResource extends Resource {
  public attributes = {
    number: null,
    startDate: new Date().getTime(),
    endDate: null,
    status: 'CREATED',
    paidStatus: 'NOT_PAID',
    deleted: false,
    cost: null,
    reason: null,
    clientIsCustomer: true,
    masterFio: null
  };

  public relationships = {
    serviceWorks: new DocumentCollection<ServiceWorkResource>(),
    serviceAddons: new DocumentCollection<ServiceAddonResource>(),
    executor: new DocumentResource<ProfileResource>(),
    client: new DocumentResource<ProfileResource>(),
    customer: new DocumentResource<CustomerResource>(),
    vehicle: new DocumentResource<VehicleResource>(),
    vehicleMileage: new DocumentResource<VehicleMileageResource>(),
  };

  get statusRus(): string {
    const { status } = this.attributes;

    if ( !status ) return 'Не указан';
    const currentStatus = statuses.find( (eachStatus) => eachStatus.id === status );
    if ( currentStatus )
      return currentStatus.name;

    return 'Не найден';
  }
  get paidStatusRus(): string {
    const { paidStatus } = this.attributes;

    if ( !paidStatus ) return 'Не указан';
    const currentStatus = paidStatuses.find( (eachStatus) => eachStatus.id === paidStatus );
    if ( currentStatus )
      return currentStatus.name;

    return 'Не найден';
  }

  get statusStyle(): string {
    const { status } = this.attributes;

    if ( !status ) return 'warn';
    const currentStatus = statuses.find( (eachStatus) => eachStatus.id === status );
    if ( currentStatus )
      return currentStatus.style;

    return 'warn';
  }
  get paidStatusStyle(): string {
    const { paidStatus } = this.attributes;

    if ( !paidStatus ) return 'warn';
    const currentStatus = paidStatuses.find( (eachStatus) => eachStatus.id === paidStatus );
    if ( currentStatus )
      return currentStatus.style;

    return 'warn';
  }

  get allStatuses(): IStatus[] {
    return statuses;
  }
  get allPaidStatuses(): IStatus[] {
    return paidStatuses;
  }

  public isCustomerCreatedByUser(user: UserResource): boolean {
    if ( !this.relationships.customer.data ) return false;

    return this.relationships.customer.data.isCreatedByUser( user );
  }

  public isClientCreatedByUser(user: UserResource): boolean {
    if ( !this.relationships.client.data ) return false;

    return this.relationships.client.data.isCreatedByUser( user );
  }

  public isVehicleCreatedByUser(user: UserResource): boolean {
    if ( !this.relationships.vehicle.data ) return false;

    return this.relationships.vehicle.data.isCreatedByUser( user );
  }
}

@Injectable()
@Autoregister()
export class ServiceDocumentResourceService extends Service<ServiceDocumentResource> {
  public resource = ServiceDocumentResource;
  public type = 'serviceDocument';
  public path = 'serviceDocuments';
}
