package net.dajman.villagershop.command.util;

import net.dajman.villagershop.command.Command;

import java.util.ArrayList;
import java.util.Optional;

public class CommandList extends ArrayList<Command> {

    public boolean add(final Command command){

        if (this.contains(command)){
            return false;
        }

        if (this.findByLabel(command.getLabel()).isPresent()){
            return false;
        }

        for (String alias : command.getAliases()) {
            if (this.findByLabel(alias).isPresent()){
                return false;
            }

        }

        return super.add(command);
    }

    public Optional<Command> findByLabel(final String label){
        return this.stream().filter(command -> command.matches(label)).findFirst();
    }

}
