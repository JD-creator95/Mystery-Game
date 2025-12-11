# Mystery Game

A simple desktop mystery game built with Java where players solve a mystery by gathering clues, questioning suspects, and making choices that lead to different outcomes.

## Project Structure

```
src/
├── Main.java           # Entry point of the application
├── gui/                # GUI components
│   └── GameWindow.java # Main game window using Swing
├── game/               # Game logic
│   └── GameController.java # Controls game flow and interactions
├── model/              # Data models
│   ├── Suspect.java    # Suspect entity
│   ├── Clue.java       # Clue entity
│   └── GameState.java  # Game state management
└── data/               # Game data and file I/O
    ├── GameData.java          # Game data management
    ├── FileLoader.java        # Loads game data from text files
    ├── InvestigationLogger.java # Logs player actions to file
    ├── DatabaseManager.java   # SQLite database management
    └── DatabaseTest.java      # Database functionality tests

data/                   # Story and mystery data files
├── case1.txt          # Crime scene description
├── clues.txt          # Detailed clue information
└── suspects.txt       # Suspect profiles and statements
```

## Features

- Interactive GUI using Java Swing
- Question multiple suspects
- Gather clues from conversations
- Make accusations to solve the mystery
- Different outcomes based on player choices
- **File I/O capabilities:**
  - Reads mystery story from `case1.txt`
  - Loads clues from `clues.txt` using BufferedReader
  - Parses suspect data from `suspects.txt`
  - Saves investigation logs to timestamped text files
  - Automatic logging of all player actions
- **Database Integration (SQLite):**
  - Player profile management
  - Case progress tracking
  - Suspect data storage
  - Player choice recording
  - Statistics and history
  - All queries use PreparedStatements for security

## File I/O Implementation

### Reading Data (BufferedReader)
The game uses `BufferedReader` to read:
- **Crime Story** - Full narrative from `data/case1.txt`
- **Clues** - Parsed from `data/clues.txt` with descriptions and locations
- **Suspects** - Character profiles and statements from `data/suspects.txt`

See `FileLoader.java` for implementation details.

### Writing Data (BufferedWriter)
The game automatically logs all player actions:
- Suspects questioned (with timestamps)
- Clues discovered
- Evidence reviews
- Accusations made
- Case summary with results

Logs are saved as `investigation_log_YYYYMMDD_HHMMSS.txt`

See `InvestigationLogger.java` for implementation details.

## Database Implementation (SQLite)

### Database Tables
The game uses SQLite with 5 main tables:

1. **players** - Player profiles with statistics
2. **case_progress** - Tracks each case playthrough
3. **suspects** - Stores suspect information
4. **player_choices** - Records which suspects were questioned
5. **clues_discovered** - Tracks clues found during gameplay

### Using PreparedStatements
All database operations use `PreparedStatement` for security:

```java
// Example: Creating a player
String sql = "INSERT INTO players (username, created_at) VALUES (?, ?)";
PreparedStatement pstmt = connection.prepareStatement(sql);
pstmt.setString(1, username);
pstmt.setString(2, timestamp);
pstmt.executeUpdate();
```

### What Gets Stored

- **Player Profiles**: Username, total games, cases solved/failed
- **Case Progress**: Start time, completion status, suspects questioned, clues found
- **Player Choices**: Which suspects questioned, in what order, clues discovered
- **Statistics**: Success rate, average clues found, time spent

See `DatabaseManager.java` for full implementation.

### Testing the Database

Run the database test:
```bash
javac src/data/DatabaseTest.java src/data/DatabaseManager.java
java -cp src data.DatabaseTest
```

This will create `mystery_game.db` and run all database operations.

## How to Run

### Compile:
```bash
javac src/Main.java src/gui/*.java src/game/*.java src/model/*.java src/data/*.java
```

### Run:
```bash
java -cp src Main
```

## Gameplay

1. Start the investigation - read the crime scene details
2. Question suspects to gather information
3. Review collected clues
4. Save your investigation log at any time
5. Make an accusation when you think you know who the culprit is
6. Save the final report
7. Solve the mystery!

## Creating New Mysteries

You can create new mystery cases by editing the text files in the `data/` folder:

1. **case1.txt** - Write the crime scene description and background
2. **clues.txt** - Add clues with format:
   ```
   CLUE #X: CLUE NAME
   Location: Where it was found
   Description: Detailed description
   ```
3. **suspects.txt** - Add suspects with format:
   ```
   SUSPECT #X: NAME
   Occupation: Their role
   OFFICIAL STATEMENT:
   Their testimony...
   ```


