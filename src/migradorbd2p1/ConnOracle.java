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
 * @author Kevin Arnold
 */
public class ConnOracle {
    private static String usuarioDB="dan";
    private static String passwordDB="1234";
    private static String serv="XE";
    public String getUsuarioDB() {
        return usuarioDB;
    }

    public static void setUsuarioDB(String usuarioDB) {
        ConnOracle.usuarioDB = usuarioDB;
    }

    public static String getPasswordDB() {
        return passwordDB;
    }

    public static void setPasswordDB(String passwordDB) {
        ConnOracle.passwordDB = passwordDB;
    }
    
    
    
    public static Connection GetConnection()
    {
        Connection conexion=null;
      
        try
        {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            String servidor = "jdbc:oracle:thin:@localhost:1521:"+serv;
            
            System.out.println(servidor+usuarioDB+passwordDB+serv);
            conexion= DriverManager.getConnection(servidor,usuarioDB,passwordDB);
            
        }
        catch(ClassNotFoundException ex)
        {
            JOptionPane.showMessageDialog(null, ex, "Error1 en la Conexión con la BD "+ex.getMessage()+"\n", JOptionPane.ERROR_MESSAGE);
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

    public static String getServ() {
        return serv;
    }

    public static void setServ(String serv) {
        ConnOracle.serv = serv;
    }
}