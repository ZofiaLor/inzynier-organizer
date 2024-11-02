import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { ProfileComponent } from './profile/profile.component';

export const routes: Routes = [
    {path: 'auth/login', component: LoginComponent},
    {path: 'auth/register', component: RegisterComponent},
    {path: 'profile', component: ProfileComponent}
];
