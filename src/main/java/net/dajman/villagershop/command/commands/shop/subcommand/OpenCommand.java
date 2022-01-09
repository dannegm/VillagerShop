package net.dajman.villagershop.command.commands.shop.subcommand;

import net.dajman.villagershop.Main;
import net.dajman.villagershop.command.Command;
import net.dajman.villagershop.command.commands.shop.ShopCommand;
import net.dajman.villagershop.util.Messages;
import net.dajman.villagershop.common.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static java.util.Objects.isNull;

public class OpenCommand extends Command {

    private static final Logger LOGGER = Logger.getLogger(OpenCommand.class);

    private static final String PERMISSION_COMMAND_SHOP_OPEN = ShopCommand.PERMISSION_COMMAND_SHOP + ".open.others";
    private static final String PERMISSION_COMMAND_SHOP_OPEN_FORCE = PERMISSION_COMMAND_SHOP_OPEN + ".force";

    public OpenCommand(Main plugin, String label, String... aliases) {
        super(plugin, label, PERMISSION_COMMAND_SHOP_OPEN, "/[prefix] [label] [player]", aliases);
    }

    @Override
    public boolean onCommand(final CommandSender sender, final String prefix, final String label, final String... args) {

        LOGGER.debug("onCommand() Received command from sender={}, prefix={}, label={} and args={}",
                sender.getName(), prefix, label, Arrays.toString(args));

        if (args.length == 0){

            LOGGER.debug("onCommand() args={} length=0", Arrays.toString(args));

            return Messages.sendMessageIfNotEmpty(sender, this.getUsage(label, prefix));
        }

        final Player targetPlayer = Bukkit.getPlayer(args[0]);

        if (isNull(targetPlayer) || !targetPlayer.isOnline()){

            LOGGER.debug("onCommand() Player with name={} not found", args[0]);

            return Messages.sendMessageIfNotEmpty(sender, this.plugin.getConfiguration().playerNotFound,
                    "{GIVEN_PLAYER_NAME}", args[0]);
        }

        if (args.length > 1 && args[1].equalsIgnoreCase("force")){

            LOGGER.debug("onCommand() sender={} try to force open shop for player={}",
                    sender.getName(), targetPlayer.getName());

            if (!sender.hasPermission(PERMISSION_COMMAND_SHOP_OPEN_FORCE)){
                LOGGER.debug("onCommand() sender={} do not have permission={}", sender.getName(),
                        PERMISSION_COMMAND_SHOP_OPEN_FORCE);
                return Messages.sendMessageIfNotEmpty(sender, this.plugin.getConfiguration().commandPermissionMessage);
            }

            LOGGER.debug("onCommand() Force opening shop for player={} by sender={}",
                    targetPlayer.getName(), sender.getName());

            this.plugin.getShopInventoryService().openShop(targetPlayer);

            return true;
        }

        if (!targetPlayer.hasPermission(ShopCommand.PERMISSION_COMMAND_SHOP)){
            LOGGER.debug("onCommand() player={} do not have permission={} to open shop. (sender={})",
                    targetPlayer.getName(), ShopCommand.PERMISSION_COMMAND_SHOP, sender.getName());

            return Messages.sendMessageIfNotEmpty(sender, this.plugin.getConfiguration().shopOpenOthersPermissionMessage,
                    "{PLAYER}", targetPlayer.getName());
        }

        LOGGER.debug("onCommand() Opening shop for player={} by sender={}", targetPlayer.getName(),
                sender.getName());

        this.plugin.getShopInventoryService().openShop(targetPlayer);

        return true;
    }
}
