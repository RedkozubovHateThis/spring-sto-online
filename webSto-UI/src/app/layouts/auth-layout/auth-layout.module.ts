import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {CommonModule} from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {AuthLayoutRoutes} from './auth-layout.routing';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';

import {LoginComponent} from '../../pages/login/login.component';
import {RegisterComponent} from '../../pages/register/register.component';
import {DemoComponent} from '../../pages/demo/demo.component';
import {RestoreComponent} from '../../pages/restore/restore.component';
import {ReportOpenComponent} from '../../pages/report-open/report-open.component';
import {PdfViewerModule} from 'ng2-pdf-viewer';
import {GetAppComponent} from '../../pages/get-app/get-app.component';

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(AuthLayoutRoutes),
    FormsModule,
    NgbModule,
    ReactiveFormsModule,
    PdfViewerModule
  ],
  declarations: [
    LoginComponent,
    RegisterComponent,
    DemoComponent,
    RestoreComponent,
    ReportOpenComponent,
    GetAppComponent
  ]
})
export class AuthLayoutModule { }
