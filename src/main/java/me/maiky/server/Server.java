package me.maiky.server;

import lombok.Getter;
import me.maiky.client.Connection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * This project is owned by Maiky Perlee - © 2021
 */

public class Server {

    @Getter private boolean running;

    @Getter private ServerSocket serverSocket;

    @Getter private final String host;
    @Getter private final int port;

    @Getter private final List<Connection> clients;

    public Server(String host, int port) {
        this.host = host;
        this.port = port;
        this.clients = new ArrayList<>();
    }

    public void load() {
        // Change state
        this.running = true;

        // Start server
        startServer();
    }

    private void startServer() {

        // Load
        try {
            // Initialize socket
            serverSocket = new ServerSocket(this.port, 50, InetAddress.getByName(this.host));

            // Listening to...
            System.out.println("Listening to port " + this.host + ":" + this.port);

            while(true) {
                Socket connection = serverSocket.accept();
                Connection sc = new Connection(connection);
                this.clients.add(sc);
                sc.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
