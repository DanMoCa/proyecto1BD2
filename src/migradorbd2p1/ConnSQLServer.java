/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package migradorbd2p1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author DanDesktop
 */
public class ConnSQLServer {
    public static Connection GetConnection()
    {
        Connection conexion=null;
      
        try
        {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String servidor = "jdbc:sqlserver://localhost:1433;";
//<<<<<<< HEAD

            
/*
=======
>>>>>>> origin/master
            String dbName = "databaseName=HR;";
            String usuarioDB="user=dan;";
            String passwordDB="password=1234;";
*/

            conexion= DriverManager.getConnection(servidor+dbName+usuarioDB+passwordDB);
        }
        catch(ClassNotFoundException ex)
        {
            JOptionPane.showMessageDialog(null, ex, "Error1 en la Conexión con la BD "+ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            conexion=null;
        }
        catch(SQLException ex)
        {
            JOptionPane.showMessageDialog(null, ex, "Error2 en la Conexión con la BD "+ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            conexion=null;
        }
        catch(Exception ex)
        {
            JOptionPane.showMessageDialog(null, ex, "Error3 en la Conexión con la BD "+ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            conexion=null;
        }
        finally
        {
            return conexion;
        }
    }
    
    private static String usuarioDB="user=HR;";
    /*dbName = "databaseName=danmoca;";
            usuarioDB="user=dan;";
            passwordDB="password=1234;";*/
    private static String passwordDB="password=1234;";
    private static String dbName="databaseName=danmoca;";
    public static String getUsuarioDB() {
        return usuarioDB;
    }

    public static void setUsuarioDB(String usuarioDB) {
        ConnSQLServer.usuarioDB = "user="+usuarioDB+";";
    }

    public static String getPasswordDB() {
        return passwordDB;
    }

    public static void setPasswordDB(String passwordDB) {
        ConnSQLServer.passwordDB = "password="+passwordDB+";";
    }

    public static String getDbName() {
        return dbName;
    }

    public static void setDbName(String db) {
        ConnSQLServer.dbName = "databaseName="+db+";";
    }
    
}
