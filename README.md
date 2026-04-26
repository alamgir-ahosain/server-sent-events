


# Server Sent Events

>A Real-time alert notification system using SSE(Server-Sent Events) protocol.

---

##  1. Tech Stack

| Component | Technologies |
| --- | --- |
| **Backend** | Java 21 (Temurin), Spring Boot 4.0.5 |
| **Database** | PostgreSQL 15 |
| **Build Tool** | Maven, Spring Boot Maven Plugin |
| **Dependencies** | Spring Security, Spring Data JPA, Lombok, Spring Validation, Spring Boot Devtools, Spring Web MVC, PostgreSQL JDBC Driver |
| **Containerization** | Docker, Docker Compose |

---

## 2. Quick Start (Local Run)

Get the entire system up and running in minutes using Docker:

1. Clone the repository

    ```bash
   https://github.com/alamgir-ahosain/server-sent-events.git
    ```
2. Navigate to the project directory

    ```bash
    cd server-sent-events
    ```
 3. Environment Configuration

    Create a `.env` file in the `server/` directory. 

     <br>

     **Root `.env`:** Used by **Docker Compose** for database containers.

    ```env
    # Database Names
    SERVER_DB_NAME=springdb

    # Database Credentials
    SERVER_DB_USER=postgres
    SERVER_DB_PASSWORD=postgresql
    ```



    **server/`.env`**

    ```env
    DB_HOST=server-db
    DB_PORT=5432
    DB_NAME=springdb
    DB_USER=postgres
    DB_PASSWORD=postgresql
    PORT=8080
    ```
    >Do NOT commit .env Put it in .gitignore    

4. Spin up the containers

    ```bash
    docker compose up --build         # Build images and start all containers
    docker compose up --build -d      # Run in background (detached mode)
    docker ps                         # View running containers
    docker compose logs -f server-api # Follow logs of a specific service
    docker compose down               # Stop all containers
    docker compose down -v            # Stop and delete all volumes (wipes DB data)
    ```

 5. Access the Application

    The API will be available at: `http://localhost:8080/`

---









## 3. Local Setup (Without Docker)

If  prefer to configure the environment manually or run the application without Docker, follow these steps:

 1. Clone the Repository

    ```bash
    https://github.com/alamgir-ahosain/server-sent-events.git
    ```
 
 2. Navigate to the project directory

    ```bash
    cd server-sent-events
    ```
 3. Environment Configuration

    Create a `.env` file in the `server/` directory. 

     <br>

     **Root `.env`:** Used by **Docker Compose** for database containers.

    ```env
    # Database Names
    SERVER_DB_NAME=springdb

    # Database Credentials
    SERVER_DB_USER=postgres
    SERVER_DB_PASSWORD=postgresql
    ```



    **server/`.env`**

    ```env
    DB_HOST=localhost
    DB_PORT=5432
    DB_NAME=springdb
    DB_USER=postgres
    DB_PASSWORD=postgresql
    PORT=8080
    ```
    >Do NOT commit .env Put it in .gitignore

 4. Build the Project

    Ensure JDK 21 is installed. Use the Maven wrapper to install dependencies:

    ```bash
    cd server
    ./mvnw clean install
    ```

 5. Run the Application

    Start the Spring Boot server:

    ```bash
    ./mvnw spring-boot:run
    ```

 6. Access the Application

    The API will be available at: `http://localhost:8080/`




---

## 4.  Project Structure

```md
├── server/                 # Spring Boot Application
│   ├── src/                # Source code
│   ├── Dockerfile          # Production Docker build
│   └── .env                # Environment variable template
└── docker-compose.yml      # Local development setup
└──  .env                   # Docker Compose for database containers.
```



---



# 5. API Reference

### User Service (8080)

| Method | Endpoint                  | Description          |
| ------ | ------------------------- | -------------------- |
| GET    | /api/user/test            | User service health  |
| POST   | /api/user/register        | Register user        |
| POST   | /api/user/login           | Login user           |
| GET    | /api/user/{id}            | Get user by ID       |
| GET    | /api/user/all             | Get all users        |
| DELETE | /api/user/{id}            | Delete user          |

### Alert Service (8080)

| Method | Endpoint                              | Description                |
| ------ | ------------------------------------- | -------------------------- |
| GET    | /api/alert/test                       | Alert service health       |
| POST   | /api/alert/create                     | Create alert               |
| GET    | /api/alert/{id}                       | Get alert by ID            |
| GET    | /api/alert/all                        | Get all alerts             |
| DELETE | /api/alert/{id}                       | Delete alert               |
| GET    | /api/alert/subscribe                  | Open global SSE stream     |
| GET    | /api/alert/subscribe/{email}          | Open user SSE stream       |
| POST   | /api/alert/broadcast                  | Broadcast alert            |
| POST   | /api/alert/unicast/{email}            | Send alert to one user     |

#### Notes

- `POST /api/user/register` currently creates users with role `FARMER`.
- `GET /api/alert/subscribe` is a global SSE stream.
- `GET /api/alert/subscribe/{email}` is a user-specific SSE stream.
- `POST /api/alert/broadcast` sends to all active subscribers.
- `POST /api/alert/unicast/{email}` sends only to the specified subscriber.


---

# 6. API Endpoints


## 1.1 Smoke test the controllers

**GET** `http://localhost:8080/api/user/test`

**GET** `http://localhost:8080/api/alert/test`


**Response - 200 OK**

```text
UserController is working!
```

```text
AlertController is working!
```

---

## 2.1 Register User

**POST** `http://localhost:8080/api/user/register`

**Request**

```json
{
  "name": "string",
  "email": "string",
  "password": "string"
}
```

**Response - 201 Created**

```json
{
  "id": "Long",
  "name": "string",
  "email": "string",
  "role": "FARMER"
}
```



**Errors**
- **400 Bad Request** - Missing or invalid fields.
- **409 Conflict** - Email is already registered.

---

## 2.2 Login User

**POST** `http://localhost:8080/api/user/login`

**Request**

```json
{
  "email": "string",
  "password": "string"
}
```

**Response - 200 OK**

```json
{
  "id": "Long",
  "name": "string",
  "email": "string",
  "role": "FARMER"
}
```

**Errors**
- **400 Bad Request** - Missing or invalid fields.
- **401 Unauthorized** or **400 Bad Request** - Invalid email or password.

---

## 2.3 Get User by ID

**GET** `http://localhost:8080/api/user/{id}`

**Response - 200 OK**

```json
{
  "id": "Long",
  "name": "string",
  "email": "string",
  "role": "FARMER"
}
```



**Errors**
- **404 Not Found**

```json
{
  "timestamp": "2026-04-26T10:10:10.100",
  "status": 404,
  "error": "Not Found",
  "message": "User not found with id: <USER_ID>"
}
```

---

## 2.4 Get All Users

**GET** `http://localhost:8080/api/user/all`

**Response - 200 OK**

```json
[
  {
    "id": "Long",
    "name": "string",
    "email": "string",
    "role": "FARMER"
  }
]
```



---

## 2.5 Delete User

**DELETE** `http://localhost:8080/api/user/{id}`

**Response - 204 No Content**



**Errors**
- **404 Not Found**

```json
{
  "timestamp": "2026-04-26T10:10:10.100",
  "status": 404,
  "error": "Not Found",
  "message": "User not found with id: <USER_ID>"
}
```

---

## 3.1 Open SSE Global Stream

**GET** `http://localhost:8080/api/alert/subscribe`

This endpoint keeps the connection open and streams all alerts.



---

## 3.2 Open SSE User Stream

**GET** `http://localhost:8080/api/alert/subscribe/{email}`



**Expected first event**

```text
event: connected
data: SSE connection established for: farmer1@example.com
```

> Keep at least one subscriber terminal open before testing broadcast or unicast.

---

## 4.1 Create Alert

**POST** `http://localhost:8080/api/alert/create`

**Request**

```json
{
  "email": "string",
  "type": "CROP | TASK | WEATHER",
  "description": "string"
}
```

**Response - 201 Created**

```json
{
  "id": "Long",
  "email": "string",
  "type": "CROP | TASK | WEATHER",
  "description": "string",
  "createdAt": "2026-04-26T10:10:10.100"
}
```


**Errors**
- **400 Bad Request** - Missing or invalid fields.
- **404 Not Found** - User not found with email.

```json
{
  "timestamp": "2026-04-26T10:10:10.100",
  "status": 404,
  "error": "Not Found",
  "message": "User not found with email: nouser@example.com"
}
```

---

## 4.2 Get Alert by ID

**GET** `http://localhost:8080/api/alert/{id}`

**Response - 200 OK**

```json
{
  "id": "Long",
  "email": "string",
  "type": "CROP | TASK | WEATHER",
  "description": "string",
  "createdAt": "2026-04-26T10:10:10.100"
}
```



**Errors**
- **404 Not Found**

```json
{
  "timestamp": "2026-04-26T10:10:10.100",
  "status": 404,
  "error": "Not Found",
  "message": "Alert not found with id: <ALERT_ID>"
}
```

---

## 4.3 Get All Alerts

**GET** `http://localhost:8080/api/alert/all`

**Response - 200 OK**

```json
[
  {
    "id": "Long",
    "email": "string",
    "type": "CROP | TASK | WEATHER",
    "description": "string",
    "createdAt": "2026-04-26T10:10:10.100"
  }
]
```



---

## 4.4 Delete Alert

**DELETE** `http://localhost:8080/api/alert/{id}`

**Response - 204 No Content**


**Errors**
- **404 Not Found**

```json
{
  "timestamp": "2026-04-26T10:10:10.100",
  "status": 404,
  "error": "Not Found",
  "message": "Alert not found with id: <ALERT_ID>"
}
```

---

## 4.5 Broadcast Alert

**POST** `http://localhost:8080/api/alert/broadcast`

**Request**

```json
{
  "email": "string",
  "type": "CROP | TASK | WEATHER",
  "description": "string"
}
```

**Response - 201 Created**

```json
{
  "id": "Long",
  "email": "string",
  "type": "CROP | TASK | WEATHER",
  "description": "string",
  "createdAt": "2026-04-26T10:10:10.100"
}
```


**Expected SSE behavior**
- All active subscribers receive a `new-alert` event.

---

## 4.6 Unicast Alert

**POST** `http://localhost:8080/api/alert/unicast/{email}`

**Request**

```json
{
  "email": "string",
  "type": "CROP | TASK | WEATHER",
  "description": "string"
}
```

**Response - 201 Created**

```json
{
  "id": "Long",
  "email": "string",
  "type": "CROP | TASK | WEATHER",
  "description": "string",
  "createdAt": "2026-04-26T10:10:10.100"
}
```


**Expected SSE behavior**
- Only the subscriber for `farmer1@example.com` receives `new-alert`.

---

## 5) Negative tests

### Invalid alert type

**POST** `http://localhost:8080/api/alert/create`

**Request**

```json
{
   "email":"admin@example.com",
    "type":"INVALID",
    "description":"bad type"
}
```

**Response - 400 Bad Request**

```json
{
  "timestamp": "2026-04-26T10:10:10.100",
  "status": 400,
  "error": "Validation Error",
  "message": "type: must not be null"
}
```

### Missing or invalid email


**POST** `http://localhost:8080/api/alert/create`

**Request**

```json
{
   "email":"admin@example.com",
    "type":"TASK",
    "description":"unknown user"
}
```

**Response - 400 Bad Request**

```json
{
  "timestamp": "2026-04-26T10:10:10.100",
  "status": 400,
  "error": "Validation Error",
  "message": "email: must not be blank"
}
```

### Duplicate registration

**POST** `http://localhost:8080/api/user/register`

**Request**

```json
{
  "name":"Farmer One"
   "email":"farmer1@example.com",
    "password":"secret123"
}
```

**Response - 400 Bad Request or 409 Conflict**

```json
{
  "timestamp": "2026-04-26T10:10:10.100",
  "status": 409,
  "error": "Conflict",
  "message": "Email is already registered: admin@example.com"
}
```

---

