import { Component, Input, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import {MatRadioModule} from '@angular/material/radio';
import { FormsModule, FormBuilder,
  FormControl,Validators, ReactiveFormsModule } from '@angular/forms';
  import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { DateTime } from "luxon";
import { File } from '../model/file';
import { EventFile } from '../model/event';
import { TaskFile } from '../model/task';
import { MatSelectModule } from '@angular/material/select';

@Component({
  selector: 'app-manage-notifs',
  standalone: true,
  imports: [MatIconModule, MatButtonModule, MatCardModule, MatFormFieldModule,
    MatInputModule,MatRadioModule, FormsModule,
    ReactiveFormsModule, MatSelectModule],
  templateUrl: './manage-notifs.component.html',
  styleUrl: './manage-notifs.component.scss'
})
export class ManageNotifsComponent implements OnInit{
  @Input() file?: File;
  // https://moment.github.io/luxon/#/
  dateOptions = ['earlier', 'from now', 'custom date'];
  units = ['months', 'weeks', 'days', 'hours', 'minutes'];

  form = this.fb.group({
    customDate: new FormControl(''),
    timeDifference: new FormControl(''),
    unitSelect: new FormControl(),
    optionSelect: new FormControl()
  })
  customDateForm = this.fb.group({
    customDate: new FormControl('')
  })

  unitsForm = this.fb.group({
    timeDifference: new FormControl(''),
    unitSelect: new FormControl(),
    fromNow: new FormControl(false)
  })

  constructor(private readonly fb: FormBuilder,) {}

  ngOnInit(): void {
    // // test
    // var dt = DateTime.local(2017, 5, 15, 8, 30);
    // console.log(dt.toISO());
    // dt = dt.plus({days: 1, minutes: 40});
    // console.log(dt.toISO());
  }

  isEvent(file: any): file is EventFile {
    return (file as EventFile).startDate !== undefined;
  }

  isTask(file: any): file is TaskFile {
    return (file as TaskFile).deadline !== undefined;
  }

}
