# FlowFX Comprehensive Notification System Implementation

## Overview

This document describes the complete notification system implementation for FlowFX that fulfills the Spanish requirements:

> "Se debe contar con un proceso de notificaciones en determinadas etapas de cada proyecto, las cuales deben llegar a cada interesado registrado, se debe notificar cuando se crea un proyecto y cada vez que se cambie su estado (notificar toda la información del proyecto al patrocinador y líderes), al crear una actividad o cada vez que se modifique su estado (notificar toda la información de la actividad al encardado de realizarla), cada vez que se agregue un seguimiento al proyecto se debe notificar toda la información al patrocinador y líderes. Todos los correos se deben enviar con una estructura atractiva en HTML."

## Architecture

The notification system consists of several key components:

### 1. Database Layer (FlowFXWS)
- **Notification Entity**: Stores notification records with subject, message, event type, and status
- **NotificationRecipient Entity**: Stores recipients for each notification with email, name, and role
- **Composite Primary Key**: Ensures unique notification-recipient combinations

### 2. Service Layer (FlowFXWS)
- **NotificationService**: CRUD operations for notifications
- **NotificationRecipientService**: CRUD operations for recipients
- **NotificationIntegrationService**: Orchestrates notification creation, database persistence, and email delivery
- **MailService**: Handles HTML email delivery with attractive templates

### 3. Web Service Layer (FlowFXWS)
- **FlowFXWS Controller**: Exposes SOAP endpoints for notification operations
- **Notification Integration Endpoints**: Methods for each notification event type

### 4. Client Layer (FlowFX)
- **NotificationIntegrationService**: Client service for asynchronous notification requests
- **Controller Integration**: Embedded notification calls in relevant user actions

## Implementation Details

### Notification Event Types

1. **PROJECT_CREATED**: When a new project is created
   - Recipients: Sponsor, Leader, Technical Leader
   - Trigger: ProjectManagementController.onActionCreateProject()

2. **PROJECT_STATUS_CHANGED**: When project status changes (P→R→S→C)
   - Recipients: Sponsor, Leader, Technical Leader
   - Trigger: ProjectExpandController.enqueueStatusChangeNotification()

3. **ACTIVITY_CREATED**: When a new activity is created
   - Recipients: Responsible Person
   - Trigger: ProjectExpandController.handleSuccessfulActivityCreation()

4. **ACTIVITY_STATUS_CHANGED**: When activity status changes
   - Recipients: Responsible Person
   - Trigger: ProjectExpandController.enqueueStatusChangeNotification()

5. **TRACKING_CREATED**: When project observations/tracking are added
   - Recipients: Sponsor, Leader, Technical Leader
   - Trigger: ProjectObservationsController.handleObservationCreationResponse()

### Database Schema

```sql
-- Notification table stores the main notification data
CREATE TABLE NOTIFICATION (
    NOTIFICATION_ID NUMBER PRIMARY KEY,
    PROJECT_ID NUMBER NOT NULL,
    ACTIVITY_ID NUMBER, -- Optional, for activity-related notifications
    SUBJECT VARCHAR2(255) NOT NULL,
    MESSAGE CLOB NOT NULL,
    STATUS CHAR(1) NOT NULL, -- P=Pending, S=Sent, F=Failed
    EVENT_TYPE VARCHAR2(50) NOT NULL,
    SENT_AT TIMESTAMP,
    CONSTRAINT FK_NOTIFICATION_PROJECT FOREIGN KEY (PROJECT_ID) REFERENCES PROJECT(PROJECT_ID),
    CONSTRAINT FK_NOTIFICATION_ACTIVITY FOREIGN KEY (ACTIVITY_ID) REFERENCES PROJECT_ACTIVITY(ACTIVITY_ID)
);

-- Notification recipient table stores who receives each notification
CREATE TABLE NOTIFICATION_RECIPIENT (
    NOTIFICATION_ID NUMBER NOT NULL,
    EMAIL VARCHAR2(255) NOT NULL,
    NAME VARCHAR2(255) NOT NULL,
    ROLE VARCHAR2(50) NOT NULL, -- SPONSOR, LEADER, TECH_LEADER, RESPONSIBLE
    PRIMARY KEY (NOTIFICATION_ID, EMAIL),
    CONSTRAINT FK_NOTIF_RECIPIENT_NOTIF FOREIGN KEY (NOTIFICATION_ID) REFERENCES NOTIFICATION(NOTIFICATION_ID)
);
```

### Key Features

#### 1. Database Persistence
- All notifications are persisted to the database for notification history
- Notification status tracking (Pending → Sent → Failed)
- Recipient tracking with roles for proper categorization

#### 2. Email Integration
- HTML email templates with attractive formatting
- Asynchronous email delivery to avoid UI blocking
- Support for multiple recipients per notification
- Automatic fallback and error handling

#### 3. Asynchronous Processing
- Client-side asynchronous notification calls using CompletableFuture
- Non-blocking UI operations
- Comprehensive error handling and logging

#### 4. Comprehensive Coverage
- ✅ Project creation notifications
- ✅ Project status change notifications
- ✅ Activity creation notifications
- ✅ Activity status change notifications
- ✅ Project tracking/observations notifications

### SOAP Web Service Endpoints

The following endpoints are available for notification integration:

```java
@WebMethod
public Respuesta notifyProjectCreated(Long projectId);

@WebMethod
public Respuesta notifyProjectStatusChanged(Long projectId, String newStatus, String oldStatus);

@WebMethod
public Respuesta notifyActivityCreated(Long activityId);

@WebMethod
public Respuesta notifyActivityStatusChanged(Long activityId, String newStatus, String oldStatus);

@WebMethod
public Respuesta notifyTrackingCreated(Long trackingId);
```

### Client Integration

Each controller integrates notifications at the appropriate points:

#### ProjectManagementController
```java
// After successful project creation
sendProjectCreationNotification(response);
```

#### ProjectExpandController
```java
// After status changes
enqueueStatusChangeNotification("PROJECT", vm.getId(), code);
enqueueStatusChangeNotification("ACTIVITY", activity.getId(), code);

// After activity creation
sendActivityCreationNotification(createdDto.getId());
```

#### ProjectObservationsController
```java
// After tracking/observation creation
sendTrackingCreationNotification(response);
```

## Configuration Requirements

### 1. Database Setup
Ensure the NOTIFICATION and NOTIFICATION_RECIPIENT tables exist in your Oracle database.

### 2. Email Configuration
Configure the Jakarta Mail Session 'mail/FlowFXSession' in Payara Server with:
- SMTP host (e.g., smtp.gmail.com)
- Port (587 for STARTTLS)
- Authentication credentials
- TLS/SSL settings

### 3. Web Service Deployment
Deploy the FlowFXWS web service with the NotificationIntegrationService properly configured.

## Benefits

1. **Complete Requirement Fulfillment**: All specified notification scenarios are implemented
2. **Database Persistence**: Enables future notification history functionality
3. **Scalable Architecture**: Asynchronous processing prevents UI blocking
4. **Professional Emails**: HTML templates provide attractive email formatting
5. **Comprehensive Logging**: Full audit trail of notification activities
6. **Error Resilience**: Graceful handling of email delivery failures

## Future Enhancements

1. **Notification History UI**: View past notifications sent to users
2. **Email Preferences**: Allow users to configure notification preferences
3. **Push Notifications**: Extend to web push notifications
4. **Templates Management**: Admin interface for email template customization
5. **Delivery Status Tracking**: Enhanced tracking of email delivery status

## Usage Example

When a user creates a new project in ProjectManagementController:
1. Project is created in database
2. `sendProjectCreationNotification()` is called
3. Client NotificationIntegrationService makes async SOAP call
4. Server NotificationIntegrationService creates notification in database
5. Server adds recipients (sponsor, leader, tech leader) to database
6. Server sends HTML emails to all recipients
7. Server updates notification status to "SENT"
8. Client receives success confirmation

This process ensures complete traceability and reliable delivery of all project notifications while maintaining a responsive user interface.