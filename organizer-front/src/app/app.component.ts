import { Component, OnDestroy, OnInit } from '@angular/core';
import { RouterOutlet, RouterModule, Router } from '@angular/router';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatMenuModule} from '@angular/material/menu';
import {MatBadgeModule} from '@angular/material/badge';
import { User } from './model/user';
import { StorageService } from './service/storage.service';
import { AuthService } from './service/auth.service';
import { NotificationService } from './service/notification.service';
import { Subject, takeUntil } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterModule, MatIconModule, MatButtonModule, MatToolbarModule, MatTooltipModule, MatMenuModule, MatBadgeModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent implements OnInit, OnDestroy{
  title = 'organizer-front';
  isLoggedIn = false;
  isAdmin = false;
  currentUser?: User;
  unreadNotifs = 0;
  // https://developer.mozilla.org/en-US/docs/Web/API/Window/setInterval
  readonly intervalID = setInterval(() => this.getUnreadNotifs(), 2000);
  private readonly _destroy$ = new Subject<void>();

  constructor (private readonly storageService: StorageService, private readonly authService: AuthService, private readonly notifService: NotificationService, 
    private readonly router: Router, private snackBar: MatSnackBar) {}
  
  ngOnInit(): void {
    this.isLoggedIn = this.storageService.isLoggedIn();
    if (this.isLoggedIn) {
      this.currentUser = this.storageService.getUser();
      this.isAdmin = this.currentUser?.role == "ROLE_ADMIN";
      this.getUnreadNotifs();
    }
  }

  ngOnDestroy(): void {
    clearInterval(this.intervalID);
  }

  onLogout(): void {
    this.isLoggedIn = false;
    this.authService.logout().subscribe({
      next: () => {
        this.storageService.clean();
        this.router.navigate(['/']).then(() => {
          window.location.reload();
        });
        // https://stackoverflow.com/questions/53569884/angular-router-navigate-then-reload
        
      },
      error: (err) => {
        if(err?.error?.text === 'Success'){
          this.storageService.clean();
          this.router.navigate(['/']).then(() => {
            window.location.reload();
          });
        } else {
          this.isLoggedIn = true;
          this.snackBar.open("Something went wrong...", undefined, {duration: 5000});
        }
      }
    })
  }

  onLogin(): void {
    this.router.navigate(['auth/login']);
  }

  getUnreadNotifs(): void {
    if (!this.isLoggedIn) return;
    this.notifService.sendNotifs().pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.notifService.getCurrentUsersNotifsByRead(false).pipe(takeUntil(this._destroy$)).subscribe({
          next: resp => {
            this.unreadNotifs = resp.body!.length;
          },
          error: err => {
            this.handleExpiredToken();
          }
        })
      },
      error: err => {
        this.handleExpiredToken();
      }
    })
    
  }

  handleExpiredToken(): void {
    this.authService.refreshToken().pipe(takeUntil(this._destroy$)).subscribe({
      error: err => {
        if (err.status != 200) {
          this.onLogout();
        }
      }
    })
  }
  
}
