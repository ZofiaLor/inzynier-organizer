import { Component, OnInit } from '@angular/core';
import { Subject, takeUntil } from 'rxjs';
import { MatCardModule } from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import { NotificationService } from '../service/notification.service';
import { Notification } from '../model/notification';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-notifs-view',
  standalone: true,
  imports: [MatCardModule, CommonModule, MatButtonModule, MatIconModule],
  templateUrl: './notifs-view.component.html',
  styleUrl: './notifs-view.component.scss'
})
export class NotifsViewComponent implements OnInit{
  private readonly _destroy$ = new Subject<void>();
  notifs: Notification[] = [];

  constructor (private readonly notifService: NotificationService, private readonly router: Router, private snackBar: MatSnackBar) {}

  ngOnInit(): void {
    this.fetchAllNotifs();
  }

  goToFile(fileId: number) {
    this.router.navigate([`/file/${fileId}`]);
  }

  fetchAllNotifs() {
    this.notifService.getCurrentUsersNotifs().pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.notifs = resp.body!;
      },
      error: err => {
        this.notifErrors(err);
      }
    })
  }

  fetchReadUnreadNotifs(read: boolean) {
    this.notifService.getCurrentUsersNotifsByRead(read).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.notifs = resp.body!;
      },
      error: err => {
        this.notifErrors(err);
      }
    })
  }

  setNotifAsReadUnread(notif: Notification, read: boolean) {
    notif.read = read;
    this.notifService.updateNotif(notif).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        notif = resp.body!;
      },
      error: err => {
        this.notifErrors(err);
      }
    })
  }

  deleteNotif(id: number): void {
    this.notifService.deleteNotif(id).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.fetchAllNotifs();
      },
      error: err => {
        this.notifErrors(err);
      }
    })
  }

  notifErrors(err: any) {
    if (err.status == 404) {
      this.snackBar.open("Notification not found", undefined, {duration: 3000});
    } else {
      this.snackBar.open("Something went wrong...", undefined, {duration: 3000});
    }
  }

}
