import {NgModule} from '@angular/core';
import {HttpClientModule} from '@angular/common/http';
import {RouterModule} from '@angular/router';
import {CommonModule} from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

import {ClipboardModule} from 'ngx-clipboard';

import {AdminLayoutRoutes} from './admin-layout.routing';
import {IconsComponent} from '../../pages/icons/icons.component';
import {MapsComponent} from '../../pages/maps/maps.component';
import {UserProfileComponent} from '../../pages/user-profile/user-profile.component';
import {DocumentsComponent} from '../../pages/documents/documents.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {DocumentComponent} from '../../pages/document/document.component';
import {UsersComponent} from '../../pages/users/users.component';
import {UserComponent} from '../../pages/user/user.component';
import {UserEditComponent} from 'src/app/pages/user-edit/user-edit.component';
import {FileSelectDirective} from 'ng2-file-upload';
import {ComponentsModule} from '../../components/components.module';
import {ReportsComponent} from '../../pages/reports/reports.component';
import {DpDatePickerModule} from 'ng2-date-picker';
import {EventMessagesComponent} from '../../pages/event-messages/event-messages.component';
import {UserAddComponent} from '../../pages/user-add/user-add.component';
import {PdfViewerModule} from 'ng2-pdf-viewer';
import {BalanceComponent} from '../../pages/balance/balance.component';
import {SubscriptionComponent} from '../../pages/subscription/subscription.component';

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(AdminLayoutRoutes),
    FormsModule,
    HttpClientModule,
    NgbModule,
    ClipboardModule,
    ComponentsModule,
    DpDatePickerModule,
    ReactiveFormsModule,
    PdfViewerModule
  ],
  declarations: [
    UserProfileComponent,
    // DocumentsComponent,
    // DocumentComponent,
    IconsComponent,
    MapsComponent,
    UsersComponent,
    UserComponent,
    UserEditComponent,
    FileSelectDirective,
    // ReportsComponent,
    EventMessagesComponent,
    UserAddComponent,
    BalanceComponent,
    SubscriptionComponent
  ]
})

export class AdminLayoutModule {}
