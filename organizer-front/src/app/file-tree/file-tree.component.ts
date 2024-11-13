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
    private readonly storageService: StorageService, private readonly router: Router, private readonly route: ActivatedRoute) {}

  files: File[] = [];
  dirs: Directory[] = [];
  afs: AccessFile[] = [];
  ads: AccessDir[] = [];
  sharedDirs: Directory[] = [];
  sharedFiles: File[] = [];
  sharedLocalBaseId?: number;
  sharedCurrentDir?: Directory;
  currentDir?: Directory;
  hasParent: boolean = false;
  private readonly _destroy$ = new Subject<void>();

  ngOnInit(): void {
    if (this.router.url.includes('/new')) 
      {  
        let id = this.route.snapshot.paramMap.get('dir');
        if (id === null) this.fetchBaseDirs();
        else {
          let intId = parseInt(id);
          if (Number.isNaN(intId)) this.fetchBaseDirs();
          else this.fetchDirById(intId);
        }
        
      }
    else if (this.router.url.includes('/file')) 
      {  
        let id = this.route.snapshot.paramMap.get('id');
        if (id === null) this.fetchBaseDirs();
        else {
          let intId = parseInt(id);
          if (Number.isNaN(intId)) this.fetchBaseDirs();
          else this.fetchFileById(intId, false);
        }
      } 
      else if (this.router.url.includes('/dir')) {
        let id = this.route.snapshot.paramMap.get('id');
        if (id === null) this.fetchBaseDirs();
        else {
          let intId = parseInt(id);
          if (Number.isNaN(intId)) this.fetchBaseDirs();
          else this.fetchDirById(intId);
        }
      } else {
        this.fetchBaseDirs();
      }
    
    this.fetchADs();
    this.fetchAFs();
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
    if (this.sharedLocalBaseId === undefined) this.sharedLocalBaseId = dirId;
    this.fetchSharedDirById(dirId);
  }

  clickUpDir(): void {
    this.fetchParent();
  }

  clickUpSharedDir(): void {
    this.fetchSharedParent();
  }

  clickFile(fileId: number): void {
    this.fetchFileById(fileId, true);
  }

  clickEditDir(): void {
    console.log(this.currentDir);
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
        this.dirSelectEmitter.emit(this.currentDir!.id);
        this.hasParent = false;
        this.fetchFilesInDir();
        this.fetchSubdirsInDir();
      },
      error: err => {
        console.log(err);
      }
    });
  }

  fetchDirById(id: number): void {
    this.dirService.getDirById(id).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.currentDir = resp.body!;
        this.dirSelectEmitter.emit(this.currentDir!.id);
        this.hasParent = this.currentDir!.parent !== null;
        this.fetchFilesInDir();
        this.fetchSubdirsInDir();
      },
      error: err => {
        console.log(err);
      }
    });
  }

  fetchSharedDirById(id: number): void {
    this.dirService.getDirById(id).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.sharedCurrentDir = resp.body!;
        this.fetchSharedSubdirsInDir();
        this.fetchSharedFilesInDir();
      },
      error: err => {
        console.log(err);
      }
    });
  }

  fetchFileById(id: number, navigate: boolean): void {
    this.fileService.getFileById(id).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        if (navigate) {
          this.router.navigate([`file/${id}`]).then(() => {
            this.itemSelectEmitter.emit();
          });
        } else if (resp.body!.owner == this.storageService.getUser()!.id){
          this.fetchDirById(resp.body!.parent);
        } else {
          this.fetchBaseDirs();
        }
        
      },
      error: err => {
        console.log(err);
      }
    });
  }

  fetchParent(): void {
    if (this.hasParent) {
      this.dirService.getDirById(this.currentDir!.parent!).pipe(takeUntil(this._destroy$)).subscribe({
        next: resp => {
          this.currentDir = resp.body!;
          this.dirSelectEmitter.emit(this.currentDir!.id);
          this.hasParent = this.currentDir!.parent !== null;
          this.fetchFilesInDir();
          this.fetchSubdirsInDir();
        },
        error: err => {
          console.log(err);
        }
      });
    }
  }

  fetchSharedParent(): void {
    if (this.sharedCurrentDir!.id == this.sharedLocalBaseId) {
      this.sharedCurrentDir = undefined;
      this.sharedLocalBaseId = undefined;
      this.fetchADs();
      this.fetchAFs();
    } else {
      this.dirService.getDirById(this.sharedCurrentDir!.parent!).pipe(takeUntil(this._destroy$)).subscribe({
        next: resp => {
          this.sharedCurrentDir = resp.body!;
          this.fetchSharedFilesInDir();
          this.fetchSharedSubdirsInDir();
        },
        error: err => {
          console.log(err);
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
        console.log(err);
      }
    });
  }

  //TODO for some reason, passing the file array as parameter doesnt seem to modify it by reference
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
        console.log(err);
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
        console.log(err);
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
        console.log(err);
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
        console.log(err);
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
          console.log(err);
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
        console.log(err);
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
          console.log(err);
        }
      })
    }
  }

}
