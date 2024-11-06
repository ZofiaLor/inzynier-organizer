import { Component, OnInit } from '@angular/core';
import { AuthService } from '../service/auth.service';
import { StorageService } from '../service/storage.service';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { FormsModule, FormBuilder,
  FormControl,Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { User } from '../model/user';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule,
    CommonModule,
    HttpClientModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    ReactiveFormsModule,
    MatButtonModule,
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent implements OnInit {
  form = this.fb.group({
    username: new FormControl('', Validators.required),
    password: new FormControl('', Validators.required),
  });
  isLoggedIn = false;
  isLoginFailed = false;
  user: User = {id: 0, username: "", role: "ROLE_USER"};

  constructor (private authService: AuthService, private storageService: StorageService, private readonly fb: FormBuilder, private snackBar: MatSnackBar,) {}

  ngOnInit(): void {
    if (this.storageService.isLoggedIn()) {
      this.isLoggedIn = true;
      this.user = this.storageService.getUser()!;
    }
  }

  onSubmit(): void {

    this.authService.login(this.form.controls.username.value!, this.form.controls.password.value!).subscribe({
      next: resp => {
        this.storageService.saveUser(resp.body);
        this.user = this.storageService.getUser()!;
        this.isLoginFailed = false;
        this.isLoggedIn = true;
        this.reloadPage();
      },
      error: err => {
        if (err.status == 403) {
          this.snackBar.open("Incorrect username or password", undefined, {duration: 3000});
        } else {
          this.snackBar.open("Something went wrong...", undefined, {duration: 3000});
        }
        this.isLoginFailed = true;
      }
    });
  }

  reloadPage(): void {
    window.location.reload();
  }

}
