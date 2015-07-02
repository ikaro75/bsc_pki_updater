/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.org.fide.daemon;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.org.fide.configuracion.Configuracion;
import mx.org.fide.modelo.Conexion;
import mx.org.fide.modelo.Fallo;

/**
 *
 * @author daniel.martinez
 */
public class Daemon {
    
    private Configuracion configuracion = null;
    private Conexion cx = null;

    public Daemon(String archivo) throws Fallo {
        try {
            System.out.println("Cargando configuracion...");
            configuracion = new Configuracion(archivo);
        } catch (Fallo ex) {
            System.out.print(ex.getMessage());
            Logger.getLogger(Daemon.class.getName()).log(Level.SEVERE, null, ex);
            throw new Fallo(ex.getMessage());
        }
    }
   
    public void actualizaIndicadores() {
        ResultSet rs;
        ResultSet rsValorIndicador;
        StringBuilder q = new StringBuilder();
        StringBuilder resultadoXML = new StringBuilder();
        Conexion oDb = new Conexion(this.configuracion.getParametros().get("db_server").toString(),
                                   this.configuracion.getParametros().get("db_name").toString(),
                                   this.configuracion.getParametros().get("db_user").toString(),
                                   this.configuracion.getParametros().get("db_pw").toString(),
                                   Conexion.DbType.valueOf(this.configuracion.getParametros().get("db_type").toString()));
                
        Conexion oDbIndicador = null;
        //1. recupera todos los indicadores
        q = new StringBuilder("SELECT clave_indicador, clave_origen_dato, consulta_actualizacion, indicador FROM fw_scorecard_indicador WHERE clave_tipo_indicador=2 AND clave_tipo_actualizacion=1 AND NOT consulta_actualizacion IS NULL");
        try {
            System.out.println("Extrayendo indicadores de la base de datos...");
            rs = oDb.getRs(q.toString());
            while (rs.next()) {
                System.out.print("\nActualizando indicador [".concat(rs.getString("indicador")).concat("]..."));
                if (rs.getInt("clave_origen_dato") == 0) {
                    //Se toma la conexión por default 
                    oDbIndicador = oDb;
                } else {
                    q = new StringBuilder("SELECT * FROM be_origen_dato WHERE clave_origen_dato=").append(rs.getInt("clave_origen_dato"));
                    rsValorIndicador = oDb.getRs(q.toString());

                    if (!rsValorIndicador.next()) {
                        System.out.println("\nERROR: No se encontró el origen de datos del indicador [".concat(rs.getString("indicador")).concat("]..."));                        
                    } else {
                        oDbIndicador = new Conexion(rsValorIndicador.getString("servidor").concat(":").concat(rsValorIndicador.getString("puerto")),
                                rsValorIndicador.getString("db"),
                                rsValorIndicador.getString("login"),
                                rsValorIndicador.getString("pw"),
                                Conexion.DbType.values()[rsValorIndicador.getInt("clave_tipo_db")]);
                    }
                }
                
                rsValorIndicador = oDbIndicador.getRs(rs.getString("consulta_actualizacion"));
                
                if (rsValorIndicador.next()) {
                    q = new StringBuilder("UPDATE fw_scorecard_indicador SET valor_actual=").append(rsValorIndicador.getDouble(1)).append(" WHERE clave_indicador=").append(rs.getInt("clave_indicador"));
                    System.out.print(" Actualizado!");
                }  else {
                    System.out.println("\nERROR: La consulta que calcula el valor del indicador [".concat(rs.getString("indicador")).concat("] está vacía"));                    
                }  
                
            }
            
            System.out.println("\n\nActualización finalizada exitosamente.");
            
        } catch (Fallo ex) {
            System.out.println("\nERROR: ".concat(ex.getMessage()));
        } catch (SQLException ex) {
            System.out.println("\nERROR: ".concat(ex.getMessage()));
        }
    }
            
    public static void main(String[] args) {

        try {
            if(args.length == 0) {
                System.out.println("Se requiere especificar el archivo de configuracion");
                System.exit(0);
            }    
            Daemon d = new Daemon(args[0]); //ReportSender -"FIDE produccion"
            d.actualizaIndicadores();
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
            Logger.getLogger(Daemon.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
