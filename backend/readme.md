# RentMate Platform Development Guidelines

### Overview
**RentMate** is a rental platform connecting **Renters**, **Roommate Hosts**, and **Property Owners**. This document provides technical guidelines for both backend (Spring Boot) and frontend (Angular) development.

---

## 🔧 Backend
**Framework:** Spring Boot (Java 17, MySQL)  
**Key Features:**
- Fully role-specific architecture (no mock data).
- Separate models, controllers, and services for each role.

### 📂 Entities
1. **AppUser (Shared)**
   - Common fields: `id`, `firstName`, `lastName`, `email`, `password`, `role` (RENTER, OWNER, ROOMMATE_HOST), `phoneNumber`, `emailVerified`.

2. **Profiles (Role-Specific)**
   - **RenterProfile:** Bio, city, profile picture, `skillsExpected`, `lifestylePreferences`, visibility settings.
   - **RoommateHostProfile:** Similar structure, but with `skillsOffered`.
   - **OwnerProfile:** Minimal fields (city, profile picture).

3. **Listings (Role-Specific)**
   - **RoomListings:** Created by Roommate Hosts (e.g., title, description, price, city, host).
   - **PropertyListings:** Created by Owners (similar structure).

4. **Interactions**
   - **SwipeLike:** Tracks user likes/dislikes/matches.
   - **VisitRequest:** Manages visit scheduling/confirmation.
   - **Notification:** Alerts users about matches, visit requests, etc.

---

### 📦 Controllers/Services
Design RESTful APIs for profile, listing management, and user interactions, including:

| **Controller**               | **Purpose**                                      |
|-------------------------------|--------------------------------------------------|
| `AuthController`              | User registration & JWT-based login.            |
| `ProfileControllers`          | CRUD for Renter, Host, and Owner profiles.       |
| `ListingControllers`          | CRUD for Room and Property Listings.            |
| `SwipeController`             | Manage likes/dislikes and detect matches.       |
| `VisitRequestController`      | Handle visit requests (date, time, responses).  |
| `NotificationController`      | Manage read/unread notifications.               |

**Security Notes:**
- **Role Validation:** Restrict route access based on user role.
- **Dropdowns Only:** Skills/lifestyle fields always use dropdowns, no free text allowed.

---

## 🌐 Frontend
**Frameworks:** Angular 17, Bootstrap/Tailwind  
**Development Requirements:**
- No inline HTML/CSS.
- Separate files for logic (`.ts`), layout (`.html`), and styles (`.css`).
- Use `HttpClient` for API calls.
- Implement JWT Interceptor for token-based authentication.

---

### Key Components
#### 🔐 **Auth Flow**
- **LandingPage:** Welcome screen.
- **Login & Register Components:** Multi-step registration (select role as RENTER/OWNER/ROOMMATE_HOST).

#### 🧍‍♂️ **Renter Dashboard**
| **Component**           | **Purpose**                                         |
|--------------------------|-----------------------------------------------------|
| `RenterDashboard`        | Main renter interface.                              |
| `RenterProfile`          | Manage bio, skills, lifestyle preferences.          |
| `SwipeComponent`         | Tinder-style card swipes for hosts.                 |
| `MatchList`              | List of confirmed matches.                          |
| `VisitRequest`           | Schedule visits (date/time picker).                 |

#### 👥 **Host Dashboard**
| **Component**       | **Purpose**                                            |
|----------------------|--------------------------------------------------------|
| `HostDashboard`      | Central hub for hosts.                                 |
| `HostProfile`        | Manage skills offered and lifestyle.                   |
| `MyListings`         | Post/manage co-living rooms.                           |
| `SwipeRequests`      | Accept/decline incoming likes from renters.            |
| `MatchList`          | View matched renters.                                  |

#### 🏠 **Owner Dashboard**
| **Component**       | **Purpose**                                            |
|----------------------|--------------------------------------------------------|
| `OwnerDashboard`     | Minimal UI for property owners.                        |
| `OwnerProfile`       | Edit basic profile info (city, picture).               |
| `MyListings`         | Create and manage Property Listings.                   |
| `Notifications`      | Receive visit and match alerts.                        |

#### ✅ **Shared Components**
- **NavbarComponent**: Dynamic based on role.
- **ToastNotification**: Alert for matches or visit requests.
- **VisitRequestComponent**: Shared logic for scheduling visits.

---

## 🔒 Security/Technical Requirements
- Role-based route guards (e.g., only Roommate Hosts can access `/swipe-requests`).
- Use JWT Interceptor to attach tokens to API calls automatically.
- Profile and listings forms must use **Reactive Forms**.
- **Dropdown fields only**: Skills/lifestyle should not accept free text.
- Avoid hardcoded/mocked data; rely on dynamic API data.

---

## 📋 Implementation Progress

### Completed
- ✅ Entity models created:
  - AppUser (with UserDetails implementation for Spring Security)
  - Role enum (RENTER, OWNER, ROOMMATE_HOST)
  - RenterProfile
  - RoommateHostProfile
  - OwnerProfile
  - RoomListing
  - PropertyListing
  - SwipeLike
  - VisitRequest
  - Notification

- ✅ Repository interfaces created for all entities

- ✅ Service classes implemented:
  - AppUserService
  - RenterProfileService
  - RoommateHostProfileService
  - OwnerProfileService
  - RoomListingService
  - PropertyListingService
  - SwipeLikeService (with matching logic)
  - VisitRequestService
  - NotificationService

### Completed (continued)
- ✅ Controllers implemented for all entities:
  - AuthController
  - RenterProfileController
  - RoommateHostProfileController
  - OwnerProfileController
  - RoomListingController
  - PropertyListingController
  - SwipeController
  - VisitRequestController
  - NotificationController

- ✅ Security configuration with JWT:
  - JwtService
  - JwtAuthenticationFilter
  - SecurityConfig
  - ApplicationConfig

- ✅ DTOs created for request/response objects:
  - Authentication DTOs (AuthenticationRequest, AuthenticationResponse, RegisterRequest)
  - Profile DTOs (RenterProfileDto, RoommateHostProfileDto, OwnerProfileDto)
  - Listing DTOs (RoomListingDto, PropertyListingDto)
  - SwipeLikeDto
  - VisitRequestDto
  - NotificationDto

- ✅ Frontend models and interfaces created:
  - User models
  - Role models
  - Auth models
  - Property models
  - Profile models
  - Swipe models
  - Visit Request models
  - Notification models

- ✅ Frontend services implemented:
  - AuthService
  - UserService
  - PropertyService
  - ProfileService
  - SwipeService
  - VisitRequestService
  - NotificationService

- ✅ Frontend security and routing:
  - JWT Interceptor for API calls
  - Error handling interceptor
  - Role-based route guards
  - Route configuration with lazy loading

- ✅ Frontend components implemented:
  - Auth components:
    - Login component
    - Register component
    - Role selection component
  - Dashboard components:
    - Renter dashboard component
    - Owner dashboard component
    - Roommate host dashboard component
  - Profile components:
    - Renter profile component
    - Owner profile component
    - Roommate host profile component
  - Listing components:
    - Property listing component
    - Room listing component
    - Property detail component
    - Room detail component
    - Property form component
    - Room form component
  - Swipe components:
    - Property swipe component
    - Roommate swipe component
    - Renter swipe component
  - Visit request components:
    - Visit request component
    - Visit request form component
    - Visit request list component

### Next Steps
- 🔄 Add unit and integration tests
