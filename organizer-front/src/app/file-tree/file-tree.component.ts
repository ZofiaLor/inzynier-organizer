import { Component, OnInit } from '@angular/core';
import {MatListModule} from '@angular/material/list';
import {MatIconModule} from '@angular/material/icon';
import { FileService } from '../service/file.service';
import { Subject, takeUntil } from 'rxjs';
import { EventFile } from '../model/event';
import { TaskFile } from '../model/task';

@Component({
  selector: 'app-file-tree',
  standalone: true,
  imports: [MatListModule, MatIconModule],
  templateUrl: './file-tree.component.html',
  styleUrl: './file-tree.component.scss'
})
export class FileTreeComponent implements OnInit{

  constructor (private readonly fileService: FileService) {}

  files: File[] = [];
  private readonly _destroy$ = new Subject<void>();

  ngOnInit(): void {
    this.fileService.getCurrentUsersFiles().pipe(takeUntil(this._destroy$)).subscribe({
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

  isEvent(file: any): file is EventFile {
    return (file as EventFile).startDate !== undefined;
  }

  isTask(file: any): file is TaskFile {
    return (file as TaskFile).deadline !== undefined;
  }

}
