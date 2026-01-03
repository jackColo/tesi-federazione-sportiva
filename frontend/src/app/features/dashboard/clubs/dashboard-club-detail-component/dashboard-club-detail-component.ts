import { CommonModule } from '@angular/common';
import { Component, computed, effect, inject, input, InputSignal, Signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import {
  faArrowLeft,
  faBan,
  faBuilding,
  faCheck,
  faCheckCircle,
  faEnvelope,
  faInfoCircle,
  faMapMarkerAlt,
  faPaperPlane,
  faPen,
  faSave,
  faTimes,
} from '@fortawesome/free-solid-svg-icons';
import { switchMap } from 'rxjs';
import { AuthService } from '../../../../core/services/auth.service';
import { ClubService } from '../../../../core/services/club.service';
import {
  AffiliationStatus,
  affiliationStatusColorClass,
  readableAffiliationStatus,
} from '../../../../enums/affiliation-status.enum';
import { Role } from '../../../../enums/role.enum';
import { Club } from '../../../../models/club.model';
import { ErrorResponse } from '../../../../models/dtos';

@Component({
  selector: 'app-dashboard-club-detail-component',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule, FontAwesomeModule],
  templateUrl: './dashboard-club-detail-component.html',
})
export class DashboardClubDetailComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private clubService = inject(ClubService);
  id: InputSignal<string> = input.required<string>();
  isEditing = false;

  icons = {
    faBuilding,
    faCheck,
    faBan,
    faSave,
    faArrowLeft,
    faPen,
    faTimes,
    faMapMarkerAlt,
    faEnvelope,
    faInfoCircle,
    faPaperPlane,
    faCheckCircle,
  };

  // Form Definition
  form: FormGroup = this.fb.group({
    name: ['', Validators.required],
    fiscalCode: ['', Validators.required],
    // email: ['', [Validators.required, Validators.email]],
    // phone: ['', Validators.required],
    // presidentName: ['', Validators.required],
    legalAddress: ['', Validators.required],
    // city: [''],
  });

  club: Signal<Club | null> = toSignal(
    toObservable(this.id).pipe(switchMap((id) => this.clubService.getClub(id))),
    { initialValue: null }
  );

  isFederation = computed(() => this.authService.userRole() === Role.FEDERATION_MANAGER);
  isClubManager = computed(() => this.authService.userRole() === Role.CLUB_MANAGER);

  constructor() {
    effect(() => {
      const c = this.club();
      if (c) {
        this.form.patchValue({
          name: c.name,
          fiscalCode: c.fiscalCode,
          legalAddress: c.legalAddress,
        });
        this.form.disable();
      }
    });
  }

  toggleEdit() {
    this.isEditing = true;
    this.form.enable();
  }

  cancelEdit() {
    this.isEditing = false;
    this.form.disable();
    const c = this.club();
    if (c) {
      this.form.patchValue({
        name: c.name,
        fiscalCode: c.fiscalCode,
        legalAddress: c.legalAddress,
      });
    }
  }

  saveChanges() {
    if (this.form.invalid || !this.club() || !this.club()?.id) return;

    const updatedClub = new Club({...this.form.getRawValue(), id: this.id()})

    this.clubService.updateClub(this.id(), updatedClub).subscribe({
      next: (res) => {
        alert('Dati aggiornati con successo');
        this.isEditing = false;
        this.form.disable();
      },
      error: (err: ErrorResponse) => alert("Errore nell'invio della richiesta"),
    });
  }

  approveAffiliation() {
    if (!this.club()) return;
    this.clubService
      .updateAffiliationStatus(this.club()!.id, AffiliationStatus.ACCEPTED)
      .subscribe(() => {
        alert('Affiliazione approvata');
      });
  }

  rejectAffiliation() {
    if (!this.club()) return;
    this.clubService
      .updateAffiliationStatus(this.club()!.id, AffiliationStatus.REJECTED)
      .subscribe(() => alert('Affiliazione rifiutata'));
  }

  resubmitAffiliation() {
    if (!this.club()) return;
    this.clubService
      .renewClubAffiliationRequest(this.club()!.id)
      .subscribe({
        next: () => {
          alert('Richiesta inviata con successo!');
          this.isEditing = false;
        },
        error: (err: ErrorResponse) => alert("Errore nell'invio della richiesta"),
      });
  }

  statusColorClass(status: AffiliationStatus) {
    return affiliationStatusColorClass(status);
  }

  readableStatusName(status: AffiliationStatus) {
    return readableAffiliationStatus(status);
  }
}
