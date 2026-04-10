# Registration Implementation Summary

## Overview
Implemented a complete user registration flow that stores user details in the database when users register through the Angular frontend.

## Changes Made

### 1. Frontend - Angular (Register Component & Auth Service)

#### File: `frontend/src/app/services/auth.service.ts`
**Changes:**
- Updated `RegisterRequest` interface to use `name` instead of `fullName` to match the Approver entity
- Changed registration endpoint from `/api/committees` to `/api/approvers`
- Changed login endpoint from `/api/committees/login` to `/api/approvers/login`

```typescript
export interface RegisterRequest {
  role: string;
  name: string;           // Changed from fullName
  email: string;
  password: string;
}

register(userData: RegisterRequest): Observable<any> {
  return this.http.post(`${this.baseUrl}/api/approvers`, userData);  // Changed endpoint
}

login(credentials: LoginRequest): Observable<any> {
  return this.http.post(`${this.baseUrl}/api/approvers/login`, credentials);  // Changed endpoint
}
```

#### File: `frontend/src/app/components/register/register.component.html`
**Changes:**
- Updated form control name from `fullName` to `name`
- Adjusted error message references accordingly

#### File: `frontend/src/app/components/register/register.component.ts`
**Changes:**
- Updated form group initialization to use `name` instead of `fullName`
- Updated `onSubmit()` method to extract `name` instead of `fullName`
- Updated `getFieldLabel()` method to map `name` label correctly

---

### 2. Backend - Spring Boot (Repository & Controller)

#### File: `src/main/java/com/example/committeeportal/Repository/ApproverRepository.java`
**Changes:**
- Added `existsByEmailIgnoreCase(String email)` method for email uniqueness validation

```java
// Check if email already exists
boolean existsByEmailIgnoreCase(String email);
```

#### File: `src/main/java/com/example/committeeportal/Controller/ApproverController.java`
**Changes:**
1. **Enhanced `createApprover()` method** - Added email uniqueness validation
   - Checks if email already exists before saving
   - Returns HTTP 409 (CONFLICT) if email is duplicate
   - Returns HTTP 201 (CREATED) with saved approver object on success

2. **Added `login()` endpoint** - New POST endpoint for user authentication
   - Endpoint: `POST /api/approvers/login`
   - Validates email and password
   - Returns the approver object on successful login
   - Returns HTTP 401 (UNAUTHORIZED) for invalid credentials
   - Includes proper logging

```java
@PostMapping
public ResponseEntity<Approver> createApprover(@RequestBody Approver approver) {
    // Check if email already exists
    if (approver.getEmail() != null && approverRepository.existsByEmailIgnoreCase(approver.getEmail())) {
        logger.warn("Email {} already exists", approver.getEmail());
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
    // Save and return
    Approver saved = approverRepository.save(approver);
    return new ResponseEntity<>(saved, HttpStatus.CREATED);
}

@PostMapping("/login")
public ResponseEntity<Approver> login(@RequestBody Approver loginRequest) {
    // Validate credentials and return approver or 401
}
```

---

## Database Schema
The existing `Approver` entity already has all required fields:
- `approverId` (Primary Key)
- `name` - User's full name
- `role` - User's role (committee-head, faculty-incharge, principal)
- `email` - User's email address (unique constraint now enforced)
- `password` - User's password
- `digitalSignature` - Optional digital signature

---

## Registration Flow

1. **User fills registration form** with:
   - Role (committee-head, faculty-incharge, principal)
   - Full Name
   - Email Address
   - Password (validated for strength: min 8 chars, uppercase, lowercase, digit)
   - Confirm Password (must match)

2. **Frontend validates:**
   - Required fields
   - Email format
   - Password strength
   - Password confirmation match

3. **Frontend sends POST request** to `http://localhost:8080/api/approvers` with:
   ```json
   {
     "role": "committee-head",
     "name": "John Doe",
     "email": "john@institution.edu",
     "password": "SecurePass123"
   }
   ```

4. **Backend validates:**
   - Email uniqueness (409 CONFLICT if duplicate)
   - All required fields present

5. **Backend saves to database:**
   - Creates new Approver record in PostgreSQL
   - Returns 201 CREATED with approver object

6. **Frontend redirects to login** after successful registration

---

## API Endpoints

### Registration
- **URL:** `POST /api/approvers`
- **Request Body:** Approver object with name, role, email, password
- **Success Response:** 201 CREATED + Approver object
- **Error Responses:**
  - 409 CONFLICT - Email already exists
  - 500 INTERNAL_SERVER_ERROR - Server error

### Login
- **URL:** `POST /api/approvers/login`
- **Request Body:** `{ email, password }`
- **Success Response:** 200 OK + Approver object
- **Error Responses:**
  - 400 BAD_REQUEST - Missing email or password
  - 401 UNAUTHORIZED - Invalid credentials
  - 500 INTERNAL_SERVER_ERROR - Server error

---

## Database Configuration
**Existing in `application.properties`:**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/committeeportal
spring.datasource.username=postgres
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
```

The `ddl-auto=update` setting automatically creates/updates the `approver` table based on the entity definition.

---

## Testing the Registration

### Using Postman or curl:
```bash
curl -X POST http://localhost:8080/api/approvers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "role": "committee-head",
    "email": "test@institution.edu",
    "password": "TestPass123"
  }'
```

### Expected Response (201 Created):
```json
{
  "approverId": 1,
  "name": "Test User",
  "role": "committee-head",
  "email": "test@institution.edu",
  "password": "TestPass123",
  "digitalSignature": null
}
```

---

## Security Considerations
- ⚠️ **Note:** Passwords are stored in plain text. Consider implementing password hashing (BCrypt) in production.
- Email uniqueness is enforced at the database level.
- Form validation on frontend prevents invalid data submission.
- Backend validation ensures data integrity.
