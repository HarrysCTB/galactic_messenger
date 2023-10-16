<div align="center">
  <img src="https://media.giphy.com/media/3o6vXVb67Ea8vUtQac/giphy.gif" alt="Galactic Messenger Logo" width="100%" height="250">
  <h1>Galactic Messenger</h1>
  <p>Navigate the Interstellar Chat Network</p>

[![GitHub License](https://img.shields.io/badge/license-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![Java Version](https://img.shields.io/badge/java-20+-orange.svg)](https://www.oracle.com/java/technologies/javase-downloads.html)
[![Gradle Version](https://img.shields.io/badge/gradle-7.0+-green.svg)](https://gradle.org/install/)

![Galactic Messenger Demo](https://media.giphy.com/media/OMeGDxdAsMPzW/giphy.gif)
</div>

## 📜 Table of Contents

- [🚀 Introduction](#-introduction)
- [✨ Features](#-features)
- [🛠️ Getting Started](#%EF%B8%8F-getting-started)
  - [📋 Prerequisites](#-prerequisites)
  - [🔧 Installation](#-installation)
- [🔥 Usage](#-usage)
  - [🌌 Authentication](#-authentication)
  - [🔐 Secure Chats](#-secure-chats)
  - [🚀 Group Chats](#-group-chats)
- [🌀 Project Structure](#-project-structure)
- [⚡ Protocols and Security](#-protocols-and-security)
- [📺 Project Demonstration](#-project-demonstration)
- [🤝 Contributing](#-contributing)
- [📝 License](#-license)

## 🚀 Introduction

Galactic Messenger is a Java-based interstellar chat application. This project focuses on network communication, protocols, and security. Whether you're exploring the depths of network programming or building a secure chat platform, Galactic Messenger has you covered.

## ✨ Features

- **User Authentication**: Register and log in with hashed passwords.
- **One-to-One Chats**: Initiate private chats with other users.
- **Group Chats**: Create and join group chats.
- **File Sharing**: Send and receive files within conversations or groups.
- **Secure Chats**: Encrypt messages in secure group chats.
- **Online User List**: See who's currently online.

## 🛠️ Getting Started

### 📋 Prerequisites

- **Java 20 or Higher**: [Download Java](https://www.oracle.com/java/technologies/javase-downloads.html)
- **Gradle 7.0 or Higher**: [Download Gradle](https://gradle.org/install/)

### 🔧 Installation

1. Clone the Galactic Messenger repository:

   ```shell
   git clone https://github.com/votre-nom/galactic_messenger.git
   ```

2. Navigate to the project directory:

   ```shell
   cd galactic_messenger
   ```

3. Build the project using Gradle:

   ```shell
   gradle build
   ```

## 🔥 Usage


### 🌌 Authentication

- Register a new user:

  ```shell
  java -jar galactic_messenger_client.jar [server IP address] [port number] /register "username" "password"
  ```

- Log in as a user:

  ```shell
  java -jar galactic_messenger_client.jar [server IP address] [port number] /login "username" "password"
  ```

### 🔐 Secure Chats

- Create a secure group chat:

  ```shell
  java -jar galactic_messenger_client.jar [server IP address] [port number] /create_secure_group "group_name" "password"
  ```

- Join a secure group chat:

  ```shell
  java -jar galactic_messenger_client.jar [server IP address] [port number] /join_secure_group "group_name" "password"
  ```

### 🚀 Group Chats

- Create a group chat:

  ```shell
  java -jar galactic_messenger_client.jar [server IP address] [port number] /create_group "group_name"
  ```

- Join a group chat:

  ```shell
  java -jar galactic_messenger_client.jar [server IP address] [port number] /join_group "group_name"
  ```

## 🌀 Project Structure

[TODO]

## ⚡ Protocols and Security

[TODO]

## 📺 Project Demonstration

[TODO]

## 🤝 Contributing

We welcome contributions to Galactic Messenger! Feel free to open issues, suggest enhancements, or submit pull requests.

## 📝 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

Created by:

- Elarif INZOUDINE 🥷🏾: [GitHub](https://github.com/HarrysCTB)
- Yassine EL GHERRABI 👮🏽‍♂️: [GitHub](https://github.com/Yassineelg)
- Yassine FELLOUS 🧙🏽‍♂️: [GitHub](https://github.com/Yassine-Fellous)

Now you're ready to embark on your interstellar messaging journey with Galactic Messenger! If you have any questions or encounter issues, don't hesitate to reach out to us. Happy coding!