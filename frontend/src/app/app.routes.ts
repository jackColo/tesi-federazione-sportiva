import { Routes } from '@angular/router';
import { roleGuard } from './core/guard/role.guard';
import { Role } from './enums/role.enum';

import { LoginComponent } from './features/auth/login-component/login-component';
import { RegisterComponent } from './features/auth/register-component/register-component';
import { HomeComponent } from './features/home/home-component/home-component';

import { EventsComponent } from './features/events/events-component/events-component';

import { AdminChatDashboardComponent } from './shared/components/chat/admin-chat-dashboard-component/admin-chat-dashboard-component';

import { DashboardUserDetailComponent } from './features/dashboard/admin/dashboard-user-detail-component/dashboard-user-detail-component';
import { DashboardUserFormComponent } from './features/dashboard/admin/dashboard-user-form-component/dashboard-user-form-component';
import { DashboardClubDetailComponent } from './features/dashboard/clubs/dashboard-club-detail-component/dashboard-club-detail-component';
import { DashboardClubsComponent } from './features/dashboard/clubs/dashboard-clubs-component/dashboard-clubs-component';
import { DashboardEventDetailComponent } from './features/dashboard/events/dashboard-event-detail-component/dashboard-event-detail-component';
import { DashboardEventFormComponent } from './features/dashboard/events/dashboard-event-form-component/dashboard-event-form-component';
import { DashboardEventsComponent } from './features/dashboard/events/dashboard-events-component/dashboard-events-component';
import { DashboardEventEnrollFormComponent } from './features/dashboard/events/enrollment-components/dashboard-event-enroll-form-component/dashboard-event-enroll-form-component';
import { DashboardComponent } from './features/dashboard/layout/dashboard-component/dashboard-component';
import { DashboardSkeletonComponent } from './features/dashboard/layout/dashboard-skeleton-component/dashboard-skeleton-component';
import { ClubChatPageComponent } from './shared/components/chat/club-chat-page-component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'auth/login', component: LoginComponent },
  { path: 'auth/register', component: RegisterComponent },
  { path: 'events', component: EventsComponent },
  {
    path: 'dashboard',
    component: DashboardSkeletonComponent,
    canActivate: [roleGuard([Role.ATHLETE, Role.CLUB_MANAGER, Role.FEDERATION_MANAGER])],
    children: [
      {
        path: '',
        component: DashboardComponent,
        canActivate: [roleGuard([Role.FEDERATION_MANAGER, Role.CLUB_MANAGER, Role.ATHLETE])],
      },
      {
        path: 'inbox',
        component: AdminChatDashboardComponent,
        canActivate: [roleGuard([Role.FEDERATION_MANAGER])]
      },
      {
        path: 'support',
        component: ClubChatPageComponent,
        canActivate: [roleGuard([Role.CLUB_MANAGER])]
      },
      {
        path: 'events',
        canActivate: [roleGuard([Role.FEDERATION_MANAGER, Role.CLUB_MANAGER, Role.ATHLETE])],
        children: [
          { path: '', component: DashboardEventsComponent },
          {
            path: 'new',
            component: DashboardEventFormComponent,
            canActivate: [roleGuard([Role.FEDERATION_MANAGER])],
          },
          {
            path: 'update/:id',
            component: DashboardEventFormComponent,
            canActivate: [roleGuard([Role.FEDERATION_MANAGER])],
          },
          {
            path: 'enroll/:enrollId',
            component: DashboardEventEnrollFormComponent,
          },
          {
            path: ':id',
            children: [
              { path: '', component: DashboardEventDetailComponent },
              {
                path: 'enroll/:clubId/:athleteId',
                component: DashboardEventEnrollFormComponent,
              },
            ],
          },
        ],
      },
      {
        path: 'clubs',
        children: [
          {
            path: '',
            canActivate: [roleGuard([Role.FEDERATION_MANAGER, Role.CLUB_MANAGER])],
            component: DashboardClubsComponent,
          },
          {
            path: ':id',
            component: DashboardClubDetailComponent,
            canActivate: [roleGuard([Role.FEDERATION_MANAGER, Role.CLUB_MANAGER])],
          },
        ],
      },
      {
        path: 'user',
        canActivate: [roleGuard([Role.FEDERATION_MANAGER, Role.CLUB_MANAGER, Role.ATHLETE])],
        children: [
          {
            path: 'new',
            component: DashboardUserFormComponent,
            canActivate: [roleGuard([Role.FEDERATION_MANAGER, Role.CLUB_MANAGER])],
          },
          {
            path: ':id',
            component: DashboardUserDetailComponent,
            canActivate: [roleGuard([Role.FEDERATION_MANAGER, Role.CLUB_MANAGER, Role.ATHLETE])],
          },
        ],
      },
    ],
  },
  { path: '**', redirectTo: '', pathMatch: 'full' },
];
