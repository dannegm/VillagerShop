package net.dajman.villagershop.util;

import java.util.Optional;

public class Numbers {

    public static Optional<Double> parseDouble(final String text){
        try {
            return Optional.of(Double.parseDouble(text));

        } catch (final NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static Optional<Float> parseFloat(final String text){
        try{
            return Optional.of(Float.parseFloat(text));

        } catch (final NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static Optional<Integer> parseInt(final String text){
        try{
            return Optional.of(Integer.parseInt(text));

        } catch (final NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static Optional<Short> parseShort(final String text){
        try{
            return Optional.of(Short.parseShort(text));

        } catch (final NumberFormatException e) {
            return Optional.empty();
        }
    }

}
