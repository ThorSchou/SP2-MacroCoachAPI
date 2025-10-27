# MacroCoachAPI

MacroCoachAPI is a RESTful Java backend application designed to manage users, daily meal plans, and nutrition tracking.  
It integrates with OpenAI for AI-generated meal planning, uses PostgreSQL for persistent data, and implements secure authentication through JWT-based role access.

---

## Overview

MacroCoachAPI allows users to:
- Register and authenticate using JWT tokens.
- Manage their daily meals and nutrition goals.
- Store recipes, ingredients, and pantry items.
- Generate AI-powered meal plans (using OpenAI API).
- Operate securely with role-based access control.

---

## Architecture and Code Structure

### 1. Controllers and Routes
- **Controllers** handle application logic and interact with DAOs and services.  
  Examples: `DayController`, `RecipeController`, `ProfileController`, `AiController`.
- **Routes** define REST API endpoints and map them to controller methods.  
  Examples: `DayRoutes`, `RecipeRoutes`, `AiRoutes`, `ProfileRoutes`.

Each route group corresponds to a resource area (days, recipes, users, etc.).

---

### 2. Data Access Objects (DAO)
- DAO classes handle database operations using JPA and Hibernate.  
- Each entity typically has a DAO responsible for CRUD operations.
- Examples: `DayDAO`, `RecipeDAO`, `UserDAO`.

All DAOs use an `EntityManagerFactory` for consistent and thread-safe database access.

---

### 3. DTOs and Entities
- **DTOs (Data Transfer Objects):** Simplified objects for communication between the backend and client.  
  Examples: `UserDTO`, `RecipeDTO`, `DayPlanDTO`, `DayPlanRequestDTO`.
- **Entities:** Represent database tables and relationships.  
  Examples: `User`, `Role`, `Recipe`, `Day`, `Meal`.

---

### 4. Security
Security is implemented using the TokenSecurity package provided by the teacher.

- **SecurityController:** Handles authentication, token generation, and registration.  
- **SecurityDAO:** Handles database operations for users and roles.  
- **JWT Authorization:** Secures endpoints based on roles (`USER`, `ADMIN`).  
- **Role-based access control** ensures proper authorization throughout the system.

---

### 5. Services and Utilities
- **AiService:** Connects to the OpenAI API to generate day meal plans, or returns a stubbed response if `OPENAI_ENABLED=false`.
- **PromptBuilder:** Constructs text prompts for OpenAI from request data.
- **ApplicationConfig:** Initializes Javalin, registers routes, configures CORS, and sets up security and exception handling.

---

### 6. Exception Management
- **ApiException:** Base class for application-level exceptions.  
- **NotAuthorizedException:** Handles missing or invalid authentication tokens.  
- Centralized error handling ensures standardized HTTP responses.

---

## Features

- Modular architecture (controllers, routes, DAOs, services, DTOs).  
- Secure JWT-based authentication and authorization.  
- Integration with PostgreSQL via Hibernate ORM.  
- AI-powered meal plan generation using OpenAI.  
- Configurable deployment via environment variables and Docker.  
- Centralized exception handling.  

---

## API Endpoints

### Authentication Endpoints
| Method | URL | Description |
|--------|------|-------------|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Log in and receive a JWT |
| POST | `/api/auth/user/addrole` | Assign a role to a user (admin only) |
| GET | `/api/protected/user_demo` | Access test endpoint for USER role |
| GET | `/api/protected/admin_demo` | Access test endpoint for ADMIN role |

---

### Profile Endpoints
| Method | URL | Description |
|--------|------|-------------|
| GET | `/api/profiles/me` | Get the current user’s profile |
| PUT | `/api/profiles/me` | Update the user’s profile |
| PATCH | `/api/profiles/me` | Partially update the profile |

---

### Day and Meal Endpoints
| Method | URL | Description |
|--------|------|-------------|
| GET | `/api/days/summary` | Get a summary of all user days |
| GET | `/api/days/{date}` | Get meals for a specific date |
| POST | `/api/days/{date}/meals` | Add a meal to a day |
| PATCH | `/api/days/meals/{mealId}` | Update a meal |
| DELETE | `/api/days/meals/{mealId}` | Delete a meal |

---

### Recipe Endpoints
| Method | URL | Description |
|--------|------|-------------|
| GET | `/api/recipes` | Get all recipes |
| GET | `/api/recipes/{id}` | Get a recipe by ID |
| POST | `/api/recipes` | Create a new recipe |
| PUT | `/api/recipes/{id}` | Update a recipe |
| DELETE | `/api/recipes/{id}` | Delete a recipe |

---

### AI Endpoints
| Method | URL | Description |
|--------|------|-------------|
| POST | `/api/ai/day-plan` | Generate a daily meal plan via OpenAI |

Request body example:
```json
{
  "prompt": "Quick meals for an active day",
  "targetKcal": 2200,
  "meals": 3,
  "snacks": 2,
  "diet": "OMNIVORE",
  "allergies": ["PEANUTS"],
  "pantry": ["oats", "banana", "chicken"]
}
```

---

### Pantry Endpoints
| Method | URL | Description |
|--------|------|-------------|
| GET | `/api/pantry` | Get pantry items |
| POST | `/api/pantry` | Add new pantry items |

---

### Health Check
| Method | URL | Description |
|--------|------|-------------|
| GET | `/api/health` | Returns `{"status": "ok"}` to verify the API is running |

---

## Error Codes

| Code | Description |
|------|-------------|
| 1 | Resource not found |
| 2 | Invalid input |
| 3 | Unauthorized access |
| 4 | Database error |
| 5 | Internal server error |

---

## Deployment

MacroCoachAPI is designed for containerized deployment using Docker and Caddy.  
A standard `docker-compose.yml` setup includes:
- **PostgreSQL** for data storage.
- **MacroCoachAPI** container for the Java application.
- **Caddy** for HTTPS reverse proxy.
- **Watchtower** for automatic updates.

Environment variables are defined in a `.env` file and injected into containers at runtime.

---

## Technology Stack

| Category | Technology |
|-----------|-------------|
| Language | Java 17 |
| Framework | Javalin 6 |
| ORM | Hibernate / JPA |
| Database | PostgreSQL |
| Authentication | JWT (TokenSecurity) |
| AI Integration | OpenAI API |
| Deployment | Docker, Docker Compose, Caddy, Watchtower |

---

## Local Development

1. Set up PostgreSQL and update `.env` with local credentials.
2. Run the project:
   ```bash
   mvn clean package
   java -jar target/macrocoach-api.jar
   ```
3. Access the API locally at `http://localhost:7000/api`

---

## Environment Variables

Example `.env`:
```
POSTGRES_USER=postgres
POSTGRES_PASSWORD=password
DB_NAME=macrocoach
DB_USERNAME=postgres
DB_PASSWORD=password
CONNECTION_STR=jdbc:postgresql://db:5432/

SECRET_KEY=replace_with_secret_key
ISSUER=MacroCoachAPI
TOKEN_EXPIRE_TIME=18000

OPENAI_ENABLED=true
OPENAI_API_KEY=sk-yourkey
```

---

## Summary

MacroCoachAPI provides a modular and secure backend for meal and nutrition management.  
It features OpenAI-powered meal generation, role-based authentication, and a well-defined REST structure.  
All components are designed for Docker-based deployment and easy integration with frontend clients.
