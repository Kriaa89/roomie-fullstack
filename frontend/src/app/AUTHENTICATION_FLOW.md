# Authentication Flow in Roomie Application

This document describes the expected authentication flow in the Roomie application and what should happen at each step.

## Landing Page

1. User visits the application and sees the landing page
2. The landing page displays:
   - Welcome message and application description
   - Features for different user roles (Property Owners, Renters, Roommate Hosts)
   - Buttons to "Create Account" or "Login"

## Registration Flow

1. User clicks "Create Account" on the landing page
2. User is directed to the registration page
3. User fills out the registration form with:
   - First name and last name
   - Email address
   - Phone number
   - Location
   - Age (must be at least 18)
   - Password and password confirmation
4. Form validates all inputs and shows appropriate error messages
5. User submits the form
6. If registration is successful:
   - User is authenticated (token stored)
   - User is redirected to the role selection page
7. If registration fails:
   - Error message is displayed
   - User can correct the information and try again
8. User can navigate back to the landing page or to the login page if they already have an account

## Login Flow

1. User clicks "Login" on the landing page
2. User is directed to the login page
3. User enters their email and password
4. Form validates inputs and shows appropriate error messages
5. User submits the form
6. If login is successful:
   - User is authenticated (token stored)
   - If user already has roles, they are redirected to the dashboard
   - If user has no roles, they are redirected to the role selection page
7. If login fails:
   - Error message is displayed
   - User can correct the information and try again
8. User can navigate back to the landing page or to the registration page if they don't have an account

## Role Selection Flow

1. After registration or if a user logs in without any roles, they are directed to the role selection page
2. User sees available roles (filtered to exclude roles they already have)
3. User selects a role by clicking on a role card
4. User clicks "Confirm Selection" to submit their choice
5. If role selection is successful:
   - User's roles are updated
   - User is redirected to the dashboard
6. If role selection fails:
   - Error message is displayed
   - User can try again
7. User can log out if they decide not to select a role

## Dashboard

1. After successful login and role selection, user is directed to the dashboard
2. Dashboard displays:
   - User information and roles
   - Navigation menu based on user roles
   - List of properties (if available)
3. User can:
   - View properties
   - Create properties (if they have the OWNER role)
   - Navigate to other sections of the application
   - Log out

## Logout Flow

1. User clicks "Logout" button
2. User's authentication state is cleared (token removed)
3. User is redirected to the login page

## Edge Cases

1. **Session Expiration**: If the user's token expires, they should be redirected to the login page
2. **Unauthorized Access**: If a user tries to access a page they don't have permission for, they should be redirected to an appropriate page
3. **No Available Roles**: If a user has all possible roles, the role selection page should display a message and redirect to the dashboard
4. **Network Errors**: If there are network errors during API calls, appropriate error messages should be displayed

## Testing Checklist

- [ ] Landing page loads correctly with all elements
- [ ] Registration form validates all fields correctly
- [ ] Login form validates all fields correctly
- [ ] Role selection page displays available roles correctly
- [ ] Dashboard loads user information and properties correctly
- [ ] Navigation between pages works as expected
- [ ] Logout functionality works correctly
- [ ] Error handling works for all API calls
- [ ] Edge cases are handled appropriately
