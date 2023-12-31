# Yet Another Survival Multiplayer Plugin

### Adds following features:
- Spawnelytra
- Open every crafting table (and Catographietable and Enderchest and so on and so on) without placing (Sneak + right click)
- Disable explosion block damage for Creepers and/or TNT. It's your choice!
- Lock End access for an joint event
- Teleport to spawn and soon to player specific homes with a customizable cooldown (Title, Chat, Sound)
- Restart your server with a countdown and dell your players about the reason (Title, Chat, Sound)
- Change (alomost) every plugin setting ingame
- Customize join and quit messages including chat format

#### Every message is completly customizable through the `messages.yml` file using modern formatting thanks to [MiniMessage](https://docs.advntr.dev/minimessage/index.html)


### Commands

| Command                            | Permission                          | Description                                                       | Status                 |
|------------------------------------|-------------------------------------|-------------------------------------------------------------------|------------------------|
| `/spawn`                           | `yasmpp.command.spawn`              | `Teleports you to the spawn location`                             |✅Done                  |
| `/setspawn`                        | `yasmpp.command.setspawn`           | `Sets your current location as the spawn location`                |✅Done                  |
| `/gamesettings <setting> [args]`   | `yasmpp.command.gamesettings`       | `Changes a configuration setting to the given value`              |🟨Add missing settings  |
| `/game <start\|stop>`              | `yasmpp.command.game.<start\|stop>` | `Doesn't do much at the moment except change the worldborder size`|🟨Add more functionality|
| `/restartcountdown start <duration> <reason>`                   | `yasmpp.command.reload`             | `Schedules a server restart an broadcasts warings`                                              |✅Done                  |
| `/yasmpp reload`                   | `yasmpp.command.reload`             | `Reloads the plugin`                                              |✅Done                  |


