package net.dajman.villagershop.command;

import net.dajman.villagershop.Main;
import net.dajman.villagershop.command.util.CommandList;
import net.dajman.villagershop.util.Messages;
import net.dajman.villagershop.common.logging.Logger;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Optional;

public abstract class Command extends org.bukkit.command.Command {

    public static final String PERMISSION_COMMAND_PREFIX = Main.PERMISSION_PREFIX + "cmd.";

    private static final Logger LOGGER = Logger.getLogger(Command.class);

    protected final Main plugin;
    protected final CommandList subCommands;

    public Command(final Main plugin, final String label, final String permission,
                   final String usage, final String... aliases) {

        super(label, "", usage, Arrays.asList(aliases));

        this.plugin = plugin;
        this.subCommands = new CommandList();

        this.setPermission(permission);
    }


    public String getLabel() {
        return this.getName();
    }

    public String getUsage(final String label){
        return this.plugin.getConfiguration().commandUsageMessage
                .replace("{USAGE}",
                        this.getUsage().replace("[label]", label));
    }

    public String getUsage(final String label, final String prefix){
        return this.getUsage(label).replace("[prefix]", prefix);
    }

    public boolean addSubCommand(final Command command){
        return this.subCommands.add(command);
    }

    public boolean matches(final String label){
        if (this.getName().equalsIgnoreCase(label)){
            return true;
        }
        return this.getAliases().stream().anyMatch(alias -> alias.equalsIgnoreCase(label));
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        return this.onRawCommand(sender, "", label, args);
    }

    private boolean onRawCommand(final CommandSender sender, final String prefix, final String label, final String... args){

        LOGGER.debug("onRawCommand() Received raw command from sender={}, prefix={}, label={}, args={}",
                sender.getName(), prefix, label, Arrays.toString(args));

        if (args.length > 0){

            LOGGER.debug("onRawCommand() args={} length > 0, looking for subcommand.", Arrays.toString(args));

            final Optional<Command> optionalSubCommand = this.subCommands.findByLabel(args[0]);

            if (optionalSubCommand.isPresent()){

                final Command subCommand = optionalSubCommand.get();

                LOGGER.debug("onRawCommand() Found subcommand={} for arg={}", subCommand.getLabel(), args[0]);

                final String newPrefix = prefix.isEmpty() ? label : prefix + " " + label;

                return subCommand.onRawCommand(sender, newPrefix, args[0],
                        Arrays.copyOfRange(args, 1, args.length));
            }

            LOGGER.debug("onRawCommand() SubCommand for arg={} not found", args[0]);

        }

        if (!sender.hasPermission(this.getPermission())){

            LOGGER.debug("onRawCommand() Sender={} do not have permission={}", sender.getName(), this.getPermission());

            Messages.sendMessageIfNotEmpty(sender, this.plugin.getConfiguration().commandPermissionMessage);
            return true;
        }

        LOGGER.debug("onRawCommand() executing command={}.", label);

        return this.onCommand(sender, prefix, label, args);

    }

    public abstract boolean onCommand(final CommandSender sender, final String prefix, final String label, final String... args);

}
