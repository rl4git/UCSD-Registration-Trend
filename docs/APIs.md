> domain: `api.ucsdregistration.com`

### Get All Departments

- **Endpoint**: `GET /api/courses/departments`
- **Parameters**: None
- **Sample Request**:

  - `/api/courses/departments`

- **Successful Response (200 OK)**:

  ```json
  [
    "SE",
    "GLBH",
    "BGRD",
    "LTSP",
    "MUS",
    ...,
    "MATH"
  ]
  ```

---

### Get All Course IDs by Department

- **Endpoint**: `GET /api/courses/courseId/by-department`
- **Parameters**:

  - `department`: _(Required)_ Department identifier, e.g., `CSE`, `MATH`

- **Sample Request**:

  - `/api/courses/courseId/by-department?department=CSE`

- **Successful Response (200 OK)**:

  ```json
  [
    "176E",
    "293",
    "290",
    "150A",
    "182",
    "209B",
    "140L",
    "20",
    "253R",
    ...,
    "118"
  ]
  ```

---

### Get All Course Information by Professor

- **Endpoint**: `GET /api/courses/by-prof-name`
- **Query Parameters**:

  - `profFirstName`: Professor's first name
  - `profLastName`: Professor's last name

- **Sample Request**:

  - `/api/courses/by-prof-name?profFirstName=Richard&profLastName=Averitt`

- **Successful Response (200 OK)**:
  _(Sample JSON omitted for brevity—identical to your provided structure.)_

---

### Get Course Information by Department and Course ID

- **Endpoint**: `GET /api/courses/by-department-course-id`
- **Query Parameters**:

  - `department`: _(Required)_ Department identifier, e.g., `CSE`, `MATH`
  - `course_id`: _(Required)_ Course ID, e.g., `120`, `110`

- **Sample Request**:

  - Case-insensitive (server will normalize to lowercase for comparison)
  - `/api/courses/by-department-courseid?department=CSE&courseId=120`

- **Successful Response (200 OK)**:
  _(Sample JSON omitted for brevity—identical to your provided structure.)_

---

### Get Enrollment Data by Course Offering ID

- **Endpoint**: `/api/enrollments/`

- **Query Parameters**:

  - `course_offering_id`: Course offering primary key string (can be obtained from other APIs)

- **Sample Request**:

  - Case-insensitive
  - `/api/enrollments/?courseOfferingId=08bb2302174fab75dd2f588e20aa9226ff8d99412c634ace7785e843ebfe52eb`

- **Successful Response (200 OK)**:
  _(Sample JSON omitted for brevity—identical to your provided structure.)_

- **Special Case (200 OK)**:
  If the course does not exist, an empty JSON object is returned:

  ```json
  {}
  ```

---

## Comment-related Endpoints (Not Yet Implemented)

### Get Comments for a Course (and Optional Professor)

- **Endpoint**: `GET /comments`

- **Query Parameters**:

  - `department`: _(Required)_ e.g., `CSE`, `MATH`
  - `course_id`: _(Required)_ e.g., `120`, `110`
  - `professor`: _(Optional)_ Professor name in the format `prof_first_name prof_last_name`

- **Sample Requests**:

  - Case-insensitive
  - `/comments?department=CSE&course_id=120`
  - `/comments?department=CSE&course_id=120&professor=Geoffrey%20Voelker`

- **Successful Response (200 OK)**:
  _(Sample JSON omitted for brevity—identical to your provided structure.)_

- **Failed Response (400)**:

  ```json
  {
    "error": "Error message, e.g., department cannot be empty."
  }
  ```

---

### Submit a New Comment

- **Endpoint**: `POST /comments`
- **Request Body**:

  ```json
  {
    "department": "CSE",
    "course_id": "120",
    "professor": "prof_first_name prof_last_name",
    "year": 2024,
    "quarter": "Fall",
    "rate": 2.5,
    "comment": "This course sucks."
  }
  ```

- **Successful Response (200 OK)**:

  ```json
  {
    "message": "Comment created.",
    "comment_id": "asdnkhabjdsna"
  }
  ```

- **Failed Response (400)**:

  ```json
  {
    "message": "Request body incorrect format."
  }
  ```

---

---

### Like or Dislike a Comment

- **Endpoint**: `POST /comments/like`

- **Request Body**:

  ```json
  {
    "comment_id": "abc123",
    "like": true
  }
  ```

  - `comment_id`: _(Required)_ The ID of the comment to like or dislike.
  - `like`: _(Required)_ A boolean value. `true` to like the comment, `false` to dislike it.

- **Successful Response (200 OK)**:

  ```json
  {
    "message": "Comment liked successfully."
  }
  ```

  or

  ```json
  {
    "message": "Comment disliked successfully."
  }
  ```

- **Failed Response (400)**:

  ```json
  {
    "error": "Missing comment_id or like value."
  }
  ```

  or

  ```json
  {
    "error": "Invalid comment_id."
  }
  ```

- **Note**:

  - Rate-limiting should be enforced (e.g., only 1 like/dislike per IP per minute) to avoid spam.
  - Future versions may support like/dislike counts shown with comments.

---
