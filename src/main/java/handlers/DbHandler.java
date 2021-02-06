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

    private void configDataBase() throws SQLException {
        Statement st = connection.createStatement();
//        st.execute("PRAGMA journal_mode = \"WAL\";");
//        st.execute("PRAGMA busy_timeout = 5000;");
        st.executeUpdate("CREATE TABLE if not exists 'all_statistics' ('word' text primary key, count int default 1)");
        connection.setAutoCommit(false);
        connection.commit();
    }



    public Statement createNewStatement(String tempTableName) {
        Statement st = null;
        try {
            st = connection.createStatement();
            CreateTempTable(st, tempTableName);
        } catch (SQLException e) {
            errorHandling(e);
        }
        return st;
    }

    private void CreateTempTable(Statement st, String tempTableName) throws SQLException {
        st.executeUpdate("CREATE TABLE if not exists '" + tempTableName + "' ('word' text primary key, count int default 1)");
        close(st);
    }

    public void commit() {
        try{
            connection.commit();
        }catch (SQLException e) {
            errorHandling(e);
        }
    }

    public void close(Statement st) {
        try{
            st.close();
        }catch (SQLException e) {
            errorHandling(e);
        }
    }

    public Map<String, Integer> getAllWords(String tempTableName) {
        LinkedHashMap<String, Integer> wordToCount = new LinkedHashMap<String, Integer>();
        try (Statement st = connection.createStatement()) {
            ResultSet resultSet = st.executeQuery("SELECT word, count FROM last_statistics ORDER BY count DESC, word ASC");
            while (resultSet.next()) {
                wordToCount.put(resultSet.getString("word"), resultSet.getInt("count"));
            }
            close(st);
            return wordToCount;
        } catch (SQLException e) {
            errorHandling(e);
            return Collections.emptyMap();
        }
    }

    public void printAllStatistics() {
        try (Statement st = connection.createStatement()) {
            ResultSet resultSet = st.executeQuery("SELECT word, count FROM 'all_statistics' ORDER BY count DESC, word ASC");
//            System.out.println("Всего слов: " + resultSet.getInt("all_count"));
            //TODO доделать
            while (resultSet.next()) {
                System.out.println(resultSet.getString("word") + " : " + resultSet.getInt("count"));
            }
            close(st);
        } catch (SQLException e) {
            errorHandling(e);
        }
    }

    // Добавление продукта в БД
    public void addProduct(Statement st,String tempTableName,  String word) {
        try {
            count = count + st.executeUpdate("INSERT INTO 'last_statistics' ('word') VALUES('" + word + "') " +
                    "on conflict (word) do update set count = count + 1;");
            st.executeUpdate("INSERT INTO 'all_statistics' ('word') VALUES('" + word + "') " +
                    "on conflict (word) do update set count = count + 1;");
        } catch (SQLException e) {
            errorHandling(e);
        }
    }

    public void clearLastStatistics(String tempDatabaseName) {
        try (Statement st = connection.createStatement()){
            st.execute("DROP TABLE IF EXISTS last_statistics");
            connection.commit();
        } catch (SQLException e) {
            errorHandling(e);
        }
    }

    public boolean notEmpty() {
        return count > 0;
    }

    private void errorHandling(SQLException e) {
        e.printStackTrace();
        Log.severe(this, e.toString());
    }
}