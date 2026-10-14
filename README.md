
# Fitness & Wellness Android App

A feature-rich Android application built with Java to support everyday fitness and wellness management. The app brings together workout planning, fitness goals, diet management, nutrition information, and personal wellness tools in a single mobile experience.

## Overview

The Fitness & Wellness Android App helps users organize exercise routines, manage personal fitness goals, explore nutrition information, and track selected wellness activities. It combines locally stored user data with online nutrition services to provide a convenient fitness companion.

## Features

### User Management
- User registration and login
- Profile management
- Local storage of user details and preferences

### Fitness & Workouts
- Weekly exercise schedule
- Timed workout sessions across multiple workout categories
- Workout history and progress tracking
- Personal fitness goals
- Running activity tracking
- Daily fitness motivation and articles

### Nutrition & Diet
- Daily diet-plan creation and management
- Online food nutrition lookup using the USDA FoodData Central API
- Nutrition information for supported food items

### Health & Wellness
- Body Mass Index (BMI) calculator
- Fitness knowledge quizzes

## Technology Stack

| Technology | Purpose |
|---|---|
| Java | Android application development |
| Android SDK | Native Android platform |
| SQLite | Local database storage |
| SharedPreferences | Local settings and session preferences |
| Retrofit | HTTP API communication |
| Gson | JSON serialization and deserialization |
| Android WorkManager | Scheduled background tasks |
| USDA FoodData Central API | Food and nutrition data |

## Application Architecture

The application uses a native Android structure with Java activities for individual screens, XML layouts for the user interface, SQLite and SharedPreferences for local persistence, and Retrofit for online API communication.

## Getting Started

### Prerequisites

- Android Studio
- A compatible Android SDK
- A JDK supported by the project's Gradle configuration
- Internet connectivity for online nutrition lookups

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/Siddhesh716/Android-Project.git
   ```

2. Open the cloned project in Android Studio.
3. Allow Gradle to sync and resolve project dependencies.
4. Configure the USDA FoodData Central API key using the configuration expected by the application.
5. Select an Android emulator or connect a compatible physical device.
6. Build and run the application from Android Studio.

## External API

### USDA FoodData Central

The application uses the USDA FoodData Central API to retrieve food and nutrition information.

- API documentation: https://fdc.nal.usda.gov/api-guide.html
- API key registration: https://fdc.nal.usda.gov/api-key-signup/

**Security note:** Do not commit API keys, passwords, or other credentials to the repository. Use an appropriate local configuration method and review the repository before publishing changes.

## Data Storage

SQLite is used for local database records, while SharedPreferences stores selected application preferences and session-related information. Online nutrition lookup requires network connectivity.

## Repository Structure

```text
Android-Project/
├── app/
│   └── src/
│       └── main/
│           ├── java/com/example/basic/
│           ├── res/
│           └── AndroidManifest.xml
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## Future Scope

Potential enhancements include richer fitness analytics, expanded nutrition coverage, more personalized workout recommendations, and additional wellness insights.

## Author

**Siddhesh**

GitHub: https://github.com/Siddhesh716

## License

No license has been specified in this repository. Reuse and redistribution permissions should be clarified by adding an appropriate license.
