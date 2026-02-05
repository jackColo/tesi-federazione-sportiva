import { CommonModule } from '@angular/common';
import { Component, computed, inject, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import {
  faArrowLeft,
  faBuilding,
  faCheck,
  faEnvelope,
  faExclamationTriangle,
  faLock,
  faShieldAlt,
  faTimes,
  faUser,
  faVenusMars,
} from '@fortawesome/free-solid-svg-icons';
import { AuthService } from '../../../../core/services/auth.service';
import { ClubService } from '../../../../core/services/club.service';
import { UserService } from '../../../../core/services/user.service';
import { GenderEnum } from '../../../../enums/gender.enum';
import { readableRole, Role } from '../../../../enums/role.enum';
import { Club } from '../../../../models/club.model';
import { CreateUserDTO, ErrorResponse } from '../../../../models/dtos';

@Component({
  selector: 'app-dashboard-user-form-component',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, FontAwesomeModule],
  templateUrl: './dashboard-user-form-component.html',
})
export class DashboardUserFormComponent {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private authService = inject(AuthService);
  private clubService = inject(ClubService);
  private userService = inject(UserService);

  icons = {
    faArrowLeft,
    faEnvelope,
    faShieldAlt,
    faBuilding,
    faCheck,
    faTimes,
    faUser,
    faLock,
    faVenusMars,
    faExclamationTriangle
  };

  availableClubs: Signal<Club[]> = toSignal(this.clubService.getAllClubs(), {
    initialValue: [],
  });

  isAdmin = computed(() => this.authService.userRole() === Role.FEDERATION_MANAGER);

  form = this.fb.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    role: [null as Role | null, Validators.required],
    clubId: [null as string | null],
    birthDate: [null as string | null],
    weight: [null as number | null],
    height: [null as number | null],
    gender: [null as GenderEnum | null],
    medicalCertificateNumber: [null as string | null],
    medicalCertificateExpireDate: [null as string | null],
  });

  constructor() {
    this.form.get('role')?.valueChanges.subscribe((value) => {
      if (value && value === Role.ATHLETE || value === Role.CLUB_MANAGER) {
        this.form.get('clubId')?.setValidators([Validators.required]);
      } else {
        this.form.get('clubId')?.clearValidators();
      }
      this.form.get('clubId')?.updateValueAndValidity({ emitEvent: false });
    });
  }

  availableRoles = this.isAdmin()
    ? [
        {
          id: Role.CLUB_MANAGER,
          name: readableRole(Role.CLUB_MANAGER),
        },
        {
          id: Role.FEDERATION_MANAGER,
          name: readableRole(Role.FEDERATION_MANAGER),
        },
        {
          id: Role.ATHLETE,
          name: readableRole(Role.ATHLETE),
        },
      ]
    : [
        {
          id: Role.ATHLETE,
          name: readableRole(Role.ATHLETE),
        },
      ];

  getErrorMessage(controlName: string): string {
    const control = this.form.get(controlName);

    if (control?.hasError('required')) {
      return 'Questo campo è obbligatorio';
    }
    if (control?.hasError('email')) {
      return 'Inserisci un indirizzo email valido';
    }
    if (control?.hasError('minlength')) {
      const requiredLength = control.errors?.['minlength'].requiredLength;
      return `La password deve essere di almeno ${requiredLength} caratteri`;
    }

    return '';
  }

  shouldShowError(controlName: string): boolean {
    const control = this.form.get(controlName);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  onSubmit() {
    if (this.form.valid) {
      let newUser: CreateUserDTO;
      switch (this.form.value.role) {
        case Role.ATHLETE:
          newUser = {
            firstName: this.form.value.firstName!,
            lastName: this.form.value.lastName!,
            email: this.form.value.email!,
            password: this.form.value.password!,
            role: this.form.value.role!,
            birthDate: this.form.value.birthDate!,
            weight: this.form.value.weight!,
            height: this.form.value.height!,
            gender: this.form.value.gender!,
            medicalCertificateNumber: this.form.value.medicalCertificateNumber!,
            medicalCertificateExpireDate: this.form.value.medicalCertificateExpireDate!,
            clubId: this.form.value.clubId!,
          };
          break;
        case Role.CLUB_MANAGER:
          newUser = {
            firstName: this.form.value.firstName!,
            lastName: this.form.value.lastName!,
            email: this.form.value.email!,
            password: this.form.value.password!,
            role: this.form.value.role!,
            clubId: this.form.value.clubId!,
          };
          break;
        case Role.FEDERATION_MANAGER:
          newUser = {
            firstName: this.form.value.firstName!,
            lastName: this.form.value.lastName!,
            email: this.form.value.email!,
            password: this.form.value.password!,
            role: this.form.value.role!,
          };
          break;
        default:
          throw new Error('Ruolo utente non valido');
      }
      this.userService.createUser(newUser).subscribe({
        next: (user) => {
          alert(`Utente ${user.firstName} ${user.lastName} creato con successo!`);
          this.router.navigate(['/dashboard']);
        },
        error: (err: ErrorResponse) => {
          alert(`Errore durante la creazione dell'utente: ${err.error.message}`);
        },
      });
    }
  }
}
