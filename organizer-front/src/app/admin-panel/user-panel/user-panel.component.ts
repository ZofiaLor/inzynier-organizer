import { Component, OnInit } from '@angular/core';
import { TableBaseComponent } from '../table-base/table-base.component';
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
  constructor (private userService: UserService, private storageService: StorageService, private authService: AuthService) {}
  ngOnInit(): void {
    this.userService.getAllUsers().pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        console.log(resp);
        this.userData = resp.body!;
      },
      error: err => {
        console.log(err);
      }
    })
  }

  selectUser(id: number): void {
    this.userService.getUserById(id).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        console.log(resp);
        this.user = resp.body!;
        this.isCurrentUser = this.user.username == this.storageService.getUser()?.username;
        this.isAdminSelected = this.user.role == "ROLE_ADMIN";
      },
      error: err => {
        console.log(err);
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
        console.log(err);
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
        console.log(err);
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
        console.log(err);
      }
    })
  }

}
