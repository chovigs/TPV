/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ctpv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;

/**
 *
 * @author chovi_000
 */
public class Factura {

    String ficherofactura;
    DefaultListModel modelo;
    BigDecimal big;
    String terminal;

    //Constructor
    public Factura(String ficherofactura, DefaultListModel modelo, BigDecimal big, String terminal) {
        this.ficherofactura = ficherofactura;
        this.modelo = modelo;
        this.big = big;
        this.terminal = terminal;
        escribirEnConsola();//escribe en consola el resultado
        escribirEnFichero();//escribe la factura en el archivo
    }

    //método para escribir en el fichero
    private void escribirEnFichero() {
        if (big == null) {  //para que no salga null
            big = new BigDecimal(0);
        }
        Date fecha = new Date();
        try { //escribimos en el fichero. 
            FileWriter fw = new FileWriter(ficherofactura, true);
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write("\n");
            bw.write(" " + terminal + " Fecha " + fecha + "\n");
            bw.write("---------------------------------------------\n");
           
            bw.write("  PRODUCTO          PRECIO         SUBTOTAL  \n");
            bw.write("---------------------------------------------\n");
            
            for (int i = 0; i < modelo.size(); i++) {//leemos del DefaultModelList
                bw.write(modelo.elementAt(i).toString() + "\n");
            }
            bw.write("---------------------------------------------\n");
            bw.write("                    Total  " + big + " € \n");
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(Factura.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //para que salga en la consola
    private void escribirEnConsola() {
        Date fecha = new Date();
        System.out.println("");
        System.out.print(" " + terminal + " Fecha " + fecha + "\n");
        System.out.print("---------------------------------------------\n");        
        System.out.print("  PRODUCTO          PRECIO         SUBTOTAL  \n");
        System.out.print("---------------------------------------------\n");
   
        for (int i = 0; i < modelo.size(); i++) {
            System.out.print(modelo.elementAt(i).toString() + "\n");
        }
        System.out.print("---------------------------------------------\n");
        System.out.print("                    Total  " + big + " € \n");
    }

    //si queremos que salga un mensaje
    public String option() {
        Date fecha = new Date();
        String op = " " + terminal + " Fecha " + fecha + "\n" + "---------------------------------------------\n"+ "  PRODUCTO          PRECIO         SUBTOTAL  \n"+"---------------------------------------------\n";
        for (int i = 0; i < modelo.size(); i++) {
            op = op + (modelo.elementAt(i).toString() + "\n");
        }
        op = op + "---------------------------------------------\n" + "                    Total  " + big + " € \n";
        return op;
    }
}
