# Frontend API Contract

This contract reflects the actual backend implementation in the project as of August 2026. It is intentionally aligned to the Java DTOs and controller methods that are currently present in the codebase.

Base URL:
- Local development: http://localhost:8080
- All routes below are relative to this base URL.

## 1. Type conventions and important backend realities

The backend uses a mix of numeric IDs and UUIDs depending on the domain:

- User IDs are `Long` values, for example `1`, `42`, `123`.
- Field IDs, project IDs, membership IDs, component IDs, and picture IDs are `UUID` values, for example `"8d5f0ff2-14be-4d52-99f8-9ee6fef1a4eb"`.
- `PATCH` endpoints usually require the full object payload including the `id` field.
- Most `DELETE` endpoints return an empty response body with HTTP `200 OK` in the current implementation, even though they are not explicitly returning `204 No Content`.
- Some GET endpoints are implemented as `GET` methods with a request body, which is unusual but matches the current controller code.
- The project includes multiple resource groups, not just users.

### UserRole enum
The `role` field on users is serialized as one of:

- `STUDENT`
- `TUTOR`
- `SUPERVISOR`

### Common response pattern
For successful reads and writes, the backend returns the resource object itself as JSON. For deletes, it returns empty content with `200 OK` unless a not-found exception is thrown.

### Expected error behavior
The current implementation generally follows this pattern:

- `400 Bad Request`: invalid JSON, missing required fields, or validation errors
- `404 Not Found`: item not found by ID
- `200 OK`: successful request, including deletes

---

## 2. Users module

Base path: `/users`

### 2.1 User model

```json
{
  "id": 1,
  "name": "Alice Johnson",
  "email": "alice@example.com",
  "role": "STUDENT",
  "fieldId": "8d5f0ff2-14be-4d52-99f8-9ee6fef1a4eb",
  "projectIds": [
    "2a4ad203-8f07-4685-a5ac-9d48fc2d179b"
  ]
}
```

`UserStorageModel` fields:
- `id`: `Long`
- `name`: `String`
- `email`: `String`
- `role`: `UserRole`
- `fieldId`: `UUID`
- `projectIds`: `List<UUID>`

### 2.2 GET /users
Fetch all users.

Example request:

```http
GET /users
```

Example response:

```json
[
  {
    "id": 1,
    "name": "Alice Johnson",
    "email": "alice@example.com",
    "role": "STUDENT",
    "fieldId": "8d5f0ff2-14be-4d52-99f8-9ee6fef1a4eb",
    "projectIds": [
      "2a4ad203-8f07-4685-a5ac-9d48fc2d179b"
    ]
  },
  {
    "id": 2,
    "name": "Daniel Smith",
    "email": "daniel@example.com",
    "role": "TUTOR",
    "fieldId": "8d5f0ff2-14be-4d52-99f8-9ee6fef1a4eb",
    "projectIds": []
  }
]
```

### 2.3 GET /users/{id}
Fetch a single user by numeric ID.

Path variable:
- `id`: `Long` user ID

Example request:

```http
GET /users/1
```

Example response:

```json
{
  "id": 1,
  "name": "Alice Johnson",
  "email": "alice@example.com",
  "role": "STUDENT",
  "fieldId": "8d5f0ff2-14be-4d52-99f8-9ee6fef1a4eb",
  "projectIds": [
    "2a4ad203-8f07-4685-a5ac-9d48fc2d179b"
  ]
}
```

Error example:

```json
{
  "message": "User with ID 999 was not found."
}
```

### 2.4 POST /users
Create a user.

Request body (`UserCreateModel`):

```json
{
  "id": 202402880,
  "name": "Alice Johnson",
  "email": "alice@example.com",
  "role": "STUDENT",
  "fieldId": "8d5f0ff2-14be-4d52-99f8-9ee6fef1a4eb",
  "projectIds": [
    "2a4ad203-8f07-4685-a5ac-9d48fc2d179b"
  ]
}
```

Validation notes:
- `id`: required `Long` user ID
- `name`: required, non-blank
- `email`: required, valid email
- `role`: required, one of `STUDENT`, `TUTOR`, `SUPERVISOR`
- `fieldId`: required `UUID`
- `projectIds`: optional list of `UUID`

Example request:

```http
POST /users
Content-Type: application/json
```

Example response:

```json
{
  "id": 1,
  "name": "Alice Johnson",
  "email": "alice@example.com",
  "role": "STUDENT",
  "fieldId": "8d5f0ff2-14be-4d52-99f8-9ee6fef1a4eb",
  "projectIds": [
    "2a4ad203-8f07-4685-a5ac-9d48fc2d179b"
  ]
}
```

### 2.5 PATCH /users
Update an existing user.

Request body (`UserPatchModel`):

```json
{
  "id": 1,
  "name": "Alice Johnson",
  "email": "alice@example.com",
  "role": "STUDENT",
  "fieldId": "8d5f0ff2-14be-4d52-99f8-9ee6fef1a4eb",
  "projectIds": [
    "2a4ad203-8f07-4685-a5ac-9d48fc2d179b",
    "3c1de816-7ef2-4bdd-8ac9-ffd6cc1486bd"
  ]
}
```

Important note:
- The ID is provided in the request body, not in the URL.

Example request:

```http
PATCH /users
Content-Type: application/json
```

Example response:

```json
{
  "id": 1,
  "name": "Alice Johnson",
  "email": "alice@example.com",
  "role": "STUDENT",
  "fieldId": "8d5f0ff2-14be-4d52-99f8-9ee6fef1a4eb",
  "projectIds": [
    "2a4ad203-8f07-4685-a5ac-9d48fc2d179b",
    "3c1de816-7ef2-4bdd-8ac9-ffd6cc1486bd"
  ]
}
```

### 2.6 DELETE /users/{id}
Delete a user by numeric ID.

Path variable:
- `id`: `Long` user ID

Example request:

```http
DELETE /users/1
```

Example response:

```http
HTTP/1.1 200 OK
Content-Length: 0
```

---

## 3. Fields module

Base path: `/fields`

### 3.1 Field model

```json
{
  "id": "8d5f0ff2-14be-4d52-99f8-9ee6fef1a4eb",
  "fieldName": "Computer Science",
  "projects": [
    "2a4ad203-8f07-4685-a5ac-9d48fc2d179b"
  ]
}
```

`FieldStorageModel` fields:
- `id`: `UUID`
- `fieldName`: `String`
- `projects`: `List<UUID>`

### 3.2 GET /fields
Fetch all fields.

Example request:

```http
GET /fields
```

Example response:

```json
[
  {
    "id": "8d5f0ff2-14be-4d52-99f8-9ee6fef1a4eb",
    "fieldName": "Computer Science",
    "projects": [
      "2a4ad203-8f07-4685-a5ac-9d48fc2d179b"
    ]
  },
  {
    "id": "6f09f7f0-2800-4d45-8d12-8f2f2647d90a",
    "fieldName": "Mechanical Engineering",
    "projects": []
  }
]
```

### 3.3 GET /fields/{id}
Fetch one field by UUID.

Path variable:
- `id`: `UUID` field ID

Example request:

```http
GET /fields/8d5f0ff2-14be-4d52-99f8-9ee6fef1a4eb
```

Example response:

```json
{
  "id": "8d5f0ff2-14be-4d52-99f8-9ee6fef1a4eb",
  "fieldName": "Computer Science",
  "projects": [
    "2a4ad203-8f07-4685-a5ac-9d48fc2d179b"
  ]
}
```

### 3.4 GET /fields/{id}/projects
Return project IDs associated with a field.

Path variable:
- `id`: `UUID` field ID

Example request:

```http
GET /fields/8d5f0ff2-14be-4d52-99f8-9ee6fef1a4eb/projects
```

Example response:

```json
[
  "2a4ad203-8f07-4685-a5ac-9d48fc2d179b",
  "4f91b9d5-1435-4fd0-95b1-f7d67d09af41"
]
```

### 3.5 GET /fields/{id}/users
Return user IDs associated with a field.

Path variable:
- `id`: `UUID` field ID

Example request:

```http
GET /fields/8d5f0ff2-14be-4d52-99f8-9ee6fef1a4eb/users
```

Example response:

```json
[
  1,
  7,
  21
]
```

### 3.7 POST /fields
Create a field.

Request body (`FieldCreateModel`):

```json
{
  "fieldName": "Computer Science"
}
```

Example request:

```http
POST /fields
Content-Type: application/json
```

Example response:

```json
{
  "id": "8d5f0ff2-14be-4d52-99f8-9ee6fef1a4eb",
  "fieldName": "Computer Science",
  "projects": []
}
```

### 3.8 PATCH /fields
Update a field.

Request body (`FieldPatchModel`):

```json
{
  "id": "8d5f0ff2-14be-4d52-99f8-9ee6fef1a4eb",
  "fieldName": "Software Engineering"
}
```

Example request:

```http
PATCH /fields
Content-Type: application/json
```

Example response:

```json
{
  "id": "8d5f0ff2-14be-4d52-99f8-9ee6fef1a4eb",
  "fieldName": "Software Engineering",
  "projects": [
    "2a4ad203-8f07-4685-a5ac-9d48fc2d179b"
  ]
}
```

### 3.9 DELETE /fields/{id}
Delete a field by UUID.

Path variable:
- `id`: `UUID` field ID

Example request:

```http
DELETE /fields/8d5f0ff2-14be-4d52-99f8-9ee6fef1a4eb
```

Example response:

```http
HTTP/1.1 200 OK
Content-Length: 0
```

---

## 4. Projects module

Base path: `/projects`

### 4.1 Project model

```json
{
  "id": "2a4ad203-8f07-4685-a5ac-9d48fc2d179b",
  "title": "Smart Locker Dashboard",
  "description": "A project to build a student inventory dashboard.",
  "academicYear": 2026,
  "tutorId": 7,
  "supervisorId": 42,
  "fieldId": "8d5f0ff2-14be-4d52-99f8-9ee6fef1a4eb",
  "pictures": [
    "aa8d0b53-57ab-4ed7-b595-7dd4f23d447e"
  ],
  "memberships": [
    "3a354569-bcab-4ef9-81df-5f3152c33ee8"
  ]
}
```

`ProjectStorageModel` fields:
- `id`: `UUID`
- `title`: `String`
- `description`: `String`
- `academicYear`: `int` (validated 2000-2100)
- `tutorId`: `Long` (nullable)
- `supervisorId`: `Long`
- `fieldId`: `UUID`
- `pictures`: `List<UUID>`
- `memberships`: `List<UUID>`

### 4.2 GET /projects
Fetch all projects.

Example request:

```http
GET /projects
```

Example response:

```json
[
  {
    "id": "2a4ad203-8f07-4685-a5ac-9d48fc2d179b",
    "title": "Smart Locker Dashboard",
    "description": "A project to build a student inventory dashboard.",
    "academicYear": 2026,
    "tutorId": 7,
    "supervisorId": 42,
    "fieldId": "8d5f0ff2-14be-4d52-99f8-9ee6fef1a4eb",
    "pictures": [
      "aa8d0b53-57ab-4ed7-b595-7dd4f23d447e"
    ],
    "memberships": [
      "3a354569-bcab-4ef9-81df-5f3152c33ee8"
    ]
  }
]
```

### 4.3 GET /projects/{id}
Fetch a single project by UUID.

Path variable:
- `id`: `UUID` project ID

Example request:

```http
GET /projects/2a4ad203-8f07-4685-a5ac-9d48fc2d179b
```

Example response:

```json
{
  "id": "2a4ad203-8f07-4685-a5ac-9d48fc2d179b",
  "title": "Smart Locker Dashboard",
  "description": "A project to build a student inventory dashboard.",
  "academicYear": 2026,
  "tutorId": 7,
  "supervisorId": 42,
  "fieldId": "8d5f0ff2-14be-4d52-99f8-9ee6fef1a4eb",
  "pictures": [
    "aa8d0b53-57ab-4ed7-b595-7dd4f23d447e"
  ],
  "memberships": [
    "3a354569-bcab-4ef9-81df-5f3152c33ee8"
  ]
}
```

### 4.4 GET /projects/{id}/members
Return member user IDs for a project.

Path variable:
- `id`: `UUID` project ID

Example request:

```http
GET /projects/2a4ad203-8f07-4685-a5ac-9d48fc2d179b/members
```

Example response:

```json
[
  1,
  4,
  12
]
```

### 4.5 POST /projects
Create a project.

Request body (`ProjectCreateModel`):

```json
{
  "title": "Smart Locker Dashboard",
  "description": "A project to build a student inventory dashboard.",
  "academicYear": 2026,
  "tutorId": 7,
  "supervisorId": 42,
  "fieldId": "8d5f0ff2-14be-4d52-99f8-9ee6fef1a4eb"
}
```

Validation notes:
- `title`: required, non-blank
- `academicYear`: required, between `2000` and `2100`
- `tutorId`: optional `Long`
- `supervisorId`: required `Long`
- `fieldId`: required `UUID`

Example request:

```http
POST /projects
Content-Type: application/json
```

Example response:

```json
{
  "id": "2a4ad203-8f07-4685-a5ac-9d48fc2d179b",
  "title": "Smart Locker Dashboard",
  "description": "A project to build a student inventory dashboard.",
  "academicYear": 2026,
  "tutorId": 7,
  "supervisorId": 42,
  "fieldId": "8d5f0ff2-14be-4d52-99f8-9ee6fef1a4eb",
  "pictures": [],
  "memberships": []
}
```

### 4.6 PATCH /projects
Update an existing project.

Request body (`ProjectPatchModel`):

```json
{
  "id": "2a4ad203-8f07-4685-a5ac-9d48fc2d179b",
  "title": "Smart Locker Dashboard",
  "description": "Updated dashboard scope for 2026.",
  "academicYear": 2026,
  "tutorId": 7,
  "supervisorId": 42,
  "fieldId": "8d5f0ff2-14be-4d52-99f8-9ee6fef1a4eb"
}
```

Example request:

```http
PATCH /projects
Content-Type: application/json
```

Example response:

```json
{
  "id": "2a4ad203-8f07-4685-a5ac-9d48fc2d179b",
  "title": "Smart Locker Dashboard",
  "description": "Updated dashboard scope for 2026.",
  "academicYear": 2026,
  "tutorId": 7,
  "supervisorId": 42,
  "fieldId": "8d5f0ff2-14be-4d52-99f8-9ee6fef1a4eb",
  "pictures": [
    "aa8d0b53-57ab-4ed7-b595-7dd4f23d447e"
  ],
  "memberships": [
    "3a354569-bcab-4ef9-81df-5f3152c33ee8"
  ]
}
```

### 4.7 DELETE /projects/{id}
Delete a project by UUID.

Path variable:
- `id`: `UUID` project ID

Example request:

```http
DELETE /projects/2a4ad203-8f07-4685-a5ac-9d48fc2d179b
```

Example response:

```http
HTTP/1.1 200 OK
Content-Length: 0
```

---

## 5. Project memberships module

Base path: `/memberships`

### 5.1 Membership model

```json
{
  "id": "3a354569-bcab-4ef9-81df-5f3152c33ee8",
  "projectId": "2a4ad203-8f07-4685-a5ac-9d48fc2d179b",
  "memberId": 12
}
```

`ProjectMembershipStorageModel` fields:
- `id`: `UUID`
- `projectId`: `UUID`
- `memberId`: `Long`

### 5.2 GET /memberships
Fetch all memberships.

Example request:

```http
GET /memberships
```

Example response:

```json
[
  {
    "id": "3a354569-bcab-4ef9-81df-5f3152c33ee8",
    "projectId": "2a4ad203-8f07-4685-a5ac-9d48fc2d179b",
    "memberId": 12
  },
  {
    "id": "b86b29d8-f93d-4a8d-b6d7-7f9fe8851d1d",
    "projectId": "4f91b9d5-1435-4fd0-95b1-f7d67d09af41",
    "memberId": 4
  }
]
```

### 5.3 GET /memberships/{id}
Fetch a single membership by UUID.

Path variable:
- `id`: `UUID` membership ID

Example request:

```http
GET /memberships/3a354569-bcab-4ef9-81df-5f3152c33ee8
```

Example response:

```json
{
  "id": "3a354569-bcab-4ef9-81df-5f3152c33ee8",
  "projectId": "2a4ad203-8f07-4685-a5ac-9d48fc2d179b",
  "memberId": 12
}
```

### 5.4 POST /memberships
Create a membership.

Request body (`ProjectMembershipCreateModel`):

```json
{
  "projectId": "2a4ad203-8f07-4685-a5ac-9d48fc2d179b",
  "memberId": 12
}
```

Example request:

```http
POST /memberships
Content-Type: application/json
```

Example response:

```json
{
  "id": "3a354569-bcab-4ef9-81df-5f3152c33ee8",
  "projectId": "2a4ad203-8f07-4685-a5ac-9d48fc2d179b",
  "memberId": 12
}
```

### 5.5 PATCH /memberships
Update an existing membership.

Request body (`ProjectMembershipPatchModel`):

```json
{
  "id": "3a354569-bcab-4ef9-81df-5f3152c33ee8",
  "projectId": "2a4ad203-8f07-4685-a5ac-9d48fc2d179b",
  "memberId": 15
}
```

Example request:

```http
PATCH /memberships
Content-Type: application/json
```

Example response:

```json
{
  "id": "3a354569-bcab-4ef9-81df-5f3152c33ee8",
  "projectId": "2a4ad203-8f07-4685-a5ac-9d48fc2d179b",
  "memberId": 15
}
```

### 5.6 DELETE /memberships/{id}
Delete a membership by UUID.

Path variable:
- `id`: `UUID` membership ID

Example request:

```http
DELETE /memberships/3a354569-bcab-4ef9-81df-5f3152c33ee8
```

Example response:

```http
HTTP/1.1 200 OK
Content-Length: 0
```

### 5.7 DELETE /memberships/project/{projectId}
Delete all memberships related to a project.

Path variable:
- `projectId`: `UUID` project ID

Example request:

```http
DELETE /memberships/project/2a4ad203-8f07-4685-a5ac-9d48fc2d179b
```

Example response:

```http
HTTP/1.1 200 OK
Content-Length: 0
```

### 5.8 DELETE /memberships/user/{userId}
Delete all memberships related to a user.

Path variable:
- `userId`: `Long` user ID

Example request:

```http
DELETE /memberships/user/12
```

Example response:

```http
HTTP/1.1 200 OK
Content-Length: 0
```

---

## 6. Components module

Base path: `/components`

### 6.1 Component model

```json
{
  "id": "a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d",
  "name": "Raspberry Pi 4",
  "totalQuantity": 25,
  "availableQuantity": 17,
  "reservations": [
    "e6f4d55b-b61b-4b95-a8fb-65a6db4f2f3c"
  ]
}
```

`ComponentStorageModel` fields:
- `id`: `UUID`
- `name`: `String`
- `totalQuantity`: `int`
- `availableQuantity`: `int`
- `reservations`: `List<UUID>`

### 6.2 GET /components
Fetch all components.

Example request:

```http
GET /components
```

Example response:

```json
[
  {
    "id": "a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d",
    "name": "Raspberry Pi 4",
    "totalQuantity": 25,
    "availableQuantity": 17,
    "reservations": [
      "e6f4d55b-b61b-4b95-a8fb-65a6db4f2f3c"
    ]
  },
  {
    "id": "d14b8262-0646-4b6a-bd2b-89e22d6be32d",
    "name": "Arduino Uno",
    "totalQuantity": 12,
    "availableQuantity": 12,
    "reservations": []
  }
]
```

### 6.3 GET /components/{id}
Fetch a single component by UUID.

Path variable:
- `id`: `UUID` component ID

Example request:

```http
GET /components/a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d
```

Example response:

```json
{
  "id": "a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d",
  "name": "Raspberry Pi 4",
  "totalQuantity": 25,
  "availableQuantity": 17,
  "reservations": [
    "e6f4d55b-b61b-4b95-a8fb-65a6db4f2f3c"
  ]
}
```

### 6.5 POST /components
Create a component.

Request body (`ComponentCreateModel`):

```json
{
  "name": "Raspberry Pi 4",
  "totalQuantity": 25,
  "availableQuantity": 17
}
```

Validation notes:
- `name`: required
- `totalQuantity`: required, must be positive
- `availableQuantity`: required, must be zero or positive

Example request:

```http
POST /components
Content-Type: application/json
```

Example response:

```json
{
  "id": "a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d",
  "name": "Raspberry Pi 4",
  "totalQuantity": 25,
  "availableQuantity": 17,
  "reservations": []
}
```

### 6.6 PATCH /components
Update an existing component.

Request body (`ComponentPatchModel`):

```json
{
  "id": "a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d",
  "name": "Raspberry Pi 4",
  "totalQuantity": 30,
  "availableQuantity": 20
}
```

Example request:

```http
PATCH /components
Content-Type: application/json
```

Example response:

```json
{
  "id": "a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d",
  "name": "Raspberry Pi 4",
  "totalQuantity": 30,
  "availableQuantity": 20,
  "reservations": [
    "e6f4d55b-b61b-4b95-a8fb-65a6db4f2f3c"
  ]
}
```

### 6.7 DELETE /components/{id}
Delete a component by UUID.

Path variable:
- `id`: `UUID` component ID

Example request:

```http
DELETE /components/a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d
```

Example response:

```http
HTTP/1.1 200 OK
Content-Length: 0
```

---

## 7. Component reservations module

Base path: `/componentReservations`

This controller is mapped to `/componentReservations` exactly, including the capital `R`.

### 7.1 Reservation model

```json
{
  "id": "e6f4d55b-b61b-4b95-a8fb-65a6db4f2f3c",
  "componentId": "a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d",
  "reservedFrom": "2026-09-01T09:00:00Z",
  "reservedTo": "2026-09-01T12:00:00Z",
  "quantity": 2,
  "borrowerId": 12
}
```

`ComponentReservationStorageModel` fields:
- `id`: `UUID`
- `componentId`: `UUID`
- `reservedFrom`: `ZonedDateTime`
- `reservedTo`: `ZonedDateTime`
- `quantity`: `int`
- `borrowerId`: `Long`

### 7.2 GET /componentReservations
Fetch all reservations.

Example request:

```http
GET /componentReservations
```

Example response:

```json
[
  {
    "id": "e6f4d55b-b61b-4b95-a8fb-65a6db4f2f3c",
    "componentId": "a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d",
    "reservedFrom": "2026-09-01T09:00:00Z",
    "reservedTo": "2026-09-01T12:00:00Z",
    "quantity": 2,
    "borrowerId": 12
  }
]
```

### 7.3 GET /componentReservations/{id}
Fetch one reservation by UUID.

Path variable:
- `id`: `UUID` reservation ID

Example request:

```http
GET /componentReservations/e6f4d55b-b61b-4b95-a8fb-65a6db4f2f3c
```

Example response:

```json
{
  "id": "e6f4d55b-b61b-4b95-a8fb-65a6db4f2f3c",
  "componentId": "a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d",
  "reservedFrom": "2026-09-01T09:00:00Z",
  "reservedTo": "2026-09-01T12:00:00Z",
  "quantity": 2,
  "borrowerId": 12
}
```

### 7.4 GET /componentReservations/component/{componentId}
Fetch reservations for a specific component.

Path variable:
- `componentId`: `UUID` component ID

Example request:

```http
GET /componentReservations/component/a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d
```

Example response:

```json
[
  {
    "id": "e6f4d55b-b61b-4b95-a8fb-65a6db4f2f3c",
    "componentId": "a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d",
    "reservedFrom": "2026-09-01T09:00:00Z",
    "reservedTo": "2026-09-01T12:00:00Z",
    "quantity": 2,
    "borrowerId": 12
  }
]
```

### 7.5 GET /componentReservations/user/{userId}
Fetch reservations for a specific borrower.

Path variable:
- `userId`: `Long` user ID

Example request:

```http
GET /componentReservations/user/12
```

Example response:

```json
[
  {
    "id": "e6f4d55b-b61b-4b95-a8fb-65a6db4f2f3c",
    "componentId": "a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d",
    "reservedFrom": "2026-09-01T09:00:00Z",
    "reservedTo": "2026-09-01T12:00:00Z",
    "quantity": 2,
    "borrowerId": 12
  }
]
```

### 7.7 POST /componentReservations
Create a reservation.

Request body (`ComponentReservationCreateModel`):

```json
{
  "componentId": "a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d",
  "reservedFrom": "2026-09-01T09:00:00Z",
  "reservedTo": "2026-09-01T12:00:00Z",
  "quantity": 2,
  "borrowerId": 12
}
```

Validation notes:
- `componentId`: required `UUID`
- `reservedFrom`: required `ZonedDateTime`
- `reservedTo`: required `ZonedDateTime`
- `quantity`: required, must be positive
- `borrowerId`: required `Long`

Example request:

```http
POST /componentReservations
Content-Type: application/json
```

Example response:

```json
{
  "id": "e6f4d55b-b61b-4b95-a8fb-65a6db4f2f3c",
  "componentId": "a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d",
  "reservedFrom": "2026-09-01T09:00:00Z",
  "reservedTo": "2026-09-01T12:00:00Z",
  "quantity": 2,
  "borrowerId": 12
}
```

### 7.8 PATCH /componentReservations
Update a reservation.

Request body (`ComponentReservationPatchModel`):

```json
{
  "id": "e6f4d55b-b61b-4b95-a8fb-65a6db4f2f3c",
  "componentId": "a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d",
  "reservedFrom": "2026-09-01T10:00:00Z",
  "reservedTo": "2026-09-01T13:00:00Z",
  "quantity": 3,
  "borrowerId": 12
}
```

Example request:

```http
PATCH /componentReservations
Content-Type: application/json
```

Example response:

```json
{
  "id": "e6f4d55b-b61b-4b95-a8fb-65a6db4f2f3c",
  "componentId": "a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d",
  "reservedFrom": "2026-09-01T10:00:00Z",
  "reservedTo": "2026-09-01T13:00:00Z",
  "quantity": 3,
  "borrowerId": 12
}
```

### 7.9 DELETE /componentReservations/{id}
Delete a reservation by UUID.

Path variable:
- `id`: `UUID` reservation ID

Example request:

```http
DELETE /componentReservations/e6f4d55b-b61b-4b95-a8fb-65a6db4f2f3c
```

Example response:

```http
HTTP/1.1 200 OK
Content-Length: 0
```

### 7.10 DELETE /componentReservations/component/{componentId}
Delete all reservations for a component.

Path variable:
- `componentId`: `UUID` component ID

Example request:

```http
DELETE /componentReservations/component/a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d
```

Example response:

```http
HTTP/1.1 200 OK
Content-Length: 0
```

---

## 8. Project pictures module

Base path: `/projectpictures`

### 8.1 Project picture model

```json
{
  "id": "a8f99dfe-6f1d-44e2-a1b1-81c5deab06af",
  "projectId": "2a4ad203-8f07-4685-a5ac-9d48fc2d179b",
  "storageKey": "projects/2a4ad203-8f07-4685-a5ac-9d48fc2d179b/cover.jpg",
  "order": 0,
  "caption": "Project cover image"
}
```

`ProjectPictureStorageModel` fields:
- `id`: `UUID`
- `projectId`: `UUID`
- `storageKey`: `String`
- `order`: `int`
- `caption`: `String`

### 8.2 GET /projectpictures
Fetch all project pictures.

Example request:

```http
GET /projectpictures
```

Example response:

```json
[
  {
    "id": "a8f99dfe-6f1d-44e2-a1b1-81c5deab06af",
    "projectId": "2a4ad203-8f07-4685-a5ac-9d48fc2d179b",
    "storageKey": "projects/2a4ad203-8f07-4685-a5ac-9d48fc2d179b/cover.jpg",
    "order": 0,
    "caption": "Project cover image"
  }
]
```

### 8.3 GET /projectpictures/{id}
Fetch one project picture by UUID.

Path variable:
- `id`: `UUID` picture ID

Example request:

```http
GET /projectpictures/a8f99dfe-6f1d-44e2-a1b1-81c5deab06af
```

Example response:

```json
{
  "id": "a8f99dfe-6f1d-44e2-a1b1-81c5deab06af",
  "projectId": "2a4ad203-8f07-4685-a5ac-9d48fc2d179b",
  "storageKey": "projects/2a4ad203-8f07-4685-a5ac-9d48fc2d179b/cover.jpg",
  "order": 0,
  "caption": "Project cover image"
}
```

### 8.4 GET /projectpictures/project/{projectId}
Fetch all pictures for a project.

Path variable:
- `projectId`: `UUID` project ID

Example request:

```http
GET /projectpictures/project/2a4ad203-8f07-4685-a5ac-9d48fc2d179b
```

Example response:

```json
[
  {
    "id": "a8f99dfe-6f1d-44e2-a1b1-81c5deab06af",
    "projectId": "2a4ad203-8f07-4685-a5ac-9d48fc2d179b",
    "storageKey": "projects/2a4ad203-8f07-4685-a5ac-9d48fc2d179b/cover.jpg",
    "order": 0,
    "caption": "Project cover image"
  },
  {
    "id": "f7e7ca63-6d50-4fb9-a878-a240bcbd1c2d",
    "projectId": "2a4ad203-8f07-4685-a5ac-9d48fc2d179b",
    "storageKey": "projects/2a4ad203-8f07-4685-a5ac-9d48fc2d179b/detail-1.jpg",
    "order": 1,
    "caption": "Dashboard screenshot"
  }
]
```

### 8.5 POST /projectpictures
Create a project picture.

Request body (`ProjectPictureCreateModel`):

```json
{
  "projectId": "2a4ad203-8f07-4685-a5ac-9d48fc2d179b",
  "storageKey": "projects/2a4ad203-8f07-4685-a5ac-9d48fc2d179b/cover.jpg",
  "order": 0,
  "caption": "Project cover image"
}
```

Example request:

```http
POST /projectpictures
Content-Type: application/json
```

Example response:

```json
{
  "id": "a8f99dfe-6f1d-44e2-a1b1-81c5deab06af",
  "projectId": "2a4ad203-8f07-4685-a5ac-9d48fc2d179b",
  "storageKey": "projects/2a4ad203-8f07-4685-a5ac-9d48fc2d179b/cover.jpg",
  "order": 0,
  "caption": "Project cover image"
}
```

### 8.6 PATCH /projectpictures
Update a project picture.

Request body (`ProjectPicturePatchModel`):

```json
{
  "id": "a8f99dfe-6f1d-44e2-a1b1-81c5deab06af",
  "projectId": "2a4ad203-8f07-4685-a5ac-9d48fc2d179b",
  "storageKey": "projects/2a4ad203-8f07-4685-a5ac-9d48fc2d179b/cover-updated.jpg",
  "order": 0,
  "caption": "Updated cover image"
}
```

Example request:

```http
PATCH /projectpictures
Content-Type: application/json
```

Example response:

```json
{
  "id": "a8f99dfe-6f1d-44e2-a1b1-81c5deab06af",
  "projectId": "2a4ad203-8f07-4685-a5ac-9d48fc2d179b",
  "storageKey": "projects/2a4ad203-8f07-4685-a5ac-9d48fc2d179b/cover-updated.jpg",
  "order": 0,
  "caption": "Updated cover image"
}
```

### 8.7 DELETE /projectpictures/{id}
Delete a project picture by UUID.

Path variable:
- `id`: `UUID` picture ID

Example request:

```http
DELETE /projectpictures/a8f99dfe-6f1d-44e2-a1b1-81c5deab06af
```

Example response:

```http
HTTP/1.1 200 OK
Content-Length: 0
```

### 8.8 DELETE /projectpictures/project/{projectId}
Delete all pictures for a project.

Path variable:
- `projectId`: `UUID` project ID

Example request:

```http
DELETE /projectpictures/project/2a4ad203-8f07-4685-a5ac-9d48fc2d179b
```

Example response:

```http
HTTP/1.1 200 OK
Content-Length: 0
```

---

## 9. Component pictures module

Base path: `/componentpictures`

### 9.1 Component picture model

```json
{
  "id": "3d7b9cf0-b36c-49d9-9de0-9983a6244f09",
  "componentId": "a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d",
  "storageKey": "components/a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d/front.jpg",
  "order": 0,
  "caption": "Board front view"
}
```

`ComponentPictureStorageModel` fields:
- `id`: `UUID`
- `componentId`: `UUID`
- `storageKey`: `String`
- `order`: `int`
- `caption`: `String`

### 9.2 GET /componentpictures
Fetch all component pictures.

Example request:

```http
GET /componentpictures
```

Example response:

```json
[
  {
    "id": "3d7b9cf0-b36c-49d9-9de0-9983a6244f09",
    "componentId": "a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d",
    "storageKey": "components/a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d/front.jpg",
    "order": 0,
    "caption": "Board front view"
  }
]
```

### 9.3 GET /componentpictures/{id}
Fetch a single component picture by UUID.

Path variable:
- `id`: `UUID` picture ID

Example request:

```http
GET /componentpictures/3d7b9cf0-b36c-49d9-9de0-9983a6244f09
```

Example response:

```json
{
  "id": "3d7b9cf0-b36c-49d9-9de0-9983a6244f09",
  "componentId": "a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d",
  "storageKey": "components/a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d/front.jpg",
  "order": 0,
  "caption": "Board front view"
}
```

### 9.5 GET /componentpictures/component/{componentId}/single
Fetch a single component picture for a component by component ID.

Path variable:
- `componentId`: `UUID` component ID

Example request:

```http
GET /componentpictures/component/a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d/single
```

Example response:

```json
{
  "id": "3d7b9cf0-b36c-49d9-9de0-9983a6244f09",
  "componentId": "a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d",
  "storageKey": "components/a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d/front.jpg",
  "order": 0,
  "caption": "Board front view"
}
```

### 9.6 GET /componentpictures/component/{componentId}/single/{order}
Fetch a component picture for a component by component ID and order index.

Path variables:
- `componentId`: `UUID` component ID
- `order`: `int` image order

Example request:

```http
GET /componentpictures/component/a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d/single/0
```

Example response:

```json
{
  "id": "3d7b9cf0-b36c-49d9-9de0-9983a6244f09",
  "componentId": "a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d",
  "storageKey": "components/a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d/front.jpg",
  "order": 0,
  "caption": "Board front view"
}
```

### 9.7 GET /componentpictures/component/{componentId}
Fetch all pictures for a component.

Path variable:
- `componentId`: `UUID` component ID

Example request:

```http
GET /componentpictures/component/a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d
```

Example response:

```json
[
  {
    "id": "3d7b9cf0-b36c-49d9-9de0-9983a6244f09",
    "componentId": "a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d",
    "storageKey": "components/a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d/front.jpg",
    "order": 0,
    "caption": "Board front view"
  },
  {
    "id": "8fa5773d-1804-4344-b61d-7fcbb8be13da",
    "componentId": "a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d",
    "storageKey": "components/a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d/back.jpg",
    "order": 1,
    "caption": "Board back view"
  }
]
```

### 9.8 POST /componentpictures
Create a component picture.

Request body (`ComponentPictureCreateModel`):

```json
{
  "componentId": "a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d",
  "storageKey": "components/a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d/front.jpg",
  "order": 0,
  "caption": "Board front view"
}
```

Example request:

```http
POST /componentpictures
Content-Type: application/json
```

Example response:

```json
{
  "id": "3d7b9cf0-b36c-49d9-9de0-9983a6244f09",
  "componentId": "a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d",
  "storageKey": "components/a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d/front.jpg",
  "order": 0,
  "caption": "Board front view"
}
```

### 9.9 PATCH /componentpictures
Update a component picture.

Request body (`ComponentPicturePatchModel`):

```json
{
  "id": "3d7b9cf0-b36c-49d9-9de0-9983a6244f09",
  "componentId": "a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d",
  "storageKey": "components/a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d/front-updated.jpg",
  "order": 0,
  "caption": "Updated board front view"
}
```

Example request:

```http
PATCH /componentpictures
Content-Type: application/json
```

Example response:

```json
{
  "id": "3d7b9cf0-b36c-49d9-9de0-9983a6244f09",
  "componentId": "a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d",
  "storageKey": "components/a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d/front-updated.jpg",
  "order": 0,
  "caption": "Updated board front view"
}
```

### 9.10 DELETE /componentpictures/{id}
Delete a component picture by UUID.

Path variable:
- `id`: `UUID` picture ID

Example request:

```http
DELETE /componentpictures/3d7b9cf0-b36c-49d9-9de0-9983a6244f09
```

Example response:

```http
HTTP/1.1 200 OK
Content-Length: 0
```

### 9.11 DELETE /componentpictures/component/{componentId}
Delete all pictures for a component.

Path variable:
- `componentId`: `UUID` component ID

Example request:

```http
DELETE /componentpictures/component/a56f6ad3-28cb-4326-93c9-c6b7a0ccac8d
```

Example response:

```http
HTTP/1.1 200 OK
Content-Length: 0
```

---

## 10. Global notes for the frontend

1. The API is currently implementation-driven rather than perfectly normalized.
2. The route names and parameter conventions are not fully uniform:
   - Users use numeric `Long` IDs.
   - Most other resources use `UUID`s.
   - Some `GET` requests accept a body instead of path variables.
   - The path `/componentReservations` includes a capital `R`.
3. `PATCH` requests should include the target resource `id` in the body.
4. Delete routes currently return empty bodies with HTTP `200 OK`.
5. For example payloads, use JSON field names exactly as they appear in the Java DTOs, including names such as `fieldId`, `projectId`, `componentId`, `borrowerId`, `memberId`, `storageKey`, and `fieldName`.

This document is intended to reflect the backend exactly as it exists today, so frontend code should treat it as the contract until the server is refactored or normalized.
