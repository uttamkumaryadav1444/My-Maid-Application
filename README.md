# 🧹 MyMaid Application - Domestic Service Platform

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://java.com)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.x-green.svg)](https://spring.io)
[![React Native](https://img.shields.io/badge/React%20Native-0.72.x-blue.svg)](https://reactnative.dev)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-orange.svg)](https://mysql.com)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 📋 Project Overview

**MyMaid Application** is a comprehensive digital platform that connects customers (users) with verified domestic service providers (maids). It eliminates the traditional challenges of finding trustworthy domestic help by providing a secure, location-aware, and feature-rich platform.

### 🎯 Key Features

#### 👤 **User Features**
- Secure registration with OTP verification
- Location-based maid search with real-time distance calculation
- Multiple booking types (Hourly, Daily, Weekly, Monthly)
- Secure online payments via RazorPay
- Real-time booking tracking and status updates
- Rating and review system
- Multi-language support
- Push notifications for booking updates
- **Real-time Chat System** with maids

#### 🧹 **Maid Features**
- Profile creation with document verification (Aadhar, PAN, Selfie)
- Availability management
- Hourly rate settings
- Booking request management (Accept/Reject)
- Earnings tracking and analytics
- Location sharing for real-time tracking
- **Real-time Chat System** with customers

#### 👑 **Admin Features**
- User and maid verification
- Document approval system (Aadhar, PAN, Selfie)
- Platform monitoring and analytics
- Dispute resolution

---

## 💬 **Chat Feature - Why Added?**

### **Problem Statement**
Before adding the chat feature, users and maids had to communicate via phone calls or SMS, which led to several issues:
- ❌ No record of conversations
- ❌ Missed calls and delayed responses
- ❌ Privacy concerns (personal numbers exposed)
- ❌ Miscommunication about service details
- ❌ No way to share location or photos

### **Solution - Real-time Chat System**
The chat feature was added to provide:
- ✅ **Instant Communication** - Real-time messaging between users and maids
- ✅ **Conversation History** - All chats stored for reference
- ✅ **Privacy Protection** - No personal phone numbers shared
- ✅ **Service Coordination** - Discuss specific requirements before service
- ✅ **Location Sharing** - Share real-time location during service
- ✅ **Photo Sharing** - Share photos of work done or requirements
- ✅ **Dispute Evidence** - Chat history as proof for dispute resolution

### **Chat Feature Technical Implementation**

#### **WebSocket Configuration**
```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*")
                .withSockJS();
    }
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
    }
}




Chat Features Implemented
One-on-One Chat - Private chat for each booking

Typing Indicators - Shows when user/maid is typing

Read Receipts - Shows when message is read

File Sharing - Share images and documents

Location Sharing - Share real-time location

Message History - Persistent chat history

Push Notifications - Notify on new messages

Offline Messages - Store undelivered messages





🏗️ System Architecture
text
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENT LAYER                             │
│              React Native Mobile App + React.js Web             │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      API GATEWAY LAYER                          │
│                    Spring Boot REST APIs                        │
│                   Authentication & Security                     │
└─────────────────────────────────────────────────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        ▼                     ▼                     ▼
┌───────────────┐    ┌───────────────┐    ┌───────────────┐
│  WebSocket    │    │  Booking      │    │  Location     │
│  Chat Server  │    │  Service      │    │  Service      │
└───────────────┘    └───────────────┘    └───────────────┘
        │                     │                     │
        └─────────────────────┼─────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                        DATABASE LAYER                           │
│                 MySQL (Primary) + MongoDB (Logs)                │
└─────────────────────────────────────────────────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        ▼                     ▼                     ▼
┌───────────────┐    ┌───────────────┐    ┌───────────────┐
│  RazorPay     │    │  OpenRoute    │    │  MSG91        │
│  Gateway      │    │  Service      │    │  OTP Service  │
└───────────────┘    └───────────────┘    └───────────────┘
🛠️ Technology Stack
Backend
Technology	Version	Purpose
Java	17	Core programming language
Spring Boot	3.1.x	Application framework
Spring Security	3.1.x	Authentication & Authorization
Spring Data JPA	3.1.x	Database ORM
Spring WebSocket	3.1.x	Real-time chat
MySQL	8.0	Relational database
MongoDB	5.0	Chat history and logs
Frontend
Technology	Version	Purpose
React Native	0.72.x	Mobile app (iOS & Android)
React.js	18.2.x	Web application
Redux Toolkit	1.9.x	State management
Socket.io-client	4.5.x	WebSocket client
External Services
Service	Purpose
RazorPay	Payment processing
OpenRouteService	Distance calculation
MSG91	OTP verification
Firebase Cloud Messaging	Push notifications
📁 Project Structure
text
MyMaidApplication/
├── src/main/java/MaidRepository/maid/
│   ├── config/                    # Configuration classes
│   │   ├── SwaggerConfig.java     # API documentation
│   │   ├── SecurityConfig.java    # Security configuration
│   │   ├── WebSocketConfig.java   # WebSocket config (CHAT)
│   │   └── WebConfig.java         # Web configuration
│   ├── controller/                # REST API endpoints
│   │   ├── AuthController.java    # Authentication APIs
│   │   ├── BookingController.java # Booking APIs
│   │   ├── ChatController.java    # CHAT APIs (NEW)
│   │   ├── LocationController.java # Location APIs
│   │   ├── MaidController.java    # Maid management
│   │   ├── UserController.java    # User management
│   │   ├── RatingReviewController.java # Rating APIs
│   │   └── SubscriptionController.java # Subscription APIs
│   ├── dto/                       # Data Transfer Objects
│   ├── model/                     # Entity classes
│   │   ├── ChatMessage.java       # CHAT entity (NEW)
│   │   ├── ChatRoom.java          # CHAT room entity (NEW)
│   │   ├── User.java
│   │   ├── Maid.java
│   │   └── Booking.java
│   ├── repository/                # JPA repositories
│   │   ├── ChatMessageRepository.java # CHAT repository (NEW)
│   │   └── ChatRoomRepository.java    # CHAT repository (NEW)
│   ├── service/                   # Service interfaces
│   │   └── ChatService.java       # CHAT service (NEW)
│   └── impl/                      # Service implementations
│       ├── ChatServiceImpl.java   # CHAT implementation (NEW)
│       └── ...
└── src/main/resources/
    ├── application.properties
    └── application-dev.properties
🚀 Installation & Setup
Prerequisites
Java 17 or higher

MySQL 8.0 or higher

Maven 3.8+

Node.js 18+ (for frontend)

Android Studio / Xcode (for mobile app)

Step 1: Clone Repository
bash
git clone https://github.com/yourusername/MyMaidApplication.git
cd MyMaidApplication
Step 2: Configure Database
sql
CREATE DATABASE maid_repository;
USE maid_repository;
Step 3: Configure Application Properties
properties
# application.properties
server.port=8080

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/maid_repository?useSSL=false
spring.datasource.username=root
spring.datasource.password=root

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# RazorPay
razorpay.key.id=rzp_test_YOUR_KEY
razorpay.key.secret=YOUR_SECRET

# OpenRouteService
ors.api.key=YOUR_ORS_KEY
ors.base.url=https://api.openrouteservice.org

# MSG91
msg91.auth.key=YOUR_MSG91_KEY
msg91.template.id=YOUR_TEMPLATE_ID

# WebSocket
websocket.endpoint=/ws
Step 4: Build & Run Backend
bash
mvn clean install
cd target
java -jar maid-repository-1.0.0.jar
Step 5: Run Frontend (React Native)
bash
cd frontend
npm install
npx react-native run-android  # For Android
npx react-native run-ios      # For iOS
📡 API Documentation
Base URL
text
http://localhost:8080/api
WebSocket Connection (CHAT)
text
ws://localhost:8080/ws
Authentication APIs
Method	Endpoint	Description
POST	/auth/user/register	User registration
POST	/auth/maid/register	Maid registration
POST	/auth/login	Login
POST	/auth/send-otp	Send OTP
POST	/auth/verify-otp	Verify OTP
Chat APIs
Method	Endpoint	Description
WebSocket	/app/chat.send	Send message
WebSocket	/topic/chat/{bookingId}	Subscribe to chat
WebSocket	/app/chat.typing	Typing indicator
WebSocket	/app/chat.read	Read receipt
GET	/api/chat/history/{bookingId}	Get chat history
GET	/api/chat/unread-count	Get unread count
Booking APIs
Method	Endpoint	Description
GET	/bookings/check-availability	Check maid availability
GET	/bookings/calculate-amount	Calculate booking amount
POST	/bookings/create-order	Create RazorPay order
POST	/bookings/pay-and-book	Confirm booking
GET	/bookings/my-bookings	Get user bookings
GET	/bookings/ongoing	Get ongoing bookings
POST	/bookings/{id}/cancel	Cancel booking
Location APIs
Method	Endpoint	Description
POST	/location/user/update	Update user location
POST	/location/maid/update	Update maid location
GET	/location/nearby	Find nearby maids
GET	/location/distance-to-maid	Calculate distance
Subscription APIs
Method	Endpoint	Description
GET	/subscriptions/plans/all	Get all plans
POST	/subscriptions/activate	Activate subscription
GET	/subscriptions/status	Check subscription status
