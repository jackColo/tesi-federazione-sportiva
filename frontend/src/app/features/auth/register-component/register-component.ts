import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faEye, faEyeSlash } from '@fortawesome/free-solid-svg-icons';
import { CreateClubDTO, ErrorResponse } from '../../../models/dtos';
import { AffiliationStatus } from '../../../enums/affiliation-status.enum';
import { Role } from '../../../enums/role.enum';
import { ClubService } from '../../../core/services/club.service';
import { lastValueFrom } from 'rxjs';

@Component({
  selector: 'app-register-component',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterLink, FontAwesomeModule],
  templateUrl: './register-component.html',
})
export class RegisterComponent {
  private userService = inject(ClubService);
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
        manager: {
          firstName: this.createClubForm.value.manager?.firstName!,
          lastName: this.createClubForm.value.manager?.lastName!,
          email: this.createClubForm.value.manager?.email!,
          password: this.createClubForm.value.manager?.password!,
          role: Role.CLUB_MANAGER,
        },
      };
    } else {
      console.error('Form is invalid');
      return;
    }

    try {
      const result = await lastValueFrom(this.userService.createClub(club));
      console.log('Club creato con successo:', result);
      alert('Club creato con successo! Puoi accedere come manager con le credenzili indicate in fase di registrazione.',);
      this.router.navigate(['/auth/login']); 

    } catch (err: ErrorResponse | any) {
        alert("Errore durante la registrazione: "+ err.error.message);
    } finally {
      this.isSubmitting = false;
    }
  }
}
