import { Routes } from '@angular/router';
import { Role } from './models/role.model';
import { AuthGuard } from './guards/auth.guard';

// These component imports will be implemented later
// Auth components
const LoginComponent = () => import('./components/auth/login/login.component').then(m => m.LoginComponent);
const RegisterComponent = () => import('./components/auth/register/register.component').then(m => m.RegisterComponent);
const RoleSelectionComponent = () => import('./components/auth/role-selection/role-selection.component').then(m => m.RoleSelectionComponent);

// Dashboard components
const LandingComponent = () => import('./components/landing/landing.component').then(m => m.LandingComponent);
const RenterDashboardComponent = () => import('./components/dashboard/renter-dashboard/renter-dashboard.component').then(m => m.RenterDashboardComponent);
const OwnerDashboardComponent = () => import('./components/dashboard/owner-dashboard/owner-dashboard.component').then(m => m.OwnerDashboardComponent);
const RoommateHostDashboardComponent = () => import('./components/dashboard/roommate-host-dashboard/roommate-host-dashboard.component').then(m => m.RoommateHostDashboardComponent);

// Profile components
const RenterProfileComponent = () => import('./components/profile/renter-profile/renter-profile.component').then(m => m.RenterProfileComponent);
const OwnerProfileComponent = () => import('./components/profile/owner-profile/owner-profile.component').then(m => m.OwnerProfileComponent);
const RoommateHostProfileComponent = () => import('./components/profile/roommate-host-profile/roommate-host-profile.component').then(m => m.RoommateHostProfileComponent);

// Listing components
const PropertyListingComponent = () => import('./components/listing/property-listing/property-listing.component').then(m => m.PropertyListingComponent);
const RoomListingComponent = () => import('./components/listing/room-listing/room-listing.component').then(m => m.RoomListingComponent);
const PropertyDetailComponent = () => import('./components/listing/property-detail/property-detail.component').then(m => m.PropertyDetailComponent);
const RoomDetailComponent = () => import('./components/listing/room-detail/room-detail.component').then(m => m.RoomDetailComponent);
const PropertyFormComponent = () => import('./components/listing/property-form/property-form.component').then(m => m.PropertyFormComponent);
const RoomFormComponent = () => import('./components/listing/room-form/room-form.component').then(m => m.RoomFormComponent);

// Swipe components
const PropertySwipeComponent = () => import('./components/swipe/property-swipe/property-swipe.component').then(m => m.PropertySwipeComponent);
const RoommateSwipeComponent = () => import('./components/swipe/roommate-swipe/roommate-swipe.component').then(m => m.RoommateSwipeComponent);
const RenterSwipeComponent = () => import('./components/swipe/renter-swipe/renter-swipe.component').then(m => m.RenterSwipeComponent);

// Visit request components
const VisitRequestComponent = () => import('./components/visit-request/visit-request.component').then(m => m.VisitRequestComponent);
const VisitRequestFormComponent = () => import('./components/visit-request/visit-request-form/visit-request-form.component').then(m => m.VisitRequestFormComponent);
const VisitRequestListComponent = () => import('./components/visit-request/visit-request-list/visit-request-list.component').then(m => m.VisitRequestListComponent);

// Match components
const MatchListComponent = () => import('./components/match/match-list/match-list.component').then(m => m.MatchListComponent);

// Notification components
const NotificationComponent = () => import('./components/notification/notification.component').then(m => m.NotificationComponent);

export const routes: Routes = [
  // Public routes
  { path: '', component: LandingComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  // Role selection (requires authentication)
  {
    path: 'select-role',
    component: RoleSelectionComponent,
    canActivate: [AuthGuard]
  },

  // Renter routes
  {
    path: 'renter',
    canActivate: [AuthGuard],
    data: { roles: [Role.RENTER] },
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
    data: { roles: [Role.OWNER] },
    children: [
      { path: '', component: OwnerDashboardComponent },
      { path: 'profile', component: OwnerProfileComponent },
      { path: 'properties', component: PropertyListingComponent },
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
    data: { roles: [Role.ROOMMATE_HOST] },
    children: [
      { path: '', component: RoommateHostDashboardComponent },
      { path: 'profile', component: RoommateHostProfileComponent },
      { path: 'rooms', component: RoomListingComponent },
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

  // Catch-all route
  { path: '**', redirectTo: '' }
];
