import { Component, OnInit } from '@angular/core';
import { StorageService } from '../service/storage.service';
import { User } from '../model/user';
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
  
  user?: User;

  ngOnInit(): void {
    this.user = this.storageService.getUser();
  }

}
