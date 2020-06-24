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
// import {DocumentResponseController} from './controller/document-response.controller';
import {EventMessageController} from './controller/event-message.controller';
import {UserController} from './controller/user.controller';
import {InfobarService} from './api/infobar.service';
import {PaymentService} from './api/payment.service';
import { UserResourceService } from './model/resource/user.resource.service';
import {UserRoleResourceService} from './model/resource/user-role.resource.service';
import {NgxJsonapiModule} from 'ngx-jsonapi';
import {SubscriptionResourceService} from './model/resource/subscription.resource.service';
import {SubscriptionTypeResourceService} from './model/resource/subscription-type.resource.service';
import {PaymentRecordResourceService} from './model/resource/payment-record.resource.service';
import {EventMessageResourceService} from './model/resource/event-message.resource.service';

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
      url: 'https://local.buromotors.ru:8080/api/'
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
    // DocumentResponseController,
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
