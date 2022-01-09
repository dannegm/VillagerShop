package net.dajman.villagershop.command.manager;

import net.dajman.villagershop.command.Command;
import net.dajman.villagershop.command.util.CommandList;
import net.dajman.villagershop.common.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;

public class CommandManager extends CommandList {

    private static final Logger LOGGER = Logger.getLogger(CommandManager.class);

    public boolean registerAll(){
        return this.register(this.toArray(new Command[0]));
    }

    public boolean register(final Command... commands){

        LOGGER.debug("register() Trying to register commands={}",
                Arrays.toString(Arrays.stream(commands).map(Command::getLabel).toArray()));

        if (commands.length == 0){
            LOGGER.debug("register() No commands found to register.");
            return true;
        }

        try{
            final Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            final CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());

            Arrays.stream(commands).forEach(command -> {

                commandMap.register(command.getLabel(), command);

                this.add(command);

                LOGGER.debug("register() Command={} registered", command.getLabel());
            });

            return true;

        } catch (final NoSuchFieldException | IllegalAccessException e){
            LOGGER.error("register() Error while registering commands. {}", e);

            if (LOGGER.isDebugMode()){
                e.printStackTrace();
            }
        }

        return false;
    }

    public boolean unregisterAll(){
        return this.unregister(this.toArray(new Command[0]));
    }

    public boolean unregister(final Command... commands){

        LOGGER.debug("unregister() Trying to unregister command={}",
                Arrays.toString(Arrays.stream(commands).map(Command::getLabel).toArray()));

        if (commands.length == 0){
            LOGGER.debug("unregister() No commands found to unregister.");
            return true;
        }

        try{
            final Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            final CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());

            if (!(commandMap instanceof SimpleCommandMap)){
                LOGGER.warn("unregister() Server command map is not instance of SimpleCommandMap, " +
                        "commands cannot be unregistered. Try restart or reload server.");

                return false;
            }

            final SimpleCommandMap simpleCommandMap = (SimpleCommandMap) commandMap;

            final Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);

            final Map<String, org.bukkit.command.Command> knownCommands =
                    (Map<String, org.bukkit.command.Command>) knownCommandsField.get(simpleCommandMap);

            Arrays.stream(commands).forEach(command -> {
                knownCommands.remove(command.getLabel());
                command.getAliases().forEach(knownCommands::remove);

                this.remove(command);

                LOGGER.debug("unregister() Command={} unregistered", command.getLabel());
            });

            return true;

        } catch (final NoSuchFieldException | IllegalAccessException e){

            LOGGER.error("unregister() Error while unregistering commands. {}", e);

            if (LOGGER.isDebugMode()){
                e.printStackTrace();
            }
        }

        return false;
    }
}
