import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatIconModule} from '@angular/material/icon';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatInputModule} from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { FormsModule, FormBuilder,
  FormControl,Validators, ReactiveFormsModule } from '@angular/forms';
import { File } from '../model/file';
import { EventFile } from '../model/event';
import { NoteFile } from '../model/note';
import { TaskFile } from '../model/task';
import { FileService } from '../service/file.service';
import { CommonModule } from '@angular/common';
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-file-view',
  standalone: true,
  imports: [MatFormFieldModule, CommonModule, FormsModule, ReactiveFormsModule, MatInputModule, MatIconModule, MatCheckboxModule, MatButtonModule],
  templateUrl: './file-view.component.html',
  styleUrl: './file-view.component.scss'
})
export class FileViewComponent implements OnChanges{

  @Input() file?: File;
  event?: EventFile;
  note?: NoteFile;
  task?: TaskFile;
  private readonly _destroy$ = new Subject<void>();

  noteForm = this.fb.group({
    filename: new FormControl(''),
    content: new FormControl('')
  });

  eventForm = this.fb.group({
    filename: new FormControl(''),
    content: new FormControl(''),
    startDate: new FormControl(''),
    endDate: new FormControl(''),
    location: new FormControl(''),
  });

  taskForm = this.fb.group({
    filename: new FormControl(''),
    content: new FormControl(''),
    deadline: new FormControl(''),
    isFinished: new FormControl(false),
  });

  constructor (private readonly fileService: FileService, private readonly fb: FormBuilder) {}

  ngOnChanges(changes: SimpleChanges): void {
    this.inferFileType();
    if (this.event) this.setUpEventForm();
    else if (this.note) this.setUpNoteForm();
    else this.setUpTaskForm();
  }

  inferFileType(): void {
    this.event = undefined;
    this.note = undefined;
    this.task = undefined;
    if (this.file === undefined) return;
    if (this.isEvent(this.file)) {
      this.event = this.file as EventFile;
    } else if (this.isTask(this.file)) {
      this.task = this.file as TaskFile;
    } else {
      this.note = this.file as NoteFile;
    }
  }

  updateNote(): void {
    console.log("update note");
    this.note!.name = this.noteForm.controls.filename.value!;
    this.note!.textContent = this.noteForm.controls.content.value!;
    this.fileService.updateNote(this.note!).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        console.log(resp);
        this.note = resp.body!;
      },
      error: err => {
        console.log(err);
      }
    });
  }

  updateEvent(): void {
    console.log("update event");
    this.event!.name = this.eventForm.controls.filename.value!;
    this.event!.textContent = this.eventForm.controls.content.value!;
    this.event!.startDate = this.eventForm.controls.startDate.value!
    this.event!.endDate = this.eventForm.controls.endDate.value!
    this.event!.location = this.eventForm.controls.location.value!;
    this.fileService.updateEvent(this.event!).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        console.log(resp);
        this.event = resp.body!;
      },
      error: err => {
        console.log(err);
      }
    });
  }

  updateTask(): void {
    console.log("update task");
    this.task!.name = this.taskForm.controls.filename.value!;
    this.task!.textContent = this.taskForm.controls.content.value!;
    this.task!.deadline = this.taskForm.controls.deadline.value!;
    this.task!.finished = this.taskForm.controls.isFinished.value!;
    this.fileService.updateTask(this.task!).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        console.log(resp);
        this.task = resp.body!;
      },
      error: err => {
        console.log(err);
      }
    });
  }

  setUpNoteForm(): void {
    if (this.note === undefined) return;
    this.noteForm.controls.filename.setValue(this.note?.name!);
    this.noteForm.controls.content.setValue(this.note?.textContent!);
  }

  setUpEventForm(): void {
    if (this.event === undefined) return;
    this.eventForm.controls.filename.setValue(this.event?.name!);
    this.eventForm.controls.content.setValue(this.event?.textContent!);
    if (this.event?.startDate !== undefined) this.eventForm.controls.startDate.setValue(this.event?.startDate!);
    if (this.event?.endDate !== undefined) this.eventForm.controls.endDate.setValue(this.event?.endDate!);
    this.eventForm.controls.location.setValue(this.event?.location!);
  }

  setUpTaskForm(): void {
    if (this.task === undefined) return;
    this.taskForm.controls.filename.setValue(this.task?.name!);
    this.taskForm.controls.content.setValue(this.task?.textContent!);
    if (this.task?.deadline !== undefined) this.taskForm.controls.deadline.setValue(this.task?.deadline!);
    this.taskForm.controls.isFinished.setValue(this.task?.finished);
    console.log(this.task?.finished);
  }

  isEvent(file: any): file is EventFile {
    return (file as EventFile).startDate !== undefined;
  }

  isTask(file: any): file is TaskFile {
    return (file as TaskFile).deadline !== undefined;
  }

}
