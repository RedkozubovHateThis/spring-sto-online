import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {CommonModule} from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

import {ClipboardModule} from 'ngx-clipboard';

import {AdminLayoutRoutes} from './admin-layout.routing';
import {IconsComponent} from '../../pages/icons/icons.component';
import {MapsComponent} from '../../pages/maps/maps.component';
import {UserProfileComponent} from '../../pages/user-profile/user-profile.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {UsersComponent} from '../../pages/users/users.component';
import {UserComponent} from '../../pages/user/user.component';
import {UserEditComponent} from 'src/app/pages/user-edit/user-edit.component';
import {FileSelectDirective} from 'ng2-file-upload';
import {ComponentsModule} from '../../components/components.module';
import {DpDatePickerModule} from 'ng2-date-picker';
import {EventMessagesComponent} from '../../pages/event-messages/event-messages.component';
import {UserAddComponent} from '../../pages/user-add/user-add.component';
import {PdfViewerModule} from 'ng2-pdf-viewer';
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

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(AdminLayoutRoutes),
    FormsModule,
    NgbModule,
    ClipboardModule,
    ComponentsModule,
    DpDatePickerModule,
    ReactiveFormsModule,
    PdfViewerModule
  ],
  declarations: [
    UserProfileComponent,
    DocumentsComponent,
    DocumentComponent,
    DocumentEditComponent,
    DocumentAddComponent,
    IconsComponent,
    MapsComponent,
    UsersComponent,
    UserComponent,
    UserEditComponent,
    FileSelectDirective,
    ReportsComponent,
    EventMessagesComponent,
    UserAddComponent,
    BalanceComponent,
    SubscriptionComponent,
    ServiceWorkDictionariesComponent,
    ServiceAddonDictionariesComponent,
    VehicleDictionariesComponent,
    VehiclesComponent,
    CustomersComponent
  ]
})

export class AdminLayoutModule {}
