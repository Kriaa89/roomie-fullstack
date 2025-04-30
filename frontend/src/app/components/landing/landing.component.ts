import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './landing.component.html',
  styleUrls: ['./landing.component.css']
})
export class LandingComponent implements OnInit {
  activeTab: string = 'renter'; // Default active tab

  constructor(private router: Router) {}

  ngOnInit(): void {
    // Initialize with the renter tab active
  }

  setActiveTab(tabName: string): void {
    this.activeTab = tabName;
  }

  navigateToRegister(): void {
    this.router.navigate(['/register']);
  }

  navigateToLogin(): void {
    this.router.navigate(['/login']);
  }

  scrollToFeatures(): void {
    const featuresElement = document.getElementById('features');
    if (featuresElement) {
      featuresElement.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }
}
