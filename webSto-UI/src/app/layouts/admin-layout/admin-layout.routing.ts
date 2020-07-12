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
  // { path: 'reports',        component: ReportsComponent },
  // { path: 'event-messages', component: EventMessagesComponent },
  // { path: 'balance',        component: BalanceComponent },
  // { path: 'subscription',   component: SubscriptionComponent }
];
