package com.fabledclan.Website;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.fabledclan.DatabaseManager;
import com.fabledclan.Main;
import com.fabledclan.Commands.LeaderboardCommand;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Website {

    private static HttpServer server;
    private static Map<String, String> verificationCodes = new HashMap<>();
    private static Map<String, Integer> failedAttempts = new HashMap<>();
    private static Map<String, Long> lastCodeTimes = new HashMap<>();

    public static void initWebsite() {
        try {
            server = HttpServer.create(new InetSocketAddress(25564), 0);
            server.createContext("/", httpExchange -> servePage(httpExchange, "home.html"));
            server.createContext("/leaderboard", httpExchange -> servePage(httpExchange, "leaderboard.html"));
            server.createContext("/shop", httpExchange -> servePage(httpExchange, "shop.html"));
            server.createContext("/changelog", httpExchange -> servePage(httpExchange, "changelog.html"));
            // API
            server.createContext("/api/leaderboard", Website::serveLeaderboardData);
            server.createContext("/api/verify", new VerifyHandler());
            server.createContext("/api/buy", Website::buy);
            server.createContext("/api/exp", Website::getPlayerExp);

            server.setExecutor(null); // creates a default executor
            server.start();
            System.out.println("Server started on port 25564");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void getPlayerExp(HttpExchange httpExchange) throws IOException {
        String username = httpExchange.getRequestURI().getQuery().split("=")[1];
        Player player = Bukkit.getPlayer(username);
        UUID uuid = player.getUniqueId();
        int playerExp = DatabaseManager.getPlayerExperiencePlayerStats(uuid);
        String response = String.format("{\"exp\": %d}", playerExp);
        httpExchange.getResponseHeaders().set("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
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

    public static void stopWebsite() {
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

    private static class VerifyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String requestBody = new String(httpExchange.getRequestBody().readAllBytes());
            Map<String, String> request = new Gson().fromJson(requestBody, Map.class);
            String username = request.get("username");
            String code = request.get("code");

            if (code == null) {
                // Check if enough time has passed since the last code was sent
                long currentTime = System.currentTimeMillis();
                long lastCodeTime = lastCodeTimes.getOrDefault(username, 0L);
                if (currentTime - lastCodeTime < 30 * 1000) {
                    // Not enough time has passed
                    sendResponse(httpExchange, 429, "");
                    return;
                }

                // Generate a new verification code
                String newCode = generateRandomCode();
                verificationCodes.put(username, newCode);
                lastCodeTimes.put(username, currentTime);
                // Send the code to the player in-game
                Player player = Bukkit.getPlayer(username);
                if (player != null) {
                    player.sendMessage("Your verification code is: " + newCode);
                    sendResponse(httpExchange, 200, "");
                } else {
                    sendResponse(httpExchange, 400, "");
                }
            } else {
                // Check the verification code
                String correctCode = verificationCodes.get(username);
                if (code.equals(correctCode)) {
                    // The code is correct
                    failedAttempts.remove(username);
                    sendResponse(httpExchange, 200, "");
                } else {
                    // The code is incorrect
                    int attempts = failedAttempts.getOrDefault(username, 0);
                    attempts++;
                    failedAttempts.put(username, attempts);
                    if (attempts >= 5) {
                        sendResponse(httpExchange, 429, "");
                    } else {
                        sendResponse(httpExchange, 401, "");
                    }
                }
            }
        }

        private String generateRandomCode() {
            return String.valueOf(new Random().nextInt(900000) + 100000); // Generate a random 6-digit code
        }
    }

    private static void buy(HttpExchange httpExchange) throws IOException {
        String requestBody = new String(httpExchange.getRequestBody().readAllBytes());
        Map<String, String> request = new Gson().fromJson(requestBody, Map.class);
        String username = request.get("username");
        String itemId = request.get("itemId");
        int price;
        Player player = Bukkit.getPlayer(username);
        UUID uuid = player.getUniqueId();
        if (uuid == null) {
            // The player is not online
            sendResponse(httpExchange, 400, "");
            return;
        }

        int playerExp = DatabaseManager.getPlayerExperiencePlayerStats(uuid);
        if (itemId.equals("leather_armor")) {
            price = 5;
            if (playerExp < price) {
                // The player doesn't have enough exp
                player.sendMessage("Not enough exp for web purchase!");
                sendResponse(httpExchange, 400, "");
                return;
            }
            // Deduct the price from the player's exp and give them the item
            DatabaseManager.updatePlayerExperiencePlayerStats(uuid, playerExp - price);
            Bukkit.getPlayer(uuid).getInventory().addItem(new ItemStack(Material.LEATHER_CHESTPLATE, 1));
            player.sendMessage("Purchased leather armor!");
            sendResponse(httpExchange, 200, "");
            return;
        } else if (itemId.equals("iron_sword")) {
            price = 10;
            if (playerExp < price) {
                player.sendMessage("Not enough exp for web purchase!");
                sendResponse(httpExchange, 400, "");
                return;
            }
            DatabaseManager.updatePlayerExperiencePlayerStats(uuid, playerExp - price);
            Bukkit.getPlayer(uuid).getInventory().addItem(new ItemStack(Material.IRON_SWORD, 1));
            player.sendMessage("Purchased iron sword!");
            sendResponse(httpExchange, 200, "");
        } else if (itemId.equals("diamond_pickaxe")) {
            price = 20;
            if (playerExp < price) {
                player.sendMessage("Not enough exp for web purchase!");
                sendResponse(httpExchange, 400, "");
                return;
            }
            DatabaseManager.updatePlayerExperiencePlayerStats(uuid, playerExp - price);
            Bukkit.getPlayer(uuid).getInventory().addItem(new ItemStack(Material.DIAMOND_PICKAXE, 1));
            player.sendMessage("Purchased diamond pickaxe!");
            sendResponse(httpExchange, 200, "");
        } else {
            sendResponse(httpExchange, 400, "");
        }
    }

    private static void sendResponse(HttpExchange httpExchange, int statusCode, String responseText) throws IOException {
        httpExchange.sendResponseHeaders(statusCode, responseText.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(responseText.getBytes());
        os.close();
    }
}