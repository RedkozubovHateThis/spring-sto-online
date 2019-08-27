import { Routes } from '@angular/router';

import { DashboardComponent } from '../../pages/dashboard/dashboard.component';
import { UserProfileComponent } from '../../pages/user-profile/user-profile.component';
import { DocumentsComponent } from '../../pages/documents/documents.component';
import { DocumentComponent } from "../../pages/document/document.component";
import { UsersComponent } from "../../pages/users/users.component";
import { UserComponent } from 'src/app/pages/user/user.component';
import { UserEditComponent } from 'src/app/pages/user-edit/userEdit.component';

export const AdminLayoutRoutes: Routes = [
  { path: 'dashboard',      component: DashboardComponent },
  { path: 'user-profile',   component: UserProfileComponent },
  { path: 'documents',      component: DocumentsComponent },
  { path: 'users',          component: UsersComponent },
  { path: 'users/:id',      component: UserComponent },
  { path: 'users/:id/edit', component: UserEditComponent },
  { path: 'documents/:id',  component: DocumentComponent }
];
