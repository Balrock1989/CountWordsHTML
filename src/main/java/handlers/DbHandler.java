package handlers;

import org.sqlite.JDBC;
import util.Log;

import java.nio.file.Paths;
import java.sql.*;
import java.util.*;

import static java.util.stream.Collectors.*;

/*** Класс для работы с базой данных*/
public class DbHandler {
    private static final String CON_STR = "jdbc:sqlite:"
            + Paths.get(System.getProperty("user.dir"), "src", "main", "java", "db", "word_statistics.db").toString();
    private static Connection connection;
    private static DbHandler instance = null;

    public static synchronized DbHandler getInstance() {
        try {
            if (instance == null)
                instance = new DbHandler();
        } catch (SQLException e) {
            e.printStackTrace();
            Log.severe(DbHandler.class, e);
        }
        return instance;
    }

    private DbHandler() throws SQLException {
        DriverManager.registerDriver(new JDBC());
        connection = DriverManager.getConnection(CON_STR);
        configDataBase();
    }

    private void configDataBase() throws SQLException {
        Statement st = connection.createStatement();
        st.execute("PRAGMA busy_timeout = 5000;");
        st.execute("PRAGMA journal_mode = \"WAL\";");
        st.executeUpdate("CREATE TABLE if not exists 'all_statistics' ('word' text primary key, count int default 1)");
        connection.setAutoCommit(false);
        connection.commit();
    }

    public void createTempTable(String tempTableName) {
        try (Statement st = connection.createStatement()) {
            st.executeUpdate("CREATE TEMP TABLE if not exists '" + tempTableName + "' ('word' text primary key, count int default 1)");
        } catch (SQLException e) {
            errorHandling(e);
        }
    }

    public Map<String, Integer> getAllWords(String tempTableName) {
        LinkedHashMap<String, Integer> wordToCount = new LinkedHashMap<String, Integer>();
        try (Statement st = connection.createStatement()) {
            ResultSet resultSet = st.executeQuery("SELECT word, count FROM '" + tempTableName + "' ORDER BY count DESC, word ASC");
            while (resultSet.next()) {
                wordToCount.put(resultSet.getString("word"), resultSet.getInt("count"));
            }
            st.close();
            return wordToCount;
        } catch (SQLException e) {
            errorHandling(e);
            return Collections.emptyMap();
        }
    }

    public int getCountForWords(String... words) {
        String request = Arrays.stream(words).map(s -> "'" + s + "'").collect(joining(","));
        return getCount("SELECT sum(count) as count FROM 'all_statistics' WHERE word IN (" + request + ")");
    }

    public void printAllStatistics(int limit) {
        try (Statement st = connection.createStatement()) {
            connection.commit();
            ResultSet resultSet = st.executeQuery("SELECT word, count FROM 'all_statistics' ORDER BY count DESC, word ASC LIMIT " + limit);
            System.out.println(limit + " the most common words from the general statistics:");
            while (resultSet.next()) {
                System.out.println(resultSet.getString("word") + " : " + resultSet.getInt("count"));
            }
        } catch (SQLException e) {
            errorHandling(e);
        }
    }

    public void addProduct(String tempTableName, String word) {
        try (Statement st = connection.createStatement()) {
            st.executeUpdate("INSERT INTO '" + tempTableName + "' ('word') VALUES('" + word + "') " +
                    "on conflict (word) do update set count = count + 1;");
            st.executeUpdate("INSERT INTO 'all_statistics' ('word') VALUES('" + word + "') " +
                    "on conflict (word) do update set count = count + 1;");
        } catch (SQLException e) {
            errorHandling(e);
        }
    }

    public void clearTempTable(String tempTableName) {
        try (Statement st = connection.createStatement()) {
            st.execute("DROP TABLE IF EXISTS '" + tempTableName + "'");
        } catch (SQLException e) {
            errorHandling(e);
        }
    }

    public boolean notEmpty(String tempTableName) {
        int count = getCount("SELECT count(word) as count FROM '" + tempTableName + "'");
        return count != 0;
    }

    private int getCount(String query) {
        int count = 0;
        try (Statement st = connection.createStatement()) {
            ResultSet resultSet = st.executeQuery(query);
            while (resultSet.next()) {
                count = resultSet.getInt("count");
            }
            st.close();
            return count;
        } catch (SQLException e) {
            errorHandling(e);
            return count;
        }
    }

    private void errorHandling(SQLException e) {
        e.printStackTrace();
        Log.severe(DbHandler.class, e);
    }
}