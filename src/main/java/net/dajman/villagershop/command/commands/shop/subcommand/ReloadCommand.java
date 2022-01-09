package net.dajman.villagershop.command.commands.shop.subcommand;

import net.dajman.villagershop.Main;
import net.dajman.villagershop.command.Command;
import net.dajman.villagershop.command.commands.shop.ShopCommand;
import net.dajman.villagershop.util.Colors;
import net.dajman.villagershop.util.Messages;
import net.dajman.villagershop.common.logging.Logger;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class ReloadCommand extends Command {

    private static final Logger LOGGER = Logger.getLogger(ReloadCommand.class);

    private static final String PERMISSION_COMMAND_SHOP_RELOAD = ShopCommand.PERMISSION_COMMAND_SHOP + ".reload";
    private static final String CONFIGURATION_RELOADED_MESSAGE = Colors.fixColors("&aConfiguration reloaded.");

    public ReloadCommand(Main plugin, String label, String... aliases) {
        super(plugin, label, PERMISSION_COMMAND_SHOP_RELOAD, "/[prefix] [label]", aliases);
    }

    @Override
    public boolean onCommand(final CommandSender sender, final String prefix, final String label, final String... args) {

        LOGGER.debug("onCommand() Received command from sender={}, prefix={}, label={} and args={}",
                sender.getName(), prefix, label, Arrays.toString(args));

        this.plugin.getConfiguration().load();
        this.plugin.getCategoryDataService().load();

        LOGGER.debug("onCommand() Configuration reloaded by sender={}", sender.getName());

        return Messages.sendMessageIfNotEmpty(sender, CONFIGURATION_RELOADED_MESSAGE);
    }
}
