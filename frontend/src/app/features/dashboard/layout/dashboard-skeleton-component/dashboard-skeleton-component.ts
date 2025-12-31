import { Component } from '@angular/core';
import { SidebarComponent } from '../sidebar-component/sidebar-component';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-dashboard-skeleton-component',
  standalone: true,
  imports: [SidebarComponent, RouterOutlet],
  templateUrl: './dashboard-skeleton-component.html',
})
export class DashboardSkeletonComponent {

}
