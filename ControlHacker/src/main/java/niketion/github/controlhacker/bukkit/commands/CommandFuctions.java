package niketion.github.controlhacker.bukkit.commands;

import niketion.github.controlhacker.bukkit.Main;
import niketion.github.controlhacker.bukkit.Permissions;
import niketion.github.controlhacker.bukkit.filemanager.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CommandFuctions {

    /**
     * Get instance of main
     */
    private Main main = Main.getInstance();

    /**
     * Constructor
     */
    public CommandFuctions() {
    }

    /**
     * Check if player has permission from enum "Permissions"
     *
     * @param namePlayer  - Name of player to check permission
     * @param permissions - Permissions enum
     * @return boolean
     */
    boolean hasPermission(String namePlayer, Permissions permissions) {
        if (Bukkit.getPlayerExact(namePlayer).hasPermission(permissions.toString())) {
            return true;
        } else {
            Bukkit.getPlayerExact(namePlayer).sendMessage(Permissions.PERMISSIONS_DENIED.toString());
            return false;
        }
    }

    /**
     * Check if zone is set to location.yml
     *
     * @param zone - Zone to check
     * @return boolean
     */
    boolean isSet(String zone) {
        if (new FileManager("location", "locations").getConfig().getString(zone.toUpperCase() + ".world") != null) {
            return true;
        } else {
            Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + zone.toUpperCase() + " location isn't set. (Please contact a admin /ch)");
            return false;
        }
    }

    /**
     * Get string from config.yml
     *
     * @param path - Path input
     * @return String
     */
    String getString(String path) {
        return main.getConfig().getString(path);
    }

    /**
     * Check if "commandSender" is a player
     *
     * @param commandSender - CommandSender
     * @return boolean
     */
    boolean isPlayer(CommandSender commandSender) {
        if (commandSender instanceof Player) {
            return true;
        } else {
            commandSender.sendMessage(ChatColor.DARK_PURPLE + "Only players can execute this command");
            return false;
        }
    }

    /**
     * Check if "namePlayer" is online
     *
     * @param commandSender - To send message
     * @param namePlayer    - Player to find
     * @return boolean
     */
    boolean foundPlayer(CommandSender commandSender, String namePlayer) {
        if (Bukkit.getPlayerExact(namePlayer) != null) {
            return true;
        } else {
            commandSender.sendMessage(main.format(getString("player-not-found").replace("%player%", namePlayer)));
            return false;
        }
    }

    /**
     * Check if commandSender and "namePlayer" is equal
     *
     * @param commandSender - CommandSender
     * @param namePlayer    - Target
     * @return boolean
     */
    boolean checkYourself(CommandSender commandSender, String namePlayer) {
        if (!main.getConfig().getBoolean("check-yourself.enabled")) {
            if (commandSender.getName().equals(namePlayer)) {
                commandSender.sendMessage(main.format(getString("check-yourself.message")));
                return false;
            }
        }
        return true;
    }

    /**
     * Check wether the player is already in check
     *
     * @param commandSender - CommandSender
     * @param targetName    - Target
     * @return
     */
    boolean alreadyInCheck(CommandSender commandSender, String targetName) {
        if (main.getInCheck().containsKey(targetName)) {
            commandSender.sendMessage(main.format(getString("is-already-in-check").replace("%player%", targetName)));
            return true;
        }
        return false;
    }

    /**
     * Get zone from location.yml
     *
     * @param nameZone - Name of zone to get
     * @return Location
     */
    public Location getZone(String nameZone) {
        String nameZoneUpper = nameZone.toUpperCase();
        FileConfiguration fileConfiguration = new FileManager("location", "locations").getConfig();
        return new Location(Bukkit.getWorld(fileConfiguration.getString(nameZoneUpper + ".world")), fileConfiguration.getDouble(nameZoneUpper + ".x"),
                fileConfiguration.getDouble(nameZoneUpper + ".y"), fileConfiguration.getDouble(nameZoneUpper + ".z"),
                (float) fileConfiguration.getDouble(nameZoneUpper + ".yaw"), (float) fileConfiguration.getDouble(nameZoneUpper + ".pitch"));
    }

    /**
     * Finish a control
     *
     * @param target        - Cheater
     * @param commandSender - Checker
     */
    public void finishControl(Player target, CommandSender commandSender) {
        if (main.getConfig().getBoolean("old-finish") || !(commandSender instanceof Player)) {
            // Reset title
            if (main.rightVersion()) {
                main.getTitle().sendTitle(target, "a", 1, 1, 1);
            }

            // Teleport cheater to spawn
            try {
                target.teleport(getZone("end"));
            } catch (NullPointerException e) {
                target.teleport(target.getWorld().getSpawnLocation());
            }

            // Disable fly
            target.setAllowFlight(false);

            // Send Messages
            target.sendMessage(main.format(main.getConfig().getString("finish-cheater-message")));
            commandSender.sendMessage(main.format(main.getConfig().getString("finish-checker-message").replaceAll("%player%", target.getName())));
        } else {
            // Reset title
            if (main.rightVersion()) {
                // If version was changed or the client is a fork
                try {
                    main.getTitle().sendTitle(target, "a", 1, 1, 1);
                } catch (NullPointerException ignored) {
                }
            }

            // Open FinishGui
            if (target.isOnline())
                main.getFinishGUI().openGui((Player) commandSender);
        }
        // Close ControlGui inventory if still open
        target.closeInventory();
    }

}
