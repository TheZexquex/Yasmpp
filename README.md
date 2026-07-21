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

| Command                                       | Permission                                                                    | Description                                                        | Status                 |
|-----------------------------------------------|-------------------------------------------------------------------------------|--------------------------------------------------------------------|------------------------|
| `/spawn`                                      | `yasmpp.command.spawn`                                                        | `Teleports you to the spawn location`                              |✅Done                  |
| `/home` `/sethome` `/delhome` `/homes`        | `yasmpp.command.<home\|delhome\|sethome\|homes>` <br/>`yasmpp.homes.<amount>` | `Create, delete, list and teleport to player homes`                |✅Done                    |
| `/setspawn`                                   | `yasmpp.command.setspawn`                                                     | `Sets your current location as the spawn location`                 |✅Done                  |
| `/gamesettings <setting> [args]`              | `yasmpp.command.gamesettings`                                                 | `Changes a configuration setting to the given value`               |🟨Add missing settings  |
| `/game <start\|reset> (--hard)`               | `yasmpp.command.game.<start\|reset>`                                          | `Doesn't do much at the moment except change the worldborder size` |🟨Add more functionality|
| `/restartcountdown start <duration> <reason>` | `yasmpp.command.reload`                                                       | `Schedules a server restart an broadcasts warings`                 |✅Done                  |
| `/astralib reload Yasmpp`                     | `astralib.command.reload`                                                     | `Reloads the plugin`                                               |✅Done                  |
