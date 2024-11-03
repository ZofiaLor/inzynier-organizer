import { Component, OnInit } from '@angular/core';
import { TableBaseComponent } from '../table-base/table-base.component';
import { MatCardModule } from '@angular/material/card';
import {MatGridListModule} from '@angular/material/grid-list';
import { FlexLayoutModule } from '@angular/flex-layout';
import { UserService } from '../../service/user.service';
import { User } from '../../model/user';
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-user-panel',
  standalone: true,
  imports: [TableBaseComponent, MatCardModule, MatGridListModule, FlexLayoutModule],
  templateUrl: './user-panel.component.html',
  styleUrl: './user-panel.component.scss'
})
export class UserPanelComponent implements OnInit{
  private readonly _destroy$ = new Subject<void>();
  userData: User[] = [];
  user?: User;
  columns: string[] = ["id", "username", "name", "email"];
  constructor (private userService: UserService) {}
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
      },
      error: err => {
        console.log(err);
      }
    })
  }

}
