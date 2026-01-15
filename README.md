# GlyphNotes

GlyphNotes is a modern, offline-first note-taking application for Android, built with the latest technologies to provide a seamless and intuitive user experience.

## ✨ Features

*   **📝 Create & Edit Notes:** A rich text editor to write and format your notes.
*   **🏷️ Tag-Based Organization:** Organize your notes with custom tags for easy filtering and searching.
*   **❤️ Favorites:** Mark important notes as favorites for quick access.
*   **📅 Calendar View:** Visualize your notes on a calendar, linking them to specific dates.
*   **🔍 Search:** Quickly find the notes you're looking for.
*   **📱 App Widget:** Access your notes directly from your home screen.
*   **🎨 Material You Theme:** A modern and dynamic theme that adapts to your device's wallpaper.

## 🛠️ Tech Stack & Architecture

This project follows modern Android development practices and is built using a 100% Kotlin stack.

*   **Architecture:** MVVM (Model-View-ViewModel)
*   **UI:** Jetpack Compose for building the user interface declaratively.
*   **Database:** Room for local data persistence.
*   **Navigation:** Navigation Compose for handling in-app navigation.
*   **Dependency Injection:** Hilt for managing dependencies.
*   **Asynchronous Operations:** Kotlin Coroutines for managing background threads.
*   **UI Components:** Material 3 for modern UI components.
*   **App Widgets:** Jetpack Glance for creating home screen widgets.

## Project Structure

The project is organized into the following main packages:

*   **/data**: Contains the data layer, including the Room database, DAOs (Data Access Objects), and entity definitions.
*   **/di**: Holds the dependency injection modules for Hilt.
*   **/ui**: Contains all UI-related components.
    *   **/screens**: Composable functions for each screen of the application (`HomeScreen`, `NoteScreen`, etc.).
    *   **/viewmodel**: ViewModels that expose state to the UI and handle business logic.
    *   **/components**: Reusable UI components.
    *   **/theme**: The application's theme, colors, and typography.
*   **MainActivity.kt**: The main entry point of the application.

## 🚀 How to Build

1.  Clone the repository.
2.  Open the project in Android Studio.
3.  Let Gradle sync the dependencies.
4.  Build and run the application on an emulator or a physical device.

## 📚 Third-Party Libraries

This project uses the following third-party libraries:

### SSJetpackComposeSwipeableView

A library that provides support for swipeable views in Jetpack Compose.

- **Repository:** [SimformSolutionsPvtLtd/SSJetpackComposeSwipeableView](https://github.com/SimformSolutionsPvtLtd/SSJetpackComposeSwipeableView)
- **License:** MIT License

```
MIT License

Copyright (c) 2022 Simform Solutions

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
