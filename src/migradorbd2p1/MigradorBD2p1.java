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
import javax.swing.JOptionPane;
import java.sql.CallableStatement;
import java.util.Vector;
import oracle.jdbc.OracleTypes;

public class MigradorBD2p1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // TODO code application logic here
            if(JOptionPane.showConfirmDialog(null, "¿Es usted el usuario dan?")!=0)
            {
                
                ConnOracle.setServ(JOptionPane.showInputDialog(null, "Inserte el valor del servicio"));
                ConnOracle.setUsuarioDB(JOptionPane.showInputDialog(null, "Inserte el usuario"));
                ConnOracle.setPasswordDB(JOptionPane.showInputDialog(null, "Inserte la contraseña"));
            
            }
            Connection conn = ConnOracle.GetConnection();
            String sql = "SELECT table_name FROM user_tables";
            PreparedStatement ps;
            ResultSet rs,rs2;
            
            CallableStatement ini;
            ps = conn.prepareStatement(sql);
            
            rs = ps.executeQuery();
            
            while(rs.next()){
                String tabla=rs.getString(1);
                String descripcion="";
                String sql2="Select COLUMN_NAME from user_tab_columns where upper(table_name) =upper('"+rs.getString(1)+"')";
                ps=conn.prepareStatement(sql2);
                rs2=ps.executeQuery(sql2);
                System.out.println("+++"+rs.getString(1)+"+++");
                System.out.println(rs.getString(1));
                while(rs2.next())
                {
                    System.out.println(rs2.getString(1));
                    String call="{? = call estruct_columnu(?,?,?)}";
                    ini=conn.prepareCall(call);
                    ini.registerOutParameter(1, OracleTypes.VARCHAR);
                    ini.setString(2, rs.getString(1));
                    ini.setString(3, rs2.getString(1));
                    ini.setString(4, ConnOracle.getUsuarioDB());
                    ini.execute();
                    System.out.print(" estruct "+ini.getString(1));
                    System.out.println();
                    descripcion+=ini.getString(1)+"/";
                    
                }
                
                //nivel de tabla
                descripcion=descripcion.substring(0, descripcion.length()-1);
                System.out.println("\n"+tabla+"\n"+descripcion+"\n");
                MigradorBD2p1.crearTabla(tabla, descripcion);
            }
            //conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(MigradorBD2p1.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
    }
    
    public static void crearTabla(String tabla,String desc)
    {
        String upd="CREATE TABLE "+tabla+" (";
        String[] columnas=desc.split("/");
        String[] datos;
        for(int i = 0;i<columnas.length;i++)
        {
            datos=columnas[i].split(" ");
            //primer dato nombre de columna
            //segundo dato tipo de dato
            //tercer dato longitud del dato}+
            String tipo="";
            switch(datos[1])
            {
                case "NUMBER":
                    try {
                        CallableStatement chk;
                        String call="{? = call GET_DSCALEU(?,?,?)}";
                        chk =ConnOracle.GetConnection().prepareCall(call);
                        chk.registerOutParameter(1, OracleTypes.NUMBER);
                        chk.setString(2, tabla);
                        chk.setString(3, datos[0]);
                        chk.setString(4, ConnOracle.getUsuarioDB());

                        chk.execute();
                        System.out.println("\n**"+chk.getInt(1)+"**\n");
                        tipo=tratarNumber(chk.getInt(1));
                    } catch (SQLException ex) {
                        Logger.getLogger(MigradorBD2p1.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                default:
                    tipo=tratar(datos[1],datos[2]);
            }/*
            if(datos[1]!="NUMBER")
                tipo=tratar(datos[1],datos[2]);
            else
            {
                try {
                    CallableStatement chk;
                    String call="? = call GET_DSCALE(?,?)";
                    chk =ConnOracle.GetConnection().prepareCall(call);
                    chk.registerOutParameter(1, OracleTypes.NUMBER);
                    chk.setString(2, tabla);
                    chk.setString(3, datos[0]);
                    chk.execute();
                    System.out.println("\n**"+chk.getInt(1)+"**\n");
                    tipo=tratarNumber(chk.getInt(1));
                } catch (SQLException ex) {
                    Logger.getLogger(MigradorBD2p1.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            */
            if((i+1)!=columnas.length)
                upd+=datos[0]+" "+tipo+",";
            else
            {
                upd+=datos[0]+" "+tipo;
            }
        }
        
        upd+=")";
        
        System.out.println("\n"+upd+"\n");
    }

    private static String tratar(String tipo, String tamaño) {
        String res="";
        switch(tipo)
        {
            case "CHAR":
            case "VARCHAR":
                res=tipo+"("+tamaño+")";
                break;
            case "VARCHAR2":
                res="VARCHAR"+"("+tamaño+")";
                break;
            case "DATE":
                res="DATETIME";
                break;
            case "FLOAT":
                res = tipo;
                break;
                
        }
        
        return res.toUpperCase();
    }

    private static String tratarNumber(int aInt) {
        if(aInt!=0)
        {
            return "FLOAT";
        }
        else
        {
            return "INTEGER";
        }
    }
    
    private void selectsInserts()
    {
        //obtener tipos de datos
        Vector tipos;
        tipos = new Vector(10);
        
    }
    
}
