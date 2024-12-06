import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { ProfileComponent } from './profile/profile.component';
import { UserPanelComponent } from './admin-panel/user-panel/user-panel.component';
import { BrowserComponent } from './browser/browser.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';

export const routes: Routes = [
    {path: 'auth/login', component: LoginComponent},
    {path: 'auth/register', component: RegisterComponent},
    {path: 'profile', component: ProfileComponent},
    {path: 'admin/users', component: UserPanelComponent},
    {path: '', component: BrowserComponent},
    {path: 'file/:id', component: BrowserComponent},
    {path: 'new/:type/:dir', component: BrowserComponent},
    {path: 'dir/:id', component: BrowserComponent},
    {path: '**', component: PageNotFoundComponent},
];
