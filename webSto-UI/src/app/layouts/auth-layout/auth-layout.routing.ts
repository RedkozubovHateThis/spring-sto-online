import {Routes} from '@angular/router';

import {LoginComponent} from '../../pages/login/login.component';
import {RegisterComponent} from '../../pages/register/register.component';
import {DemoComponent} from '../../pages/demo/demo.component';
import {RestoreComponent} from '../../pages/restore/restore.component';
import {ReportOpenComponent} from '../../pages/report-open/report-open.component';

export const AuthLayoutRoutes: Routes = [
    { path: 'login',          component: LoginComponent },
    { path: 'register',       component: RegisterComponent },
    { path: 'restore',        component: RestoreComponent },
    { path: 'demo',           component: DemoComponent },
    { path: 'report',         component: ReportOpenComponent }
];
