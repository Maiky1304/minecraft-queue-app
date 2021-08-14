package me.maiky.util;

/**
 * This project is owned by Maiky Perlee - © 2021
 */

public class Integers {

    public static boolean isInt(String input) {
        try {
            Integer.parseInt(input);
        } catch (NumberFormatException exception) {
            return false;
        }
        return true;
    }

    public static int toInt(String input) {
        return Integer.parseInt(input);
    }

}
