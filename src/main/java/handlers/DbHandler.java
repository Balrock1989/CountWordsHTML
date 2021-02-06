package handlers;

import org.sqlite.JDBC;
import util.Log;

import java.nio.file.Paths;
import java.sql.*;
import java.util.*;


public class DbHandler {
    private static final String CON_STR = "jdbc:sqlite:"
            + Paths.get(System.getProperty("user.dir"), "src", "main","java","db", "word_statistics.db").toString();
    private static DbHandler instance = null;
    private static Connection connection;
    private int count = 0;

    public static synchronized DbHandler getInstance(){
        try {
            if (instance == null)
                instance = new DbHandler();
        }
        catch (SQLException e){
            e.printStackTrace();
            Log.severe(DbHandler.class, e.toString());
        }
        return instance;
    }

    private DbHandler() throws SQLException {
        DriverManager.registerDriver(new JDBC());
        connection = DriverManager.getConnection(CON_STR);
        connection.setAutoCommit(false);
        CreateTables();
//        Statement st = connection.createStatement();
//        st.execute("PRAGMA journal_mode = \"WAL\";");
//        st.execute("PRAGMA encoding = \"UTF-8\";");
//        connection.commit();
    }

    public void CreateTables() throws SQLException {
        Statement st = connection.createStatement();
        st.executeUpdate("CREATE TABLE if not exists 'all_statistics' ('word' text primary key, count int default 1)");
        st.executeUpdate("CREATE TABLE if not exists 'last_statistics' ('word' text primary key, count int default 1)");
        connection.commit();
    }

    public Statement createNewStatement() {
        Statement st = null;
        try {
            st = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            Log.severe(this, e.toString());
        }
        return st;
    }

    public void commit() {
        try{
            connection.commit();
        }catch (SQLException e) {
            e.printStackTrace();
            Log.severe(this, e.toString());
        }
    }

    public Map<String, Integer> getAllWords() {
        LinkedHashMap<String, Integer> wordToCount = new LinkedHashMap<String, Integer>();
        try (Statement st = connection.createStatement()) {
            ResultSet resultSet = st.executeQuery("SELECT word, count FROM last_statistics ORDER BY count DESC, word ASC");
            while (resultSet.next()) {
                wordToCount.put(resultSet.getString("word"), resultSet.getInt("count"));
            }
            st.close();
            return wordToCount;
        } catch (SQLException e) {
            e.printStackTrace();
            Log.severe(this, e.toString());
            return Collections.emptyMap();
        }
    }

    // Добавление продукта в БД
    public void addProduct(Statement st, String word) {
        try {
            count = count + st.executeUpdate("INSERT INTO 'last_statistics' ('word') VALUES('" + word + "') " +
                    "on conflict (word) do update set count = count + 1;");
            st.executeUpdate("INSERT INTO 'all_statistics' ('word') VALUES('" + word + "') " +
                    "on conflict (word) do update set count = count + 1;");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.severe(this, e.toString());
        }
    }

    public void clearLastStatistics() {
        try (Statement st = connection.createStatement()){
            st.execute("DROP TABLE IF EXISTS last_statistics");
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            Log.severe(this, e.toString());
        }
    }

    public boolean notEmpty() {
        return count > 0;
    }
}