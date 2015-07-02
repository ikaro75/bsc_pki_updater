/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.org.fide.configuracion;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import mx.org.fide.archivo.Archivo; 
import mx.org.fide.modelo.Fallo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Daniel
 */
public class Configuracion {
    private LinkedHashMap parametros = new LinkedHashMap();
    private String configuracionActual;
    
    public Configuracion(String archivo) throws Fallo{
        try {
            /*ClassLoader classLoader = Thread.currentThread().getContextClassgetClass().getClassLoader().getResource(".").getPath()Loader();
            URL url = classLoader.getResource("com/administrax/configuracion"); 
            * String path = ;
            * */
            File xml = new File(archivo);

            //Eliminar esta linea cuando entre a producción
            //xml = new File("configuracion.xml");
            
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xml);
            doc.getDocumentElement().normalize();
            NodeList nlConfiguracionEmail = doc.getElementsByTagName("configuracion_email");
            
            if (nlConfiguracionEmail.getLength()==0) {
                    throw new Fallo("Faltan nodos en la etiqueta <configuracion_email>");
            }
            
            Node nConfiguracionEmail = nlConfiguracionEmail.item(0);
            if (nConfiguracionEmail.getNodeType() != Node.ELEMENT_NODE) {
                    throw new Fallo("Tipo de nodo no válido <configuracion_email>");
            }
            
            Element eConfiguracionEmail = (Element) nConfiguracionEmail;
            parametros.put("iniciotls_habilitado", getValue("iniciotls_habilitado", eConfiguracionEmail));
            parametros.put("servidor_smtp", getValue("servidor_smtp", eConfiguracionEmail));
            parametros.put("puerto_smtp", getValue("puerto_smtp", eConfiguracionEmail));
            parametros.put("usuario_smtp", getValue("usuario_smtp", eConfiguracionEmail));
            parametros.put("password_smtp", getValue("password_smtp", eConfiguracionEmail));

            NodeList nlDb = doc.getElementsByTagName("db");
            
            if (nlDb.getLength() == 0) {
                    throw new Fallo("Falta nodo db, verifique");
            }
            
            Element eDb = (Element) nlDb.item(0);
            
            parametros.put("password_smtp", getValue("servidor", eDb));
            parametros.put("db_server", getValue("servidor", eDb));
            parametros.put("db_name", getValue("nombre", eDb));
            parametros.put("db_user", getValue("usuario", eDb));
            parametros.put("db_pw", getValue("password", eDb));
            parametros.put("db_type", getValue("tipo", eDb));      
                           
        } catch (FileNotFoundException  fe) {
            throw new Fallo(fe.getMessage());
        } catch (Exception ex) {
            throw new Fallo(ex.getMessage());
        } 
    }
    
    private static String getValue(String tag, Element element) throws Fallo {
        if (element.getElementsByTagName(tag).item(0)==null) {
            throw new Fallo("No se encontró la etiqueta ".concat(tag.toUpperCase()).concat(" en el documento XML"));
        }
        NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodes.item(0);
        
        if (node==null)
            return null;
        else            
            return node.getNodeValue();
    }
    
    public LinkedHashMap getParametros() {
        return parametros;
    }

    public void setParametros(LinkedHashMap parametros) {
        this.parametros = parametros;
    }

    public String getConfiguracionActual() {
        return configuracionActual;
    }

    public void setConfiguracionActual(String configuracionActual) {
        this.configuracionActual = configuracionActual;
    }
    
}
