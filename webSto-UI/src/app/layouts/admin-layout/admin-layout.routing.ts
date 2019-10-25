import { Routes } from '@angular/router';

import { DashboardComponent } from '../../pages/dashboard/dashboard.component';
import { UserProfileComponent } from '../../pages/user-profile/user-profile.component';
import { DocumentsComponent } from '../../pages/documents/documents.component';
import { DocumentComponent } from "../../pages/document/document.component";
import { UsersComponent } from "../../pages/users/users.component";
import { UserComponent } from 'src/app/pages/user/user.component';
import { UserEditComponent } from 'src/app/pages/user-edit/userEdit.component';
import { ChatComponent } from '../../pages/chat/chat.component';
import {ReportsComponent} from '../../pages/reports/reports.component';
import {EventMessagesComponent} from '../../pages/event-messages/event-messages.component';
import {UserAddComponent} from '../../pages/user-add/user-add.component';

export const AdminLayoutRoutes: Routes = [
  { path: 'dashboard',      component: DashboardComponent },
  { path: 'chat',           component: ChatComponent },
  { path: 'user-profile',   component: UserProfileComponent },
  { path: 'documents',      component: DocumentsComponent },
  { path: 'users',          component: UsersComponent },
  { path: 'users/add',      component: UserAddComponent },
  { path: 'users/:id',      component: UserComponent },
  { path: 'users/:id/edit', component: UserEditComponent },
  { path: 'documents/:id',  component: DocumentComponent },
  { path: 'reports',        component: ReportsComponent },
  { path: 'event-messages', component: EventMessagesComponent }
];
