import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from './sidebar/sidebar.component';
import { NavbarComponent } from './navbar/navbar.component';
import { FooterComponent } from './footer/footer.component';
import { RouterModule } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import {InfobarComponent} from "./infobar/infobar.component";
import {ImgPreviewComponent} from './imgPreview/img-preview.component';
import {DocumentFilterComponent} from './documentFilter/document-filter.component';
import {FormsModule} from '@angular/forms';
import { EventMessageFilterComponent } from './eventMessageFilter/event-message-filter.component';
import {ReportButtonComponent} from './report-button/report-button.component';
import {PdfViewerModule} from 'ng2-pdf-viewer';
import {PasswordChangeButtonComponent} from './password-change-button/password-change-button.component';
import {DpDatePickerModule} from 'ng2-date-picker';

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
    PasswordChangeButtonComponent
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
    PasswordChangeButtonComponent
  ]
})
export class ComponentsModule { }
