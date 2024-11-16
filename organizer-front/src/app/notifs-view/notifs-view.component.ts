import { Component, OnInit } from '@angular/core';
import { Subject, takeUntil } from 'rxjs';
import { MatCardModule } from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import { NotificationService } from '../service/notification.service';
import { Notification } from '../model/notification';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

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

  constructor (private readonly notifService: NotificationService, private readonly router: Router) {}

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
        console.log(err);
      }
    })
  }

  setNotifAsReadUnread(notif: Notification, read: boolean) {
    notif.read = read;
    this.notifService.updateNotif(notif).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        console.log(resp);
        notif = resp.body!;
      },
      error: err => {
        console.log(err);
      }
    })
  }


}
