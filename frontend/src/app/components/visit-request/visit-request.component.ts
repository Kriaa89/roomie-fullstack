import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-visit-request',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './visit-request.component.html',
  styleUrls: ['./visit-request.component.css']
})
export class VisitRequestComponent {
  // This is a container component that will be used to manage visit requests
  // It will use the visit-request-form and visit-request-list components

  constructor() { }
}
