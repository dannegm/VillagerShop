package net.dajman.villagershop.common.logging;

import net.dajman.villagershop.Main;

import java.util.Objects;

import static java.util.Objects.nonNull;


public class Logger {

    private static final String DEBUG_PREFIX = "[DEBUG] ";

    private static Main plugin;
    private static java.util.logging.Logger logger = java.util.logging.Logger.getLogger("Minecraft");
    private final String className;

    private Logger(final Class<?> clazz){
        this.className = clazz.getName();
    }

    public static Logger getLogger(final Class<?> clazz){
        return new Logger(clazz);
    }

    public static void init(final Main plugin){
        Logger.plugin = plugin;
        Logger.logger = plugin.getLogger();
    }

    public boolean isDebugMode(){
        return nonNull(plugin) && nonNull(plugin.getConfiguration()) && plugin.getConfiguration().debug;
    }

    public boolean debug(final String message, final String... replacements){

        if (!this.isDebugMode()){
            return false;
        }

        Logger.logger.info(DEBUG_PREFIX + "[" + className + "] " + replace(message, replacements));
        return true;
    }

    public boolean debug(final String message, final Object... replacements){

        if (!this.isDebugMode()){
            return false;
        }

        Logger.logger.info(DEBUG_PREFIX + "[" + className + "] " + replace(message, replacements));
        return true;
    }

    public void info(final String message, final String... replacements){
        Logger.logger.info(replace(message, replacements));
    }

    public void info(final String message, final Object... replacements){
        Logger.logger.info(replace(message, replacements));
    }

    public void warn(final String message, final String... replacements){
        Logger.logger.warning(replace(message, replacements));
    }

    public void warn(final String message, final Object... replacements){
        Logger.logger.warning(replace(message, replacements));
    }

    public void error(final String message, final String... replacements){
        Logger.logger.severe(replace(message, replacements));
    }

    public void error(final String message, final Object... replacements){
        Logger.logger.severe(replace(message, replacements));
    }

    private String replace(final String message, final String... replacements){
        String result = message;

        for(String replacement : replacements){
            result = result.replaceFirst("\\{}", replacement);
        }

        return result;
    }

    private String replace(final String message, final Object... replacements){
        String result = message;

        for(Object replacement : replacements){

            if (replacement instanceof Throwable){
                result = result.replaceFirst("\\{}", throwableToString((Throwable) replacement));
                continue;
            }

            if (replacement instanceof String){
                result = result.replaceFirst("\\{}", (String) replacement);
                continue;
            }

            result = result.replaceFirst("\\{}", Objects.toString(replacement));

        }

        return result;
    }

    private static String throwableToString(final Throwable throwable){
        return throwable.getClass().getName() + ": " + throwable.getMessage();
    }
    
}
