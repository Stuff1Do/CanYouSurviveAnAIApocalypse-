# Can You Survive an AI Apocalypse?

Project for CMSC 170 (Introduction to Artificial Intelligence). Inspired by "Who wants to be a Millionare?", answer AI-related questions across increasing difficulty tiers as you slowly transform into AI to survive.

## Tech Stack

- Java 21
- Swing
- Maven

## Getting Started

### Prerequisites

- JDK 21+
- Maven

### Build

```bash
mvn clean compile
```

### Run

```bash
java -cp target/classes com.cysaaa.App
```

## Project Structure

```
src/main/java/com/cysaaa/
├── App.java     # entry point
├── gui/         # UI components
└── util/        # game state, dialogue
src/main/resources/
├── backgrounds/
├── buttons/
└── labels/
```

## License

MIT