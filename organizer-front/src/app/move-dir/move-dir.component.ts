import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import {MatListModule} from '@angular/material/list';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatTooltipModule} from '@angular/material/tooltip';
import { DirectoryService } from '../service/directory.service';
import { Directory } from '../model/directory';
import { Subject, takeUntil } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-move-dir',
  standalone: true,
  imports: [MatListModule, MatIconModule, MatButtonModule, MatTooltipModule],
  templateUrl: './move-dir.component.html',
  styleUrl: './move-dir.component.scss'
})
export class MoveDirComponent implements OnChanges {
  private readonly _destroy$ = new Subject<void>();

  @Input() origParentId?: number;
  @Input() movedDirId?: number;
  @Output() moveEmitter = new EventEmitter<number | undefined>();
  origDir?: Directory;
  currentDir?: Directory;
  dirs: Directory[] = [];
  hasParent = false;

  constructor (private readonly dirService: DirectoryService, private snackBar: MatSnackBar) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (this.origParentId == undefined) return;
    this.dirService.getDirById(this.origParentId!).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.origDir = resp.body!;
        this.currentDir = this.origDir;
        this.hasParent = this.currentDir!.parent !== null;
        this.fetchSubdirsInDir();
      },
      error: err => {
        this.getterErrors(err);
      }
    })
  }

  clickDir(id: number) {
    this.fetchDirById(id);
  }

  clickUpDir(): void {
    this.fetchParent();
  }

  confirmMove(): void {
    this.moveEmitter.emit(this.currentDir!.id);
  }

  cancelMove(): void {
    this.moveEmitter.emit(undefined);
  }

  fetchParent(): void {
    if (this.hasParent) {
      this.dirService.getDirById(this.currentDir!.parent!).pipe(takeUntil(this._destroy$)).subscribe({
        next: resp => {
          this.currentDir = resp.body!;
          this.hasParent = this.currentDir!.parent !== null;
          this.fetchSubdirsInDir();
        },
        error: err => {
          this.getterErrors(err);
        }
      });
    }
  }

  fetchDirById(id: number): void {
    this.dirService.getDirById(id).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.currentDir = resp.body!;
        this.hasParent = this.currentDir!.parent !== null;
        this.fetchSubdirsInDir();
      },
      error: err => {
        this.getterErrors(err);
      }
    });
  }

  fetchSubdirsInDir(): void {
    if (this.currentDir === undefined) return;
    this.dirService.getDirsByParentId(this.currentDir!.id).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.dirs = resp.body!;
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
        this.getterErrors(err);
      }
    });
  }

  getterErrors(err: any) {
    if (err.status == 403) {
      this.snackBar.open("You don't have access to view this folder", undefined, {duration: 3000});
    } else if (err.status == 404) {
      this.snackBar.open("This folder could not be found", undefined, {duration: 3000});
    } else {
      this.snackBar.open("Something went wrong...", undefined, {duration: 3000});
    }
  }
}
