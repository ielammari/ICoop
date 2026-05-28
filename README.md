# IC-CoOp

A 2D tile-based, two-player cooperative game. Two characters, a fire (red) player and a water (blue) player, explore a set of grid areas, solve cooperative puzzles, fight enemies, and collect the items needed to open the final door. It runs on a `game-engine` library and renders through a Swing window. The base foundation (that engine plus the starter skeleton) comes from the EPFL CS-107 course project.

## Requirements

- **Java 21** (the project compiles with `maven.compiler.source`/`target` set to 21).
- **Maven** (multi-module build).
- **`game-engine` 1.0.0-RC5**: the rendering/area/interaction framework. It is bundled as a Maven module in this repository (`game-engine/`), so there is no external download.
- No test framework or linter is configured.

## Project layout

The project root is a Maven parent POM (`ch.epfl.cs107:mp2:2024`) with three modules:

- **`game-engine/`**: the pre-built engine library (do not modify). Provides `AreaGame`, `Area`, `AreaEntity`/`MovableAreaEntity`, the `Interactor`/`Interactable` interaction system, logic signals, math, and the Swing window.
- **`iccoop/`**: the game itself (the main work in this project).
- **`tutos/`**: tutorial examples showing engine usage patterns.

## Build

### Command line

From the project root:

```bash
mvn clean package
```

This produces a runnable fat jar at `iccoop/target/IC-CoOp-2024-jar-with-dependencies.jar` (built by the assembly plugin, main class `ch.epfl.cs107.Play`).

### IntelliJ IDEA

1. **File > Open** and select the project root. IntelliJ detects the Maven project and imports the three modules automatically.
2. Set the Project SDK to a Java 21 JDK (**File > Project Structure > Project**).
3. Build with **Build > Build Project**, or open the **Maven** tool window and run the `mp2` parent's `package` lifecycle goal to produce the fat jar.

## Run

### Command line

From the project root, either run via Maven:

```bash
mvn exec:java -pl iccoop -Dexec.mainClass="ch.epfl.cs107.Play"
```

or run the packaged jar:

```bash
java -jar iccoop/target/IC-CoOp-2024-jar-with-dependencies.jar
```

### IntelliJ IDEA

Open `iccoop/src/main/java/ch/epfl/cs107/Play.java` and run the `main` method.

The game opens a 550x550 window and runs at 24 FPS.

### Window position

The game is fully playable as is. By default the window opens in the top-left corner of the screen. To make it appear in the middle instead, edit the engine file `game-engine/src/main/java/ch/epfl/cs107/play/window/swing/SwingWindow.java` and add `frame.setLocationRelativeTo(null);` just before `frame.setVisible(true);` in the constructor.

## Code organization (`iccoop/src/main/java/ch/epfl/cs107/`)

- **`Play`**: entry point; sets up the file system and Swing window and runs the game loop.
- **`icoop/ICoop`**: main game class (`AreaGame`); registers the areas and the two players, handles area transitions, dialogs, and the reset keys.
- **`icoop/ICoopBehavior`**: the per-cell walkability grid read from each area's behavior image (wall, walkable, door, rock, obstacle, and other cell types).
- **`icoop/KeyBindings`**: per-player key configuration and global action keys.
- **`icoop/DialogHandler`**: interface the areas use to publish dialogs to the game.
- **`icoop/area/`**: `ICoopArea` base class plus the four areas: `Spawn`, `OrbWay`, `Maze`, `Arena`.
- **`icoop/actor/`**: all in-world entities: `ICoopPlayer`, `Health`, items and equipment (`ICoopItem`, `Staff`, `Key`, `Explosive`, `Orb`, `Heart`), obstacles (`Rock`, `Obstacle`), `Door`/`Teleporter`/`ManorDoor`, elemental walls (`FireWall`, `WaterWall`), enemies (`Foe`, `HellSkull`, `BombFoe`), projectiles (`Fire`, `MagicProjectile`), `PressurePlate`, and supporting types (`Element`, `DamageType`, `Unstoppable`, `ElementalEntity`, `CenterOfMass`).
- **`icoop/handler/`**: `ICoopInteractionVisitor` (the visitor interface dispatching interactions between entity pairs), `ICoopInventory`, and `ICoopPlayerStatusGUI`.

Resources (area background/foreground/behavior images, sprites, fonts, and XML dialog files) live under `iccoop/src/main/resources/`.

## Controls

| Action | Red player      | Blue player |
| --- |-----------------| --- |
| Move up / left / down / right | `Z` `Q` `S` `D` | `I` `J` `K` `L` |
| Switch held item | `A`             | `U` |
| Use held item | `E`             | `O` |

Global keys:

| Key | Action |
| --- | --- |
| `Space` | Advance the current dialog |
| `R` | Reset the whole game |
| `T` | Reset the current area |

## Gameplay notes

- The two players each serve an element (red is fire, blue is water). Many obstacles are element-specific: an elemental wall can only be passed (and only deals damage) to a player of the matching element, and orbs/keys/staffs can only be collected by the matching player.
- Each player has a health bar (the value of which can be lowered for a harder challenge) and a brief invulnerability window after taking damage. Losing all health resets the current area.
- Items are used with the *use* key and cycled with the *switch* key: the sword melee-attacks foes in front, the elemental staff fires a projectile that damages enemies, destroys rocks, and triggers explosives, and explosives are placed on an adjacent free cell and destroy rocks and foes in the surrounding cells.
- Progression spans the areas `Spawn`, `OrbWay`, `Maze`, and `Arena`, gated by cooperative puzzles (pressure plates, elemental walls). Collecting both staffs in the Maze and both keys in the Arena opens the manor door back in Spawn, which shows the victory dialog.

### Have fun ;)
