import { Component, inject, signal } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { LogUserDTO } from '../../../models/dtos';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { faEye, faEyeSlash } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeModule } from "@fortawesome/angular-fontawesome";

@Component({
  selector: 'app-login-component',
  imports: [
    ReactiveFormsModule,
    CommonModule,
    RouterLink,
    FontAwesomeModule
  ],
  templateUrl: './login-component.html',
  styleUrl: './login-component.scss',
})
export class LoginComponent {
  private authService= inject(AuthService);
  private fb = inject(FormBuilder);
  private router = inject(Router);

  public loginError: string = '';
  public faEye = faEye; 
  public faEyeSlash = faEyeSlash;
  public passwordVisible = signal<boolean>(false); 

  public togglePasswordVisibility(): void {
    this.passwordVisible.update(value => !value);
  }

  public loginForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]]
  });

  async onSubmit() {
    let userCredentials: LogUserDTO;

    if (this.loginForm.valid) {
      userCredentials = {
        email: this.loginForm.value.email!,
        password: this.loginForm.value.password!
      };
    } else {
      console.error('Form is invalid');
      return;
    }

    try {
      await this.authService.login(userCredentials);
      this.loginError = "";
      this.router.navigateByUrl('/dashboard');
    } catch (error) {
      this.loginError = 'Email o password errate.';
      console.error('Login failed', error);
    }
  }
}
