import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {SidebarComponent} from './sidebar/sidebar.component';
import {NavbarComponent} from './navbar/navbar.component';
import {FooterComponent} from './footer/footer.component';
import {RouterModule} from '@angular/router';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {InfobarComponent} from './infobar/infobar.component';
import {DocumentFilterComponent} from './document-filter/document-filter.component';
import {FormsModule} from '@angular/forms';
import {ReportButtonComponent} from './report-button/report-button.component';
import {PdfViewerModule} from 'ng2-pdf-viewer';
import {PasswordChangeButtonComponent} from './password-change-button/password-change-button.component';
import {DpDatePickerModule} from 'ng2-date-picker';
import {UserFilterComponent} from './user-filter/user-filter.component';
import {PaginationComponent} from './pagination/pagination.component';
import {DeleteButtonComponent} from './delete-button/delete-button.component';
import {LoadingBarComponent} from './loading-bar/loading-bar.component';
import {RoleChangeButtonComponent} from './role-change-button/role-change-button.component';
import {DictionariesFilterComponent} from './dictionaries-filter/dictionaries-filter.component';
import {VehiclesFilterComponent} from './vehicles-filter/vehicles-filter.component';
import {CustomerFilterComponent} from './customer-filter/customer-filter.component';
import {DictionaryUpdateButtonComponent} from './dictionary-update-button/dictionary-update-button.component';
import {FileUploadModule} from 'ng2-file-upload';
import {ProfileFilterComponent} from './profile-filter/profile-filter.component';

@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    NgbModule,
    FormsModule,
    PdfViewerModule,
    DpDatePickerModule,
    FileUploadModule
  ],
  declarations: [
    FooterComponent,
    NavbarComponent,
    SidebarComponent,
    InfobarComponent,
    DocumentFilterComponent,
    ReportButtonComponent,
    PasswordChangeButtonComponent,
    UserFilterComponent,
    PaginationComponent,
    DeleteButtonComponent,
    LoadingBarComponent,
    RoleChangeButtonComponent,
    DictionariesFilterComponent,
    VehiclesFilterComponent,
    CustomerFilterComponent,
    DictionaryUpdateButtonComponent,
    ProfileFilterComponent
  ],
  exports: [
    FooterComponent,
    NavbarComponent,
    SidebarComponent,
    InfobarComponent,
    DocumentFilterComponent,
    ReportButtonComponent,
    PasswordChangeButtonComponent,
    UserFilterComponent,
    PaginationComponent,
    DeleteButtonComponent,
    LoadingBarComponent,
    RoleChangeButtonComponent,
    DictionariesFilterComponent,
    VehiclesFilterComponent,
    CustomerFilterComponent,
    DictionaryUpdateButtonComponent,
    ProfileFilterComponent
  ]
})
export class ComponentsModule { }
