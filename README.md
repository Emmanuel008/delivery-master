# Parcel Delivery Management System

A full-stack web application for managing parcel delivery requests with real-time notifications.

## Features

### User Interface
- Submit parcel delivery requests with name, location, price, and delivery possibility
- Clean, responsive form design
- Real-time form validation

### Admin Dashboard
- View all parcel delivery requests
- Real-time notifications for new requests
- Mark requests as read/unread
- Compose and send messages to customers
- View message history
- Unread request counter

### Backend API
- RESTful API for parcel requests and messages
- WebSocket support for real-time notifications
- MySQL database (configurable)
- CORS enabled for frontend integration

## Technology Stack

### Backend
- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- Spring WebSocket
- MySQL
- Maven

### Frontend
- React 18
- React Router
- Axios for API calls
- SockJS & STOMP for WebSocket
- CSS3 for styling

## Getting Started

### Prerequisites
- Java 17 or higher
- Node.js 16 or higher
- Maven 3.6 or higher

### Backend Setup

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Run the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```

   The backend will start on `http://localhost:8080`

3. Configure MySQL (Local):
   - Create database `happygo`
   - Ensure MySQL is running locally
   - Default credentials in `application.properties`:
     - Username: `root`
     - Password: `Emma@2025`
   - Connection URL: `jdbc:mysql://localhost:3306/happygo?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`

### Frontend Setup

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm start
   ```

   The frontend will start on `http://localhost:3000`

## Usage

### User Interface
1. Open `http://localhost:3000` in your browser
2. Fill out the parcel delivery request form
3. Submit the request

### Admin Dashboard
1. Navigate to `http://localhost:3000/admin`
2. View all parcel requests
3. Mark requests as read
4. Compose and send messages to customers
5. View message history

## API Endpoints

### Parcel Requests
- `POST /api/parcel-requests` - Create a new parcel request
- `GET /api/parcel-requests` - Get all parcel requests
- `GET /api/parcel-requests/unread` - Get unread parcel requests
- `GET /api/parcel-requests/unread-count` - Get unread count
- `PUT /api/parcel-requests/{id}/mark-read` - Mark request as read
- `GET /api/parcel-requests/{id}` - Get specific request

### Messages
- `POST /api/messages` - Create a new message
- `GET /api/messages` - Get all messages
- `GET /api/messages/{id}` - Get specific message
- `PUT /api/messages/{id}/mark-sent` - Mark message as sent

### WebSocket
- `ws://localhost:8080/ws` - WebSocket endpoint for real-time notifications

## Database Schema

### Parcel Requests Table
- `id` (Primary Key)
- `name` (VARCHAR)
- `location` (VARCHAR)
- `price` (DOUBLE)
- `can_deliver` (BOOLEAN)
- `phone_number` (VARCHAR)
- `created_at` (TIMESTAMP)
- `is_read` (BOOLEAN)

### Messages Table
- `id` (Primary Key)
- `content` (TEXT)
- `phone_number` (VARCHAR)
- `created_at` (TIMESTAMP)
- `is_sent` (BOOLEAN)

## Development Notes

- The application uses MySQL as the database
- WebSocket connections are established automatically when the admin dashboard loads
- All API calls include proper error handling
- The frontend includes responsive design for mobile devices
- CORS is configured to allow frontend-backend communication

## Future Enhancements

- User authentication and authorization
- Email notifications
- SMS integration for messages
- File upload for parcel images
- Advanced filtering and search
- Export functionality
- Production database integration
# delivery-master
