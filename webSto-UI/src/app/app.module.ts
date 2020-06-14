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
import {EventMessageResponseService} from './api/eventMessageResponse.service';
// import {DocumentResponseController} from './controller/document-response.controller';
import {EventMessageController} from './controller/event-message.controller';
import {UserController} from './controller/user.controller';
import {InfobarService} from './api/infobar.service';
import {PaymentService} from './api/payment.service';

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
    EventMessageResponseService,
    WebSocketService,
    // DocumentResponseController,
    EventMessageController,
    UserController,
    InfobarService,
    PaymentService,
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
