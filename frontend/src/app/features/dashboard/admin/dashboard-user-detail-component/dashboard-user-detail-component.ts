import { CommonModule } from '@angular/common';
import {
  Component,
  computed,
  effect,
  inject,
  input,
  InputSignal,
  output,
  Signal,
} from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import {
  faArrowLeft,
  faBuilding,
  faCheck,
  faEnvelope,
  faFileMedical,
  faIdBadge,
  faInfoCircle,
  faPen,
  faRulerVertical,
  faShieldAlt,
  faTimes,
  faTrash,
  faUser,
  faVenusMars,
  faWeight,
} from '@fortawesome/free-solid-svg-icons';
import { filter, switchMap } from 'rxjs';
import { AuthService } from '../../../../core/services/auth.service';
import { ClubService } from '../../../../core/services/club.service';
import { UserService } from '../../../../core/services/user.service';
import { readableRole, Role, roleClass } from '../../../../enums/role.enum';
import { Athlete } from '../../../../models/athlete.model';
import { Club } from '../../../../models/club.model';
import { ErrorResponse, UserDTO } from '../../../../models/dtos';
import { User } from '../../../../models/user.model';
import { GenderEnum } from '../../../../enums/gender.enum';

@Component({
  selector: 'app-dashboard-user-detail-component',
  standalone: true,
  imports: [CommonModule, RouterLink, FontAwesomeModule, ReactiveFormsModule],
  templateUrl: './dashboard-user-detail-component.html',
})
export class DashboardUserDetailComponent {
  private authService = inject(AuthService);
  private userService = inject(UserService);
  private clubService = inject(ClubService);
  private fb = inject(FormBuilder);
  id: InputSignal<string> = input.required<string>();
  isEditing = false;

  icons = {
    faArrowLeft,
    faPen,
    faTrash,
    faEnvelope,
    faShieldAlt,
    faBuilding,
    faIdBadge,
    faCheck,
    faTimes,
    faUser,
    faWeight,
    faRulerVertical,
    faVenusMars,
    faFileMedical,
    faInfoCircle,
  };

  isAdmin = computed(() => this.authService.userRole() === Role.FEDERATION_MANAGER);
  isClubManager = computed(() => this.authService.userRole() === Role.CLUB_MANAGER);

  user: Signal<User | null> = toSignal(
    toObservable(this.id).pipe(switchMap((id) => this.userService.getUserById(id))),
    { initialValue: null },
  );

  isTargetAthlete = computed(() => {
    const u = this.user();
    return u instanceof Athlete;
  });

  isMe = computed(() => {
    const u = this.user();
    return u ? u.id === this.authService.currentUserId() : false;
  });

  club: Signal<Club | null> = toSignal(
    toObservable(this.user).pipe(
      filter((u): u is User => !!u && !!u.clubId),
      switchMap((u) => this.clubService.getClub(u.clubId!)),
    ),
    { initialValue: null },
  );

  availableClubs: Signal<Club[]> = toSignal(this.clubService.getAllClubs(), { initialValue: [] });

  form = this.fb.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    clubId: [null as string | null],
    birthDate: [null as string | null],
    weight: [null as number | null],
    height: [null as number | null],
    gender: [null as GenderEnum | null],
    medicalCertificateNumber: [null as string | null],
    medicalCertificateExpireDate: [null as string | null],
    oldPassword: [''],
    newPassword: [''],
    confirmPassword: [''],
  });

  constructor() {
    effect(() => {
      this.patchValues(this.user());
    });

    // Se l'utente scrive qualcosa in newPassword, rendiamo obbligatori gli altri campi
    this.form.get('newPassword')?.valueChanges.subscribe((value) => {
      const oldPassCtrl = this.form.get('oldPassword');
      const confirmPassCtrl = this.form.get('confirmPassword');

      if (value && value.length > 0) {
        // Sto modificando la password -> Rendo tutto obbligatorio
        this.form.get('newPassword')?.setValidators([Validators.required, Validators.minLength(8)]);
        oldPassCtrl?.setValidators([Validators.required]);
        confirmPassCtrl?.setValidators([Validators.required]);
      } else {
        // Password vuota -> Tolgo i validatori
        this.form.get('newPassword')?.clearValidators();
        oldPassCtrl?.clearValidators();
        confirmPassCtrl?.clearValidators();
      }
      // Ricalcola lo stato di validazione
      this.form.get('newPassword')?.updateValueAndValidity({ emitEvent: false });
      oldPassCtrl?.updateValueAndValidity();
      confirmPassCtrl?.updateValueAndValidity();
    });
  }

  patchValues(u: User | null) {
    if (!u) return;

    this.form.patchValue({
      firstName: u.firstName,
      lastName: u.lastName,
      email: u.email,
      clubId: u.clubId,

      oldPassword: '',
      newPassword: '',
      confirmPassword: '',
    });

    if (u instanceof Athlete) {
      this.form.patchValue({
        birthDate: u.birthDate,
        weight: u.weight,
        height: u.height,
        gender: u.gender,
        medicalCertificateNumber: u.medicalCertificateNumber,
        medicalCertificateExpireDate: u.medicalCertificateExpireDate,
      });
    }
    this.form.disable();
  }

  toggleEdit() {
    this.isEditing = true;
    this.form.enable();
  }

  cancelEdit() {
    this.isEditing = false;
    this.patchValues(this.user());
  }

  onSubmit() {
    if (this.form.valid && this.user()) {
      const formValues = this.form.getRawValue();

      const currentUser = this.user()!;

      let updatedUser: UserDTO;

      switch (currentUser.role) {
        case Role.ATHLETE:
          updatedUser = {
            id: currentUser.id,
            firstName: formValues.firstName!,
            lastName: formValues.lastName!,
            email: formValues.email!,
            role: currentUser.role,
            clubId: formValues.clubId!,
            birthDate: formValues.birthDate!,
            weight: formValues.weight!,
            height: formValues.height!,
            gender: formValues.gender!,
            medicalCertificateNumber: formValues.medicalCertificateNumber!,
            medicalCertificateExpireDate: formValues.medicalCertificateExpireDate!,
            affiliationStatus: (currentUser as Athlete).affiliationStatus,
            affiliationDate: (currentUser as Athlete).affiliationDate,
            firstAffiliationDate: (currentUser as Athlete).firstAffiliationDate,
          };
          break;

        case Role.CLUB_MANAGER:
          updatedUser = {
            id: currentUser.id,
            firstName: formValues.firstName!,
            lastName: formValues.lastName!,
            email: formValues.email!,
            role: currentUser.role,
            clubId: formValues.clubId!,
          };
          break;

        case Role.FEDERATION_MANAGER:
          updatedUser = {
            id: currentUser.id,
            firstName: formValues.firstName!,
            lastName: formValues.lastName!,
            email: formValues.email!,
            role: currentUser.role,
          };
          break;

        default:
          throw new Error('Ruolo utente non supportato per la modifica');
      }

      this.userService.updateUser(updatedUser).subscribe({
        next: (newUser) => {
          this.isEditing = false;
          if (formValues.newPassword && formValues.newPassword.length > 0) {
            if (formValues.newPassword !== formValues.confirmPassword) {
              alert('Le password non coincidono!');
              return;
            }

            this.userService
              .changeUserPassword(this.user()!.id, formValues.oldPassword!, formValues.newPassword!)    
              .subscribe({
                next: () => {
                  alert('Utente e password aggiornati con successo! Effettua nuovamente il login.');
                  this.authService.logout();
                },
                error: (err: ErrorResponse) => {
                  alert('Errore cambio password: ' + err.error.message);
                },
              });
            return;
          }
          alert('Utente aggiornato con successo');
          this.patchValues(newUser);
          window.location.reload();
        },
        error: (err: ErrorResponse) => {
          alert("Errore durante l'aggiornamento: " + err.error.message);
        },
      });
    }
  }

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

  delete = output<void>();

  roleLabel = computed(() => {
    const u = this.user();
    if (u !== null) return readableRole(u.role);
    return '';
  });

  roleColorClass = computed(() => {
    const u = this.user();
    if (u !== null) return roleClass(u.role);
    return '';
  });
}
