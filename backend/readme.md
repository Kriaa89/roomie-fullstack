## 🎯 Project Overview

**RentMate** is a Tinder-style web application designed to streamline the process of finding housing or roommates. Users can swipe through property and user profiles to find a match, just like in a dating app. The platform supports three main roles:

- **Renter**: Searching for a room or home to rent.
- **Owner / Landlord**: Renting out a property they own.
- **Roommate-Host**: Already renting a house and looking for a roommate to share the space.

Additionally, owners can define the type of tenant they’re looking for (e.g., students, workers, families, women-only, etc.). Users can switch roles as needed.

---

## 👥 User Roles

| **Role**          | **Description**                              |
|--------------------|----------------------------------------------|
| 🧍‍♂️ **Renter**     | Searching for a room or home to rent.        |
| 🏠 **Owner**        | Renting out a property they own.            |
| 👥 **Roommate-Host**| Looking for a roommate to share their space.|

---

## 📝 Product Backlog

### 🔹 **EPIC 1: User Registration & Authentication**
- Sign up / Login 
- Email verification and password reset.
- User profile setup with role selection.

### 🔹 **EPIC 2: Profile Creation & Preferences**
- Role selection (Renter / Owner / Roommate-Host).
- Add bio, budget, habits, and preferences.
- Roommate compatibility filters (pets, smoking, etc.).
- Add occupation, current living city, and social media links.
- Ability to edit profile and role later.

### 🔹 **EPIC 3: Listing Creation (Owner & Roommate-Host)**
- Create property or room listings.
- Upload photos, price, location, and availability.
- Add house rules, description, and target audience tags.
- Edit or delete listings.

### 🔹 **EPIC 4: Swipe Matching System (Double Opt-In)**
- Swipe right or left on profiles (users + listings).
- Mutual right-swipe triggers a match.
- Swipe left removes the profile and logs behavior for smart suggestions.
- Undo swipe-left (premium feature).
- Preview profile info before swiping (bio, job, social links).

#### 🔁 **Swipe Matching Matrix**
| **Role 1**         | **Role 2**         | **Match?** |
|---------------------|--------------------|------------|
| Renter             | Owner             | ✅          |
| Renter             | Roommate-Host     | ✅          |
| Roommate-Host      | Renter            | ✅          |
| Owner              | Renter            | ✅          |
| Owner              | Owner             | ❌          |
| Roommate-Host      | Roommate-Host     | ❌          |

### 🔹 **EPIC 5: Match & Messaging System**
- Match notification with animated popup.
- Real-time chat with matched users.
- Message inbox with conversation history.
- Block, report, or unmatch functionality.

### 🔹 **EPIC 6: Geolocation & Map Integration**
- Use user location to show nearby listings.
- View listings on an interactive map.
- Adjust discovery radius.

### 🔹 **EPIC 7: Filtering & Discovery Tools**
- Filter by rent, location, preferences, and target audience.
- Compatibility score based on profile matching.
- Smart reordering of profiles based on swipe history.

### 🔹 **EPIC 8: Notification System**
- Push and in-app notifications for messages, likes, and matches.
- Notification settings management.

### 🔹 **EPIC 9: Reviews & Ratings**
- Leave reviews for landlords, roommates, and renters.
- Rating score visible on profiles.

### 🔹 **EPIC 10: Admin Dashboard**
- Manage users, listings, and reports.
- Review flagged content.
- View statistics (daily matches, active users, etc.).

### 🔹 **EPIC 11: Monetization & Premium Features**
- Boost listings or user profile visibility.
- Unlock premium filters or see who liked you.
- Payment gateway integration (Stripe, PayPal).
- Undo swipe-left action (premium feature).

### 🔹 **EPIC 12: Roommate-Host Specific Features**
- Create a roommate post as a host (not owner).
- Upload room photos and add rules/preferences.
- Swipe on potential roommates.
- Mark room as full once taken.

### 🔹 **EPIC 13: Smart Matching & Swipe Learning**
- Log swipe-left actions to refine match algorithm.
- Identify patterns in rejected profiles to improve suggestions.
- Optional feedback prompt ("Why did you swipe left?").
- Smart tags: "Too far", "Too expensive", "Unclear profile".

### 🔹 **EPIC 14: Target Audience Settings (For Owners & Hosts)**
- Select preferred audience for listings:
  - Students
  - Working professionals
  - Families
  - Women only
  - Men only
  - No preference
- Roommate-hosts can set their co-living preferences.
- Renters see whether they meet listing criteria.
- Filter listings based on target audience.
- Option to report listings for discriminatory language.

### 🔹 **EPIC 15: Group Renting Mode**
- Create a shared group profile with friends for joint rentals.
- Invite others to join the rental group.
- Allow verified groups to apply to listings together.
- Enable group chat and booking coordination.

### 🔹 **EPIC 16: Calendar Integration**
- Owners/hosts set available visit times.
- Renters request appointments via the app.
- Sync scheduled visits with external calendars (Google/Apple).
- Notify users of upcoming visits.

### 🔹 **EPIC 17: Verification Badges**
- Phone and ID verification for users.
- Admin review and approval process.
- Verified profiles get a trust badge.
- Owners can filter to see only verified renters.

### 🔹 **EPIC 18: AI Listing Description Generator (Gemini)**
- Generate appealing listing descriptions using AI.
- Input basic details (location, price, features) to get a full description.
- Improve owner/host experience and quality of listings.

### 🔹 **EPIC 19: Renter Profile Summarizer (Gemini)**
- Summarize renter bios into short profiles.
- Help owners quickly assess tenant personalities.
- Use AI to automatically generate the summary from profile fields.

### 🔹 **EPIC 20: RentMate AI Chat Assistant (Gemini)**
- In-app assistant to guide new users (onboarding, setup, filters).
- Answer common questions about listings, swipes, and scheduling.
- Available 24/7 as part of the messaging interface or help page.

### 🔹 **EPIC 21: AI Review Summarization (Gemini)**
- Analyze all reviews of a user or listing and generate a summary.
- Highlight pros/cons in 2–3 sentences.
- Simplifies renter/host evaluation.

### 🔹 **EPIC 22: Match Explanation AI (Gemini)**
- Explain to users why a profile or listing was matched.
- Improve trust and transparency in the algorithm.
- Factors: budget match, habits, city, filters, etc.

---

## 🔌 API Documentation

### Authentication Endpoints

#### 1. Register a New User

**Endpoint:** `POST http://localhost:8080/auth/register`

**Description:** Creates a new user account

**Request:**
- Method: POST
- URL: `http://localhost:8080/auth/register`
- Headers:
  - Content-Type: application/json
- Body (raw JSON):
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "password": "securePassword123",
  "passwordConfirmation": "securePassword123",
  "phoneNumber": "1234567890",
  "location": "New York"
}
```

**Important Note**: The `passwordConfirmation` field is required and must match the `password` field. Omitting this field will result in a validation error.

**Expected Response:**
- Status: 200 OK
- Body:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

#### 2. Login

**Endpoint:** `POST http://localhost:8080/auth/login`

**Description:** Authenticates a user and returns a JWT token

**Request:**
- Method: POST
- URL: `http://localhost:8080/auth/login`
- Headers:
  - Content-Type: application/json
- Body (raw JSON):
```json
{
  "email": "john.doe@example.com",
  "password": "securePassword123"
}
```

**Expected Response:**
- Status: 200 OK
- Body:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": 1,
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "roles": ["RENTER"]
}
```

#### 3. Select Role

**Endpoint:** `POST http://localhost:8080/auth/select-role/{userId}`

**Description:** Adds a role to a user and returns an updated JWT token

**Request:**
- Method: POST
- URL: `http://localhost:8080/auth/select-role/1`
- Headers:
  - Content-Type: application/json
- Body (raw JSON):
```json
{
  "roleType": "OWNER"
}
```

**Expected Response:**
- Status: 200 OK
- Body:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": 1,
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "roles": ["RENTER", "OWNER"]
}
```

### Using Authentication Token for Protected Endpoints

For any endpoints that require authentication, you'll need to include the JWT token in the request header:

1. Copy the token received from the login or register response
2. Add an Authorization header to your request:
   - Key: Authorization
   - Value: Bearer eyJhbGciOiJIUzI1NiJ9... (the token you received)

### User Management Endpoints

#### 1. Get Current User

**Endpoint:** `GET http://localhost:8080/api/users/me`

**Description:** Returns the currently authenticated user

**Authentication:** Required

**Response:**
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "1234567890",
  "profilePicture": null,
  "location": "New York",
  "age": 25,
  "emailVerified": false,
  "phoneVerified": false,
  "idVerified": false,
  "createdAt": "2023-06-15T10:30:00.000+00:00",
  "updatedAt": "2023-06-15T10:30:00.000+00:00",
  "roles": ["RENTER"]
}
```

#### 2. Get User by ID

**Endpoint:** `GET http://localhost:8080/api/users/{id}`

**Description:** Returns a user by ID

**Authentication:** Required (Admin or self)

**Response:** Same as Get Current User

#### 3. Update User

**Endpoint:** `PUT http://localhost:8080/api/users/{id}`

**Description:** Updates a user's information

**Authentication:** Required (Admin or self)

**Request:**
- Method: PUT
- URL: `http://localhost:8080/api/users/{id}`
- Headers:
  - Content-Type: application/json
  - Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
- Body (raw JSON):
```json
{
  "firstName": "John",
  "lastName": "Smith",
  "phoneNumber": "9876543210",
  "location": "Los Angeles",
  "age": 26
}
```

**Response:** Updated user object

#### 4. Delete User

**Endpoint:** `DELETE http://localhost:8080/api/users/{id}`

**Description:** Deletes a user

**Authentication:** Required (Admin or self)

#### 5. Change Password

**Endpoint:** `POST http://localhost:8080/api/users/{id}/change-password`

**Description:** Changes a user's password

**Authentication:** Required (self only)

**Request:**
- Method: POST
- URL: `http://localhost:8080/api/users/{id}/change-password`
- Headers:
  - Content-Type: application/json
  - Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
- Body (raw JSON):
```json
{
  "currentPassword": "securePassword123",
  "newPassword": "newSecurePassword456",
  "confirmPassword": "newSecurePassword456"
}
```

**Response:** Updated user object

### Role Management Endpoints

#### 1. Add Role to User

**Endpoint:** `POST http://localhost:8080/api/users/{id}/roles`

**Description:** Adds a role to a user

**Authentication:** Required (Admin only)

**Request:**
- Method: POST
- URL: `http://localhost:8080/api/users/{id}/roles`
- Headers:
  - Content-Type: application/json
  - Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
- Body (raw JSON):
```json
{
  "roleType": "OWNER"
}
```

**Response:** Updated user object

#### 2. Remove Role from User

**Endpoint:** `DELETE http://localhost:8080/api/users/{id}/roles/{roleType}`

**Description:** Removes a role from a user

**Authentication:** Required (Admin only)

**Response:** Updated user object

### Dashboard Endpoints

#### 1. Renter Dashboard

**Endpoint:** `GET http://localhost:8080/api/dashboard/renter`

**Description:** Returns the renter dashboard

**Authentication:** Required (RENTER role)

**Response:**
```json
{
  "user": {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "roles": ["RENTER"]
  },
  "role": "RENTER",
  "message": "Welcome to your Renter Dashboard!",
  "availableProperties": 0,
  "matchedProperties": 0,
  "pendingRequests": 0
}
```

#### 2. Owner Dashboard

**Endpoint:** `GET http://localhost:8080/api/dashboard/owner`

**Description:** Returns the owner dashboard

**Authentication:** Required (OWNER role)

**Response:**
```json
{
  "user": {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "roles": ["OWNER"]
  },
  "role": "OWNER",
  "message": "Welcome to your Owner Dashboard!",
  "listedProperties": 0,
  "interestedRenters": 0,
  "pendingRequests": 0
}
```

#### 3. Roommate Host Dashboard

**Endpoint:** `GET http://localhost:8080/api/dashboard/roommate-host`

**Description:** Returns the roommate host dashboard

**Authentication:** Required (ROOMMATE_HOST role)

**Response:**
```json
{
  "user": {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "roles": ["ROOMMATE_HOST"]
  },
  "role": "ROOMMATE_HOST",
  "message": "Welcome to your Roommate Host Dashboard!",
  "listedRooms": 0,
  "potentialRoommates": 0,
  "pendingRequests": 0
}
```

#### 4. Admin Dashboard

**Endpoint:** `GET http://localhost:8080/api/dashboard/admin`

**Description:** Returns the admin dashboard

**Authentication:** Required (ADMIN role)

**Response:**
```json
{
  "user": {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "roles": ["ADMIN"]
  },
  "role": "ADMIN",
  "message": "Welcome to the Admin Dashboard!",
  "totalUsers": 0,
  "totalProperties": 0,
  "pendingVerifications": 0,
  "reportedContent": 0
}
```

---

## 🔚 Conclusion

RentMate combines swipe-based simplicity with deep functionality. From role flexibility and audience targeting to calendar scheduling, AI assistance, and verified identities, it creates a modern, secure, and intelligent rental experience.

# Roomie Fullstack Application

## Problem Description

The application was experiencing an issue in the role selection component where users would see the error message "Failed to get user information". This occurred because the frontend was trying to call an API endpoint `/api/users/me` that didn't exist in the backend.

## Solution

The solution involved implementing the missing endpoint in the backend:

1. Added a `getCurrentUser()` method to the `UserService` class that:
   - Gets the authenticated user's email from the security context
   - Retrieves the user from the database using the email
   - Converts the user entity to a DTO for safe transmission to the client

2. Added a `/api/users/me` endpoint to the `UserController` class that:
   - Calls the `getCurrentUser()` method from the service
   - Returns the user information with appropriate error handling

## Frontend Integration

The frontend already has the code to call this endpoint in the `ApiService`:

```
getCurrentUser(): Observable<any> {
  return this.http.get(`${this.baseUrl}/api/users/me`, { headers: this.getAuthHeaders() });
}
```

This method is used in both the `RoleSelectionComponent` and `DashboardComponent` to get the current user's information.

## Testing

To test the fix:
1. Start the backend server
2. Start the frontend application
3. Log in with valid credentials
4. Navigate to the role selection page
5. Verify that the user information is loaded correctly and no error message is displayed

## Additional Notes

- The JWT authentication system is working correctly, with tokens being properly generated and validated
- The error was purely due to a missing endpoint in the backend
- No changes were needed to the frontend code as it was already correctly implemented
