import { Component, OnInit, HostListener } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ApiService } from '../api.service';

@Component({
  standalone: true,
  selector: 'app-landing',
  imports: [RouterLink, CommonModule],
  templateUrl: './landing.component.html',
  styleUrls: ['./landing.component.css']
})
export class LandingComponent implements OnInit {
  welcomeMessage?: string;

  // 1) dynamic login/avatar
  isLoggedIn = false;
  userAvatarUrl = 'assets/avatar-placeholder.png';

  // 2) scroll shadow
  scrolled = false;
  @HostListener('window:scroll', [])
  onWindowScroll() {
    this.scrolled = window.pageYOffset > 20;
  }

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.api.getLanding().subscribe((res: string) => {
      this.welcomeMessage = res;
    });
  }
}
