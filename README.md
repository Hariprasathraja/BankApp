# Bank Application

A secure and efficient bank application using microservices architecture with gRPC and Protocol Buffers for communication, MySQL for database management, and Flutter for the front-end interface. This project includes modules for authentication, account management, transaction handling, and notifications.

## Table of Contents
- [Features](#features)
- [Technologies](#technologies)
- [Architecture](#architecture)
- [Setup Instructions](#setup-instructions)
  - [Requirements](#requirements)
  - [Running the Project](#running-the-project)
  - [Running with Docker](#running-with-docker)
  - [Running with Docker Compose](#running-with-docker-compose)
- [Project Structure](#project-structure)
- [API Endpoints](#api-endpoints)

## Features
- **Authentication Module**: Secure login and signup functionality with password hashing.
- **Account Management**: Create and manage user accounts, including balance and transaction history.
- **Transaction Module**: Perform deposits, withdrawals, and track all account activity.
- **gRPC Communication**: Efficient service-to-service communication using Protocol Buffers.
- **MySQL Integration**: Persistent storage of all bank account and transaction data.
- **Docker Support**: Containerized deployment with Docker and Docker Compose for easy setup.

## Technologies
- **Frontend**: Flutter
- **Backend**: Java with gRPC
- **Database**: MySQL
- **Serialization**: Protocol Buffers (protobuf)
- **Containerization**: Docker, Docker Compose
- **Build Tool**: Gradle
- **Testing**: JUnit for Java services

## Architecture
This application follows a microservices architecture using gRPC for communication between services. The backend handles all core functionalities like authentication, transactions, and account management.

## Setup Instructions

### Requirements
Before setting up the project, ensure you have the following tools installed:
- [Java 22.0.1](https://openjdk.java.net/)
- [Gradle](https://gradle.org/)
- [MySQL 8.0](https://www.mysql.com/)
- [Flutter 3.24.1](https://flutter.dev/docs/get-started/install)
- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/install/)
- [Protocol Buffers Compiler (protoc)](https://grpc.io/docs/protoc-installation/)


### Running the Project

#### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/BankApplication.git
cd BankApplication
```

#### 2. Build the Backend
```bash
Copy code
./gradlew build
```

#### 3. Build and Run the Backend (gRPC Server)
```bash
Copy code
./gradlew runServer
```
The gRPC server will start on localhost:50051.

### Running with Docker

#### 1. Build Docker Images
```bash
Copy code
docker build -t bankapp-server -f server.Dockerfile .
docker build -t bankapp-client -f client.Dockerfile .
```

#### 2. Run Docker Containers
```bash
Copy code
docker run -p 50051:50051 bankapp-server
docker run -p 8080:8080 bankapp-client
```

### Running with Docker Compose
To spin up the entire application (server, database, etc.) with Docker Compose:

#### 1. Build and Run Docker Compose
```bash
Copy code
docker-compose up --build
```
This will start:
accountserver on port 50051
mysql on port 3306

#### 2. Access the Application
The gRPC server is available at localhost:50051.
The MySQL database is available at localhost:3306.


## Project Structure
```
BankApplication/
│
├── server/                # Backend services (Java, gRPC)
│   ├── src/
│   ├── build.gradle        # Build configuration
│   └── server.Dockerfile
│
├── client/                # Frontend application (Flutter)
│   ├── lib/
│   └── client.Dockerfile
│
├── docker-compose.yml      # Docker Compose configuration
├── init.sql                # MySQL database initialization script
└── README.md               # Project documentation
```

## API Endpoints
-gRPC Services
-**Authentication Service**: Handles user login and signup.
-**Account Service**: Manages account creation, balance inquiry, and transaction history.
-**Transaction Service**: Processes deposits, withdrawals, and transfers.


