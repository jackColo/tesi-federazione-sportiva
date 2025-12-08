import { Routes } from '@angular/router';
import { roleGuard } from './core/guard/role.guard';
import { Role } from './enums/role.enum';
import { LoginComponent } from './features/auth/login-component/login-component';
import { EventComponent } from './features/events/event-component/event-component';
import { CreateEventComponent } from './features/admin/create-event-component/create-event-component';
import { EnrollmentsComponent } from './features/admin/enrollments-component/enrollments-component';
import { DashboardComponent } from './features/dashboard/dashboard-component/dashboard-component';
import { RegisterComponent } from './features/auth/register-component/register-component';
import { HomeComponent } from './features/home/home-component/home-component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'auth/login', component: LoginComponent },
  { path: 'auth/register', component: RegisterComponent },
  { path: 'event', component: EventComponent },

  {
    path: 'admin/events/create',
    component: CreateEventComponent,
    canActivate: [roleGuard([Role.FEDERATION_MANAGER])],
  },

  {
    path: 'admin/enrollments',
    component: EnrollmentsComponent,
    canActivate: [roleGuard([Role.FEDERATION_MANAGER, Role.CLUB_MANAGER])],
  },

  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [roleGuard([Role.ATHLETE, Role.CLUB_MANAGER, Role.FEDERATION_MANAGER])],
  },
];
