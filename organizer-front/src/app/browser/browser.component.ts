import { Component, OnInit, ViewChild } from '@angular/core';
import { StorageService } from '../service/storage.service';
import { User } from '../model/user';
import { File } from '../model/file';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatCardModule } from '@angular/material/card';
import {MatIconModule} from '@angular/material/icon';
import { FileTreeComponent } from '../file-tree/file-tree.component';
import { FileViewComponent } from '../file-view/file-view.component';

@Component({
  selector: 'app-browser',
  standalone: true,
  imports: [FlexLayoutModule, MatCardModule, FileTreeComponent, FileViewComponent, MatIconModule],
  templateUrl: './browser.component.html',
  styleUrl: './browser.component.scss'
})
export class BrowserComponent implements OnInit{

  constructor (private readonly storageService: StorageService) {}

  // https://www.digitalocean.com/community/tutorials/angular-viewchild-access-component
  @ViewChild(FileTreeComponent) fileTreeComponent!: FileTreeComponent;
  @ViewChild(FileViewComponent) fileViewComponent!: FileViewComponent;
  
  user?: User;
  currentFile?: File;
  currentDirId?: number;
  createdType?: number;

  ngOnInit(): void {
    this.user = this.storageService.getUser();
  }

  itemSelected(): void {
    this.createdType = undefined;
    this.fileViewComponent.onSelect();
  }

  dirSelected(dirId: number): void {
    this.currentDirId = dirId;
  }

  createNewSelected(): void {
    this.currentFile = undefined;
    this.fileViewComponent.onNew();
  }

  refreshDirs(): void {
    this.fileTreeComponent.fetchSubdirsInDir();
    this.fileTreeComponent.fetchFilesInDir();
  }

}
