import { Component, OnInit, ViewChild } from '@angular/core';
import { StorageService } from '../service/storage.service';
import { User } from '../model/user';
import { File } from '../model/file';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatCardModule } from '@angular/material/card';
import { FileTreeComponent } from '../file-tree/file-tree.component';
import { FileViewComponent } from '../file-view/file-view.component';

@Component({
  selector: 'app-browser',
  standalone: true,
  imports: [FlexLayoutModule, MatCardModule, FileTreeComponent, FileViewComponent],
  templateUrl: './browser.component.html',
  styleUrl: './browser.component.scss'
})
export class BrowserComponent implements OnInit{

  constructor (private readonly storageService: StorageService) {}

  @ViewChild(FileTreeComponent) fileTreeComponent!: FileTreeComponent;
  
  user?: User;
  currentFile?: File;
  currentDirId?: number;
  createdType?: number;

  ngOnInit(): void {
    this.user = this.storageService.getUser();
  }

  fileSelected(file: File): void {
    this.createdType = undefined;
    this.currentFile = file;
  }

  dirSelected(dirId: number): void {
    this.currentDirId = dirId;
  }

  createNewSelected(typeId: number): void {
    this.currentFile = undefined;
    this.createdType = typeId;
  }

  refreshDirs(): void {
    this.fileTreeComponent.fetchSubdirsInDir();
    this.fileTreeComponent.fetchFilesInDir();
  }

}
