import { Component, OnInit } from '@angular/core';
import { User } from '../model/user';
import { StorageService } from '../service/storage.service';
import { UserService } from '../service/user.service';
import { MatCardModule } from '@angular/material/card';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormsModule, FormBuilder,
  FormControl,Validators, ReactiveFormsModule } from '@angular/forms';
  import { MatFormFieldModule } from '@angular/material/form-field';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatExpansionModule} from '@angular/material/expansion';
import { Subject, takeUntil } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';
import { NotifsViewComponent } from '../notifs-view/notifs-view.component';
import { AuthService } from '../service/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [MatCardModule, FormsModule,
    CommonModule,
    HttpClientModule,
    MatFormFieldModule,
    MatInputModule,
    ReactiveFormsModule,
    MatButtonModule,
    FlexLayoutModule,
    NotifsViewComponent,
    MatExpansionModule,
    MatIconModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent implements OnInit {

  form = this.fb.group({
    username: new FormControl('', Validators.required),
    name: new FormControl(''),
    email: new FormControl('', Validators.email),
  });

  passwordForm = this.fb.group({
    oldPassword: new FormControl('', Validators.required),
    newPassword: new FormControl('', Validators.required)
  })
  user?: User;
  private readonly _destroy$ = new Subject<void>();

  constructor (private storageService: StorageService, private userService: UserService, private readonly fb: FormBuilder, private snackBar: MatSnackBar, 
    private readonly authService: AuthService, private readonly router: Router) {}

  ngOnInit(): void {
    this.user = this.storageService.getUser();
    this.form.controls.username.setValue(this.user?.username!);
    this.form.controls.name.setValue(this.user?.name ?? null);
    this.form.controls.email.setValue(this.user?.email ?? null);
  }

  onSubmit() {
    this.user!.username = this.form.controls.username.value!;
    this.user!.name = this.form.controls.name.value!;
    this.user!.email = this.form.controls.email.value!;
    this.userService.updateUser(this.user!).subscribe({
      next: resp => {
        this.storageService.saveUser(resp.body!);
        this.user = resp.body!;
        window.location.reload();
      },
      error: err => {
        if (err.status == 403) {
          this.snackBar.open("This username is already taken!", undefined, {duration: 3000});
        } else if (err.status == 404 || err.status == 400) {
          this.snackBar.open("The user could not be found", undefined, {duration: 3000});
        } else {
          this.snackBar.open("Something went wrong...", undefined, {duration: 3000});
        }
      }
    })
  }

  onDiscard() {
    this.form.controls.username.setValue(this.user?.username!);
    this.form.controls.name.setValue(this.user?.name ?? null);
    this.form.controls.email.setValue(this.user?.email ?? null);
  }

  onChangePassword() {
    this.authService.changePassword(this.passwordForm.controls.oldPassword.value!, this.passwordForm.controls.newPassword.value!).pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.snackBar.open("You've successfully changed your password", undefined, {duration: 3000});
      },
      error: err => {
        if (err.status == 403) {
          this.snackBar.open("Your password was incorrect", undefined, {duration: 3000});
        } else if (err.status == 200) {
          this.snackBar.open("You've successfully changed your password", undefined, {duration: 3000});
        } else {
          this.snackBar.open("Something went wrong...", undefined, {duration: 3000});
        }
      }
    })
  }

  deleteAccount() {
    this.userService.deleteMyUser().pipe(takeUntil(this._destroy$)).subscribe({
      next: resp => {
        this.storageService.clean();
        this.router.navigate(['/']).then(() => {
          window.location.reload();
        });
      },
      error: err => {
        this.storageService.clean();
        this.router.navigate(['/']).then(() => {
          window.location.reload();
        });
      }
    })
  }

}
