import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import {MatListModule} from '@angular/material/list';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatMenuModule} from '@angular/material/menu';
import {MatTabsModule} from '@angular/material/tabs';
import { FileService } from '../service/file.service';
import { Subject, takeUntil } from 'rxjs';
import { File } from '../model/file';
import { EventFile } from '../model/event';
import { TaskFile } from '../model/task';
import { DirectoryService } from '../service/directory.service';
import { Directory } from '../model/directory';
import { ActivatedRoute, Router } from '@angular/router';
import { AccessFile } from '../model/access-file';
import { AccessDir } from '../model/access-dir';
import { AccessService } from '../service/access.service';
import { StorageService } from '../service/storage.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-file-tree',
  standalone: true,
  imports: [MatListModule, MatIconModule, MatButtonModule, MatTooltipModule, MatMenuModule, MatTabsModule],
  templateUrl: './file-tree.component.html',
  styleUrl: './file-tree.component.scss'
})
export class FileTreeComponent implements OnInit{

  @Output() itemSelectEmitter = new EventEmitter();
  @Output() dirSelectEmitter = new EventEmitter<number>();
  @Output() fileCreateEmitter = new EventEmitter();

  constructor (private readonly fileService: FileService, private readonly dirService: DirectoryService, private readonly accessService: AccessService,
    private readonly storageService: StorageService, private readonly router: Router, private readonly route: ActivatedRoute, private snackBar: MatSnackBar) {}

  files: File[] = [];
  dirs: Directory[] = [];
  afs: AccessFile[] = [];
  ads: AccessDir[] = [];
  sharedDirs: Directory[] = [];
  sharedFiles: File[] = [];
  sharedLocalBaseId?: number;
  sharedCurrentDir?: Directory;
  currentTab: number = 0;
  currentDir?: Directory;
  hasParent: boolean = false;
  private readonly _destroy$ = new Subject<void>();

  ngOnInit(): void {
    this.currentDir = this.storageService.getCurrentDir();
    this.sharedLocalBaseId = this.storageService.getLocalBase();
    this.sharedCurrentDir = this.storageService.getCurrentShared();
    this.currentTab = this.storageService.getTab();
    if (this.currentDir) {
      this.fetchDirById(this.currentDir.id);
    } else {
      this.fetchBaseDirs();
    }
    if (this.sharedCurrentDir) {
      this.fetchSharedDirById(this.sharedCurrentDir.id);
    } else {
      this.fetchADs();
      this.fetchAFs();
    }
  }

  onTabChange(tab: number) {
    this.currentTab = tab;
    this.storageService.saveTab(tab);
  }

  isEvent(file: any): file is EventFile {
    return (file as EventFile).startDate !== undefined;
  }

  isTask(file: any): file is TaskFile {
    return (file as TaskFile).deadline !== undefined;
  }

  clickDir(dirId: number): void {
    this.fetchDirById(dirId);
  }

  clickSharedDir(dirId: number): void {
    if (this.sharedLocalBaseId === undefined) {
      this.sharedLocalBaseId = dirId;
      this.storageService.saveLocalBase(dirId);
    } 
    this.fetchSharedDirById(dirId);
  }

  clickUpDir(): void {
    this.fetchParent();
  }

  clickUpSharedDir(): void {
    this.fetchSharedParent();
  }

  clickFile(fileId: number): void {
    this.fetchFileById(fileId);
  }

  clickEditDir(): void {
    if (this.currentDir === undefined) return;
    this.router.navigate([`dir/${this.currentDir!.id}`]).then(() => {
      this.itemSelectEmitter.emit();
    });
  }

  clickNewElement(typeId: number): void {
    this.router.navigate([`new/${typeId}/${this.currentDir!.id}`]).then(() => {
      this.fileCreateEmitter.emit();
    });
  }

  fetchBaseDirs(): void {
    this.dirService.getCurrentUsersBaseDir().pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.currentDir = resp.body!;
        this.storageService.saveCurrentDir(this.currentDir);
        this.dirSelectEmitter.emit(this.currentDir!.id);
        this.hasParent = false;
        this.fetchFilesInDir();
        this.fetchSubdirsInDir();
      }
    });
  }

  fetchDirById(id: number): void {
    this.dirService.getDirById(id).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.currentDir = resp.body!;
        this.storageService.saveCurrentDir(this.currentDir);
        this.dirSelectEmitter.emit(this.currentDir!.id);
        this.hasParent = this.currentDir!.parent !== null;
        this.fetchFilesInDir();
        this.fetchSubdirsInDir();
      },
      error: err => {
        this.onClickErrors(err);
      }
    });
  }

  fetchSharedDirById(id: number): void {
    this.dirService.getDirById(id).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.sharedCurrentDir = resp.body!;
        this.storageService.saveSharedDir(this.sharedCurrentDir);
        this.fetchSharedSubdirsInDir();
        this.fetchSharedFilesInDir();
      },
      error: err => {
        this.onClickErrors(err);
      }
    });
  }

  fetchFileById(id: number): void {
    this.fileService.getFileById(id).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.router.navigate([`file/${id}`]).then(() => {
          this.itemSelectEmitter.emit();
        });
        
      },
      error: err => {
        this.onClickErrors(err);
      }
    });
  }

  fetchParent(): void {
    if (this.hasParent) {
      this.dirService.getDirById(this.currentDir!.parent!).pipe(takeUntil(this._destroy$)).subscribe({
        next: resp => {
          this.currentDir = resp.body!;
          this.storageService.saveCurrentDir(this.currentDir);
          this.dirSelectEmitter.emit(this.currentDir!.id);
          this.hasParent = this.currentDir!.parent !== null;
          this.fetchFilesInDir();
          this.fetchSubdirsInDir();
        },
        error: err => {
          this.onClickErrors(err);
        }
      });
    }
  }

  fetchSharedParent(): void {
    if (this.sharedCurrentDir!.id == this.sharedLocalBaseId) {
      this.sharedCurrentDir = undefined;
      this.sharedLocalBaseId = undefined;
      this.storageService.saveSharedDir(undefined);
      this.storageService.saveLocalBase(undefined);
      this.fetchADs();
      this.fetchAFs();
    } else {
      this.dirService.getDirById(this.sharedCurrentDir!.parent!).pipe(takeUntil(this._destroy$)).subscribe({
        next: resp => {
          this.sharedCurrentDir = resp.body!;
          this.storageService.saveSharedDir(this.sharedCurrentDir);
          this.fetchSharedFilesInDir();
          this.fetchSharedSubdirsInDir();
        },
        error: err => {
          this.onClickErrors(err);
        }
      });
    }
  }

  fetchFilesInDir(): void {
    if (this.currentDir === undefined) return;
    this.fileService.getFilesInDirectory(this.currentDir!.id).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.files = resp.body!;
        // https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/sort
        this.files.sort((a, b) => {
          const nameA = a.name.toUpperCase();
          const nameB = b.name.toUpperCase();
          if (nameA < nameB) {
            return -1;
          }
          if (nameA > nameB) {
            return 1;
          }
          return 0;
        });
      },
      error: err => {
        this.onClickErrors(err);
      }
    });
  }

  fetchSharedFilesInDir(): void {
    if (this.sharedCurrentDir === undefined) return;
    this.fileService.getFilesInDirectory(this.sharedCurrentDir!.id).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.sharedFiles = resp.body!;
        // https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/sort
        this.sharedFiles.sort((a, b) => {
          const nameA = a.name.toUpperCase();
          const nameB = b.name.toUpperCase();
          if (nameA < nameB) {
            return -1;
          }
          if (nameA > nameB) {
            return 1;
          }
          return 0;
        });
      },
      error: err => {
        this.onClickErrors(err);
      }
    });
  }

  fetchSubdirsInDir(): void {
    if (this.currentDir === undefined) return;
    this.dirService.getDirsByParentId(this.currentDir!.id).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.dirs = resp.body!;
        // https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/sort
        this.dirs.sort((a, b) => {
          const nameA = a.name.toUpperCase();
          const nameB = b.name.toUpperCase();
          if (nameA < nameB) {
            return -1;
          }
          if (nameA > nameB) {
            return 1;
          }
          return 0;
        });
      },
      error: err => {
        this.onClickErrors(err);
      }
    });
  }

  fetchSharedSubdirsInDir(): void {
    if (this.sharedCurrentDir === undefined) return;
    this.dirService.getDirsByParentId(this.sharedCurrentDir!.id).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.sharedDirs = resp.body!;
        // https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/sort
        this.sharedDirs.sort((a, b) => {
          const nameA = a.name.toUpperCase();
          const nameB = b.name.toUpperCase();
          if (nameA < nameB) {
            return -1;
          }
          if (nameA > nameB) {
            return 1;
          }
          return 0;
        });
      },
      error: err => {
        this.onClickErrors(err);
      }
    });
  }

  fetchAFs(): void {
    let user = this.storageService.getUser();
    if (user === undefined) return;
    this.accessService.getAFsByUserId(user.id).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.afs = resp.body!;
        this.fetchFilesFromAF();
      },
      error: err => {
        this.snackBar.open("Something went wrong...", undefined, {duration: 3000});
      }
    })
  }

  fetchFilesFromAF(): void {
    this.sharedFiles = [];
    for (var af of this.afs) {
      this.fileService.getFileById(af.id.fileId).pipe(takeUntil(this._destroy$)).subscribe({
        next: resp => {
          this.sharedFiles.push(resp.body!);
        },
        error: err => {
          this.onClickErrors(err);
        }
      })
    }
  }

  fetchADs(): void {
    let user = this.storageService.getUser();
    if (user === undefined) return;
    this.accessService.getADsByUserId(user.id).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.ads = resp.body!;
        this.fetchDirsFromAD();
      },
      error: err => {
        this.snackBar.open("Something went wrong...", undefined, {duration: 3000});
      }
    })
  }

  fetchDirsFromAD(): void {
    this.sharedDirs = [];
    for (var ad of this.ads) {
      this.dirService.getDirById(ad.id.directoryId).pipe(takeUntil(this._destroy$)).subscribe({
        next: resp => {
          this.sharedDirs.push(resp.body!);
        },
        error: err => {
          this.onClickErrors(err);
        }
      })
    }
  }

  onClickErrors(err: any) {
    if (err.status == 403) {
      this.snackBar.open("You don't have access to view this resource", undefined, {duration: 3000});
    } else if (err.status == 404) {
      this.snackBar.open("This resource does not exist", undefined, {duration: 3000});
    } else {
      this.snackBar.open("Something went wrong...", undefined, {duration: 3000});
    }
  }

}
