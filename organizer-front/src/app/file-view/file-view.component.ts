import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatIconModule} from '@angular/material/icon';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatInputModule} from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import {MatExpansionModule} from '@angular/material/expansion';
import { FormsModule, FormBuilder,
  FormControl,Validators, ReactiveFormsModule } from '@angular/forms';
import { File } from '../model/file';
import { EventFile } from '../model/event';
import { NoteFile } from '../model/note';
import { TaskFile } from '../model/task';
import { FileService } from '../service/file.service';
import { CommonModule } from '@angular/common';
import { Subject, takeUntil } from 'rxjs';
import { StorageService } from '../service/storage.service';

@Component({
  selector: 'app-file-view',
  standalone: true,
  imports: [MatFormFieldModule, CommonModule, FormsModule, ReactiveFormsModule, MatInputModule, MatIconModule, MatCheckboxModule, MatButtonModule, MatExpansionModule],
  templateUrl: './file-view.component.html',
  styleUrl: './file-view.component.scss'
})
export class FileViewComponent{

  private _file?: File;
  @Input() set file(value: File | undefined) {
    this._file = value;
    if (value !== undefined){
      this.inferFileType();
      this.onChange();
    }
  }
  get file(): File | undefined {
    return this._file;
  }
  private _typeToCreate?: number;
  @Input() set typeToCreate(value: number | undefined){
    this._typeToCreate = value;
    if (value !== undefined){
      this.setUpNewFile();
      this.onChange();
    }
  }
  get typeToCreate(): number | undefined {
    return this._typeToCreate;
  }
  @Input() currentDir?: number;
  @Output() refreshDirs = new EventEmitter();
  event?: EventFile;
  note?: NoteFile;
  task?: TaskFile;
  createMode = false;
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

  onChange(): void {
    if (this.event) this.setUpEventForm();
    else if (this.note) this.setUpNoteForm();
    else this.setUpTaskForm();
  }

  inferFileType(): void {
    this.event = undefined;
    this.note = undefined;
    this.task = undefined;
    if (this._file === undefined) return;
    this.createMode = false;
    if (this.isEvent(this._file)) {
      this.event = this._file as EventFile;
    } else if (this.isTask(this._file)) {
      this.task = this._file as TaskFile;
    } else {
      this.note = this._file as NoteFile;
    }
  }

  setUpNewFile(): void {
    if (this._typeToCreate === undefined) return;
    this.createMode = true;
    this.event = undefined;
    this.note = undefined;
    this.task = undefined;
    switch(this._typeToCreate) {
      case 1:
        this.event = {id: 0, name: "Unnamed Event", textContent: "", eventDates: [], creationDate: "", owner: 0, parent: this.currentDir!};
        break;
      case 2:
        this.note = {id: 0, name: "Unnamed Note", textContent: "", creationDate: "", owner: 0, parent: this.currentDir!};
        break;
      case 3:
        this.task = {id: 0, name: "Unnamed Task", textContent: "", finished: false, creationDate: "", owner: 0, parent: this.currentDir!};
        break;
      default:
        console.log("Incorrect type");
        break;
    }
  }

  deleteFile(): void {
    this.fileService.deleteFile(this._file!.id).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this._file = undefined;
        this.event = undefined;
        this.note = undefined;
        this.task = undefined;
        this.refreshDirs.emit();
      },
      error: err => {
        console.log(err);
      }
    })
  }

  saveNote(): void {
    if (this.createMode) this.createNote();
    else this.updateNote();
  }

  saveEvent(): void {
    if (this.createMode) this.createEvent();
    else this.updateEvent();
  }

  saveTask(): void {
    if (this.createMode) this.createTask();
    else this.updateTask();
  }

  updateNote(): void {
    this.note!.name = this.noteForm.controls.filename.value!;
    this.note!.textContent = this.noteForm.controls.content.value!;
    this.fileService.updateNote(this.note!).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.note = resp.body!;
        this.refreshDirs.emit();
      },
      error: err => {
        console.log(err);
      }
    });
  }

  createNote(): void {
    this.note!.name = this.noteForm.controls.filename.value!;
    this.note!.textContent = this.noteForm.controls.content.value!;
    this.fileService.createNote(this.note!).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.note = resp.body!;
        this.refreshDirs.emit();
      },
      error: err => {
        console.log(err);
      }
    });
  }

  updateEvent(): void {
    this.event!.name = this.eventForm.controls.filename.value!;
    this.event!.textContent = this.eventForm.controls.content.value!;
    this.event!.startDate = this.eventForm.controls.startDate.value!
    this.event!.endDate = this.eventForm.controls.endDate.value!
    this.event!.location = this.eventForm.controls.location.value!;
    this.fileService.updateEvent(this.event!).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.event = resp.body!;
        this.refreshDirs.emit();
      },
      error: err => {
        console.log(err);
      }
    });
  }

  createEvent(): void {
    this.event!.name = this.eventForm.controls.filename.value!;
    this.event!.textContent = this.eventForm.controls.content.value!;
    this.event!.startDate = this.eventForm.controls.startDate.value!
    this.event!.endDate = this.eventForm.controls.endDate.value!
    this.event!.location = this.eventForm.controls.location.value!;
    this.fileService.createEvent(this.event!).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.event = resp.body!;
        this.refreshDirs.emit();
      },
      error: err => {
        console.log(err);
      }
    });
  }

  updateTask(): void {
    this.task!.name = this.taskForm.controls.filename.value!;
    this.task!.textContent = this.taskForm.controls.content.value!;
    this.task!.deadline = this.taskForm.controls.deadline.value!;
    this.task!.finished = this.taskForm.controls.isFinished.value!;
    this.fileService.updateTask(this.task!).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.task = resp.body!;
        this.refreshDirs.emit();
      },
      error: err => {
        console.log(err);
      }
    });
  }

  createTask(): void {
    this.task!.name = this.taskForm.controls.filename.value!;
    this.task!.textContent = this.taskForm.controls.content.value!;
    this.task!.deadline = this.taskForm.controls.deadline.value!;
    this.task!.finished = this.taskForm.controls.isFinished.value!;
    this.fileService.createTask(this.task!).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.task = resp.body!;
        this.refreshDirs.emit();
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
  }

  isEvent(file: any): file is EventFile {
    return (file as EventFile).startDate !== undefined;
  }

  isTask(file: any): file is TaskFile {
    return (file as TaskFile).deadline !== undefined;
  }

}
