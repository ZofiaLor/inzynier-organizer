import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar } from '@angular/material/snack-bar';
import { FormsModule, FormBuilder,
  FormControl,Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { AuthService } from '../service/auth.service';
import { User } from '../model/user';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule,
    CommonModule,
    HttpClientModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    ReactiveFormsModule,
    MatButtonModule,],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  form = this.fb.group({
    username: new FormControl('', Validators.required),
    password: new FormControl('', Validators.required),
    name: new FormControl(''),
    email: new FormControl(''),
  });

  constructor (private authService: AuthService, private readonly fb: FormBuilder, private snackBar: MatSnackBar) {}

  onSubmit(): void {
    const user: User = {
      id: 0,
      username: this.form.controls.username.value!,
      password: this.form.controls.password.value!,
      name: this.form.controls.name.value ?? undefined,
      email: this.form.controls.email.value ?? undefined,
      role: ""
    }

    this.authService.register(user).subscribe({
      next: data => {
        console.log(data);
        this.snackBar.open("You have successfully registered!", undefined, {duration: 3000});
      },
      error: err => {
        if (err.status == 403) {
          this.snackBar.open("This username is already taken", undefined, {duration: 3000});
        } else {
          this.snackBar.open("Something went wrong...", undefined, {duration: 3000});
        }
        
      }
    });
  }
}
