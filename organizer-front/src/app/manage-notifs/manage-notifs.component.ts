import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { FormsModule, FormBuilder,
  FormControl,Validators, ReactiveFormsModule } from '@angular/forms';
  import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { DateTime } from "luxon";
import { File } from '../model/file';
import { EventFile } from '../model/event';
import { TaskFile } from '../model/task';
import { MatSelectModule } from '@angular/material/select';
import { Notification } from '../model/notification';
import { NotificationService } from '../service/notification.service';
import { Subject, takeUntil } from 'rxjs';
import { CommonModule } from '@angular/common';
import { StorageService } from '../service/storage.service';

@Component({
  selector: 'app-manage-notifs',
  standalone: true,
  imports: [MatIconModule, MatButtonModule, MatCardModule, MatFormFieldModule,
    MatInputModule, FormsModule,
    ReactiveFormsModule, MatSelectModule, CommonModule],
  templateUrl: './manage-notifs.component.html',
  styleUrl: './manage-notifs.component.scss'
})
export class ManageNotifsComponent implements OnInit{
  @Input() file?: File;
  @Output() backEmitter = new EventEmitter();
  // https://moment.github.io/luxon/#/
  units = ['months', 'weeks', 'days', 'hours', 'minutes'];
  notifs: Notification[] = [];
  private readonly _destroy$ = new Subject<void>();

  customDateForm = this.fb.group({
    customDate: new FormControl('2024-01-01T00:00')
  })

  unitsForm = this.fb.group({
    timeDifference: new FormControl('0'),
    unitSelect: new FormControl('hours')
  })

  messageForm = this.fb.group({
    messageText: new FormControl('')
  })

  constructor(private readonly fb: FormBuilder, private readonly notifService: NotificationService, private readonly storageService: StorageService) {}

  ngOnInit(): void {
    this.fetchFutureNotifs();
    // // test
    // var dt = DateTime.local(2017, 5, 15, 8, 30);
    // console.log(dt.toISO());
    // dt = dt.plus({days: 1, minutes: 40});
    // console.log(dt.toISO());
  }

  addCustomDate(): void {
    var date = this.customDateForm.controls.customDate.value;
    if (date == null || date == '') return;
    var message = this.messageForm.controls.messageText.value;
    if (message == null || message == '') {
      message = "Reminder for " + this.file!.name;
    }
    var notif: Notification = {id: 0, user: this.storageService.getUser()!.id, file: this.file!.id, 
      sendTimeSetting: date!, message: message!, read: false};
    console.log(notif);
    //this.createNotif(notif);
  }

  addUnitDate(): void {
    var date = '';
    var diff = parseInt(this.unitsForm.controls.timeDifference.value!);
    var unit = this.unitsForm.controls.unitSelect.value!;
    var message = this.messageForm.controls.messageText.value;
    if (this.isEvent(this.file!)) {
      var datetime = DateTime.fromISO((this.file as EventFile).startDate!);
      if (unit == 'months') {
        date = datetime.minus({months : diff}).toISO() ?? '';
      } else if (unit == 'weeks') {
        date = datetime.minus({weeks : diff}).toISO() ?? '';
      } else if (unit == 'days') {
        date = datetime.minus({days : diff}).toISO() ?? '';
      } else if (unit == 'hours') {
        date = datetime.minus({hours : diff}).toISO() ?? '';
      } else if (unit == 'minutes') {
        date = datetime.minus({minutes : diff}).toISO() ?? '';
      }
    }
    if (message == null || message == '') {
      message = "Reminder that " + this.file!.name + " is in " + diff + " " + unit;
    }
    var notif: Notification = {id: 0, user: this.storageService.getUser()!.id, file: this.file!.id, 
      sendTimeSetting: date!, message: message!, read: false};
    console.log(notif);
  }

  isEvent(file: any): file is EventFile {
    return (file as EventFile).startDate !== undefined && (file as EventFile).startDate != '';
  }

  isTask(file: any): file is TaskFile {
    return (file as TaskFile).deadline !== undefined && (file as TaskFile).deadline != '';
  }

  createNotif(notif: Notification): void {
    this.notifService.createNotif(notif).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.fetchFutureNotifs();
      },
      error: err => {
        console.log(err);
      }
    })
  }

  fetchFutureNotifs(): void {
    this.notifService.getCurrentUsersNotifsByFile(this.file!.id).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.notifs = resp.body!;
      },
      error: err => {
        console.log(err);
      }
    })
  }

}
