import { CommonModule } from '@angular/common';
import { Component, computed, effect, inject, input, InputSignal, Signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import {
  faArrowLeft,
  faArrowRotateLeft,
  faBan,
  faCalendarAlt,
  faCheck,
  faClipboardList,
  faInfoCircle,
  faMapMarkerAlt,
  faPen,
  faSave,
  faTimes,
  faTrophy,
  faUsers,
} from '@fortawesome/free-solid-svg-icons';
import { of, switchMap } from 'rxjs';
import { AuthService } from '../../../../core/services/auth.service';
import { EventService } from '../../../../core/services/event.service';
import { CompetitionType } from '../../../../enums/competition-type.enum';
import {
  EventStatus,
  eventStatusColorClass,
  readableEventStatus,
} from '../../../../enums/event-status.enum';
import { Role } from '../../../../enums/role.enum';
import { Event } from '../../../../models/event.model';
import { ErrorResponse, EventDTO } from '../../../../models/dtos';
import { formatDateForInput } from '../../../../shared/utility/utils';

@Component({
  selector: 'app-dashboard-event-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FontAwesomeModule, RouterLink],
  templateUrl: './dashboard-event-form-component.html',
})
export class DashboardEventFormComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private eventService = inject(EventService);
  id: InputSignal<string | undefined> = input<string>();

  isEditing = false;
  competitionTypes = Object.values(CompetitionType);

  icons = {
    faArrowLeft,
    faPen,
    faSave,
    faTimes,
    faInfoCircle,
    faCheck,
    faBan,
    faTrophy,
    faMapMarkerAlt,
    faClipboardList,
    faCalendarAlt,
    faUsers,
    faArrowRotateLeft,
  };

  form: FormGroup = this.fb.group({
    name: ['', Validators.required],
    date: ['', Validators.required],
    location: ['', Validators.required],
    description: [''],
    registrationOpenDate: ['', Validators.required],
    registrationCloseDate: ['', Validators.required],
    disciplines: [[], Validators.required],
  });

  event: Signal<Event | null> = toSignal(
    toObservable(this.id).pipe(
      switchMap((id) => {
        if (!id || id === 'new') {
          const newEvent = new Event({
            id: '',
            name: '',
            description: '',
            location: '',
            date: '',
            registrationCloseDate: '',
            registrationOpenDate: '',
            status: EventStatus.SCHEDULED,
            enrolledCount: 0,
            disciplines: [],
          } as EventDTO);
          return of(newEvent);
        }
        return this.eventService.getEventById(id);
      })
    ),
    { initialValue: null }
  );

  isFederation = computed(() => this.authService.userRole() === Role.FEDERATION_MANAGER);

  constructor() {
    effect(() => {
      const evt = this.event();
      if (evt) {
        this.form.patchValue({
          name: evt.name,
          location: evt.location,
          description: evt.description,
          disciplines: evt.disciplines,
          date: formatDateForInput(evt.date),
          registrationOpenDate: formatDateForInput(evt.registrationOpenDate),
          registrationCloseDate: formatDateForInput(evt.registrationCloseDate),
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
    const evt = this.event();
    if (evt) {
      this.form.patchValue({
        name: evt.name,
        location: evt.location,
        description: evt.description,
        disciplines: evt.disciplines,
        date: formatDateForInput(evt.date),
        registrationOpenDate: formatDateForInput(evt.registrationOpenDate),
        registrationCloseDate: formatDateForInput(evt.registrationCloseDate),
      });
    }
  }

  saveChanges() {
    if (this.form.invalid || !this.event()) return;

    const rawData = this.form.getRawValue();

    const addFixedTime = (dateStr: string) => {
      return dateStr ? `${dateStr}T03:00:00` : null;
    };

    let updatedData = {
      ...rawData,
      date: addFixedTime(rawData.date),
      registrationOpenDate: addFixedTime(rawData.registrationOpenDate),
      registrationCloseDate: addFixedTime(rawData.registrationCloseDate),
    };

    if (!this.id() || this.id() === 'new') {
      this.eventService.createEvent(updatedData).subscribe({
        next: (res) => {
          alert('Evento creato con successo');
          this.isEditing = false;
          this.form.disable();
          window.location.reload();
        },
        error: (err: ErrorResponse) => alert('Errore nel salvataggio: ' + err.error.message),
      });
    } else {
      updatedData.id = this.id();
      const newEventData: Event = new Event(updatedData);
      this.eventService.updateEvent(newEventData).subscribe({
        next: (res) => {
          alert('Evento modificato con successo');
          this.isEditing = false;
          this.form.disable();
          window.location.reload();
        },
        error: (err: ErrorResponse) => alert('Errore nel salvataggio: ' + err.error.message),
      });
      
    }
  }

  getStatusLabel(status: EventStatus): string {
    return readableEventStatus(status);
  }

  getStatusClass(status: EventStatus): string {
    return eventStatusColorClass(status);
  }
}
