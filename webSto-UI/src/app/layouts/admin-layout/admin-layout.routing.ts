import {Routes} from '@angular/router';

import {UserProfileComponent} from '../../pages/user-profile/user-profile.component';
import {UsersComponent} from '../../pages/users/users.component';
import {UserComponent} from 'src/app/pages/user/user.component';
import {UserEditComponent} from 'src/app/pages/user-edit/user-edit.component';
import {EventMessagesComponent} from '../../pages/event-messages/event-messages.component';
import {UserAddComponent} from '../../pages/user-add/user-add.component';
import {BalanceComponent} from '../../pages/balance/balance.component';
import {SubscriptionComponent} from '../../pages/subscription/subscription.component';
import {DocumentsComponent} from '../../pages/documents/documents.component';
import {DocumentComponent} from '../../pages/document/document.component';
import {DocumentEditComponent} from '../../pages/document-edit/document-edit.component';
import {DocumentAddComponent} from '../../pages/document-add/document-add.component';
import {ServiceWorkDictionariesComponent} from '../../pages/service-work-dictionaries/service-work-dictionaries.component';
import {ServiceAddonDictionariesComponent} from '../../pages/service-addon-dictionaries/service-addon-dictionaries.component';
import {VehiclesComponent} from '../../pages/vehicles/vehicles.component';
import {VehicleDictionariesComponent} from '../../pages/vehicles-dictionaries/vehicle-dictionaries.component';
import {CustomersComponent} from '../../pages/customers/customers.component';
import {ReportsComponent} from '../../pages/reports/reports.component';
import {ProfilesComponent} from '../../pages/profiles/profiles.component';
import {AdEntitiesComponent} from '../../pages/ad-entities/ad-entities.component';
import {PaymentRecordsComponent} from '../../pages/payment-records/payment-records.component';

export const AdminLayoutRoutes: Routes = [
  { path: 'user-profile',   component: UserProfileComponent },
  { path: 'documents',      component: DocumentsComponent },
  { path: 'users',          component: UsersComponent },
  { path: 'users/add',      component: UserAddComponent },
  { path: 'users/:id',      component: UserComponent },
  { path: 'users/:id/edit', component: UserEditComponent },
  { path: 'documents/add',  component: DocumentAddComponent },
  { path: 'documents/:id',  component: DocumentComponent },
  { path: 'documents/:id/edit',  component: DocumentEditComponent },
  { path: 'serviceWorks',  component: ServiceWorkDictionariesComponent },
  { path: 'serviceAddons',  component: ServiceAddonDictionariesComponent },
  { path: 'vehiclesAdded',  component: VehiclesComponent },
  { path: 'vehicles',  component: VehicleDictionariesComponent },
  { path: 'customers',  component: CustomersComponent },
  { path: 'reports',        component: ReportsComponent },
  { path: 'profiles',        component: ProfilesComponent },
  // { path: 'event-messages', component: EventMessagesComponent },
  { path: 'balance',        component: BalanceComponent },
  { path: 'subscription',   component: SubscriptionComponent },
  { path: 'adEntities',   component: AdEntitiesComponent },
  { path: 'payment-records',   component: PaymentRecordsComponent }
];
