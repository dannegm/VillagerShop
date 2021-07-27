package net.dajman.villagershop.command;


import net.dajman.villagershop.Main;
import net.dajman.villagershop.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public abstract class Command extends org.bukkit.command.Command{

	private static final transient String PERMISSION_MESSAGE = ColorUtil.fixColors("&4Blad: &cNie masz uprawnien.");

	protected final transient Main plugin;
	private String permission;
	private boolean player;
	
	public Command(final Main plugin, final String name, final boolean player, final String permission, final String description, final String usage, final String... aliases){
		super(name, description, usage, Arrays.asList(aliases));
		this.permission = permission;
		this.player = player;
		this.plugin = plugin;
	}

	@Override
	public boolean execute(CommandSender sender, String command, String[] args) {
		if (this.isPlayer() && !(sender instanceof Player)){
			sender.sendMessage("Blad: Komenda dostepna tylko dla graczy.");
			return false;
		}
		if (this.getPermission() != null && !sender.hasPermission(this.getPermission())){
			sender.sendMessage(PERMISSION_MESSAGE);
			return false;
		}
		return exe(sender, command, args);
	}
	
	public abstract boolean exe(CommandSender sender, String command, String[] args);


	public String getDescription() {
		return description;
	}

	public String getPermission() {
		return permission;
	}
	public boolean isPlayer() {
		return player;
	}



	public Command setDescription(String description) {
		this.description = description;
		return this;
	}

	public Command setUsage(String usage) {
		super.setUsage(usage);
		return this;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public Command setAliases(List<String> aliases) {
		super.setAliases(aliases);
		return this;
	}

	public void setPlayer(boolean player) {
		this.player = player;
	}

	public boolean sendUsageMessage(final CommandSender sender){
		sender.sendMessage(ColorUtil.fixColors("&cPoprawne uzycie: &7" + this.getUsage()));
		return false;
	}
	
	public void register() {
    	try {
    		if (this.getName()==null)return;
	        Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
	        f.setAccessible(true);
	        CommandMap cmap = (CommandMap) f.get(Bukkit.getServer());
	        cmap.register(this.getName(), this);
    	} catch (Exception e) { 
    		e.printStackTrace();
        }
    }
}
