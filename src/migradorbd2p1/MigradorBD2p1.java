/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package migradorbd2p1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MigradorBD2p1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // TODO code application logic here
            Connection conn = ConnSQLServer.GetConnection();
            String sql = "SELECT * FROM tbusuarios";
            PreparedStatement ps;
            ResultSet rs;
            
            ps = conn.prepareStatement(sql);
            
            rs = ps.executeQuery();
            
            while(rs.next()){
                System.out.println(rs.getString(1)+ " " + rs.getString(2));
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(MigradorBD2p1.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
    }
    
}
