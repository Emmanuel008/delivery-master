# Message Controller API Documentation

## Overview
The Message Controller provides comprehensive APIs for managing messages with phone number highlighting capabilities and sample message generation.

## Base URL
```
http://localhost:8080/api/messages
```

## API Endpoints

### 1. Get Sample Message
**GET** `/api/messages/sample`

Returns a sample message with content and phone number for testing purposes.

**Response:**
```json
{
  "content": "Hello! Your parcel delivery request has been received. We will contact you shortly to confirm delivery details. Thank you for choosing our service!",
  "phoneNumber": "+255625313162"
}
```

### 2. Create Message
**POST** `/api/messages`

Creates a new message in the database.

**Request Body:**
```json
{
  "content": "Your message content here",
  "phoneNumber": "+255625313162"
}
```

**Response:**
```json
{
  "id": 1,
  "content": "Your message content here",
  "phoneNumber": "+255625313162",
  "createdAt": "2025-10-23T22:36:36.99888",
  "isSent": false
}
```

### 3. Get All Messages
**GET** `/api/messages`

Retrieves all messages from the database.

**Response:**
```json
[
  {
    "id": 1,
    "content": "Message content",
    "phoneNumber": "+255625313162",
    "createdAt": "2025-10-23T22:36:36.99888",
    "isSent": false
  }
]
```

### 4. Get Message by ID
**GET** `/api/messages/{id}`

Retrieves a specific message by its ID.

**Response:**
```json
{
  "id": 1,
  "content": "Message content",
  "phoneNumber": "+255 625313162",
  "createdAt": "2025-10-23T22:36:36.99888",
  "isSent": false
}
```

### 5. Mark Message as Sent
**PUT** `/api/messages/{id}/mark-sent`

Marks a message as sent.

**Response:**
```json
{
  "id": 1,
  "content": "Message content",
  "phoneNumber": "+255 123 456 789",
  "createdAt": "2025-10-23T22:36:36.99888",
  "isSent": true
}
```

### 6. Highlight Phone Numbers
**POST** `/api/messages/highlight-phone`

Highlights phone numbers in the provided content with HTML styling.

**Request Body:**
```json
{
  "content": "Please call us at +255 123 456 789 or 0712345678 for delivery updates"
}
```

**Response:**
```json
{
  "originalContent": "Please call us at +255 123 456 789 or 0712345678 for delivery updates",
  "highlightedContent": "Please call us at <span style='background-color: #ffff00; font-weight: bold; padding: 2px 4px; border-radius: 3px;'>+255 123 456 789</span> or <span style='background-color: #ffff00; font-weight: bold; padding: 2px 4px; border-radius: 3px;'>0712345678</span> for delivery updates",
  "phoneNumbersFound": true
}
```

### 7. Send Message with Highlight
**POST** `/api/messages/send-with-highlight`

Creates a message and returns it with highlighted phone numbers in the content.

**Request Body:**
```json
{
  "content": "Your parcel is ready! Contact us at +255 987 654 321 for pickup",
  "phoneNumber": "+255 987 654 321"
}
```

**Response:**
```json
{
  "message": {
    "id": 3,
    "content": "Your parcel is ready! Contact us at +255 987 654 321 for pickup",
    "phoneNumber": "+255 987 654 321",
    "createdAt": "2025-10-23T22:36:36.99888",
    "isSent": false
  },
  "highlightedContent": "Your parcel is ready! Contact us at <span style='background-color: #ffff00; font-weight: bold; padding: 2px 4px; border-radius: 3px;'>+255 987 654 321</span> for pickup",
  "phoneNumberHighlighted": true
}
```

## Phone Number Detection

The API uses a comprehensive regex pattern to detect phone numbers in various formats:
- International format: `+255 123 456 789`
- Local format: `0712345678`
- With dashes: `+255-123-456-789`
- With spaces: `+255 123 456 789`

## Phone Number Highlighting

Phone numbers are highlighted with:
- **Background Color**: Yellow (`#ffff00`)
- **Font Weight**: Bold
- **Padding**: 2px 4px
- **Border Radius**: 3px

## Error Handling

All endpoints return appropriate HTTP status codes:
- `200 OK` - Success
- `400 Bad Request` - Invalid request data
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

## CORS Configuration

The API is configured to accept requests from `http://localhost:3000` for frontend integration.

## Testing Examples

### Test Sample Message
```bash
curl -X GET http://localhost:8080/api/messages/sample
```

### Test Phone Highlighting
```bash
curl -X POST http://localhost:8080/api/messages/highlight-phone \
  -H "Content-Type: application/json" \
  -d '{"content": "Call us at +255 123 456 789 for support"}'
```

### Test Send with Highlight
```bash
curl -X POST http://localhost:8080/api/messages/send-with-highlight \
  -H "Content-Type: application/json" \
  -d '{"content": "Delivery ready! Call +255 987 654 321", "phoneNumber": "+255 987 654 321"}'
```
