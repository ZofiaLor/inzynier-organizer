import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
import { TableBaseComponent } from '../table-base/table-base.component';
import {MatCardModule} from '@angular/material/card';
import {MatListModule, MatSelectionList} from '@angular/material/list';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import { UserService } from '../service/user.service';
import { StorageService } from '../service/storage.service';
import { AccessService } from '../service/access.service';
import { Subject, takeUntil } from 'rxjs';
import { User } from '../model/user';
import { AccessFile } from '../model/access-file';
import { AccessDir } from '../model/access-dir';

@Component({
  selector: 'app-manage-access',
  standalone: true,
  imports: [MatListModule, MatIconModule, MatButtonModule, TableBaseComponent, MatCardModule],
  templateUrl: './manage-access.component.html',
  styleUrl: './manage-access.component.scss'
})
export class ManageAccessComponent implements OnChanges, OnInit{
  @Input() sharedItemId?: number;
  @Input() isSharedFile = false;
  @Output() backEmitter = new EventEmitter();
  @ViewChild('userAdded', {static: true}) userAddedList!: MatSelectionList;
  private readonly _destroy$ = new Subject<void>();
  userBrowseData: User[] = [];
  userAddedData: User[] = [];
  user?: User;
  afs: AccessFile[] = [];
  ads: AccessDir[] = [];
  columns: string[] = ["id", "username", "name"];

  constructor (private readonly userService: UserService, private readonly storageService: StorageService, private readonly accessService: AccessService) {}
  ngOnChanges(changes: SimpleChanges): void {
    if (this.sharedItemId) {
      if (this.isSharedFile) {
        this.fetchAFs();
      } else {
        this.fetchADs();
      }
    }
  }

  ngOnInit(): void {
    this.userService.getAllUsersSafe().pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.userBrowseData = resp.body!;
      },
      error: err => {
        console.log(err);
      }
    });
  }

  onGrantEdit() {
    console.log(this.userAddedList.selectedOptions);
    if (this.isSharedFile) {
      for (var selected of this.userAddedList.selectedOptions.selected) {
        let af: AccessFile = {id: {userId: this.userAddedData[parseInt(selected.value)].id, fileId: this.sharedItemId!}, accessPrivilege: 2};
        this.modifyAF(af);
        this.afs[parseInt(selected.value)].accessPrivilege = 2;
      }
    } else {
      for (var selected of this.userAddedList.selectedOptions.selected) {
        let ad: AccessDir = {id: {userId: this.userAddedData[parseInt(selected.value)].id, directoryId: this.sharedItemId!}, accessPrivilege: 2};
        this.modifyAD(ad);
        this.ads[parseInt(selected.value)].accessPrivilege = 2;
      }
    }
  }

  onGrantView() {
    console.log(this.userAddedList.selectedOptions.selected[0].value);
    if (this.isSharedFile) {
      for (var selected of this.userAddedList.selectedOptions.selected) {
        let af: AccessFile = {id: {userId: this.userAddedData[parseInt(selected.value)].id, fileId: this.sharedItemId!}, accessPrivilege: 1};
        this.modifyAF(af);
        this.afs[parseInt(selected.value)].accessPrivilege = 1;
      }
    } else {
      for (var selected of this.userAddedList.selectedOptions.selected) {
        let ad: AccessDir = {id: {userId: this.userAddedData[parseInt(selected.value)].id, directoryId: this.sharedItemId!}, accessPrivilege: 1};
        this.modifyAD(ad);
        this.ads[parseInt(selected.value)].accessPrivilege = 1;
      }
    }
  }

  onForbidAccess() {
    console.log(this.userAddedList.selectedOptions.selected[0].value);
  }

  selectNewUser(id: number): void {
    
  }

  selectAddedUser(id: number): void {
    
  }

  fetchAFs(): void {
    this.accessService.getAFsByFileId(this.sharedItemId!).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.afs = resp.body!;
        this.fetchUsersFromAF();
      },
      error: err => {
        console.log(err);
      }
    })
  }

  fetchADs(): void {
    this.accessService.getADsByDirId(this.sharedItemId!).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.ads = resp.body!;
        this.fetchUsersFromAD();
      },
      error: err => {
        console.log(err);
      }
    })
  }

  fetchUsersFromAF(): void {
    this.userAddedData = [];
    for (var af of this.afs) {
      console.log(af);
      this.userService.getUserByIdSafe(af.id.userId).pipe(takeUntil(this._destroy$)).subscribe({
        next: resp => {
          this.userAddedData.push(resp.body!);
        },
        error: err => {
          console.log(err);
        }
      })
    }
  }

  fetchUsersFromAD(): void {
    this.userAddedData = [];
    for (var ad of this.ads) {
      this.userService.getUserByIdSafe(ad.id.userId).pipe(takeUntil(this._destroy$)).subscribe({
        next: resp => {
          this.userAddedData.push(resp.body!);
        },
        error: err => {
          console.log(err);
        }
      })
    }
  }

  modifyAF(af: AccessFile): void {
    this.accessService.modifyAF(af).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        console.log(resp.body!);
      },
      error: err => {
        console.log(err);
      }
    })
  }

  modifyAD(ad: AccessDir): void {
    this.accessService.modifyAD(ad).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        console.log(resp.body!);
      },
      error: err => {
        console.log(err);
      }
    })
  }

}
