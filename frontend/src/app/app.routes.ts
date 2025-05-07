import { Routes } from '@angular/router';
import { Role } from './models/role.model';
import { AuthGuard } from './guards/auth.guard';
import { Type } from '@angular/core';

// These imports will be used for lazy loading
// Auth components
import { LoginComponent } from './components/auth/login/login.component';
import { RegisterComponent } from './components/auth/register/register.component';

// Dashboard components
import { LandingComponent } from './components/landing/landing.component';
import { RenterDashboardComponent } from './components/dashboard/renter-dashboard/renter-dashboard.component';
import { OwnerDashboardComponent } from './components/dashboard/owner-dashboard/owner-dashboard.component';
import { RoommateHostDashboardComponent } from './components/dashboard/roommate-host-dashboard/roommate-host-dashboard.component';

// Profile components
import { RenterProfileComponent } from './components/profile/renter-profile/renter-profile.component';
import { OwnerProfileComponent } from './components/profile/owner-profile/owner-profile.component';
import { RoommateHostProfileComponent } from './components/profile/roommate-host-profile/roommate-host-profile.component';

// Listing components
import { PropertyListingComponent } from './components/listing/property-listing/property-listing.component';
import { RoomListingComponent } from './components/listing/room-listing/room-listing.component';
import { PropertyDetailComponent } from './components/listing/property-detail/property-detail.component';
import { RoomDetailComponent } from './components/listing/room-detail/room-detail.component';
// All listing form components are now implemented
import { PropertyFormComponent } from './components/listing/property-form/property-form.component';
import { RoomFormComponent } from './components/listing/room-form/room-form.component';

// Swipe components
import { PropertySwipeComponent } from './components/swipe/property-swipe/property-swipe.component';
import { RoommateSwipeComponent } from './components/swipe/roommate-swipe/roommate-swipe.component';
import { RenterSwipeComponent } from './components/swipe/renter-swipe/renter-swipe.component';

// Visit request components
import { VisitRequestComponent } from './components/visit-request/visit-request.component';
import { VisitRequestFormComponent } from './components/visit-request/visit-request-form/visit-request-form.component';
import { VisitRequestListComponent } from './components/visit-request/visit-request-list/visit-request-list.component';

// Match components
import { MatchListComponent } from './components/match/match-list/match-list.component';

// Notification components
import { NotificationComponent } from './components/notification/notification.component';

export const routes: Routes = [
  // Public routes
  { path: '', component: LandingComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },


  // Renter routes
  {
    path: 'renter',
    canActivate: [AuthGuard],
    data: { role: [Role.RENTER] },
    children: [
      { path: '', component: RenterDashboardComponent },
      { path: 'profile', component: RenterProfileComponent },
      { path: 'properties', component: PropertySwipeComponent },
      { path: 'roommates', component: RoommateSwipeComponent },
      { path: 'matches', component: MatchListComponent },
      { path: 'visits', component: VisitRequestListComponent },
      { path: 'visits/new', component: VisitRequestFormComponent },
      { path: 'notifications', component: NotificationComponent }
    ]
  },

  // Owner routes
  {
    path: 'owner',
    canActivate: [AuthGuard],
    data: { role: [Role.OWNER] },
    children: [
      { path: '', component: OwnerDashboardComponent },
      { path: 'profile', component: OwnerProfileComponent },
      { path: 'properties', component: PropertyListingComponent },
      // These routes are now implemented
      { path: 'properties/new', component: PropertyFormComponent },
      { path: 'properties/edit/:id', component: PropertyFormComponent },
      { path: 'properties/:id', component: PropertyDetailComponent },
      { path: 'visits', component: VisitRequestListComponent },
      { path: 'notifications', component: NotificationComponent }
    ]
  },

  // Roommate host routes
  {
    path: 'host',
    canActivate: [AuthGuard],
    data: { role: [Role.ROOMMATE_HOST] },
    children: [
      { path: '', component: RoommateHostDashboardComponent },
      { path: 'profile', component: RoommateHostProfileComponent },
      { path: 'rooms', component: RoomListingComponent },
      // These routes are now implemented
      { path: 'rooms/new', component: RoomFormComponent },
      { path: 'rooms/edit/:id', component: RoomFormComponent },
      { path: 'rooms/:id', component: RoomDetailComponent },
      { path: 'renters', component: RenterSwipeComponent },
      { path: 'matches', component: MatchListComponent },
      { path: 'visits', component: VisitRequestListComponent },
      { path: 'notifications', component: NotificationComponent }
    ]
  },

  // Property detail (accessible to all authenticated users)
  {
    path: 'properties/:id',
    component: PropertyDetailComponent,
    canActivate: [AuthGuard]
  },

  // Room detail (accessible to all authenticated users)
  {
    path: 'rooms/:id',
    component: RoomDetailComponent,
    canActivate: [AuthGuard]
  },

  // Catch-all route with named component for better error handling
  {
    path: '**',
    component: LandingComponent,
    data: { error: 'Page not found' }
  }
];
