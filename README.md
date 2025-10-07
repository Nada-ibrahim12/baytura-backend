# BAYTAURA Backend

> **A comprehensive real estate management platform backend built with Spring Boot, featuring role-based access, property management, and real-time notifications.**

---

## Table of Contents
- Features
- Tech Stack
- Architecture
- Project Structure
- API Endpoints
- Setup & Installation
- Contributors

---

## Features

- **User Authentication & Authorization**
    - JWT-based authentication
    - Role-based access control (Admin, Provider, Customer)
    - Secure password hashing with BCrypt

- **Property Management**
    - CRUD operations for property listings
    - Advanced filtering and search
    - Image upload and management
    - Favorites system

- **Request System**
    - Property inquiries and requests
    - Status tracking
    - Admin management

- **Notifications**
    - Real-time notifications
    - Read/unread status
    - Admin broadcasting

- **User Profiles**
    - Profile management
    - Profile pictures
    - Role-specific features

---

## Tech Stack
- **Backend**: Java 17, Spring Boot 2.7.0
- **Database**: PostgreSQL
- **Authentication**: Spring Security, JWT
- **File Storage**: Local file system
- **Tools**: Maven, Docker, Postman
- **Testing**: JUnit 5, Mockito

---

## Architecture
```
Controller → Service → Repository → PostgreSQL
Authentication → Spring Security + JWT
```

---

## Project Structure

```
src/main/java/org/os/bayturabackend/
├── config/         # Configuration classes
├── controllers/    # REST Controllers
├── DTOs/           # Data Transfer Objects
├── entities/       # JPA Entities
├── exceptions/     # Custom exceptions
├── mappers/        # Object mappers
├── repositories/   # Data access layer
├── services/       # Business logic
└── specifications/ # JPA Specifications
```

---

## API Endpoints

### Authentication
- `POST /api/auth/register/customer` - Register a new customer
- `POST /api/auth/register/provider` - Register a new property provider
- `POST /api/auth/register/admin` - Register a new admin
- `POST /api/auth/login` - User login

### User Profile
- `GET /api/user/profile` - Get current user profile
- `PUT /api/user/profile` - Update user profile
- `DELETE /api/user/profile` - Delete a user account
- `PUT /api/user/profile/pfp` - Upload profile picture
- `DELETE /api/user/profile/pfp` - Remove profile picture
- `GET /api/user/profile/favorites` - Get user's favorite properties

### Properties
#### Public Endpoints
- `GET /api/properties` - Get all properties with filtering
    - Query params: type, purpose, searchQuery, minPrice, maxPrice, minArea, maxArea, owner, page, size
- `GET /api/properties/{id}` - Get property by ID
- `GET /api/properties/media/{id}` - Get media by ID
- `GET /api/properties/{id}/media` - Get all media for a property

#### Authenticated Endpoints
- `GET /api/properties/my` - Get current user's properties (PROVIDER/CUSTOMER)
- `POST /api/properties` - Create new property (PROVIDER only)
- `PUT /api/properties/{id}` - Update property (Owner only)
- `PUT /api/properties/{id}/change-status` - Change property status (Owner only)
- `DELETE /api/properties/{id}` - Delete property (Owner only)
- `POST /api/properties/{id}/media-upload` - Upload media for property (Owner only)
- `DELETE /api/properties/{propertyId}/media/{mediaId}` - Delete property media (Owner only)
- `POST /api/properties/{id}/favorite` - Add property to favorites (CUSTOMER only)
- `DELETE /api/properties/{id}/unfavorite` - Remove property from favorites (CUSTOMER only)

#### Admin Only
- `DELETE /api/admin/properties/{id}` - Delete any property

### Requests
#### Customer Endpoints
- `GET /api/requests` - Get all requests by current customer
- `GET /api/requests/{requestId}` - Get request details
- `POST /api/requests` - Create new request
- `DELETE /api/requests/{requestId}` - Delete request

#### Admin Endpoints
- `GET /api/admin/requests` - Get all requests with filtering
    - Query params: status, username
- `GET /api/admin/requests/{requestId}` - Get any request details
- `PUT /api/admin/requests/{requestId}/status` - Update request status
- `DELETE /api/admin/requests/{requestId}` - Delete any request

### Admin
- `PUT /api/admin/provider/{id}/status` - Update provider status
- `GET /api/admin/customer-requests` - Get all customer requests
- `GET /api/admin/provider-requests` - Get all provider requests
- `GET /api/admin/users` - Get all users with filtering
    - Query params: role, status, companyName

### Notifications
- `GET /api/notifications` - Get all user notifications
- `GET /api/notifications/unread` - Get unread notifications
- `GET /api/notifications/unread/count` - Get count of unread notifications
- `PUT /api/notifications/{id}/read` - Mark notification as read
- `DELETE /api/notifications/{id}` - Delete notification
- `POST /api/notifications/create` - Create notification (ADMIN only)
    - Params: userId, title, content, type

---

## Setup & Installation

### Prerequisites
- Java 21 or higher
- Maven 3.5.5 or higher
- PostgreSQL 13 or higher
- Cloudinary account (for image storage)
- Email service (e.g., Gmail) for email notifications

### Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/Nada-ibrahim12/baytura-backend
   cd baytura-backend
   ```

2. **Database Setup**
    - Install and start PostgreSQL
    - Create a new database for the application

3. **Cloudinary Setup**
    - Create a Cloudinary account (https://cloudinary.com)
    - Get your Cloud Name, API Key, and API Secret from the Cloudinary dashboard

4. **Email Service Setup**
    - Set up an email service (e.g., Gmail)
    - For Gmail, you may need to enable "Less secure app access" or use an App Password

5. **Configure the application**
    - Create a ```.env``` file in the project root directory
    - Copy the following variables from ```.env.example``` and fill in your values:
      ```properties
      # Database Configuration
      DB_URL=jdbc:postgresql://localhost:5432/your_database_name
      DB_USERNAME=your_db_username
      DB_PASSWORD=your_db_password
      API_PORT=8080
      
      # JWT Configuration
      JWT_SECRET=your_secure_random_string
      JWT_EXPIRATION=86400000 # 24 hours in milliseconds
      
      # Cloudinary Configuration
      CLOUDINARY_CLOUD_NAME=your_cloud_name
      CLOUDINARY_API_KEY=your_api_key
      CLOUDINARY_API_SECRET=your_api_secret
      
      # Email Configuration
      MAIL_USERNAME=your_email@example.com
      MAIL_PASSWORD=your_email_password_or_app_password
      ```

6. **Build the application**
   ```bash
   # Build the application
   ./mvnw clean install
   ```

7. **Run the application**
   ```bash
   # Run the application
   ./mvnw spring-boot:run
   ```

8. **Verify the installation**
    - The application will be available at: `http://localhost:8080`
    - Check the console output for any startup errors
---

## Contributors
- **Ephraim Youssef**
  <br>[GitHub Profile](https://github.com/EphraimYoussef)
  <br>[LinkedIn Profile](https://www.linkedin.com/in/ephraimyoussef/)
- **Nada Ibrahim**
  <br>[GitHub Profile](https://github.com/Nada-ibrahim12)
  <br>[LinkedIn Profile](https://www.linkedin.com/in/nada-ibrahim-70930725a)