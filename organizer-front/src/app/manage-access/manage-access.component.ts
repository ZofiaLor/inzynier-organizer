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
import { MatSnackBar } from '@angular/material/snack-bar';

interface UserAccess {
  user: User,
  af?: AccessFile,
  ad?: AccessDir
}

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
  user?: User;
  afs: AccessFile[] = [];
  ads: AccessDir[] = [];
  userAccess: UserAccess[] = [];
  columns: string[] = ["id", "username", "name"];

  constructor (private readonly userService: UserService, private readonly storageService: StorageService, private readonly accessService: AccessService, private snackBar: MatSnackBar) {}
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
      }
    });
  }

  onGrantAccess(access: number) {
    if (this.isSharedFile) {
      for (var selected of this.userAddedList.selectedOptions.selected) {
        let af: AccessFile = {id: {userId: this.userAccess[parseInt(selected.value)].user.id, fileId: this.sharedItemId!}, accessPrivilege: access};
        this.modifyAF(af);
        this.userAccess[parseInt(selected.value)].af!.accessPrivilege = access;
      }
    } else {
      for (var selected of this.userAddedList.selectedOptions.selected) {
        let ad: AccessDir = {id: {userId: this.userAccess[parseInt(selected.value)].user.id, directoryId: this.sharedItemId!}, accessPrivilege: access};
        this.modifyAD(ad);
        this.userAccess[parseInt(selected.value)].ad!.accessPrivilege = access;
      }
    }
  }

  onGrantAccessNew(access: number) {
    if (this.user === undefined) return;
    if (this.isSharedFile) {
      let af: AccessFile = {id: {userId: this.user.id, fileId: this.sharedItemId!}, accessPrivilege: access};
      this.modifyAF(af);
    } else {
      let ad: AccessDir = {id: {userId: this.user.id, directoryId: this.sharedItemId!}, accessPrivilege: access};
      this.modifyAD(ad);
    }
  }

  onForbidAccess() {
    if (this.isSharedFile) {
      for (var selected of this.userAddedList.selectedOptions.selected) {
        this.deleteAF(this.userAccess[parseInt(selected.value)].user.id, this.sharedItemId!);
      }
      this.fetchAFs();
    } else {
      for (var selected of this.userAddedList.selectedOptions.selected) {
        this.deleteAD(this.userAccess[parseInt(selected.value)].user.id, this.sharedItemId!);
      }
      this.fetchADs();
    }
  }

  selectNewUser(id: number): void {
    if (id != this.storageService.getUser()!.id){
      this.fetchUserById(id);
    } else {
      this.snackBar.open("You already have the access :)", undefined, {duration: 3000});
    }
    
  }
  fetchAFs(): void {
    this.accessService.getAFsByFileId(this.sharedItemId!).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.afs = resp.body!;
        this.fetchUsersFromAF();
      },
      error: err => {
        this.snackBar.open("Something went wrong...", undefined, {duration: 3000});
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
        this.snackBar.open("Something went wrong...", undefined, {duration: 3000});
      }
    })
  }

  fetchUsersFromAF(): void {
    this.userAccess = [];
    for (var af of this.afs) {
      let localAf = af;
      this.userService.getUserByIdSafe(af.id.userId).pipe(takeUntil(this._destroy$)).subscribe({
        next: resp => {
          var ua: UserAccess = {user: resp.body!, af: localAf};
          this.userAccess.push(ua);
        },
        error: err => {
          this.accessErrors(err, "User");
        }
      })
    }
    console.log(this.userAccess);
  }

  fetchUsersFromAD(): void {
    for (var ad of this.ads) {
      let localAd = ad;
      this.userService.getUserByIdSafe(ad.id.userId).pipe(takeUntil(this._destroy$)).subscribe({
        next: resp => {
          var ua: UserAccess = {user: resp.body!, ad: localAd};
          this.userAccess.push(ua);
        },
        error: err => {
          this.accessErrors(err, "User");
        }
      })
    }
  }

  fetchUserById(id: number): void {
    this.userService.getUserByIdSafe(id).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.user = resp.body!;
      },
      error: err => {
        this.accessErrors(err, "User");
      }
    })
  }

  modifyAF(af: AccessFile): void {
    this.accessService.modifyAF(af).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        if (!this.userAccess.some(ua => ua.user.id === resp.body!.id.userId) && this.user) {
          this.afs.push(resp.body!);
          
          this.userAccess.push({user: this.user, af: resp.body!});
          this.user = undefined;
        } else {
          this.userAccess[this.userAccess.findIndex(ua => ua.user.id === resp.body!.id.userId)].af!.accessPrivilege = resp.body!.accessPrivilege;
        }
      },
      error: err => {
        this.accessErrors(err, "Access entry");
      }
    });
  }

  modifyAD(ad: AccessDir): void {
    this.accessService.modifyAD(ad).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        if (!this.userAccess.some(ua => ua.user.id === resp.body!.id.userId) && this.user) {
          this.ads.push(resp.body!);
          
          this.userAccess.push({user: this.user, ad: resp.body!});
          this.user = undefined;
        } else {
          this.userAccess[this.userAccess.findIndex(ua => ua.user.id === resp.body!.id.userId)].ad!.accessPrivilege = resp.body!.accessPrivilege;
        }
      },
      error: err => {
        this.accessErrors(err, "Access entry");
      }
    });
  }

  deleteAF(userId: number, fileId: number) {
    this.accessService.deleteAF(userId, fileId).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.userAccess = this.userAccess.filter(ua => ua.user.id != userId);
      },
      error: err => {
        this.accessErrors(err, "Access entry");
      }
    })
  }

  deleteAD(userId: number, dirId: number) {
    this.accessService.deleteAD(userId, dirId).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.userAccess = this.userAccess.filter(ua => ua.user.id != userId);
      },
      error: err => {
        this.accessErrors(err, "Access entry");
      }
    })
  }

  accessErrors(err: any, element: string) {
    if (err.status == 404) {
      this.snackBar.open(element+ " not found", undefined, {duration: 3000});
    } else {
      this.snackBar.open("Something went wrong...", undefined, {duration: 3000});
    }
  }

}
