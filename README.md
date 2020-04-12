![banner](https://user-images.githubusercontent.com/30203676/79029382-c96ea980-7b59-11ea-9e22-81386118ea5a.png)

**Welcome to the JacksCars Github!**

This is the official Github for the JacksCars plugin. The entire plugin is open source, and you're welcome to contribute or copy code as you wish, just be sure to credit us if you do :).

**Creators:** JackJack33, Santio71, Appleflavored

**Description:** JacksCars was mainly built as a simple, versatile cars plugin; it's perfect for any server that has a road network and needs some kind of transportation. The config is easy to customize and is made to be able to fit your server's needs. Want to run over monsters? Have at it!

## Installation
* *this is all assuming you already have a server set up*
* First, you're going to need Spigot, head on over to the [Spigot Website](https://getbukkit.org/download/spigot) and download the 1.15.2 version. Place `Spigot-1.15.2.jar` in your server folder and delete the minecraft server jar. (This also works with Paper Spigot)
* Next, you're going to need [Vault](https://www.spigotmc.org/resources/vault.34315/) and [EssentialsX](https://www.spigotmc.org/resources/essentialsx.9089/), as both are required by this plugin for proper economy. Place both of these in your plugins folder.
* Finally, [download](https://github.com/JackJack33/JacksCars/releases) `JacksCars.jar` and put it in your plugins folder. Start/restart the server and the plugin should be installed!

## Commands
* /jackscars, /jc, or /cars

| Command         | Description     | Permission Node |
|-----------------|-----------------|-----------------|
| /jc help | Displays the help menu | N/A |
| /jc give | Gives the player a car free of charge | jc.give |
| /jc speed | Modifies the player's car's speed to whatever you want | jc.speed |
| /jc reload | Reloads the plugin config | jc.reload |

## Signs
* Permission to create signs: `jc.signs`

| Type          | Description   | Default Price |
|---------------|---------------|---------------|
| Purchase | Purchases a car | $10.00 |
| Refuel | Refuels your car's fuel tank | $2.00 |
| Upgrade | Upgrades your car's speed & fuel | $30.00 |

* To make signs, use the sign check string (by default its `[JacksCars]`, not case sensitive)
* Below it, add the type you want. If you want to set a custom price, in line four put a double (ex: 29.99, 35.00) without a symbol.

Before:

![before](http://www.bimmr.com/signs/Sign.php?Line1=%5BJacksCars%5D&Line2=Purchase&Line3=&Line4=)

After:

![after](http://www.bimmr.com/signs/Sign.php?Line1=%263%5BJacksCars%5D&Line2=Purchase&Line3=&Line4=%26a%2410.0)
