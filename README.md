# SecureChatApp

SecureChatApp is a comprehensive secure messaging platform featuring a robust Spring Boot backend and a cross-platform Flutter mobile application.

## Project Structure

This repository contains the following modules:

- **SecureChatApp**: The backend server built with Java and Spring Boot.
- **app**: The mobile frontend built with Flutter.

## Features

- **Secure Messaging**: End-to-end encryption for private conversations.
- **User Authentication**: Secure login and registration with JWT.
- **Real-time Chat**: WebSocket-based real-time communication.
- **Profile Management**: User profiles with follower/following functionality.
- **Social Features**: Posts, Bookmarks, and Reels support.

## Getting Started

### Prerequisites

- **Java Development Kit (JDK)**: Version 17 or higher.
- **Flutter SDK**: Latest stable release.
- **Maven**: For building the backend.
- **MySQL**: Database server running on `localhost:3306`.

### Backend Setup

1. **Database Configuration**:
   Ensure MySQL is running and create a database named `securechatapp`.
   Update `SecureChatApp/src/main/resources/application.properties` if your credentials differ from the defaults.

2. **Run the Backend**:
   Navigate to the backend directory:
   ```bash
   cd SecureChatApp
   ```
   Run the application using Maven:
   ```bash
   ./mvnw spring-boot:run
   ```
   The server will start on `http://localhost:8000`.

### Mobile App Setup

1. **Install Dependencies**:
   Navigate to the app directory:

   ```bash
   cd app
   flutter pub get
   ```

2. **Run the App**:
   Ensure you have an emulator running or a physical device connected.
   ```bash
   flutter run
   ```

## Contributing

Contributions are welcome! Please fork the repository and open a pull request with your changes.
