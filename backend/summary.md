# Roomie Backend System Summary

## Overview
The Roomie backend is a Spring Boot application that provides a RESTful API for a roommate matching platform. The system allows users to register, create property listings, and find potential roommates through a matching system similar to dating apps.

## Key Features Implemented

### Authentication and Security
- JWT-based authentication system
- User registration and login
- Password encryption
- Role-based authorization

### User Management
- User profile creation and updates
- Profile picture uploads
- User role assignment
- User verification statuses (email, phone, ID)

### Property Management
- Property listing creation
- Property details management (name, type, location, price, etc.)
- Property image uploads
- Property search and filtering
- User-property relationship management

### Matching System
- Swipe functionality (like dating apps)
- Match creation when two users like each other
- Match status management

### Additional Features
- File upload service for images
- Error handling and validation
- Timestamp tracking for all entities

## Database Models
- **User**: Stores user information, credentials, and profile details
- **PropretyList**: Stores property listings with detailed attributes
- **UserRole**: Manages user roles in the system
- **Swipe**: Records user swipe actions (like/dislike)
- **Matches**: Tracks matches between users
- **Notification**: Handles system notifications
- **UserSkill**: Manages skills offered or expected by users
- **VisitRequests**: Handles property visit requests

## API Endpoints

### Authentication
- `/auth/register`: Register a new user
- `/auth/login`: Authenticate a user and get JWT token

### User Management
- `/api/users/register`: Alternative user registration
- `/api/users/login`: Alternative user login
- `/api/users/roles`: Get available user roles
- `/api/users/{userId}/role`: Assign a role to a user
- `/api/users/{userId}/profile`: Update user profile
- `/api/users/{userId}/profile-picture`: Upload profile picture

### Property Management
- `/api/properties/user/{userId}`: Create or get properties for a user
- `/api/properties/{propertyId}`: Get, update, or delete a property
- `/api/properties/{propertyId}/image`: Upload property image

## Technical Implementation
- Spring Boot framework
- Spring Security for authentication and authorization
- JPA/Hibernate for database operations
- RESTful API design
- Transaction management
- File upload handling

## Conclusion
The Roomie backend system provides a solid foundation for a roommate matching application with comprehensive user and property management features, authentication, and a matching system. The codebase follows good practices with proper separation of concerns (controllers, services, repositories) and includes robust error handling.