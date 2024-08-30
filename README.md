# HolaMessage

**HolaMessage** is an Android application developed in Kotlin that allows users to chat, create, and share PDF files directly within the chat. The app provides features for login and signup using Firebase Authentication, stores chat data in Firebase Realtime Database, and stores PDF files in Firebase Storage. All data is secured using AES encryption.

## Features

- **User Authentication:**
  - Secure login and signup using Firebase Authentication.
  - Password recovery via email.

- **Chat Functionality:**
  - Real-time chat between users.
  - Chat messages are securely stored in Firebase Realtime Database.
  - Messages are encrypted using AES encryption for enhanced privacy.

- **PDF Creation and Sharing:**
  - Inbuilt feature for creating PDF files within the app.
  - Share created PDFs directly in chat with other users.
  - PDF files are securely stored in Firebase Storage.

- **Real-time Data Synchronization:**
  - Automatic synchronization of chat messages and PDFs across devices.
  - Offline access to previously stored chat messages and files.

## Tech Stack

- **Kotlin:** Primary programming language used for developing the app.
- **Firebase Authentication:** Used for user login and signup functionalities.
- **Firebase Realtime Database:** Used to store and retrieve chat messages.
- **Firebase Storage:** Used to store and retrieve PDF files.
- **AES Encryption:** Used to encrypt chat messages and files for enhanced security.
- **XML:** Used for designing the user interface.

## Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/sohan-beniwal/HolaMessage.git
   ```
2. **Open the project in Android Studio.**

3. **Set up Firebase:**
   - Create a Firebase project in the [Firebase Console](https://console.firebase.google.com/).
   - Add your Android app to the Firebase project.
   - Download the `google-services.json` file and place it in the `app` directory.
   - Enable Firebase Authentication, Realtime Database, and Firebase Storage in your Firebase console.

4. **Build the project:**
   - Sync the project with Gradle files.
   - Build and run the app on an emulator or a physical device.

## Usage

1. **Sign Up / Login:**
   - Open the app and create an account or log in with an existing account.

2. **Start Chatting:**
   - Send and receive messages in real-time.
   - Messages are securely stored and encrypted.

3. **Create and Share PDFs:**
   - Create PDF files within the app.
   - Share PDFs with other users directly in chat.
   - PDFs are securely stored in Firebase Storage.

4. **View Messages and Files:**
   - Access your chat messages and shared PDFs from any device with real-time synchronization.

## Contributing

Contributions are welcome! Please fork this repository and submit a pull request for any enhancements or bug fixes.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
