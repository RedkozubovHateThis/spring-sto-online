import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ClipboardModule } from 'ngx-clipboard';

import { AdminLayoutRoutes } from './admin-layout.routing';
import { DashboardComponent } from '../../pages/dashboard/dashboard.component';
import { IconsComponent } from '../../pages/icons/icons.component';
import { MapsComponent } from '../../pages/maps/maps.component';
import { UserProfileComponent } from '../../pages/user-profile/user-profile.component';
import { DocumentsComponent } from '../../pages/documents/documents.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import {DocumentComponent} from "../../pages/document/document.component";
import {UsersComponent} from "../../pages/users/users.component";
import {UserComponent} from "../../pages/user/user.component";
import { UserEditComponent } from 'src/app/pages/user-edit/userEdit.component';
// import { ToastrModule } from 'ngx-toastr';

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(AdminLayoutRoutes),
    FormsModule,
    HttpClientModule,
    NgbModule,
    ClipboardModule
  ],
  declarations: [
    DashboardComponent,
    UserProfileComponent,
    DocumentsComponent,
    DocumentComponent,
    IconsComponent,
    MapsComponent,
    UsersComponent,
    UserComponent,
    UserEditComponent
  ]
})

export class AdminLayoutModule {}
