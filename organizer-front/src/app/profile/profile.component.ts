import { Component, OnInit } from '@angular/core';
import { User } from '../model/user';
import { StorageService } from '../service/storage.service';
import { UserService } from '../service/user.service';
import { MatCardModule } from '@angular/material/card';
import { FormsModule, FormBuilder,
  FormControl,Validators, ReactiveFormsModule } from '@angular/forms';
  import { MatFormFieldModule } from '@angular/material/form-field';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { Subject, takeUntil } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [MatCardModule, FormsModule,
    CommonModule,
    HttpClientModule,
    MatFormFieldModule,
    MatInputModule,
    ReactiveFormsModule,
    MatButtonModule,],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent implements OnInit {

  form = this.fb.group({
    username: new FormControl('', Validators.required),
    name: new FormControl(''),
    email: new FormControl('', Validators.email),
  });
  user?: User;
  private readonly _destroy$ = new Subject<void>();

  constructor (private storageService: StorageService, private userService: UserService, private readonly fb: FormBuilder, private snackBar: MatSnackBar,) {}

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
    console.log(this.user);
    this.userService.updateUser(this.user!).subscribe({
      next: resp => {
        console.log(resp);
        this.storageService.saveUser(resp.body!);
        this.user = resp.body!;
        window.location.reload();
      },
      error: err => {
        console.log(err);
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

}
