import { Component, OnInit } from '@angular/core';
import { TableBaseComponent } from '../../table-base/table-base.component';
import { MatCardModule } from '@angular/material/card';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatExpansionModule} from '@angular/material/expansion';
import { FlexLayoutModule } from '@angular/flex-layout';
import { UserService } from '../../service/user.service';
import { User } from '../../model/user';
import { Subject, takeUntil } from 'rxjs';
import { StorageService } from '../../service/storage.service';
import { AuthService } from '../../service/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-user-panel',
  standalone: true,
  imports: [TableBaseComponent, MatCardModule, MatIconModule, MatButtonModule, FlexLayoutModule, MatExpansionModule],
  templateUrl: './user-panel.component.html',
  styleUrl: './user-panel.component.scss'
})
export class UserPanelComponent implements OnInit{
  private readonly _destroy$ = new Subject<void>();
  userData: User[] = [];
  user?: User;
  columns: string[] = ["id", "username", "name", "email"];
  isCurrentUser: boolean = false;
  isAdminSelected: boolean = false;
  blockAccess = false;
  constructor (private userService: UserService, private storageService: StorageService, private authService: AuthService, private snackBar: MatSnackBar) {}
  ngOnInit(): void {
    this.userService.getAllUsers().pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.userData = resp.body!;
      },
      error: err => {
        if (err.status == 403) {
          this.blockAccess = true;
        } else {
          this.snackBar.open("Something went wrong...", undefined, {duration: 3000});
        }
      }
    })
  }

  selectUser(id: number): void {
    this.userService.getUserById(id).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.user = resp.body!;
        this.isCurrentUser = this.user.username == this.storageService.getUser()?.username;
        this.isAdminSelected = this.user.role == "ROLE_ADMIN";
      },
      error: err => {
        this.errorSnackBar(err);
      }
    })
  }

  grantAdmin(): void {
    this.authService.grantAdmin(this.user!).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.user = resp.body!;
        this.isAdminSelected = this.user.role == "ROLE_ADMIN";
      },
      error: err => {
        this.errorSnackBar(err);
      }
    })
  }

  revokeAdmin(): void {
    this.authService.revokeAdmin(this.user!).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.user = resp.body!;
        this.isAdminSelected = this.user.role == "ROLE_ADMIN";
      },
      error: err => {
        this.errorSnackBar(err);
      }
    })
  }

  deleteUser(): void {
    this.userService.deleteUser(this.user!.id).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.user = undefined;
        window.location.reload();
      },
      error: err => {
        if (err.status == 404) {
          this.snackBar.open("This user could not be found", undefined, {duration: 3000});
        } else if (err.status == 403) {
          this.snackBar.open("You can't delete your own account from this panel!", undefined, {duration: 3000});
        } else {
          this.snackBar.open("Something went wrong...", undefined, {duration: 3000});
        }
      }
    })
  }

  errorSnackBar(err: any) {
    if (err.status == 404) {
      this.snackBar.open("This user could not be found", undefined, {duration: 3000});
    } else {
      this.snackBar.open("Something went wrong...", undefined, {duration: 3000});
    }
  }

}
