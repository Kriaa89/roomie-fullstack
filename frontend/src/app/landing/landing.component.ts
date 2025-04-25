import { Component, OnInit } from '@angular/core';
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

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.api.getLanding().subscribe(res => {
      this.welcomeMessage = res;
    });
  }
}
