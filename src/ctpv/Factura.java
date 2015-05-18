/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ctpv;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author chovi_000
 */
public class Factura {

    String ficherofactura;
    DefaultTableModel modelo;
    BigDecimal big;
    String terminal;
    ArrayList<String> listapedido;

    //Constructor
    public Factura(String ficherofactura, DefaultTableModel modelo, BigDecimal big, String terminal) {
        this.ficherofactura = ficherofactura;
        this.modelo = modelo;
        this.big = big;
        this.terminal = terminal;
//        escribirEnConsola();//escribe en consola el resultado
        escribirEnFichero();//escribe la factura en el archivo
    }

    
    private void transformarTablaEnArrayList(){
        listapedido=new ArrayList<>();
        
        for (int i=0;i<modelo.getRowCount();i++){
            listapedido.add(modelo.getValueAt(i, 0).toString()+ " x " +String.format("%-20s", modelo.getValueAt(i, 1).toString()) +  String.format("%-10s", modelo.getValueAt(i, 2).toString()) + "\t" + modelo.getValueAt(i, 3).toString());
        }
    }
    
    //método para escribir en el fichero
    private void escribirEnFichero() {
        transformarTablaEnArrayList();
        if (big == null) {  //para que no salga null
            big = new BigDecimal(0);
        }
        Date fecha = new Date();
        try { //escribimos en el fichero. 
            FileWriter fw = new FileWriter(ficherofactura, true);
            BufferedWriter bw = new BufferedWriter(fw);

           // bw.write("\n");
            bw.write("---------------------------------------------\n");
            bw.write(" " + terminal + " Fecha " + fecha + "\n");
            bw.write("---------------------------------------------\n");           
            
            for (int i = 0; i < listapedido.size(); i++) {//leemos del DefaultModelList
                bw.write(listapedido.get(i) + "\n");
            }
            bw.write("---------------------------------------------\n");            
            bw.flush();
            bw.close();
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(Factura.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


}
