import { Component, OnInit } from '@angular/core';
import {MatListModule} from '@angular/material/list';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import { FileService } from '../service/file.service';
import { Subject, takeUntil } from 'rxjs';
import { EventFile } from '../model/event';
import { TaskFile } from '../model/task';
import { DirectoryService } from '../service/directory.service';
import { Directory } from '../model/directory';

@Component({
  selector: 'app-file-tree',
  standalone: true,
  imports: [MatListModule, MatIconModule, MatButtonModule],
  templateUrl: './file-tree.component.html',
  styleUrl: './file-tree.component.scss'
})
export class FileTreeComponent implements OnInit{

  constructor (private readonly fileService: FileService, private readonly dirService: DirectoryService) {}

  files: File[] = [];
  dirs: Directory[] = [];
  currentDir?: Directory;
  hasParent: boolean = false;
  private readonly _destroy$ = new Subject<void>();

  ngOnInit(): void {
    this.fetchBaseDirs();
  }

  isEvent(file: any): file is EventFile {
    return (file as EventFile).startDate !== undefined;
  }

  isTask(file: any): file is TaskFile {
    return (file as TaskFile).deadline !== undefined;
  }

  clickDir(dirId: number): void {
    this.fetchById(dirId);
  }

  clickUpDir(): void {
    this.fetchParent();
  }

  fetchBaseDirs(): void {
    this.dirService.getCurrentUsersBaseDirs().pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        console.log(resp);
        this.dirs = resp.body!;
        this.hasParent = false;
        this.files = [];
      },
      error: err => {
        console.log(err);
      }
    });
  }

  fetchById(id: number): void {
    this.dirService.getDirById(id).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        console.log(resp);
        this.currentDir = resp.body!;
        this.hasParent = this.currentDir!.parent !== null;
        this.fetchFilesInDir();
        this.fetchSubdirsInDir();
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
          console.log(resp);
          this.currentDir = resp.body!;
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

  fetchFilesInDir(): void {
    if (this.currentDir === undefined) return;
    this.fileService.getFilesInDirectory(this.currentDir!.id).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        console.log(resp);
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

  fetchSubdirsInDir(): void {
    if (this.currentDir === undefined) return;
    this.dirService.getDirsByParentId(this.currentDir!.id).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        console.log(resp);
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

}
