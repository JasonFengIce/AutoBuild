package cn.ismartv.autobuild.login;

import sun.rmi.runtime.Log;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

/**
 * Created by huaijie on 3/24/16.
 */
@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        boolean isLoginSuccess = checkUsernameAndPassword(username, password);
        System.out.println("login:  " + isLoginSuccess);
    }

    private boolean checkUsernameAndPassword(String username, String password) {

        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:/Users/huaijie/test.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS person (id INTEGER, username string, password)");
            ResultSet rs = statement.executeQuery("SELECT * FROM person");
            while (rs.next()) {
                // read the result set
                String name = rs.getString("username");
                String pwd = rs.getString("password");
                System.out.println("name = " + name);
                System.out.println("pwd = " + pwd);
            }
            System.out.println(rs.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return true;
    }


    private void checkUserIsExistOrNot() {

    }


    private void sqliteTest() {
        Connection connection = null;
        try {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:/Users/huaijie/test.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            statement.executeUpdate("DROP TABLE IF EXISTS person");
            statement.executeUpdate("CREATE TABLE person (id INTEGER, name string)");
            statement.executeUpdate("INSERT INTO person VALUES(1, 'leo')");
            statement.executeUpdate("INSERT INTO person VALUES(2, 'yui')");
            ResultSet rs = statement.executeQuery("SELECT * FROM person");
            while (rs.next()) {
                // read the result set
                System.out.println("name = " + rs.getString("name"));
                System.out.println("id = " + rs.getInt("id"));
            }
        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e);
            }
        }

    }
}
