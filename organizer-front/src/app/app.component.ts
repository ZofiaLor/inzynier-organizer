import { Component, OnInit } from '@angular/core';
import { RouterOutlet, RouterModule, Router } from '@angular/router';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatMenuModule} from '@angular/material/menu';
import { User } from './model/user';
import { StorageService } from './service/storage.service';
import { AuthService } from './service/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterModule, MatIconModule, MatButtonModule, MatToolbarModule, MatTooltipModule, MatMenuModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent implements OnInit{
  title = 'organizer-front';
  isLoggedIn = false;
  isAdmin = false;
  currentUser?: User;

  constructor (private readonly storageService: StorageService, private readonly authService: AuthService, private readonly router: Router) {}
  
  ngOnInit(): void {
    this.isLoggedIn = this.storageService.isLoggedIn();
    if (this.isLoggedIn) {
      this.currentUser = this.storageService.getUser();
      this.isAdmin = this.currentUser?.role == "ROLE_ADMIN";
    }
  }

  onLogout(): void {
    this.authService.logout().subscribe({
      next: () => {
        this.storageService.clean();
        this.router.navigate(['auth/login']).then(() => {
          window.location.reload();
        });
        // https://stackoverflow.com/questions/53569884/angular-router-navigate-then-reload
        
      },
      error: (err) => {
        console.log(err);
        if(err?.error?.text === 'Success'){
          this.storageService.clean();
          this.router.navigate(['auth/login']).then(() => {
            window.location.reload();
          });
          
        }
      }
    })
  }

  onLogin(): void {
    this.router.navigate(['auth/login']);
  }
  
}
