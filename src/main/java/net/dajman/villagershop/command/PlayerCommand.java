package net.dajman.villagershop.command;

import net.dajman.villagershop.Main;
import net.dajman.villagershop.util.Messages;
import net.dajman.villagershop.util.logging.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public abstract class PlayerCommand extends Command{

    private static final Logger LOGGER = Logger.getLogger(PlayerCommand.class);

    public static final String COMMAND_ONLY_FOR_PLAYERS_MESSAGE = "This command is available only for players";

    public PlayerCommand(Main plugin, String label, String permission, String usage, String... aliases) {
        super(plugin, label, permission, usage, aliases);
    }

    @Override
    public boolean onCommand(final CommandSender sender, final String prefix, final String label, final String... args) {

        LOGGER.debug("onCommand() Received command from sender={}, prefix={}, label={}, args={}",
                sender.getName(), prefix, label, Arrays.toString(args));

        if (!(sender instanceof Player)){

            LOGGER.debug("onCommand() sender={} is not a player", sender.getName());

            return Messages.sendMessageIfNotEmpty(sender, COMMAND_ONLY_FOR_PLAYERS_MESSAGE);
        }

        return this.onPlayerCommand((Player) sender, prefix, label, args);
    }

    public abstract boolean onPlayerCommand(final Player player, final String prefix, final String label, final String... args);

}
