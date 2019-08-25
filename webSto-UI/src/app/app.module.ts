import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { RouterModule, Router } from '@angular/router';

import { AppComponent } from './app.component';
import { AdminLayoutComponent } from './layouts/admin-layout/admin-layout.component';
import { AuthLayoutComponent } from './layouts/auth-layout/auth-layout.component';

import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { AppRoutingModule } from './app.routing';
import { ComponentsModule } from './components/components.module';
import {UserService} from "./api/user.service";
import {BrowserModule} from "@angular/platform-browser";
import {LoginComponent} from "./pages/login/login.component";
import {RegisterComponent} from "./pages/register/register.component";
import {DocumentResponseService} from "./api/documentResponse.service";

import { registerLocaleData } from '@angular/common';
import localeRu from '@angular/common/locales/ru';
import { ErrorInterceptor } from './variables/error.interceptor';
import {ClientResponseService} from "./api/clientResponse.service";
registerLocaleData(localeRu, 'ru');

@NgModule({
  imports: [
    BrowserAnimationsModule,
    FormsModule,
    HttpClientModule,
    ComponentsModule,
    NgbModule,
    RouterModule,
    AppRoutingModule,
    BrowserModule,
    RouterModule,
    ReactiveFormsModule,
    HttpClientModule
  ],
  declarations: [
    AppComponent,
    AdminLayoutComponent,
    AuthLayoutComponent,
    AppComponent,
    //LoginComponent,
   // RegisterComponent
  ],
  providers: [UserService, DocumentResponseService, ClientResponseService,
    {
      provide: HTTP_INTERCEPTORS,
      useFactory: function(userService:UserService) {
        return new ErrorInterceptor(userService);
      },
      multi: true,
      deps: [UserService]
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
