package me.maiky;

import me.maiky.server.Server;
import me.maiky.util.Integers;

import java.io.IOException;

/**
 * This project is owned by Maiky Perlee - © 2021
 */

public class App {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Provide a host and port in the system arguments.");
            System.exit(0);
        }

        String host = args[0];
        String port = args[1];
        if (!Integers.isInt(port)) {
            System.out.println("Invalid port provided.");
            System.exit(0);
        }

        Server server = new Server(host, Integers.toInt(port));
        server.load();
    }

}
