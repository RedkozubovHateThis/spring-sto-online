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
import {EventMessagesComponent} from './eventMessages/event-messages.component';

@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    NgbModule,
    FormsModule
  ],
  declarations: [
    FooterComponent,
    NavbarComponent,
    SidebarComponent,
    InfobarComponent,
    ImgPreviewComponent,
    DocumentFilterComponent,
    EventMessagesComponent
  ],
  exports: [
    FooterComponent,
    NavbarComponent,
    SidebarComponent,
    InfobarComponent,
    ImgPreviewComponent,
    DocumentFilterComponent,
    EventMessagesComponent
  ]
})
export class ComponentsModule { }
