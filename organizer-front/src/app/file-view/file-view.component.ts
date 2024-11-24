import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
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
import { MoveDirComponent } from '../move-dir/move-dir.component';
import { ManageAccessComponent } from '../manage-access/manage-access.component';
import { ManageNotifsComponent } from '../manage-notifs/manage-notifs.component';
import { VotingPanelComponent } from '../voting-panel/voting-panel.component';
import { DirectoryService } from '../service/directory.service';
import { ActivatedRoute, Router } from '@angular/router';
import { StorageService } from '../service/storage.service';
import { Directory } from '../model/directory';
import { AccessService } from '../service/access.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-file-view',
  standalone: true,
  imports: [MatFormFieldModule, CommonModule, FormsModule, ReactiveFormsModule, MatInputModule, MatIconModule, MatCheckboxModule, MatButtonModule, 
    MatExpansionModule, MoveDirComponent, ManageAccessComponent, ManageNotifsComponent, VotingPanelComponent],
  templateUrl: './file-view.component.html',
  styleUrl: './file-view.component.scss'
})
export class FileViewComponent implements OnInit{

  _file?: File;
  _typeToCreate?: number;
  @Input() currentDir?: number;
  @Output() refreshDirs = new EventEmitter();
  event?: EventFile;
  note?: NoteFile;
  task?: TaskFile;
  dir?: Directory;
  createMode = false;
  movingElement = false;
  sharingElement = false;
  managingNotifs = false;
  inVotingPanel = false;
  sharedItemId?: number;
  isSharedFile = false;
  isOwner = false;
  canEdit = false;
  parentId?: number;
  movedDirId?: number;
  private readonly _destroy$ = new Subject<void>();

  dirForm = this.fb.group({
    dirname: new FormControl('', Validators.required)
  });

  noteForm = this.fb.group({
    filename: new FormControl('', Validators.required),
    content: new FormControl('')
  });

  eventForm = this.fb.group({
    filename: new FormControl('', Validators.required),
    content: new FormControl(''),
    startDate: new FormControl(''),
    endDate: new FormControl(''),
    location: new FormControl(''),
  });

  taskForm = this.fb.group({
    filename: new FormControl('', Validators.required),
    content: new FormControl(''),
    deadline: new FormControl(''),
    isFinished: new FormControl(false),
  });

  //https://stackoverflow.com/questions/45997369/how-to-get-param-from-url-in-angular-4
  constructor (private readonly fileService: FileService, private readonly dirService: DirectoryService, private readonly storageService: StorageService,
    private readonly fb: FormBuilder, private route: ActivatedRoute, private readonly router: Router, private readonly accessService: AccessService, private snackBar: MatSnackBar) {}
  ngOnInit(): void {
    if (this.router.url.includes('/new')) 
      {  
        this.onNew();
      }
    else if (this.router.url.includes('/file') || this.router.url.includes('/dir')) 
      {  
        this.onSelect();
      }
    
  }

  onChange(): void {
    if (this.dir) this.setUpDirForm();
    else if (this.event) this.setUpEventForm();
    else if (this.note) this.setUpNoteForm();
    else this.setUpTaskForm();
  }

  onSelect(): void {
    let id = this.route.snapshot.paramMap.get('id');
    if (id === null) return;
    // https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/parseInt
    let intId = parseInt(id);
    if (Number.isNaN(intId)) return;
    if (this.router.url.includes('/file')) this.fetchFileById(intId);
    else if (this.router.url.includes('/dir')) this.fetchDirById(intId);
    
  }

  onNew(): void {
    let type = this.route.snapshot.paramMap.get('type');
    let dir = this.route.snapshot.paramMap.get('dir');
    if (type === null || dir === null) return;
    let typeId = parseInt(type);
    let dirId = parseInt(dir);
    if (Number.isNaN(typeId) || Number.isNaN(dirId)) return;
    // only the owner can create
    this.dirService.getDirById(dirId).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.currentDir = dirId;
        if (resp.body!.owner == this.storageService.getUser()!.id) {
          this._typeToCreate = typeId;
          this.setUpNewFile();
          this.onChange();
        } else {
          this.snackBar.open("You cannot create items in folders you don't own", undefined, {duration: 3000});
        }
      },
      error: err => {
        this.getterErrors(err, "folder");
      }
    })
    
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
    this.dir = undefined;
    this.event = undefined;
    this.note = undefined;
    this.task = undefined;
    switch(this._typeToCreate) {
      case 0:
        this.dir = {id: 0, name: "Unnamed Directory", owner: 0, parent: this.currentDir!, children: [], files: []};
        break;
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
        this.snackBar.open("Could not parse url to set up new resource", undefined, {duration: 3000});
        break;
    }
  }

  deleteFile(): void {
    if (this.dir) {
      this.dirService.deleteDir(this.dir.id).pipe(takeUntil(this._destroy$)).subscribe({
        next: resp => {
          this.dir = undefined;
          this.router.navigate(['/']);
        },
        error: err => {
          if (err.status == 404) {
            this.snackBar.open("Folder not found", undefined, {duration: 3000});
          } else {
            this.snackBar.open("Something went wrong...", undefined, {duration: 3000});
          }
        }
      })
    } else {
      this.fileService.deleteFile(this._file!.id).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this._file = undefined;
        this.event = undefined;
        this.note = undefined;
        this.task = undefined;
        this.router.navigate(['/']);
      },
      error: err => {
        if (err == 404) {
          this.snackBar.open("File not found", undefined, {duration: 3000});
        } else {
          this.snackBar.open("Something went wrong...", undefined, {duration: 3000});
        }
      }
    });
    }
    
  }

  saveItem(): void {
    if (this.dir) this.saveDir();
    else if (this.event) this.saveEvent();
      else if (this.note) this.saveNote();
      else this.saveTask();
  }

  saveDir(): void {
    if (this.createMode) this.createDir();
    else this.updateDir();
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

  moveDirs(): void {
    if (this.dir) {
      if (this.dir.parent === null) return;
      this.parentId = this.dir.parent!;
      this.movedDirId = this.dir.id;
    }
    else {
      this.parentId = this._file?.parent;
      this.movedDirId = undefined;
    }
    this.movingElement = true;
  }

  onFinishMoving(newParentId: number | undefined) {
    this.movingElement = false;
    if (newParentId !== undefined) {
      if (this.dir) {
        this.dir.parent = newParentId;
        this.updateDir();
      } else {
        this._file!.parent = newParentId;
        this.inferFileType();
        if (this.event) this.updateEvent();
        else if (this.note) this.updateNote();
        else this.updateTask();
      }
      
    }
  }

  onChangeEventDate() {
    this.inVotingPanel = false;
    this.fileService.updateEvent(this.event!).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.event = resp.body!;
        this.setUpEventForm();
        this.refreshDirs.emit();
      },
      error: err => {
        this.modifyErrors(err, "event");
      }
    });
  }

  shareItem(): void {
    if (this.dir) {
      this.isSharedFile = false;
      this.sharedItemId = this.dir.id;
    } else {
      this.isSharedFile = true;
      this.sharedItemId = this._file?.id;
    }
    this.sharingElement = true;
  }

  manageNotifs(): void {
    this.managingNotifs = true;
  }

  goToVotingPanel(): void {
    this.inVotingPanel = true;
  }

  createDir(): void {
    this.dir!.name = this.dirForm.controls.dirname.value!;
    this.dirService.createDir(this.dir!).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.dir = resp.body!;
        this.router.navigate([`dir/${this.dir!.id}`]);
      },
      error: err => {
        this.modifyErrors(err, "folder");
      }
    });
  }

  updateDir(): void {
    this.dir!.name = this.dirForm.controls.dirname.value!;
    this.dirService.updateDir(this.dir!).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.dir = resp.body!;
        this.refreshDirs.emit();
      },
      error: err => {
        this.modifyErrors(err, "folder");
      }
    });
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
        this.modifyErrors(err, "note");
      }
    });
  }

  createNote(): void {
    this.note!.name = this.noteForm.controls.filename.value!;
    this.note!.textContent = this.noteForm.controls.content.value!;
    this.fileService.createNote(this.note!).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this._file = resp.body!;
        this.createMode = false;
        this.router.navigate([`file/${this._file!.id}`]);
      },
      error: err => {
        this.modifyErrors(err, "note");
      }
    });
  }

  updateEvent(): void {
    this.event!.name = this.eventForm.controls.filename.value!;
    this.event!.textContent = this.eventForm.controls.content.value!;
    this.event!.startDate = this.eventForm.controls.startDate.value!;
    this.event!.endDate = this.eventForm.controls.endDate.value!;
    this.event!.location = this.eventForm.controls.location.value!;
    if (this.event!.startDate != null && this.event!.endDate != null) {
      var start = new Date(this.event!.startDate!);
      var end = new Date(this.event!.endDate!);
      if (start > end) {
        this.snackBar.open("The start of the event cannot be after the end!", "OK", {duration: 10000});
        return;
      }
    }
    this.fileService.updateEvent(this.event!).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.event = resp.body!;
        this.refreshDirs.emit();
      },
      error: err => {
        this.modifyErrors(err, "event");
      }
    });
  }

  createEvent(): void {
    this.event!.name = this.eventForm.controls.filename.value!;
    this.event!.textContent = this.eventForm.controls.content.value!;
    this.event!.startDate = this.eventForm.controls.startDate.value!
    this.event!.endDate = this.eventForm.controls.endDate.value!
    this.event!.location = this.eventForm.controls.location.value!;
    if (this.event!.startDate != null && this.event!.endDate != null) {
      var start = new Date(this.event!.startDate!);
      var end = new Date(this.event!.endDate!);
      if (start > end) {
        this.snackBar.open("The start of the event cannot be after the end!", "OK", {duration: 10000});
        return;
      }
    }
    this.fileService.createEvent(this.event!).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this._file = resp.body!;
        this.createMode = false;
        this.router.navigate([`file/${this._file!.id}`]);
      },
      error: err => {
        this.modifyErrors(err, "event");
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
        this.modifyErrors(err, "task");
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
        this._file = resp.body!;
        this.createMode = false;
        this.router.navigate([`file/${this._file!.id}`]);
      },
      error: err => {
        this.modifyErrors(err, "task");
      }
    });
  }

  setUpForm(): void {
    if (this.dir) this.setUpDirForm();
    else if (this.event) this.setUpEventForm();
      else if (this.note) this.setUpNoteForm();
      else this.setUpTaskForm();
  }

  setUpDirForm(): void {
    if (this.dir === undefined) return;
    this.dirForm.controls.dirname.setValue(this.dir?.name!);
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

  fetchFileById(id: number): void {
    this.fileService.getFileById(id).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this._file = resp.body!;
        this.isOwner = this._file!.owner == this.storageService.getUser()!.id;
        if (!this.isOwner) {
          this.fileService.canEditFile(id).pipe(takeUntil(this._destroy$)).subscribe({
            next: resp => {
              this.canEdit = resp.body!;
            },
            error: err => {
              this.getterErrors(err, "file");
            }
          });
        }
        this.dir = undefined;
        this.inferFileType();
        this.onChange();
      },
      error: err => {
        this.getterErrors(err, "file");
      }
    });
  }

  fetchDirById(id: number): void {
    this.dirService.getDirById(id).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.dir = resp.body!;
        this.isOwner = this.dir!.owner == this.storageService.getUser()!.id;
        if (!this.isOwner) {
          this.dirService.canEditDir(id).pipe(takeUntil(this._destroy$)).subscribe({
            next: resp => {
              this.canEdit = resp.body!;
            },
            error: err => {
              this.getterErrors(err, "folder");
            }
          });
        }
        this._file = undefined;
        this.onChange();
      },
      error: err => {
        this.getterErrors(err, "folder");
      }
    });
  }

  getterErrors(err: any, resource: string) {
    if (err.status == 403) {
      this.snackBar.open("You don't have access to view this " + resource, undefined, {duration: 3000});
    } else if (err.status == 404) {
      this.snackBar.open("This " + resource + " could not be found", undefined, {duration: 3000});
    } else {
      this.snackBar.open("Something went wrong...", undefined, {duration: 3000});
    }
  }

  modifyErrors(err: any, resource: string) {
    if (err.status == 403) {
      this.snackBar.open("You don't have access to edit this " + resource, undefined, {duration: 3000});
    } else if (err.status == 404) {
      this.snackBar.open("This " + resource + " could not be found", undefined, {duration: 3000});
    } else {
      this.snackBar.open("Something went wrong...", undefined, {duration: 3000});
    }
  }

}
