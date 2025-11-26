import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { faHouse, faCalendarWeek } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';


@Component({
  selector: 'app-header',
  imports: [
    CommonModule,
    RouterLink,
    FontAwesomeModule
  ],
  templateUrl: './header.html',
  styleUrl: './header.scss',
})
export class Header {
  items = [
    {
      label: 'Home',
      icon: faHouse,
      routerLink: '/dashboard',
    },
    {
      label: 'Eventi',
      icon: faCalendarWeek,
      routerLink: '/event',
    },
    {
      label: 'Admin',
      icon: '',
      routerLink: '/admin/events/create',
    },
    {
      label: 'Login',
      icon: '',
      routerLink: '/auth/login',
    },
  ];
}
