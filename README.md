
# Firebase Login & Expense Tracker

A Firebase-integrated Android application that provides user authentication, expense tracking, budgeting, and reporting features.

## Table of Contents

1. [Project Overview](#project-overview)
2. [Repository Structure](#repository-structure)
3. [Build and Installation](#build-and-installation)
   - [Prerequisites](#prerequisites)
   - [Setup Instructions](#setup-instructions)
4. [Dependencies](#dependencies)
5. [Features](#features)


---

## Project Overview

This project is an Android application that uses Firebase for authentication and Firestore as its database. Key features include:
- User authentication and role management (Admin/Viewer).
- Adding, viewing, and editing expenses.
- Budget setting and notifications for overspending.
- Exporting expense data to CSV and PDF.
- Interactive pie charts for expense summaries.

---

## Repository Structure

The repository is organized as follows:

```
src/
├── main/
│   ├── java/com/example/firebaselogin/
│   │   ├── Activities (e.g., `AddExpenseActivity`, `MainActivity`, etc.)
│   │   ├── Adapters (e.g., `ExpenseAdapter`, `UserAdapter`)
│   │   ├── Data models (e.g., `Expense.java`, `User.java`)
│   │   ├── Managers (e.g., `UserManager.java`, `DatabaseManager.java`)
│   ├── res/ (XML layouts, drawable assets, etc.)
│   ├── AndroidManifest.xml
├── build.gradle (Project dependencies and build configuration)
```

Key directories/files:
- **Activities**: Contains all the screens (e.g., login, expense management, dashboard).
- **Adapters**: Handles RecyclerView data binding (e.g., `ExpenseAdapter` for expenses).
- **Models**: Data representations (e.g., `User` and `Expense`).
- **Managers**: Provides abstraction for interacting with the database (`DatabaseManager`).

---

## Build and Installation

### Prerequisites
1. **Android Studio**: Ensure you have the latest version installed.
2. **Firebase Project**: Set up a Firebase project and download the `google-services.json` file.
3. **Gradle**: Comes bundled with Android Studio but ensure it’s up-to-date.

### Setup Instructions
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/your-repo/firebase-expense-tracker.git
   cd firebase-expense-tracker
   ```

2. **Import Project**:
   - Open Android Studio.
   - Click on "Open an Existing Project" and navigate to the cloned repository.

3. **Add Firebase Configuration**:
   - Place the downloaded `google-services.json` file into the `app/` directory.

4. **Build the Project**:
   - Sync Gradle files by clicking on "Sync Now" in the top-right corner of Android Studio.

5. **Run the Application**:
   - Connect an Android device or start an emulator.
   - Click "Run" to install the application.

---

## Dependencies

This project relies on the following dependencies:

### Core Libraries
- **Firebase Authentication**: User authentication and role management.
- **Firestore**: Cloud-based NoSQL database for data storage.
- **Firebase UI Firestore**: Adapters for RecyclerView integration.
- **MPAndroidChart**: For displaying pie charts in `ExpenseSummaryActivity`.

### Additional Libraries
- **AndroidX**: Core AndroidX components.
- **Material Design**: Modern UI components.
- **exp4j**: A lightweight library for mathematical expressions (used in Calculator).

Refer to the `build.gradle` file for version details.

---

## Features

- **Authentication**:
  - Register and log in as a user or admin.
  - Role-based access control.

- **Expense Management**:
  - Add, edit, and delete expenses.
  - Export expenses as CSV or PDF.

- **Budget Tracking**:
  - Set and reset budgets by category.
  - Receive warnings for overspending.

- **Visualizations**:
  - View expense summaries as pie charts.

- **Multi-User Support**:
  - Admins can create and manage users within their group.


