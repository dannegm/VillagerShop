package net.dajman.villagershop.util;

public class NumberUtil {

    public static boolean isDouble(final String text){
        try{
            Double.parseDouble(text);
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }

    public static boolean isFloat(final String text){
        try{
            Float.parseFloat(text);
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }

    public static boolean isInteger(String text){
        try{
            Integer.parseInt(text);
            return true;
        }
        catch(NumberFormatException e){
            return false;
        }
    }

    public static boolean isShort(final String text){
        try{
            Short.parseShort(text);
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }

    public static float round(final float value, final int dec){
        final int d = (int) Math.pow(10,  dec);
        return (float) Math.round(value * d) / d;
    }



}
