# Yet Another Survival Multiplayer Plugin

### Dependencies
- Java 25
- Paper 26.2 
- [AstraLib](https://github.com/UnknownCityMC/AstraLib)
- DiscordSRV
- PlayerAnalytics (Optional)
- PlaceholderAPI (Optional)

### How to build from source
- Clone the source code to a local folder on your computer
- Navigate to the plugins root directory
- Run `gradlew shadowJar` or `./gradlew shadowJar`, depending on your operating system.

### Adds following features:
- Spawn-Elytra (Works in a configurable radius around the spawn)
- Playtime statistics powered by [PlayerAnalytics](https://github.com/UnknownCityMC/PlayerAnalytics)
- Lock the Nether by requiring your players to complete a Nether portal holgram that can be fully customized and positioned where you want
- Player homes with the option to buy more homes via the home shop
- Open every crafting table (Craftingtable, Smithingtable,and Ender-Chest and so on and so on) without placing (Sneak + right click)
- Disable explosion block damage for Creepers and/or TNT. It's your choice!
- Lock End access for a joint event
- Teleport to spawn and player specific homes with a customizable cooldown (Title, Chat, Sound)
- Restart your server with a countdown and tell your players about the reason (Title, Chat, Sound)
- Change (almost) every plugin setting in-game ober commands
- Customize join and quit messages including chat format

#### Every message is completely customizable through the `lang/de_DE.yml` file using modern formatting thanks to [MiniMessage](https://docs.advntr.dev/minimessage/index.html)


### Commands

- **`/spawn`**
  - **Permission:** `yasmpp.command.spawn`
  - **Description:** Teleports you to the spawn location.
- **`/setspawn`**
  - **Permission:** `yasmpp.command.setspawn`
  - **Description:** Sets your current location as the server spawn location.
- **`/home <homeName>`**
  - **Permission:** `yasmpp.command.home`
  - **Description:** Teleports you to a saved home location with a cooldown countdown.
- **`/sethome <homeName> [--override|-o]`**
  - **Permission:** `yasmpp.command.sethome`
  - **Description:** Sets a new home at your current location (or overwrites an existing one if the override flag is used), up to your unlocked home slot limit.
- **`/delhome <homeName>`**
  - **Permission:** `yasmpp.command.delhome`
  - **Description:** Deletes a saved home location.
- **`/homes`**
  - **Permission:** `yasmpp.command.homes`
  - **Description:** Lists all your saved home locations.
- **`/homeshop`**
  - **Permission:** `yasmpp.command.homeshop`
  - **Description:** Opens the interactive GUI shop to purchase additional home slots.
- **`/gamesettings <setting> [value]`**
  - **Permission:** `yasmpp.command.gamesettings`
  - **Description:** Displays the current value or updates a plugin game setting (e.g. portal locks, explosion damage, spawn elytra, teleport cooldowns).
- **`/game <start|reset> [--hard]`**
  - **Permission:** `yasmpp.command.game.start`, `yasmpp.command.game.reset`
  - **Description:** Starts the game with a countdown, teleports players to spawn, and resizes the world border. Or resets the game back to lobby settings (with `--hard` clearing player inventories, Ender Chests, advancements, and recipes).
- **`/gamemode <gamemode> [player]`** (Alias: `/gm`)
  - **Permission:** `yasmpp.command.gamemode`
  - **Description:** Changes the game mode (survival, creative, adventure, spectator) for yourself or a specified player.
- **`/speed <0-10> [player]`**
  - **Permission:** `yasmpp.command.speed`
  - **Description:** Adjusts walk or fly speed (from 0 to 10) for yourself or a specified player.
- **`/restartcountdown <start <duration> <reason>|abort>`**
  - **Permission:** `yasmpp.command.restartcountdown`
  - **Description:** Schedules a server restart with countdown broadcasts (titles/chat) and a reason, or aborts an active restart countdown.
- **`/captcha <target>`**
  - **Permission:** `yasmpp.command.captcha`
  - **Description:** Opens a captcha verification GUI for the target player to verify if they are human.
- **`/portal <moveHere|fix>`**
  - **Permission:** `yasmpp.command.portal`
  - **Description:** Spawns/moves the locked Nether portal structure to the current location or respawns/fixes it.
- **`/ontime [player]`**
  - **Permission:** `yasmpp.command.ontime.self`
  - **Description:** Displays detailed playtime statistics for yourself or the specified player (powered by PlayerAnalytics).
- **`/ontimetop [page]`**
  - **Permission:** `yasmpp.command.ontimetop`
  - **Description:** Displays a leaderboard of players with the highest playtime (powered by PlayerAnalytics).
- **`/lastonline [page]`**
  - **Permission:** `yasmpp.command.lastonline`
  - **Description:** Displays a paginated list of when players were last seen online (powered by PlayerAnalytics).
- **`/astralib reload Yasmpp`**
  - **Permission:** `astralib.command.reload`
  - **Description:** Reloads the plugin configuration and translation files.
