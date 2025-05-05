import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { VisitRequestService } from '../../../services/visit-request.service';
import { VisitRequestCreateRequest } from '../../../models/visit-request.model';

@Component({
  selector: 'app-visit-request-form',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  templateUrl: './visit-request-form.component.html',
  styleUrls: ['./visit-request-form.component.css']
})
export class VisitRequestFormComponent implements OnInit {
  visitForm: FormGroup;
  loading = false;
  submitted = false;
  error = '';
  success = '';
  propertyId?: number;
  roommateHostId?: number;

  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private visitRequestService: VisitRequestService
  ) {
    this.visitForm = this.formBuilder.group({
      requestDate: ['', Validators.required],
      requestTime: ['', Validators.required],
      message: ['']
    });
  }

  ngOnInit(): void {
    // Check if we have propertyId or roommateHostId in query params
    this.route.queryParams.subscribe(params => {
      if (params['propertyId']) {
        this.propertyId = +params['propertyId'];
      }
      if (params['roommateHostId']) {
        this.roommateHostId = +params['roommateHostId'];
      }

      // If neither is provided, redirect to dashboard
      if (!this.propertyId && !this.roommateHostId) {
        this.error = 'Please select a property or roommate host to request a visit.';
      }
    });
  }

  // Convenience getter for easy access to form fields
  get f() { return this.visitForm.controls; }

  onSubmit(): void {
    this.submitted = true;

    // Stop here if form is invalid
    if (this.visitForm.invalid) {
      return;
    }

    this.loading = true;
    this.error = '';
    this.success = '';

    const visitRequest: VisitRequestCreateRequest = {
      propertyId: this.propertyId,
      roommateHostId: this.roommateHostId,
      requestDate: this.f['requestDate'].value,
      requestTime: this.f['requestTime'].value,
      message: this.f['message'].value
    };

    this.visitRequestService.createVisitRequest(visitRequest).subscribe({
      next: () => {
        this.success = 'Visit request created successfully!';
        this.loading = false;

        // Redirect after a short delay
        setTimeout(() => {
          this.router.navigate(['/renter/visits']);
        }, 2000);
      },
      error: error => {
        this.error = error;
        this.loading = false;
      }
    });
  }
}
