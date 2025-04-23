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
- Sign up / Login with email, Google, or Facebook.
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

## 🔚 Conclusion

RentMate combines swipe-based simplicity with deep functionality. From role flexibility and audience targeting to calendar scheduling, AI assistance, and verified identities, it creates a modern, secure, and intelligent rental experience.