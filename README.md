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


### Compose Rich Text Editor

A rich text editor library for Jetpack Compose that provides WYSIWYG editing capabilities with support for bold, italic, underline, lists, links, and more.

- **Repository:** [MohamedRejeb/Compose-Rich-Editor](https://github.com/MohamedRejeb/Compose-Rich-Editor)
- **License:** Apache License 2.0

```
                                 Apache License
                           Version 2.0, January 2004
                        http://www.apache.org/licenses/

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
