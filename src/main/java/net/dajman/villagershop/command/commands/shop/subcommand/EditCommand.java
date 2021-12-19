package net.dajman.villagershop.command.commands.shop.subcommand;

import net.dajman.villagershop.Main;
import net.dajman.villagershop.category.Category;
import net.dajman.villagershop.command.PlayerCommand;
import net.dajman.villagershop.command.commands.shop.ShopCommand;
import net.dajman.villagershop.util.Colors;
import net.dajman.villagershop.util.Messages;
import net.dajman.villagershop.util.logging.Logger;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class EditCommand extends PlayerCommand {

    private static final Logger LOGGER = Logger.getLogger(EditCommand.class);

    private static final String PERMISSION_COMMAND_SHOP_EDIT = ShopCommand.PERMISSION_COMMAND_SHOP + ".edit";

    private static final List<String> USAGE_MESSAGE = Colors.fixColors(Arrays.asList(
            "&2Usage:",
            "&a/[prefix] [label] list &7 - list of categories",
            "&a/[prefix] [label] [category] &7- edit category"));

    private static final String LIST_EMPTY_MESSAGE = Colors.fixColors("&cList is empty.");
    private static final String LIST_OF_CATEGORIES_MESSAGE = Colors.fixColors("&aList of categories");
    private static final String CATEGORY_NAME_IN_LIST_PREFIX = Colors.fixColors(" &7- ");
    private static final String CATEGORY_NOT_FOUND_MESSAGE = Colors.fixColors("&cCategory &7{CATEGORY} &cnot found.");

    public EditCommand(Main plugin, String label, String... aliases) {
        super(plugin, label, PERMISSION_COMMAND_SHOP_EDIT, "/[prefix] [label] [category]", aliases);
    }

    @Override
    public boolean onPlayerCommand(final Player player, final  String prefix, final  String label, final  String... args) {

        LOGGER.debug("onPlayerCommand() Received command for player={}, prefix={}, label={}, args={}",
                player.getName(), prefix, label, Arrays.toString(args));

        if (args.length == 0){

            LOGGER.debug("onPlayerCommand() args={} length=0", Arrays.toString(args));

            return Messages.sendMessageIfNotEmpty(player, this.getUsage(label, prefix));
        }

        if (args[0].equalsIgnoreCase("list")){

            LOGGER.debug("onPlayerCommand() arg[0] = list");

            if (this.plugin.getCategories().isEmpty()){

                LOGGER.debug("onPlayerCommand() list of categories is empty");

                return Messages.sendMessageIfNotEmpty(player, LIST_EMPTY_MESSAGE);
            }

            final List<String> categoryNames = this.plugin.getCategories().stream().map(Category::getName).collect(toList());

            player.sendMessage(LIST_OF_CATEGORIES_MESSAGE);
            categoryNames.forEach(categoryName -> player.sendMessage(CATEGORY_NAME_IN_LIST_PREFIX + categoryName));

            LOGGER.debug("onPlayerCommand() List of categories={} send to player={}", categoryNames.toString());

            return true;
        }

        final Optional<Category> optionalCategory = this.plugin.getCategories().getByName(args[0]);

        if (!optionalCategory.isPresent()){

            LOGGER.debug("onPlayerCommand() Category with name={} not found", args[0]);

            return Messages.sendMessageIfNotEmpty(player, CATEGORY_NOT_FOUND_MESSAGE, "{CATEGORY}", args[0]);
        }

        final Category category = optionalCategory.get();
        player.openInventory(category.getConfigInventory());

        LOGGER.debug("onPlayerCommand() Open config inventory for player={} and category={}",
                player.getName(), category.getName());
        return true;
    }

}
