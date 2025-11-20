# webforJ Terminal with Snake Game

An interactive web-based terminal built with webforJ featuring a fully playable Snake game. Demonstrates advanced terminal control, real-time game rendering, and extensible command architecture.

## Features

- **Snake Game**: Classic Snake with collision detection, scoring, and smooth ANSI rendering
- **Command System**: Extensible command pattern with help, clear, time, dialogs, and history
- **History Navigation**: Arrow key support (↑↓) to navigate command history
- **Smart Event Handling**: History navigation automatically disabled during gameplay

## Prerequisites

- Java 21 or newer
- Maven 3.9+

## Getting Started

Run the application:

```bash
mvn spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080) and type `snake` to play!

## Available Commands

- `snake` - Play the Snake game
- `help` - Show all available commands
- `clear` - Clear the terminal screen
- `time` - Display current date and time
- `msg <text>` - Show a message dialog
- `prompt <question>` - Prompt for user input
- `history` - Show command history

## Technical Highlights

- **ANSI Escape Sequences**: Full color support and cursor control
- **Command Pattern**: Easy to add new commands
- **Event-Driven**: Separate handlers for keyboard and data events
- **Game Loop**: Uses webforJ Interval for smooth 60 FPS gameplay
- **State Management**: Clean separation between terminal and game state

## Building for Production

```bash
mvn clean package -Pprod
java -jar target/terminal-1.0-SNAPSHOT.jar
```

## Learn More

- [webforJ Documentation](https://docs.webforj.com)
- [Terminal Component](https://docs.webforj.com/docs/components/terminal)
