package com.fabledclan.Website;

import java.net.InetSocketAddress;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.fabledclan.Main;
import com.fabledclan.Commands.LeaderboardCommand;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Website {

    private static HttpServer server;

    public static void initWebsite() {
        try {
            server = HttpServer.create(new InetSocketAddress(25564), 0);
            server.createContext("/", httpExchange -> servePage(httpExchange, "home.html"));
            server.createContext("/leaderboard", httpExchange -> servePage(httpExchange, "leaderboard.html"));
            server.createContext("/shop", httpExchange -> servePage(httpExchange, "shop.html"));
            server.createContext("/changelog", httpExchange -> servePage(httpExchange, "changelog.html"));
            //API
            server.createContext("/api/leaderboard", Website::serveLeaderboardData);
            server.setExecutor(null); // creates a default executor
            server.start();
            System.out.println("Server started on port 25564");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void servePage(HttpExchange httpExchange, String page) {
        try {
            Main plugin = Main.getInstance();
            InputStream in = plugin.getResource(page);
            if (in == null) {
                // Handle the case where the resource is not found
                httpExchange.sendResponseHeaders(404, 0);
                return;
            }
            String response = new String(in.readAllBytes());
            httpExchange.getResponseHeaders().set("Content-Type", "text/html");
            httpExchange.sendResponseHeaders(200, response.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void stopWebsite()
    {

        server.stop(0);
    }

    private static void serveLeaderboardData(HttpExchange httpExchange) {
        try {
            List<LeaderboardCommand.Player> topPlayers = LeaderboardCommand.getTopPlayersByLevelWebsite(10);
            String response = new Gson().toJson(topPlayers);
            httpExchange.getResponseHeaders().set("Content-Type", "application/json");
            httpExchange.sendResponseHeaders(200, response.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

}
    
