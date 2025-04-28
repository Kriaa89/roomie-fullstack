# Roomie API Documentation

This document provides comprehensive documentation for testing all endpoints in the Roomie application. Each endpoint is documented with its HTTP method, URL path, required permissions, request body (if applicable), and expected response.

## Table of Contents

1. [Authentication](#authentication)
2. [User Management](#user-management)
3. [Property Management](#property-management)
4. [Role-Specific Endpoints](#role-specific-endpoints)
   - [Admin](#admin)
   - [Owner](#owner)
   - [Renter](#renter)
   - [Roommate Host](#roommate-host)
5. [Dashboard](#dashboard)

## Authentication

### Register a new user

**Endpoint:** `POST /auth/register`

**Permission:** Public

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "1234567890",
  "password": "password123",
  "passwordConfirmation": "password123",
  "location": "New York",
  "age": 30
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": 1,
  "roles": []
}
```

### Login

**Endpoint:** `POST /auth/login`

**Permission:** Public

**Request Body:**
```json
{
  "email": "john.doe@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": 1,
  "roles": ["RENTER"]
}
```

### Select Role

**Endpoint:** `POST /auth/select-role/{userId}`

**Permission:** Authenticated

**Request Body:**
```json
{
  "roleType": "OWNER"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": 1,
  "roles": ["RENTER", "OWNER"]
}
```

## User Management

### Get Current User

**Endpoint:** `GET /api/users/me`

**Permission:** Authenticated

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
  "age": 30,
  "emailVerified": false,
  "phoneVerified": false,
  "idVerified": false,
  "createdAt": "2023-06-15T10:30:00",
  "updatedAt": "2023-06-15T10:30:00",
  "roles": ["RENTER", "OWNER"]
}
```

### Get User by ID

**Endpoint:** `GET /api/users/{id}`

**Permission:** ADMIN or the user themselves

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
  "age": 30,
  "emailVerified": false,
  "phoneVerified": false,
  "idVerified": false,
  "createdAt": "2023-06-15T10:30:00",
  "updatedAt": "2023-06-15T10:30:00",
  "roles": ["RENTER", "OWNER"]
}
```

### Update User

**Endpoint:** `PUT /api/users/{id}`

**Permission:** ADMIN or the user themselves

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Smith",
  "phoneNumber": "0987654321",
  "profilePicture": "profile.jpg",
  "location": "Los Angeles",
  "age": 31
}
```

**Response:**
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Smith",
  "email": "john.doe@example.com",
  "phoneNumber": "0987654321",
  "profilePicture": "profile.jpg",
  "location": "Los Angeles",
  "age": 31,
  "emailVerified": false,
  "phoneVerified": false,
  "idVerified": false,
  "createdAt": "2023-06-15T10:30:00",
  "updatedAt": "2023-06-15T11:45:00",
  "roles": ["RENTER", "OWNER"]
}
```

### Change Password

**Endpoint:** `POST /api/users/{id}/change-password`

**Permission:** The user themselves

**Request Body:**
```json
{
  "currentPassword": "password123",
  "newPassword": "newPassword123",
  "confirmPassword": "newPassword123"
}
```

**Response:**
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Smith",
  "email": "john.doe@example.com",
  "phoneNumber": "0987654321",
  "profilePicture": "profile.jpg",
  "location": "Los Angeles",
  "age": 31,
  "emailVerified": false,
  "phoneVerified": false,
  "idVerified": false,
  "createdAt": "2023-06-15T10:30:00",
  "updatedAt": "2023-06-15T12:00:00",
  "roles": ["RENTER", "OWNER"]
}
```

## Property Management

### Get All Properties

**Endpoint:** `GET /api/properties`

**Permission:** Authenticated

**Response:**
```json
[
  {
    "id": 1,
    "name": "Cozy Apartment",
    "type": "APARTMENT",
    "location": "New York",
    "price": "1500",
    "description": "A cozy apartment in downtown",
    "images": "image1.jpg,image2.jpg",
    "amenities": "WiFi,Parking,Pool",
    "surface": "800 sqft",
    "numberOfRooms": 3,
    "numberOfBathrooms": 1,
    "numberOfBedrooms": 2,
    "propertyRules": "No pets, No smoking",
    "availability": true,
    "audiance": "SINGLES",
    "createdAt": "2023-06-15T14:00:00",
    "updatedAt": "2023-06-15T14:00:00",
    "owner": {
      "id": 1,
      "firstName": "John",
      "lastName": "Smith"
    }
  }
]
```

### Get Property by ID

**Endpoint:** `GET /api/properties/{id}`

**Permission:** Authenticated

**Response:**
```json
{
  "id": 1,
  "name": "Cozy Apartment",
  "type": "APARTMENT",
  "location": "New York",
  "price": "1500",
  "description": "A cozy apartment in downtown",
  "images": "image1.jpg,image2.jpg",
  "amenities": "WiFi,Parking,Pool",
  "surface": "800 sqft",
  "numberOfRooms": 3,
  "numberOfBathrooms": 1,
  "numberOfBedrooms": 2,
  "propertyRules": "No pets, No smoking",
  "availability": true,
  "audiance": "SINGLES",
  "createdAt": "2023-06-15T14:00:00",
  "updatedAt": "2023-06-15T14:00:00",
  "owner": {
    "id": 1,
    "firstName": "John",
    "lastName": "Smith"
  }
}
```

### Get Properties by Owner

**Endpoint:** `GET /api/properties/owner/{ownerId}`

**Permission:** Authenticated

**Response:**
```json
[
  {
    "id": 1,
    "name": "Cozy Apartment",
    "type": "APARTMENT",
    "location": "New York",
    "price": "1500",
    "description": "A cozy apartment in downtown",
    "images": "image1.jpg,image2.jpg",
    "amenities": "WiFi,Parking,Pool",
    "surface": "800 sqft",
    "numberOfRooms": 3,
    "numberOfBathrooms": 1,
    "numberOfBedrooms": 2,
    "propertyRules": "No pets, No smoking",
    "availability": true,
    "audiance": "SINGLES",
    "createdAt": "2023-06-15T14:00:00",
    "updatedAt": "2023-06-15T14:00:00",
    "owner": {
      "id": 1,
      "firstName": "John",
      "lastName": "Smith"
    }
  }
]
```

### Get Available Properties

**Endpoint:** `GET /api/properties/available`

**Permission:** Authenticated

**Response:**
```json
[
  {
    "id": 1,
    "name": "Cozy Apartment",
    "type": "APARTMENT",
    "location": "New York",
    "price": "1500",
    "description": "A cozy apartment in downtown",
    "images": "image1.jpg,image2.jpg",
    "amenities": "WiFi,Parking,Pool",
    "surface": "800 sqft",
    "numberOfRooms": 3,
    "numberOfBathrooms": 1,
    "numberOfBedrooms": 2,
    "propertyRules": "No pets, No smoking",
    "availability": true,
    "audiance": "SINGLES",
    "createdAt": "2023-06-15T14:00:00",
    "updatedAt": "2023-06-15T14:00:00",
    "owner": {
      "id": 1,
      "firstName": "John",
      "lastName": "Smith"
    }
  }
]
```

### Get Properties by Type

**Endpoint:** `GET /api/properties/type/{type}`

**Permission:** Authenticated

**Response:**
```json
[
  {
    "id": 1,
    "name": "Cozy Apartment",
    "type": "APARTMENT",
    "location": "New York",
    "price": "1500",
    "description": "A cozy apartment in downtown",
    "images": "image1.jpg,image2.jpg",
    "amenities": "WiFi,Parking,Pool",
    "surface": "800 sqft",
    "numberOfRooms": 3,
    "numberOfBathrooms": 1,
    "numberOfBedrooms": 2,
    "propertyRules": "No pets, No smoking",
    "availability": true,
    "audiance": "SINGLES",
    "createdAt": "2023-06-15T14:00:00",
    "updatedAt": "2023-06-15T14:00:00",
    "owner": {
      "id": 1,
      "firstName": "John",
      "lastName": "Smith"
    }
  }
]
```

### Get Properties by Location

**Endpoint:** `GET /api/properties/location/{location}`

**Permission:** Authenticated

**Response:**
```json
[
  {
    "id": 1,
    "name": "Cozy Apartment",
    "type": "APARTMENT",
    "location": "New York",
    "price": "1500",
    "description": "A cozy apartment in downtown",
    "images": "image1.jpg,image2.jpg",
    "amenities": "WiFi,Parking,Pool",
    "surface": "800 sqft",
    "numberOfRooms": 3,
    "numberOfBathrooms": 1,
    "numberOfBedrooms": 2,
    "propertyRules": "No pets, No smoking",
    "availability": true,
    "audiance": "SINGLES",
    "createdAt": "2023-06-15T14:00:00",
    "updatedAt": "2023-06-15T14:00:00",
    "owner": {
      "id": 1,
      "firstName": "John",
      "lastName": "Smith"
    }
  }
]
```

### Create Property

**Endpoint:** `POST /api/properties`

**Permission:** OWNER

**Request Body:**
```json
{
  "name": "Cozy Apartment",
  "type": "APARTMENT",
  "location": "New York",
  "price": "1500",
  "description": "A cozy apartment in downtown",
  "images": "image1.jpg,image2.jpg",
  "amenities": "WiFi,Parking,Pool",
  "surface": "800 sqft",
  "numberOfRooms": 3,
  "numberOfBathrooms": 1,
  "numberOfBedrooms": 2,
  "propertyRules": "No pets, No smoking",
  "availability": true,
  "audiance": "SINGLES"
}
```

**Response:**
```json
{
  "id": 1,
  "name": "Cozy Apartment",
  "type": "APARTMENT",
  "location": "New York",
  "price": "1500",
  "description": "A cozy apartment in downtown",
  "images": "image1.jpg,image2.jpg",
  "amenities": "WiFi,Parking,Pool",
  "surface": "800 sqft",
  "numberOfRooms": 3,
  "numberOfBathrooms": 1,
  "numberOfBedrooms": 2,
  "propertyRules": "No pets, No smoking",
  "availability": true,
  "audiance": "SINGLES",
  "createdAt": "2023-06-15T14:00:00",
  "updatedAt": "2023-06-15T14:00:00",
  "owner": {
    "id": 1,
    "firstName": "John",
    "lastName": "Smith"
  }
}
```

### Update Property

**Endpoint:** `PUT /api/properties/{id}`

**Permission:** OWNER

**Request Body:**
```json
{
  "name": "Updated Apartment",
  "price": "1600",
  "description": "An updated description",
  "availability": false
}
```

**Response:**
```json
{
  "id": 1,
  "name": "Updated Apartment",
  "type": "APARTMENT",
  "location": "New York",
  "price": "1600",
  "description": "An updated description",
  "images": "image1.jpg,image2.jpg",
  "amenities": "WiFi,Parking,Pool",
  "surface": "800 sqft",
  "numberOfRooms": 3,
  "numberOfBathrooms": 1,
  "numberOfBedrooms": 2,
  "propertyRules": "No pets, No smoking",
  "availability": false,
  "audiance": "SINGLES",
  "createdAt": "2023-06-15T14:00:00",
  "updatedAt": "2023-06-15T15:30:00",
  "owner": {
    "id": 1,
    "firstName": "John",
    "lastName": "Smith"
  }
}
```

### Delete Property

**Endpoint:** `DELETE /api/properties/{id}`

**Permission:** OWNER

**Response:** HTTP 200 OK

## Role-Specific Endpoints

### Admin

#### Get All Users

**Endpoint:** `GET /api/admin/users`

**Permission:** ADMIN

**Response:**
```json
[
  {
    "id": 1,
    "firstName": "John",
    "lastName": "Smith",
    "email": "john.doe@example.com",
    "phoneNumber": "0987654321",
    "profilePicture": "profile.jpg",
    "location": "Los Angeles",
    "age": 31,
    "emailVerified": false,
    "phoneVerified": false,
    "idVerified": false,
    "createdAt": "2023-06-15T10:30:00",
    "updatedAt": "2023-06-15T12:00:00",
    "roles": ["RENTER", "OWNER"]
  }
]
```

#### Get User by ID (Admin)

**Endpoint:** `GET /api/admin/users/{id}`

**Permission:** ADMIN

**Response:**
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Smith",
  "email": "john.doe@example.com",
  "phoneNumber": "0987654321",
  "profilePicture": "profile.jpg",
  "location": "Los Angeles",
  "age": 31,
  "emailVerified": false,
  "phoneVerified": false,
  "idVerified": false,
  "createdAt": "2023-06-15T10:30:00",
  "updatedAt": "2023-06-15T12:00:00",
  "roles": ["RENTER", "OWNER"]
}
```

#### Update User (Admin)

**Endpoint:** `PUT /api/admin/users/{id}`

**Permission:** ADMIN

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Updated",
  "phoneNumber": "1122334455",
  "location": "Chicago",
  "age": 32
}
```

**Response:**
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Updated",
  "email": "john.doe@example.com",
  "phoneNumber": "1122334455",
  "profilePicture": "profile.jpg",
  "location": "Chicago",
  "age": 32,
  "emailVerified": false,
  "phoneVerified": false,
  "idVerified": false,
  "createdAt": "2023-06-15T10:30:00",
  "updatedAt": "2023-06-15T16:00:00",
  "roles": ["RENTER", "OWNER"]
}
```

#### Delete User

**Endpoint:** `DELETE /api/admin/users/{id}`

**Permission:** ADMIN

**Response:** HTTP 200 OK

#### Add Role to User

**Endpoint:** `POST /api/admin/users/{id}/roles`

**Permission:** ADMIN

**Request Body:**
```json
{
  "roleType": "ROOMMATE_HOST"
}
```

**Response:**
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Updated",
  "email": "john.doe@example.com",
  "phoneNumber": "1122334455",
  "profilePicture": "profile.jpg",
  "location": "Chicago",
  "age": 32,
  "emailVerified": false,
  "phoneVerified": false,
  "idVerified": false,
  "createdAt": "2023-06-15T10:30:00",
  "updatedAt": "2023-06-15T16:30:00",
  "roles": ["RENTER", "OWNER", "ROOMMATE_HOST"]
}
```

#### Remove Role from User

**Endpoint:** `DELETE /api/admin/users/{id}/roles/{roleType}`

**Permission:** ADMIN

**Response:**
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Updated",
  "email": "john.doe@example.com",
  "phoneNumber": "1122334455",
  "profilePicture": "profile.jpg",
  "location": "Chicago",
  "age": 32,
  "emailVerified": false,
  "phoneVerified": false,
  "idVerified": false,
  "createdAt": "2023-06-15T10:30:00",
  "updatedAt": "2023-06-15T16:45:00",
  "roles": ["RENTER", "ROOMMATE_HOST"]
}
```

#### Get All Properties (Admin)

**Endpoint:** `GET /api/admin/properties`

**Permission:** ADMIN

**Response:**
```json
[
  {
    "id": 1,
    "name": "Updated Apartment",
    "type": "APARTMENT",
    "location": "New York",
    "price": "1600",
    "description": "An updated description",
    "images": "image1.jpg,image2.jpg",
    "amenities": "WiFi,Parking,Pool",
    "surface": "800 sqft",
    "numberOfRooms": 3,
    "numberOfBathrooms": 1,
    "numberOfBedrooms": 2,
    "propertyRules": "No pets, No smoking",
    "availability": false,
    "audiance": "SINGLES",
    "createdAt": "2023-06-15T14:00:00",
    "updatedAt": "2023-06-15T15:30:00",
    "owner": {
      "id": 1,
      "firstName": "John",
      "lastName": "Updated"
    }
  }
]
```

#### Get Property by ID (Admin)

**Endpoint:** `GET /api/admin/properties/{id}`

**Permission:** ADMIN

**Response:**
```json
{
  "id": 1,
  "name": "Updated Apartment",
  "type": "APARTMENT",
  "location": "New York",
  "price": "1600",
  "description": "An updated description",
  "images": "image1.jpg,image2.jpg",
  "amenities": "WiFi,Parking,Pool",
  "surface": "800 sqft",
  "numberOfRooms": 3,
  "numberOfBathrooms": 1,
  "numberOfBedrooms": 2,
  "propertyRules": "No pets, No smoking",
  "availability": false,
  "audiance": "SINGLES",
  "createdAt": "2023-06-15T14:00:00",
  "updatedAt": "2023-06-15T15:30:00",
  "owner": {
    "id": 1,
    "firstName": "John",
    "lastName": "Updated"
  }
}
```

#### Delete Property (Admin)

**Endpoint:** `DELETE /api/admin/properties/{id}`

**Permission:** ADMIN

**Response:** HTTP 200 OK

#### Get Admin Stats

**Endpoint:** `GET /api/admin/stats`

**Permission:** ADMIN

**Response:**
```json
{
  "totalUsers": 10,
  "totalProperties": 25
}
```

### Owner

#### Get My Properties

**Endpoint:** `GET /api/owner/properties`

**Permission:** OWNER

**Response:**
```json
[
  {
    "id": 1,
    "name": "Updated Apartment",
    "type": "APARTMENT",
    "location": "New York",
    "price": "1600",
    "description": "An updated description",
    "images": "image1.jpg,image2.jpg",
    "amenities": "WiFi,Parking,Pool",
    "surface": "800 sqft",
    "numberOfRooms": 3,
    "numberOfBathrooms": 1,
    "numberOfBedrooms": 2,
    "propertyRules": "No pets, No smoking",
    "availability": false,
    "audiance": "SINGLES",
    "createdAt": "2023-06-15T14:00:00",
    "updatedAt": "2023-06-15T15:30:00",
    "owner": {
      "id": 1,
      "firstName": "John",
      "lastName": "Updated"
    }
  }
]
```

#### Get My Property by ID

**Endpoint:** `GET /api/owner/properties/{id}`

**Permission:** OWNER

**Response:**
```json
{
  "id": 1,
  "name": "Updated Apartment",
  "type": "APARTMENT",
  "location": "New York",
  "price": "1600",
  "description": "An updated description",
  "images": "image1.jpg,image2.jpg",
  "amenities": "WiFi,Parking,Pool",
  "surface": "800 sqft",
  "numberOfRooms": 3,
  "numberOfBathrooms": 1,
  "numberOfBedrooms": 2,
  "propertyRules": "No pets, No smoking",
  "availability": false,
  "audiance": "SINGLES",
  "createdAt": "2023-06-15T14:00:00",
  "updatedAt": "2023-06-15T15:30:00",
  "owner": {
    "id": 1,
    "firstName": "John",
    "lastName": "Updated"
  }
}
```

#### Create Property (Owner)

**Endpoint:** `POST /api/owner/properties`

**Permission:** OWNER

**Request Body:**
```json
{
  "name": "New Property",
  "type": "HOUSE",
  "location": "Boston",
  "price": "2000",
  "description": "A beautiful house",
  "images": "house1.jpg,house2.jpg",
  "amenities": "WiFi,Parking,Garden",
  "surface": "1500 sqft",
  "numberOfRooms": 5,
  "numberOfBathrooms": 2,
  "numberOfBedrooms": 3,
  "propertyRules": "No pets",
  "availability": true,
  "audiance": "FAMILIES"
}
```

**Response:**
```json
{
  "id": 2,
  "name": "New Property",
  "type": "HOUSE",
  "location": "Boston",
  "price": "2000",
  "description": "A beautiful house",
  "images": "house1.jpg,house2.jpg",
  "amenities": "WiFi,Parking,Garden",
  "surface": "1500 sqft",
  "numberOfRooms": 5,
  "numberOfBathrooms": 2,
  "numberOfBedrooms": 3,
  "propertyRules": "No pets",
  "availability": true,
  "audiance": "FAMILIES",
  "createdAt": "2023-06-15T17:00:00",
  "updatedAt": "2023-06-15T17:00:00",
  "owner": {
    "id": 1,
    "firstName": "John",
    "lastName": "Updated"
  }
}
```

#### Update Property (Owner)

**Endpoint:** `PUT /api/owner/properties/{id}`

**Permission:** OWNER

**Request Body:**
```json
{
  "price": "2100",
  "description": "A beautiful updated house",
  "availability": false
}
```

**Response:**
```json
{
  "id": 2,
  "name": "New Property",
  "type": "HOUSE",
  "location": "Boston",
  "price": "2100",
  "description": "A beautiful updated house",
  "images": "house1.jpg,house2.jpg",
  "amenities": "WiFi,Parking,Garden",
  "surface": "1500 sqft",
  "numberOfRooms": 5,
  "numberOfBathrooms": 2,
  "numberOfBedrooms": 3,
  "propertyRules": "No pets",
  "availability": false,
  "audiance": "FAMILIES",
  "createdAt": "2023-06-15T17:00:00",
  "updatedAt": "2023-06-15T17:30:00",
  "owner": {
    "id": 1,
    "firstName": "John",
    "lastName": "Updated"
  }
}
```

#### Delete Property (Owner)

**Endpoint:** `DELETE /api/owner/properties/{id}`

**Permission:** OWNER

**Response:** HTTP 200 OK

#### Get Property Stats

**Endpoint:** `GET /api/owner/stats`

**Permission:** OWNER

**Response:**
```json
{
  "totalProperties": 2
}
```

### Renter

#### Get Available Properties (Renter)

**Endpoint:** `GET /api/renter/properties`

**Permission:** RENTER

**Response:**
```json
[
  {
    "id": 2,
    "name": "New Property",
    "type": "HOUSE",
    "location": "Boston",
    "price": "2100",
    "description": "A beautiful updated house",
    "images": "house1.jpg,house2.jpg",
    "amenities": "WiFi,Parking,Garden",
    "surface": "1500 sqft",
    "numberOfRooms": 5,
    "numberOfBathrooms": 2,
    "numberOfBedrooms": 3,
    "propertyRules": "No pets",
    "availability": true,
    "audiance": "FAMILIES",
    "createdAt": "2023-06-15T17:00:00",
    "updatedAt": "2023-06-15T17:30:00",
    "owner": {
      "id": 1,
      "firstName": "John",
      "lastName": "Updated"
    }
  }
]
```

#### Get Property by ID (Renter)

**Endpoint:** `GET /api/renter/properties/{id}`

**Permission:** RENTER

**Response:**
```json
{
  "id": 2,
  "name": "New Property",
  "type": "HOUSE",
  "location": "Boston",
  "price": "2100",
  "description": "A beautiful updated house",
  "images": "house1.jpg,house2.jpg",
  "amenities": "WiFi,Parking,Garden",
  "surface": "1500 sqft",
  "numberOfRooms": 5,
  "numberOfBathrooms": 2,