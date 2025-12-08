import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faEye, faEyeSlash } from '@fortawesome/free-solid-svg-icons';
import { CreateClubDTO } from '../../../models/dtos';
import { AffiliationStatus } from '../../../enums/affiliation-status.enum';
import { Role } from '../../../enums/role.enum';

@Component({
  selector: 'app-register-component',
  imports: [ReactiveFormsModule, CommonModule, RouterLink, FontAwesomeModule],
  templateUrl: './register-component.html',
  styleUrl: './register-component.scss',
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  protected isSubmitting = false;
  protected faEye = faEye; 
  protected faEyeSlash = faEyeSlash;

  public passwordVisible = signal<boolean>(false);

  public togglePasswordVisibility(): void {
    this.passwordVisible.update((value) => !value);
  }

  public createClubForm = this.fb.group({
    club: this.fb.group({
      name: ['', [Validators.required]],
      fiscalCode: ['', [Validators.required]],
      legalAddress: ['', [Validators.required]],
    }),

    manager: this.fb.group({
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
    }),
  });

  async onSubmit() {
    this.isSubmitting = true;

    let club: CreateClubDTO;

    if (this.createClubForm.valid) {
      club = {
        name: this.createClubForm.value.club?.name!,
        fiscalCode: this.createClubForm.value.club?.fiscalCode!,
        legalAddress: this.createClubForm.value.club?.legalAddress!,
        affiliationStatus: AffiliationStatus.SUBMITTED,
        managers: [
          {
            firstName: this.createClubForm.value.manager?.firstName!,
            lastName: this.createClubForm.value.manager?.lastName!,
            email: this.createClubForm.value.manager?.email!,
            password: this.createClubForm.value.manager?.password!,
            role: Role.CLUB_MANAGER,
          },
        ],
      };
    } else {
      console.error('Form is invalid');
      return;
    }

    try {
      this.router.navigateByUrl('/auth/login');
    } catch (error) {
      // this.loginError = 'Email o password errate.';
      console.error('Login failed', error);
    }
  }
}
