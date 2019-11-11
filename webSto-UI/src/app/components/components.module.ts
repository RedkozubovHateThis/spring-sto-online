import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from './sidebar/sidebar.component';
import { NavbarComponent } from './navbar/navbar.component';
import { FooterComponent } from './footer/footer.component';
import { RouterModule } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import {InfobarComponent} from "./infobar/infobar.component";
import {ImgPreviewComponent} from './img-preview/img-preview.component';
import {DocumentFilterComponent} from './document-filter/document-filter.component';
import {FormsModule} from '@angular/forms';
import { EventMessageFilterComponent } from './event-message-filter/event-message-filter.component';
import {ReportButtonComponent} from './report-button/report-button.component';
import {PdfViewerModule} from 'ng2-pdf-viewer';
import {PasswordChangeButtonComponent} from './password-change-button/password-change-button.component';
import {DpDatePickerModule} from 'ng2-date-picker';
import {UserFilterComponent} from './user-filter/user-filter.component';
import {PaginationComponent} from './pagination/pagination.component';
import {DeleteButtonComponent} from './delete-button/delete-button.component';

@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    NgbModule,
    FormsModule,
    PdfViewerModule,
    DpDatePickerModule
  ],
  declarations: [
    FooterComponent,
    NavbarComponent,
    SidebarComponent,
    InfobarComponent,
    ImgPreviewComponent,
    DocumentFilterComponent,
    EventMessageFilterComponent,
    ReportButtonComponent,
    PasswordChangeButtonComponent,
    UserFilterComponent,
    PaginationComponent,
    DeleteButtonComponent
  ],
  exports: [
    FooterComponent,
    NavbarComponent,
    SidebarComponent,
    InfobarComponent,
    ImgPreviewComponent,
    DocumentFilterComponent,
    EventMessageFilterComponent,
    ReportButtonComponent,
    PasswordChangeButtonComponent,
    UserFilterComponent,
    PaginationComponent,
    DeleteButtonComponent
  ]
})
export class ComponentsModule { }
