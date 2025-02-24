# 🚀 Java Shortcut Listener

A simple **Java application** that listens for the **CTRL + ALT + H** shortcut to detect the currently active application and display a list of its keyboard shortcuts in a modern popup.



## 🎯 Features

- **Global Hotkey Listener** using `JNativeHook`.
- **Detects Active Window** and retrieves application name.
- **Displays Pop-up** with rounded corners and modern styling.
- **Shortcut Table Layout** (keys on left, description on right).
- **Auto-reload on file changes**.
- **Edit Mode (CTRL + E)** to modify shortcuts dynamically.
- **Dismiss Button (X)** for manual closing.

## 🛠 Installation

### Prerequisites

- Java 8 or later
- IntelliJ IDEA (or any Java IDE)
- **JNativeHook**, **JNA**, and **JNA-Platform** libraries

### 📥 Clone & Setup

```sh
# Clone the repository
git clone https://github.com/yourusername/shortcut-listener.git
cd shortcut-listener
```

### 🔧 Install Dependencies (Manually if Needed)

If using Maven, add these to `pom.xml`:

```xml
<dependency>
    <groupId>com.github.kwhat</groupId>
    <artifactId>jnativehook</artifactId>
    <version>2.2.2</version>
</dependency>
<dependency>
    <groupId>net.java.dev.jna</groupId>
    <artifactId>jna</artifactId>
    <version>5.13.0</version>
</dependency>
<dependency>
    <groupId>net.java.dev.jna</groupId>
    <artifactId>jna-platform</artifactId>
    <version>5.13.0</version>
</dependency>
```

### 🏃 Run the Application

```sh
javac -cp ".;lib/*" -d out/ src/*.java
java -cp ".;lib/*;out/" ShortcutListener
```

## 📖 Usage

1. \*\*Press \*\*\`\` → Shows the shortcuts for the active app.
2. \*\*Press \*\*\`\` → Opens `shortcuts.txt` in a text editor.
3. \*\*Modify & Save \*\*\`\` → Auto-reloads shortcuts dynamically.

### 📂 `shortcuts.txt` Format

```txt
# Mozilla Firefox
Mozilla Firefox, Ctrl+T - New Tab
Mozilla Firefox, Ctrl+W - Close Tab

# Google Chrome
Google Chrome, Ctrl+T - New Tab
Google Chrome, Ctrl+W - Close Tab
```

## ✨ Customizations

- **Modify UI Colors, Font, and Positioning** in `ShortcutListener.java`.
- **Add More Applications** in `shortcuts.txt`.


## 📜 License

This project is licensed under the MIT License.

