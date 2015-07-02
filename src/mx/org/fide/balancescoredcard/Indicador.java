/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.org.fide.balancescoredcard;

import mx.org.fide.modelo.Conexion;
import mx.org.fide.modelo.Fallo;
import java.sql.ResultSet;
import java.sql.SQLException;
import mx.org.fide.modelo.Conexion.DbType;

public class Indicador  {

    Integer claveIndicador;
    Integer claveIndicadorPadre;
    String indicador;
    String descripcion;
    String departamento;
    Integer claveTipoIndicador;
    Integer diasFrecuenciaRastreo;
    Double valorActual;
    String formato;
    Integer claveTipoActualizacion;
    Integer claveOrigenDato;
    String consultaActualizacion;
    Integer claveFormaDetalle;
    Integer porcentajePeso;
    Integer orden;

    public Indicador() {
    }

    public Indicador(Integer claveIndicador) {
        this.claveIndicador = claveIndicador;
    }

}
