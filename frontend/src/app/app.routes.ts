import { Routes } from '@angular/router';
import { roleGuard } from './core/guard/role.guard';
import { Role } from './enums/role.enum';
import { LoginComponent } from './features/auth/login-component/login-component';
import { EventComponent } from './features/events/event-component/event-component';
import { CreateEventComponent } from './features/dashboard/admin/create-event-component/create-event-component';
import { EnrollmentsComponent } from './features/dashboard/admin/enrollments-component/enrollments-component';
import { DashboardComponent } from './features/dashboard/dashboard-component/dashboard-component';
import { RegisterComponent } from './features/auth/register-component/register-component';
import { HomeComponent } from './features/home/home-component/home-component';
import { DashboardSkeletonComponent } from './features/dashboard/dashboard-skeleton-component/dashboard-skeleton-component';
export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'auth/login', component: LoginComponent },
  { path: 'auth/register', component: RegisterComponent },
  { path: 'event', component: EventComponent },
  {
    
    path: 'dashboard',
    component: DashboardSkeletonComponent,
    canActivate: [roleGuard([Role.ATHLETE, Role.CLUB_MANAGER, Role.FEDERATION_MANAGER])],
    children: [
      { path: '', 
        component: DashboardComponent,
        canActivate: [roleGuard([Role.FEDERATION_MANAGER])] 
      },
      { path: 'events', 
        canActivate: [roleGuard([Role.FEDERATION_MANAGER])],
        children: [
          { path: '', redirectTo: 'create', pathMatch: 'full' },
          { path: 'create', component: CreateEventComponent },
          { path: 'enrollments', component: EnrollmentsComponent },
        ] 
      },
      { path: 'clubs', 
        component: EnrollmentsComponent,
        canActivate: [roleGuard([Role.FEDERATION_MANAGER])] 
      },
    ]
  }
];
