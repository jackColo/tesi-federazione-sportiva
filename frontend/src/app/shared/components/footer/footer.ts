import { Component } from '@angular/core';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [],
  templateUrl: './footer.html',
})
export class Footer {
  currentYear: number;

  constructor() {
    this.currentYear = new Date().getFullYear();
  }
}