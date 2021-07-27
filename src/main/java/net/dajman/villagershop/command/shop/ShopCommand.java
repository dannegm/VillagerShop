package net.dajman.villagershop.command.shop;

import net.dajman.villagershop.Main;
import net.dajman.villagershop.category.Category;
import net.dajman.villagershop.command.Command;
import net.dajman.villagershop.util.ColorUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCommand extends Command {

    public ShopCommand(Main plugin) {
        super(plugin, "shop", false, null, "open shop", "/sklep", "sklep");
    }

    @Override
    public boolean exe(CommandSender sender, String command, String[] args) {
        if (sender.hasPermission("villagershop.admin") && args.length > 0){
            if (args[0].equalsIgnoreCase("reload")){
                this.plugin.reloadConfig();
                this.plugin.getCategories().clear();
                this.plugin.getConfiguration().load();
                this.plugin.getCategoryData().load();
                sender.sendMessage(ColorUtil.fixColors("&aConfiguration reloaded."));
                return true;
            }
            if (args[0].equalsIgnoreCase("edit")){
                if (args.length == 1){
                    sender.sendMessage(ColorUtil.fixColors("&2Usage:"));
                    sender.sendMessage(ColorUtil.fixColors("&a/shop edit list &7- list of categories"));
                    sender.sendMessage(ColorUtil.fixColors("&a/shop edit [name] &7- edit category"));
                    return false;
                }
                if (args[1].equalsIgnoreCase("list")){
                    if (this.plugin.getCategories().size() == 0){
                        sender.sendMessage(ColorUtil.fixColors("&cList is empty."));
                        return false;
                    }
                    sender.sendMessage(ColorUtil.fixColors("&aList of categories:"));
                    for (Category category : this.plugin.getCategories()) {
                        sender.sendMessage(ColorUtil.fixColors(" &7- " + category.getName()));
                    }
                    return true;
                }
                final Category category = this.plugin.getCategories().get(args[1]);
                if (category == null){
                    sender.sendMessage(ColorUtil.fixColors("&cCategory not found."));
                    return false;
                }
                if (!(sender instanceof Player)){
                    sender.sendMessage("Only players can edit category.");
                    return false;
                }
                final Player player = (Player) sender;
                player.openInventory(category.getConfigInventory());
                return true;
            }
        }
        if (!(sender instanceof Player)){
            return false;
        }
        final Player player = (Player)sender;
        this.plugin.getMainInventoryBuilder().open(player);
        return true;
    }
}
