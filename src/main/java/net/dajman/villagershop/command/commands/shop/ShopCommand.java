package net.dajman.villagershop.command.commands.shop;

import net.dajman.villagershop.Main;
import net.dajman.villagershop.category.Category;
import net.dajman.villagershop.command.Command;
import net.dajman.villagershop.command.PlayerCommand;
import net.dajman.villagershop.util.Messages;
import net.dajman.villagershop.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Optional;

import static java.util.Objects.isNull;

public class ShopCommand extends Command {

    private static final Logger LOGGER = Logger.getLogger(ShopCommand.class);

    public static final String PERMISSION_COMMAND_SHOP = Command.PERMISSION_COMMAND_PREFIX + "shop";
    private static final String PERMISSION_OPEN_CATEGORY = ShopCommand.PERMISSION_COMMAND_SHOP + ".category";
    private static final String PERMISSION_OPEN_CATEGORY_OTHERS = PERMISSION_OPEN_CATEGORY + ".others";
    private static final String PERMISSION_OPEN_CATEGORY_OTHERS_FORCE = PERMISSION_OPEN_CATEGORY_OTHERS + ".force";

    public ShopCommand(Main plugin, String label, String... aliases) {
        super(plugin, label, PERMISSION_COMMAND_SHOP, "/[label] [category] [player]", aliases);

    }

    @Override
    public boolean onCommand(final CommandSender sender, final String prefix, final String label, final String... args) {

        LOGGER.debug("onCommand() Received command for sender={}, prefix={}, label={}, args={}",
                sender.getName(), prefix, label, Arrays.toString(args));

        if (args.length == 1){

            LOGGER.debug("onCommand() args={} length=1", Arrays.toString(args));

            if (!sender.hasPermission(PERMISSION_OPEN_CATEGORY)){
                LOGGER.debug("onCommand() sender={} do not have permission={}", sender.getName(), PERMISSION_OPEN_CATEGORY);
                return Messages.sendMessageIfNotEmpty(sender, this.plugin.getConfiguration().commandPermissionMessage);
            }

            if (!(sender instanceof Player)){
                LOGGER.debug("onCommand() sender={} is not a player", sender.getName());
                return Messages.sendMessageIfNotEmpty(sender, PlayerCommand.COMMAND_ONLY_FOR_PLAYERS_MESSAGE);
            }

            final Player player = (Player) sender;

            final Optional<Category> optionalCategory = this.plugin.getCategories().getByName(args[0]);

            if (!optionalCategory.isPresent()){
                LOGGER.debug("onCommand() Category by name={} not found.", args[0]);
                return Messages.sendMessageIfNotEmpty(sender, this.plugin.getConfiguration().categoryNotFound,
                        "{GIVEN_CATEGORY}", args[0]);
            }

            final Category category = optionalCategory.get();
            LOGGER.debug("onCommand() Category with name={} found", category.getName());

            if (!sender.hasPermission(category.getPermission())){
                LOGGER.debug("onCommand() sender={} do not have permission={} for open category={}",
                        sender.getName(), category.getPermission(), category.getName());
                return Messages.sendMessageIfNotEmpty(sender, this.plugin.getConfiguration().categoryPermissionMessage,
                        "{CATEGORY}", category.getName());
            }

            LOGGER.debug("onCommand() Opening category={} for player={}", category.getName(), player.getName());
            this.plugin.getTradeInventoryBuilder().open(player, category);
            return true;
        }

        if (args.length >= 1){

            LOGGER.debug("onCommand() args={} length=1", Arrays.toString(args));

            if (!sender.hasPermission(PERMISSION_OPEN_CATEGORY_OTHERS)){
                LOGGER.debug("onCommand() sender={} do not have permission={}", sender.getName(), PERMISSION_OPEN_CATEGORY_OTHERS);
                return Messages.sendMessageIfNotEmpty(sender, this.plugin.getConfiguration().commandPermissionMessage);
            }

            final Optional<Category> optionalCategory = this.plugin.getCategories().getByName(args[0]);

            if (!optionalCategory.isPresent()){
                LOGGER.debug("onCommand() Category by name={} not found.", args[0]);
                return Messages.sendMessageIfNotEmpty(sender, this.plugin.getConfiguration().categoryNotFound,
                        "{GIVEN_CATEGORY}", args[0]);
            }

            final Category category = optionalCategory.get();
            LOGGER.debug("onCommand() Category with name={} found", category.getName());

            final Player targetPlayer = Bukkit.getPlayer(args[1]);

            if (isNull(targetPlayer) || !targetPlayer.isOnline()){
                LOGGER.debug("onCommand() Player with name={} not found", args[1]);
                return Messages.sendMessageIfNotEmpty(sender, this.plugin.getConfiguration().playerNotFound,
                        "{GIVEN_PLAYER_NAME}", args[1]);
            }

            if (args.length >= 3 && args[2].equalsIgnoreCase("force")){

                LOGGER.debug("onCommand() sender={} try to force open category={} for player={}",
                        sender.getName(), targetPlayer.getName(), category.getName());

                if (!sender.hasPermission(PERMISSION_OPEN_CATEGORY_OTHERS_FORCE)){
                    LOGGER.debug("onCommand() sender={} do not have permission={}", sender.getName(), PERMISSION_OPEN_CATEGORY_OTHERS_FORCE);
                    return Messages.sendMessageIfNotEmpty(sender, this.plugin.getConfiguration().commandPermissionMessage);
                }

                LOGGER.debug("onCommand() Force opening category={} for player={}", category.getName(), targetPlayer.getName());
                this.plugin.getTradeInventoryBuilder().open(targetPlayer, category);

                return true;
            }

            if (!targetPlayer.hasPermission(category.getPermission())){
                LOGGER.debug("onCommand() player={} do not have permission={} for category={} (sender={})",
                        targetPlayer.getName(), category.getPermission(), category.getName(), sender.getName());

                return Messages.sendMessageIfNotEmpty(sender, this.plugin.getConfiguration().categoryOthersPermissionMessage,
                        "{PLAYER}", targetPlayer.getName(), "{CATEGORY}", category.getName());
            }

            LOGGER.debug("onCommand() Opening category={} for player={}", category.getName(), targetPlayer.getName());
            this.plugin.getTradeInventoryBuilder().open(targetPlayer, category);
            return true;
        }

        if (!(sender instanceof Player)){
            LOGGER.debug("onCommand() sender={} is not a player", sender.getName());
            return Messages.sendMessageIfNotEmpty(sender, PlayerCommand.COMMAND_ONLY_FOR_PLAYERS_MESSAGE);
        }

        final Player player = (Player) sender;

        LOGGER.debug("onCommand() Opening shop for player={}", player.getName());
        this.plugin.getMainInventoryBuilder().open(player);
        return true;
    }
}
