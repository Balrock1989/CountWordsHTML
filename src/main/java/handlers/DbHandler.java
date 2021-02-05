package handlers;

import org.sqlite.JDBC;
import util.Log;

import java.nio.file.Paths;
import java.sql.*;


public class DbHandler {
    private static final String CON_STR = "jdbc:sqlite:"
            + Paths.get(System.getProperty("user.dir"), "src", "main","java","db", "word_statistics.db").toString();
    private static DbHandler instance = null;
    public static Statement statmt;
    private static Connection connection;

    public static synchronized DbHandler getInstance() throws SQLException {
        try {
            if (instance == null)
                instance = new DbHandler();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return instance;
    }

    private DbHandler() throws SQLException {
        DriverManager.registerDriver(new JDBC());
        connection = DriverManager.getConnection(CON_STR);
//        connection.setAutoCommit(false);
        statmt = connection.createStatement();
//        statmt.execute("PRAGMA journal_mode = WAL;");
        statmt.execute("PRAGMA encoding = \"UTF-8\";");
        CreateTables();


    }

    public void CreateTables() throws SQLException {
//        statmt.execute("CREATE TABLE if not exists 'statistics' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'word' text, 'count' INT);");
        statmt.execute("CREATE TABLE if not exists 'all_statistics' ('word' text primary key, count int default 1)");
        statmt.execute("CREATE TABLE if not exists 'last_statistics' ('word' text primary key, count int default 1)");
    }

    public void commit() {
        try {
            connection.commit();
        } catch (SQLException e){
            Log.severe(this, e.toString());
        }
    }

    public void getAllProducts() {

//        // Statement используется для того, чтобы выполнить sql-запрос
//        try (Statement statement = this.connection.createStatement()) {
//            // В данный список будем загружать наши продукты, полученные из БД
//            List<Product> products = new ArrayList<Product>();
//            // В resultSet будет храниться результат нашего запроса,
//            // который выполняется командой statement.executeQuery()
//            ResultSet resultSet = statement.executeQuery("SELECT id, good, price, category_name FROM products");
//            // Проходимся по нашему resultSet и заносим данные в products
//            while (resultSet.next()) {
//                products.add(new Product(resultSet.getInt("id"),
//                        resultSet.getString("good"),
//                        resultSet.getDouble("price"),
//                        resultSet.getString("category_name")));
//            }
//            // Возвращаем наш список
//            return products;
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            // Если произошла ошибка - возвращаем пустую коллекцию
//            return Collections.emptyList();
//        }
    }

    // Добавление продукта в БД
    public void addProduct(String word) {
//        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO 'statistics' ('word', 'count') VALUES(?, ?)")){
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO 'last_statistics' ('word') VALUES(?) " +
                "on conflict (word) do update set count = count + 1;")){
            statement.setObject(1, word);
//            statement.setObject(2, count);
            statement.execute();
        } catch (SQLException e) {
            Log.severe(this, e.toString());
            }
    }

    // Удаление продукта по id
    public void deleteProduct(int id) {
        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM Products WHERE id = ?")) {
            statement.setObject(1, id);
            // Выполняем запрос
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}