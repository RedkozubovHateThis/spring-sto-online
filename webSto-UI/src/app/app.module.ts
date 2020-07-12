import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {Router, RouterModule} from '@angular/router';

import {AppComponent} from './app.component';
import {AdminLayoutComponent} from './layouts/admin-layout/admin-layout.component';
import {AuthLayoutComponent} from './layouts/auth-layout/auth-layout.component';

import {NgbDropdownModule, NgbModule} from '@ng-bootstrap/ng-bootstrap';

import {AppRoutingModule} from './app.routing';
import {ComponentsModule} from './components/components.module';
import {UserService} from './api/user.service';
import {BrowserModule} from '@angular/platform-browser';

import {DatePipe, registerLocaleData} from '@angular/common';
import localeRu from '@angular/common/locales/ru';
import {ErrorInterceptor} from './variables/error.interceptor';
import {WebSocketService} from './api/webSocket.service';
import {ToastrModule} from 'ngx-toastr';
import {EventMessageService} from './api/event-message.service';
import {EventMessageController} from './controller/event-message.controller';
import {UserController} from './controller/user.controller';
import {InfobarService} from './api/infobar.service';
import {PaymentService} from './api/payment.service';
import {UserResourceService} from './model/resource/user.resource.service';
import {UserRoleResourceService} from './model/resource/user-role.resource.service';
import {NgxJsonapiModule} from 'ngx-jsonapi';
import {SubscriptionResourceService} from './model/resource/subscription.resource.service';
import {SubscriptionTypeResourceService} from './model/resource/subscription-type.resource.service';
import {PaymentRecordResourceService} from './model/resource/payment-record.resource.service';
import {EventMessageResourceService} from './model/resource/event-message.resource.service';
import {DocumentController} from './controller/document.controller';
import {DocumentService} from './api/document-service.service';
import {ServiceDocumentResourceService} from './model/resource/service-document.resource.service';
import {ServiceWorkResourceService} from './model/resource/service-work.resource.service';
import {ServiceAddonResourceService} from './model/resource/service-addon.resource.service';
import {VehicleResourceService} from './model/resource/vehicle.resource.service';
import {VehicleMileageResourceService} from './model/resource/vehicle-mileage.resource.service';
import {ProfileResourceService} from './model/resource/profile.resource.service';
import {ServiceWorkService} from './api/service-work.service';
import {ServiceAddonService} from './api/service-addon.service';
import {VehicleService} from './api/vehicle.service';
import {ProfileService} from './api/profile.service';
import {VehicleDictionaryResourceService} from './model/resource/vehicle-dictionary.resource.service';
import {ServiceAddonDictionaryResourceService} from './model/resource/service-addon-dictionary.resource.service';
import {
  ServiceWorkDictionaryResource,
  ServiceWorkDictionaryResourceService
} from './model/resource/service-work-dictionary.resource.service';
import {VehicleDictionaryService} from './api/vehicle.dictionary.service';
import {ServiceWorkDictionaryService} from './api/service-work.dictionary.service';
import {ServiceAddonDictionaryService} from './api/service-addon.dictionary.service';
import {environment} from '../environments/environment';
import {CustomerResourceService} from './model/resource/customer.resource.service';
import {CustomerService} from './api/customer.service';
import {ServiceWorkDictionaryController} from './controller/service-work-dictionary.controller';
import {ServiceAddonDictionaryController} from './controller/service-addon-dictionary.controller';

registerLocaleData(localeRu, 'ru');

@NgModule({
  imports: [
    BrowserAnimationsModule,
    FormsModule,
    HttpClientModule,
    ComponentsModule,
    NgbModule,
    NgbDropdownModule,
    RouterModule,
    AppRoutingModule,
    BrowserModule,
    RouterModule,
    ReactiveFormsModule,
    HttpClientModule,
    ToastrModule.forRoot({
      timeOut: 3000
    }),
    NgxJsonapiModule.forRoot({
      url: environment.baseUrl
    })
  ],
  declarations: [
    AppComponent,
    AdminLayoutComponent,
    AuthLayoutComponent,
    AppComponent
    // LoginComponent,
    // RegisterComponent
  ],
  providers: [
    DatePipe,
    UserService,
    EventMessageService,
    WebSocketService,
    DocumentController,
    DocumentService,
    EventMessageController,
    UserController,
    InfobarService,
    PaymentService,
    UserResourceService,
    UserRoleResourceService,
    SubscriptionResourceService,
    SubscriptionTypeResourceService,
    PaymentRecordResourceService,
    EventMessageResourceService,
    ServiceDocumentResourceService,
    ServiceWorkResourceService,
    ServiceAddonResourceService,
    VehicleResourceService,
    VehicleMileageResourceService,
    ProfileResourceService,
    ServiceWorkService,
    ServiceAddonService,
    VehicleService,
    ProfileService,
    VehicleDictionaryResourceService,
    ServiceWorkDictionaryResourceService,
    ServiceAddonDictionaryResourceService,
    VehicleDictionaryService,
    ServiceWorkDictionaryService,
    ServiceAddonDictionaryService,
    ServiceWorkDictionaryController,
    ServiceAddonDictionaryController,
    CustomerResourceService,
    CustomerService,
    {
      provide: HTTP_INTERCEPTORS,
      useFactory(userService: UserService, router: Router) {
        return new ErrorInterceptor(userService, router);
      },
      multi: true,
      deps: [UserService, Router]
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
