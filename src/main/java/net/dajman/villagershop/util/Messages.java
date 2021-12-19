package net.dajman.villagershop.util;

import org.bukkit.command.CommandSender;

import java.util.List;

import static java.util.Objects.isNull;

public class Messages {

    public static boolean sendMessageIfNotEmpty(final CommandSender sender, String message, final String... replacements){

        if (isNull(message) || message.isEmpty()){
            return true;
        }

        for(int i = 1; i < replacements.length; i += 2){
            message = message.replace(replacements[i - 1], replacements[i]);
        }

        sender.sendMessage(message);
        return true;
    }

    public static boolean sendMessage(final CommandSender sender, final List<String> messages, final String... replacements){

        if (isNull(messages) || messages.isEmpty()){
            return true;
        }

        for (String message : messages) {

            for(int i = 1; i < replacements.length; i += 2){
                message = message.replace(replacements[i - 1], replacements[i]);
            }

            sender.sendMessage(message);
        }

        return true;
    }

}
