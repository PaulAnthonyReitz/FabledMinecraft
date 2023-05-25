package com.fabledclan;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.fabledclan.Enemy.EnemyData;
import com.fabledclan.Player.PlayerStats;

public class DatabaseManager {
    private static final String DB_FILE = "player_stats.db";
    private static Connection connection;

    public static void initDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + Main.getPlugin().getDataFolder().toPath().resolve(DB_FILE));
            createTables();
            createEnemyTable();
            createBounties();
            createLockTable();
            createPlayerConfig();
            createCustomContainerTable();
            createExperienceContainerTable();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static void createExperienceContainerTable() {
        Connection connection = getConnection();
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS xp_container" +
            "(player TEXT NOT NULL," +
            "xp INTEGER NOT NULL, " +
            "name TEXT NOT NULL)");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void createCustomContainerTable() {
        Connection connection = getConnection();
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS container_blocks" +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "world TEXT NOT NULL," +
                    "x INTEGER NOT NULL," +
                    "y INTEGER NOT NULL," +
                    "z INTEGER NOT NULL," +
                    "block_name TEXT NOT NULL);");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void createLockTable() {
        Connection conn = getConnection();
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS locked_blocks ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "world TEXT NOT NULL,"
                    + "x INTEGER NOT NULL,"
                    + "y INTEGER NOT NULL,"
                    + "z INTEGER NOT NULL,"
                    + "pin TEXT NOT NULL,"
                    + "owner_uuid TEXT NOT NULL,"
                    + "owner_name TEXT NOT NULL);");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void createTables() {
        Connection conn = getConnection();
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS player_stats ("
                    + "uuid TEXT PRIMARY KEY,"
                    + "attack INT DEFAULT 1,"
                    + "defense INT DEFAULT 1,"
                    + "exp INT DEFAULT 0,"
                    + "level INT DEFAULT 1,"
                    + "max_health INT DEFAULT 20,"
                    + "movement_speed DOUBLE DEFAULT 1,"
                    + "name TEXT,"
                    + "magic INT DEFAULT 1,"
                    + "stamina INT DEFAULT 1);");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void createPlayerConfig() {
        Connection conn = getConnection();
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS player_config ("
                    + "uuid TEXT PRIMARY KEY,"
                    + "start_book BOOLEAN DEFAULT TRUE,"
                    + "start_glytch BOOLEAN DEFAULT TRUE,"
                    + "name STRING NOT NULL);");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void createBounties() {
        Connection conn = getConnection();
        try (Statement stmt = conn.createStatement()) {
            String createTable = "CREATE TABLE IF NOT EXISTS bounties (" +
                    "uuid TEXT PRIMARY KEY," +
                    "bounty INTEGER" +
                    ");";
            stmt.execute(createTable);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void createEnemyTable() {
        Connection conn = getConnection();
        try {
            Statement stmt = conn.createStatement();
            String sqlCreate = "CREATE TABLE IF NOT EXISTS enemies (" +
                    "entity_name TEXT PRIMARY KEY," +
                    "hp INT NOT NULL," +
                    "hp_scale INT NOT NULL," +
                    "base_attack INT NOT NULL," +
                    "attack_scale INT NOT NULL," +
                    "base_exp INT NOT NULL," +
                    "exp_scale INT NOT NULL," +
                    "defense INT NOT NULL," +
                    "defense_scale INT NOT NULL" +
                    ")";
            stmt.execute(sqlCreate);

            String sqlInsert = "INSERT OR IGNORE INTO enemies (entity_name, hp, hp_scale, base_attack, attack_scale, base_exp, exp_scale, defense, defense_scale) VALUES "
                    +
                    "('axolotl', 4, 1, 1, 1, 1, 1, 1, 1)," +
                    "('bat', 4, 1, 1, 1, 1, 1, 1, 1)," +
                    "('bee', 6, 1, 2, 1, 8, 1, 1, 1)," +
                    "('blaze', 20, 1, 6, 1, 45, 1, 1, 1)," +
                    "('cave_spider', 8, 1, 4, 1, 10, 1, 1, 1)," +
                    "('cat', 6, 1, 2, 1, 8, 1, 1, 1)," +
                    "('chicken', 4, 1, 1, 1, 1, 1, 1, 1)," +
                    "('cod', 8, 1, 1, 1, 2, 1, 1, 1)," +
                    "('cow', 8, 1, 1, 1, 2, 1, 1, 1)," +
                    "('creeper', 12, 1, 1, 1, 15, 1, 1, 1)," +
                    "('dolphin', 10, 1, 2, 1, 10, 1, 1, 1)," +
                    "('donkey', 20, 1, 2, 1, 4, 1, 1, 1)," +
                    "('dragon', 300, 5, 20, 5, 500, 5, 1, 1)," +
                    "('drowned', 8, 1, 3, 1, 8, 2, 1, 1)," +
                    "('elder_guardian', 80, 3, 10, 2, 100, 3, 1, 1)," +
                    "('ender_dragon', 200, 5, 25, 3, 1000, 5, 1, 1)," +
                    "('endermite', 4, 1, 1, 1, 3, 1, 1, 1)," +
                    "('enderman', 20, 1, 7, 1, 25, 1, 1, 1)," +
                    "('evoker', 15, 2, 5, 1, 30, 2, 1, 1)," +
                    "('fox', 10, 1, 2, 1, 5, 1, 1, 1)," +
                    "('frog', 10, 1, 2, 1, 5, 1, 1, 1)," +
                    "('goat', 6, 1, 1, 1, 1, 1, 1, 1)," +
                    "('ghast', 15, 2, 4, 1, 25, 2, 1, 1)," +
                    "('guardian', 20, 2, 6, 1, 30, 2, 1, 1)," +
                    "('hoglin', 30, 2, 2, 1, 5, 1, 1, 1)," +
                    "('horse', 30, 2, 2, 1, 5, 1, 1, 1)," +
                    "('husk', 10, 1, 3, 1, 8, 2, 1, 1)," +
                    "('iron_golem', 100, 4, 1, 1, 1, 1, 1, 1)," +
                    "('llama', 15, 1, 2, 1, 5, 1, 1, 1)," +
                    "('illusioner', 15, 1, 2, 1, 5, 1, 1, 1)," +
                    "('magma_cube', 20, 1, 5, 1, 35, 2, 1, 1)," +
                    "('mule', 20, 1, 5, 1, 35, 2, 1, 1)," +
                    "('mooshroom', 10, 1, 2, 1, 3, 1, 1, 1)," +
                    "('ocelot', 10, 1, 1, 1, 3, 1, 1, 1)," +
                    "('parrot', 8, 1, 1, 1, 2, 1, 1, 1)," +
                    "('panda', 8, 1, 1, 1, 2, 1, 1, 1)," +
                    "('pig', 8, 1, 1, 1, 2, 1, 1, 1)," +
                    "('phantom', 12, 1, 4, 1, 10, 1, 1, 1)," +
                    "('pillager', 18, 11, 1, 1, 12, 1, 1, 1)," +
                    "('polar_bear', 25, 1, 0, 0, 7, 1, 1, 1)," +
                    "('pufferfish', 8, 1, 0, 0, 7, 1, 1, 1)," +
                    "('ravager', 40, 2, 8, 1, 20, 1, 1, 1)," +
                    "('rabbit', 3, 1, 1, 1, 1, 1, 1, 1)," +
                    "('salmon', 4, 1, 1, 1, 2, 1, 1, 1)," +
                    "('sheep', 6, 1, 1, 1, 2, 1, 1, 1)," +
                    "('shulker', 30, 0, 0, 0, 0, 0, 1, 1)," +
                    "('slime', 10, 0, 0, 0, 0, 0, 1, 1)," +
                    "('silverfish', 6, 1, 1, 1, 3, 1, 1, 1)," +
                    "('skeleton_horse', 30, 2, 0, 0, 8, 1, 1, 1)," +
                    "('skeleton', 6, 1, 1, 1, 8, 1, 1, 1)," +
                    "('snow_golem', 4, 1, 0, 0, 0, 0, 1, 1)," +
                    "('snowman', 4, 1, 0, 0, 0, 0, 1, 1)," +
                    "('stray', 12, 1, 1, 1, 8, 1, 1, 1)," +
                    "('spider', 12, 1, 2, 1, 12, 1, 1, 1)," +
                    "('squid', 4, 1, 2, 1, 1, 1, 1, 1)," +
                    "('tadpole', 5, 1, 0, 0, 1, 1, 1, 1)," +
                    "('tropical_fish', 5, 1, 0, 0, 1, 1, 1, 1)," +
                    "('turtle', 30, 1, 0, 0, 5, 1, 1, 1)," +
                    "('vex', 7, 1, 3, 1, 5, 1, 1, 1)," +
                    "('villager', 20, 1, 0, 0, 10, 1, 1, 1)," +
                    "('vindicator', 24, 1, 6, 1, 12, 1, 1, 1)," +
                    "('witch', 13, 1, 1, 1, 20, 1, 1, 1)," +
                    "('wither', 300, 4, 12, 2, 250, 1, 1, 1)," +
                    "('wither_skeleton', 20, 1, 5, 1, 15, 1, 1, 1)," +
                    "('wolf', 8, 1, 0, 0, 2, 1, 1, 1)," +
                    "('zombie_villager', 8, 1, 0, 0, 2, 1, 1, 1)," +
                    "('zombie_horse', 16, 1, 0, 0, 2, 1, 1, 1)," +
                    "('zombified_piglin', 8, 1, 0, 0, 2, 1, 1, 1)," +
                    "('zombie', 8, 1, 1, 1, 8, 1, 1, 1)";

            stmt.execute(sqlInsert);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setPlayerStats(UUID uuid, double movementSpeed, int attack, int defense, int maxHealth, int exp,
            int level, String name, int magic, int stamina) {
        Connection conn = getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(
                "INSERT OR REPLACE INTO player_stats (uuid, movement_speed, attack, defense, max_health, exp, level, name, magic, stamina) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);")) {
            pstmt.setString(1, uuid.toString());
            pstmt.setDouble(2, movementSpeed);
            pstmt.setInt(3, attack);
            pstmt.setInt(4, defense);
            pstmt.setInt(5, maxHealth);
            pstmt.setInt(6, exp);
            pstmt.setInt(7, level);
            pstmt.setString(8, name);
            pstmt.setInt(9, magic);
            pstmt.setInt(10, stamina);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void ensureConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            // Replace this line with your actual code to open the connection
            connection = DriverManager.getConnection("jdbc:sqlite:" + Main.getPlugin().getDataFolder().toPath().resolve(DB_FILE));
        }
    }

    public static void insertPlayerExperience(Player player, int xpLevel) {
        Connection connection = getConnection();
        try (PreparedStatement ps = connection.prepareStatement("INSERT INTO xp_container (player, xp, name) VALUES (?, ?, ?)")) {
            ps.setString(1, player.getUniqueId().toString());
            ps.setInt(2, xpLevel);
            ps.setString(3, player.getName());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updatePlayerExperience(UUID playerID, int xp) {
        Connection connection = getConnection();
        try (PreparedStatement ps = connection.prepareStatement("UPDATE xp_container SET xp = ? WHERE player = ?")) {
            ps.setInt(1, xp);
            ps.setString(2, playerID.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updatePlayerExperiencePlayerStats(UUID playerID, int exp) {
        Connection connection = getConnection();
        try (PreparedStatement ps = connection.prepareStatement("UPDATE player_stats SET exp = ? WHERE uuid = ?")) {
            ps.setInt(1, exp);
            ps.setString(2, playerID.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int getPlayerExperienceXPContainer(UUID playerID) {
        Connection connection = getConnection();
        try (PreparedStatement ps = connection.prepareStatement("SELECT xp FROM xp_container WHERE player = ?")) {
            ps.setString(1, playerID.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int getPlayerExperiencePlayerStats(UUID playerID) {
        Connection connection = getConnection();
        try (PreparedStatement ps = connection.prepareStatement("SELECT exp FROM player_stats WHERE uuid = ?")) {
            ps.setString(1, playerID.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void insertLockedBlock(Location location, String pin, UUID owner_uuid, String owner_name) {
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(
                        "INSERT INTO locked_blocks (world, x, y, z, pin, owner_uuid, owner_name) VALUES (?, ?, ?, ?, ?, ?, ?)")) {

            pstmt.setString(1, location.getWorld().getName());
            pstmt.setInt(2, location.getBlockX());
            pstmt.setInt(3, location.getBlockY());
            pstmt.setInt(4, location.getBlockZ());
            pstmt.setString(5, pin);
            pstmt.setString(6, owner_uuid.toString());
            pstmt.setString(7, owner_name);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isLocked(Location location) {
        return getLockedBlockPin(location) != null;
    }

    public static UUID getLockedBlockOwnerUUID(Location location) {
        Connection conn = getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(
                "SELECT owner_uuid FROM locked_blocks WHERE world = ? AND x = ? AND y = ? AND z = ?")) {
            pstmt.setString(1, location.getWorld().getName());
            pstmt.setInt(2, location.getBlockX());
            pstmt.setInt(3, location.getBlockY());
            pstmt.setInt(4, location.getBlockZ());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return UUID.fromString(rs.getString("owner_uuid"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String getLockedBlockPin(Location location) {
        Connection conn = getConnection();
        try (PreparedStatement pstmt = conn
                .prepareStatement("SELECT pin FROM locked_blocks WHERE world = ? AND x = ? AND y = ? AND z = ?")) {
            pstmt.setString(1, location.getWorld().getName());
            pstmt.setInt(2, location.getBlockX());
            pstmt.setInt(3, location.getBlockY());
            pstmt.setInt(4, location.getBlockZ());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("pin");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void deleteLockedBlock(Location location) {
        Connection conn = getConnection();
        try (PreparedStatement pstmt = conn
                .prepareStatement("DELETE FROM locked_blocks WHERE world = ? AND x = ? AND y = ? AND z = ?")) {
            pstmt.setString(1, location.getWorld().getName());
            pstmt.setInt(2, location.getBlockX());
            pstmt.setInt(3, location.getBlockY());
            pstmt.setInt(4, location.getBlockZ());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void insertCustomContainerBlock(Location location, String name) {
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(
                        "INSERT INTO container_blocks (world, x, y, z, block_name) VALUES (?, ?, ?, ?, ?)")) {
            pstmt.setString(1, location.getWorld().getName());
            pstmt.setInt(2, location.getBlockX());
            pstmt.setInt(3, location.getBlockY());
            pstmt.setInt(4, location.getBlockZ());
            pstmt.setString(5, name);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getCustomContainerName(Location location) {
        Connection connection = getConnection();
        try (PreparedStatement pstmt = connection.prepareStatement(
                "SELECT block_name FROM container_blocks WHERE world = ? AND x = ? AND y = ? AND z = ?")) {
            pstmt.setString(1, location.getWorld().getName());
            pstmt.setInt(2, location.getBlockX());
            pstmt.setInt(3, location.getBlockY());
            pstmt.setInt(4, location.getBlockZ());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("block_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static ArrayList<Location> getAllCustomContainerLocations(World world) {
        Connection connection = getConnection();
        try (PreparedStatement preparedStatement = connection
                .prepareStatement("SELECT * FROM container_blocks WHERE world = ?")) {
            preparedStatement.setString(1, world.getName());
            ResultSet resultSet = preparedStatement.executeQuery();
            ArrayList<Location> ret = new ArrayList<Location>();
            while (resultSet.next()) {
                int X = resultSet.getInt(3);
                int Y = resultSet.getInt(4);
                int Z = resultSet.getInt(5);
                ret.add(new Location(world, X, Y, Z));
            }
            return ret;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void deleteCustomContainerBlock(Location location) {
        Connection connection = getConnection();
        try (PreparedStatement statement = connection
                .prepareStatement("DELETE FROM container_blocks WHERE world = ? AND x = ? AND y = ? AND z = ?")) {
            statement.setString(1, location.getWorld().getName());
            statement.setInt(2, location.getBlockX());
            statement.setInt(3, location.getBlockY());
            statement.setInt(4, location.getBlockZ());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void applyPlayerConfig(UUID uuid) {
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(
                        "INSERT OR IGNORE INTO player_config (uuid, start_book, start_glytch, name) VALUES (?, ?, ?, ?)")) {

            pstmt.setString(1, uuid.toString());
            pstmt.setBoolean(2, true); // Default value for start_book
            pstmt.setBoolean(3, true); // Default value for start_glytch
            pstmt.setString(4, Bukkit.getOfflinePlayer(uuid).getName()); // Default value for start_glytch
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static PlayerStats getPlayerStats(UUID uuid) {
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            Connection conn = getConnection();
            pstmt = conn.prepareStatement("SELECT * FROM player_stats WHERE uuid = ?;");
            pstmt.setString(1, uuid.toString());
            resultSet = pstmt.executeQuery();

            if (resultSet != null && resultSet.next()) {
                double movementSpeed = resultSet.getDouble("movement_speed");
                int attack = resultSet.getInt("attack");
                int defense = resultSet.getInt("defense");
                int maxHealth = resultSet.getInt("max_health");
                int exp = resultSet.getInt("exp");
                int level = resultSet.getInt("level");
                String name = resultSet.getString("name");
                int magic = resultSet.getInt("magic");
                int stamina = resultSet.getInt("stamina");
                return new PlayerStats(movementSpeed, attack, defense, maxHealth, exp, level, name, magic, stamina);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private static final double DEFAULT_MOVEMENT_SPEED = 1.0;
    private static final int DEFAULT_ATTACK = 1;
    private static final int DEFAULT_DEFENSE = 1;
    private static final int DEFAULT_MAX_HEALTH = 20;
    private static final int DEFAULT_EXP = 0;
    private static final int DEFAULT_LEVEL = 1;
    private static final int DEFAULT_MAGIC = 1;
    private static final int DEFAULT_STAMINA = 1;

    // rest of your class code here

    public static void createNewPlayerStats(UUID uuid) {
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            conn = getConnection();
            statement = conn.prepareStatement("SELECT * FROM player_stats WHERE uuid = ?");
            statement.setString(1, uuid.toString());
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Player record already exists, update it
                // setPlayerStats(uuid, DEFAULT_MOVEMENT_SPEED, DEFAULT_ATTACK, DEFAULT_DEFENSE,
                // DEFAULT_MAX_HEALTH, DEFAULT_EXP, DEFAULT_LEVEL);
            } else {
                // Player record does not exist, insert a new one
                setPlayerStats(uuid, DEFAULT_MOVEMENT_SPEED, DEFAULT_ATTACK, DEFAULT_DEFENSE, DEFAULT_MAX_HEALTH,
                        DEFAULT_EXP, DEFAULT_LEVEL, Bukkit.getOfflinePlayer(uuid).getName(), DEFAULT_MAGIC,
                        DEFAULT_STAMINA);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager
                        .getConnection("jdbc:sqlite:" + Main.getPlugin().getDataFolder().toPath().resolve(DB_FILE));
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void addExp(UUID uuid, int exp) {
        Connection conn = getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement("UPDATE player_stats SET exp = exp + ? WHERE uuid = ?;")) {
            pstmt.setInt(1, exp);
            pstmt.setString(2, uuid.toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static EnemyData getEnemyData(String entityName) {
        Connection conn = getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM enemies WHERE entity_name = ?;")) {
            pstmt.setString(1, entityName.toLowerCase());
            try (ResultSet resultSet = pstmt.executeQuery()) {
                if (!resultSet.next()) {
                    System.out.println("No data found for entity: " + entityName);
                    return null;
                }

                EnemyData enemyData = new EnemyData(
                        resultSet.getString("entity_name"),
                        resultSet.getInt("hp"),
                        resultSet.getInt("hp_scale"),
                        resultSet.getInt("base_attack"),
                        resultSet.getInt("attack_scale"),
                        resultSet.getInt("base_exp"),
                        resultSet.getInt("exp_scale"),
                        resultSet.getInt("defense"),
                        resultSet.getInt("defense_scale"));

                return enemyData;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getBounty(UUID uuid) {
        try (Connection conn = getConnection();
                PreparedStatement statement = conn.prepareStatement("SELECT bounty FROM bounties WHERE uuid = ?")) {

            statement.setString(1, uuid.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("bounty");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static void setBounty(UUID uuid, int bounty) {
        try (Connection conn = getConnection();
                PreparedStatement statement = conn
                        .prepareStatement("INSERT OR REPLACE INTO bounties (uuid, bounty) VALUES (?, ?)")) {

            statement.setString(1, uuid.toString());
            statement.setInt(2, bounty);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean shouldGiveCustomBook(UUID uuid) {
        try (Connection conn = getConnection();
                PreparedStatement statement = conn
                        .prepareStatement("SELECT start_book FROM player_config WHERE uuid = ?")) {

            statement.setString(1, uuid.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean("start_book");
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean shouldGiveGlytchPotion(UUID uuid) {
        try (Connection conn = getConnection();
                PreparedStatement statement = conn
                        .prepareStatement("SELECT start_glytch FROM player_config WHERE uuid = ?")) {

            statement.setString(1, uuid.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean("start_glytch");
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void updatePlayerSetting(UUID uuid, String setting, boolean value) {
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn
                        .prepareStatement("UPDATE player_config SET " + setting + " = ? WHERE uuid = ?")) {

            pstmt.setBoolean(1, value);
            pstmt.setString(2, uuid.toString());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int addBounty(UUID uuid, int amount) {
        int currentBounty = getBounty(uuid);
        int newBounty = currentBounty + amount;
        setBounty(uuid, newBounty);
        return newBounty;
    }

    
    // Other database-related methods go here
}
