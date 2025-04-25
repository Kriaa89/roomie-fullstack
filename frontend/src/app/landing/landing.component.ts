import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './landing.component.html',
  styleUrls: ['./landing.component.css']
})
export class LandingComponent {
  constructor(private router: Router) {}

  navigateToRegister() {
    this.router.navigate(['/auth'], { queryParams: { mode: 'register' } });
  }

  navigateToLogin() {
    this.router.navigate(['/auth'], { queryParams: { mode: 'login' } });
  }
}
