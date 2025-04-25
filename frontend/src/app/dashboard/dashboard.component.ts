import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../api.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  userRoles: any[] = [];

  constructor(private apiService: ApiService) {}

  ngOnInit() {
    const userId = Number(localStorage.getItem('userId'));

    if (userId) {
      this.apiService.getUserRoles(userId).subscribe(
        roles => {
          this.userRoles = roles;
        },
        error => {
          console.error('Failed to load user roles', error);
        }
      );
    }
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('userId');
    window.location.href = '/';
  }
}
