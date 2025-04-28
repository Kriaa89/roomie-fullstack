import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-roommate-host-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './roommate-host-dashboard.component.html',
  styleUrls: ['./roommate-host-dashboard.component.css']
})
export class RoommateHostDashboardComponent implements OnInit {
  user: any = null;
  isLoading: boolean = false;
  errorMessage: string = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadUserData();
  }

  loadUserData(): void {
    this.authService.currentUser$.subscribe(user => {
      if (user) {
        this.user = user;
        if (!this.authService.hasRole('ROOMMATE_HOST')) {
          // Redirect to appropriate dashboard if not a roommate host
          this.router.navigate(['/dashboard']);
        }
      } else {
        this.router.navigate(['/login']);
      }
    });
  }

  logout(): void {
    this.authService.logout();
  }
}
