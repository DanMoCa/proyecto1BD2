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
                
                ConnSQLServer.setDbName(JOptionPane.showInputDialog(null, "Inserte el valor de la Base de datos"));
                ConnSQLServer.setUsuarioDB(JOptionPane.showInputDialog(null, "Inserte el usuario"));
                ConnSQLServer.setPasswordDB(JOptionPane.showInputDialog(null, "Inserte la contraseña"));  
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
             MigradorBD2p1.selectsInserts();
            //conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(MigradorBD2p1.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
    }
    
    public static void crearTabla(String tabla,String desc)
    {
        String estruct="";
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
                estruct+=datos[0]+" "+tipo+",";
            else
            {
                estruct+=datos[0]+" "+tipo;
            }
        }
        
        estruct+="";
        
        System.out.println("\n"+tabla+" "+estruct+"\n");//modelo de panque
        try {
            crearTabla2(tabla, estruct);
        } catch (SQLException ex) {
            Logger.getLogger(MigradorBD2p1.class.getName()).log(Level.SEVERE, null, ex);
        }
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
    
    private static void selectsInserts()
    {
        Vector<String> tipos;
        
        try {
            //obtener tipos de datos
            
            Connection conn = ConnOracle.GetConnection();
            String sql = "SELECT table_name FROM user_tables";
            PreparedStatement ps;
            ResultSet rs,rs2;
            CallableStatement ini;
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while(rs.next()){//maeja tablas
                String tabla=rs.getString(1);
                tipos = new Vector<String>(10);
                String descripcion="'";
                String sql2="Select DATA_TYPE from user_tab_columns where upper(table_name) =upper('"+rs.getString(1)+"')";
                ps=conn.prepareStatement(sql2);
                rs2=ps.executeQuery(sql2);
                System.out.println(rs.getString(1));
                while(rs2.next())
                {
                    //System.out.println(rs2.getString(1));
                    tipos.add(rs2.getString(1));
                    
                }
                sql2="Select * from "+rs.getString(1);
                ps=conn.prepareStatement(sql2);
                rs2=ps.executeQuery(sql2);
                String values="";
                while(rs2.next())//maneja resultados del select
                {
                    values="";
                    String res="";
                    for(int i = 0;i<tipos.size();i++)
                        
                    {
                        switch(tipos.elementAt(i))
                        {
                            case "CHAR":
                            
                           
                            case "VARCHAR":
                           
                            case "VARCHAR2":
                                res="'"+rs2.getString(i+1)+"'";
                                break;
                            
                            case "DATE":
                            res=""+rs2.getDate(i+1)+"";
                                break;
                            case "FLOAT":
                                res = ""+rs2.getFloat(i+1);
                                break;
                            case "INTEGER":
                                res = ""+rs2.getInt(i+1);
                                break;
                            case "NUMBER":
                                res = ""+rs2.getInt(i+1);
                                break;
                        }
                        values+=res+",";
                        
                    }
                    values=values.substring(0, values.length()-1);
                    
                        values+="";
                        insertarTabla(tabla,values);
                    
                }
                
                
                
                
                //nivel de tabla
                
            }
        } catch (SQLException ex) {
            Logger.getLogger(MigradorBD2p1.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }

    private static void insertar(String tabla, String values) {
        //try {
            System.out.println("Insertando: "+values+" en "+tabla);
            Connection conn;
            conn=ConnSQLServer.GetConnection();

            PreparedStatement ps;
            ResultSet rs,rs2;
            CallableStatement ini;
            
        //} catch (SQLException ex) {
        //    Logger.getLogger(MigradorBD2p1.class.getName()).log(Level.SEVERE, null, ex);
        //}
        
    }
    
    public static void insertarTabla(String tableName, String values) throws SQLException 
    { 
        try
        {
        System.out.println("Insertando: "+values+" en "+tableName);
        Connection con = ConnSQLServer.GetConnection(); 
        CallableStatement cst = null; 
        String call="{call insertIntoTable(?,?)}"; 
        cst = con.prepareCall(call); 
        cst.setString(1, tableName); 
        cst.setString(2, values); 
        cst.execute(); 
        con.commit();
        }
        catch(SQLException a)
        {   
           JOptionPane.showMessageDialog(null, a.getMessage()); 
        }
            
    }
    public static void crearTabla2(String tableName, String values) throws SQLException 
    { 
        try
        {
        Connection con = ConnSQLServer.GetConnection(); 
        CallableStatement cst = null; 
        String call="{call createTable(?,?)}"; 
        cst = con.prepareCall(call); 
        cst.setString(1, tableName); 
        cst.setString(2, values); 
        cst.execute(); 
        con.commit();
        }
        catch(SQLException a)
        {
           JOptionPane.showMessageDialog(null, a.getMessage()+"\n");
        } 
    }
    }


