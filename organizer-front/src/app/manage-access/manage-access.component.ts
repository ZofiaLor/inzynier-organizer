import { Component, Input, OnInit } from '@angular/core';
import { TableBaseComponent } from '../table-base/table-base.component';
import { UserService } from '../service/user.service';
import { StorageService } from '../service/storage.service';
import { AccessService } from '../service/access.service';
import { Subject, takeUntil } from 'rxjs';
import { User } from '../model/user';

@Component({
  selector: 'app-manage-access',
  standalone: true,
  imports: [TableBaseComponent],
  templateUrl: './manage-access.component.html',
  styleUrl: './manage-access.component.scss'
})
export class ManageAccessComponent implements OnInit{
  @Input() sharedItemId?: number;
  @Input() isSharedFile = false;
  private readonly _destroy$ = new Subject<void>();
  userData: User[] = [];
  user?: User;
  columns: string[] = ["id", "username", "name"];

  constructor (private readonly userService: UserService, private readonly storageService: StorageService, private readonly accessService: AccessService) {}

  ngOnInit(): void {
    this.userService.getAllUsers().pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        console.log(resp);
        this.userData = resp.body!;
      },
      error: err => {
        console.log(err);
      }
    })
  }

  selectUser(id: number): void {
    
  }

}
