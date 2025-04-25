import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ApiService } from '../api.service';

@Component({
  selector: 'app-role-selection',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './role-selection.component.html',
  styleUrls: ['./role-selection.component.css']
})
export class RoleSelectionComponent {
  selectedRole: string | null = null;

  constructor(
    private apiService: ApiService,
    private router: Router
  ) {}

  selectRole(role: string) {
    this.selectedRole = role;
  }

  confirmRole() {
    if (this.selectedRole) {
      const userId = Number(localStorage.getItem('userId'));

      this.apiService.assignRole(userId, this.selectedRole).subscribe(
        response => {
          console.log('Role assigned successfully', response);
          // Navigate to dashboard
          this.router.navigate(['/dashboard']);
        },
        error => {
          console.error('Role assignment failed', error);
          // Handle error (show message to user)
        }
      );
    }
  }
}
