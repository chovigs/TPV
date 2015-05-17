/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ctpv;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author chovi_000
 */
public class HiloLeeVentas extends Thread {

    String ficherofactura;
    static int ventasAM, ventasPM, numeroLineas;

    public HiloLeeVentas(String ficherofactura) {
        this.ficherofactura = ficherofactura;

        this.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(500);
                leerNumeroLineas();
                System.out.println(" ventas " + numeroLineas);
                System.out.println(" ventasAM " + ventasAM);
                System.out.println(" ventasPM " + ventasPM);
            } catch (InterruptedException ex) {
                Logger.getLogger(HiloLeeVentas.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private void leerNumeroLineas() {
        ventasAM = 0;
        ventasPM = 0;
        numeroLineas = 0;
        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;
        archivo = new File(ficherofactura);
        System.out.println("fichero " + archivo.getPath());
        String linea;

        long tamanioArchivo = archivo.length();//Longitud del fichero

        //Si el tamaño del fichero es mayor a 0, es decir si no esta vacio
        if (tamanioArchivo > 0) {
            try {
                fr = new FileReader(archivo);
                br = new BufferedReader(fr);

                while ((linea = br.readLine())!= null) {
                    linea = "" + linea;
                    comprobarlinea(linea);
                }
                br.close();                
            } catch (FileNotFoundException ex) {
                Logger.getLogger(HiloLeeVentas.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(HiloLeeVentas.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (fr != null) {
                        fr.close();                       
                    }
                } catch (Exception e) {
                }
            }
        }

    }

    private void comprobarlinea(String linea) {
        
        StringTokenizer stk = new StringTokenizer(linea);
        String aux = stk.nextToken();
        if (aux.equals("Terminal")) {
            for (int i = 0; i < 7; i++) {
                aux = stk.nextToken();
            }
            obtenerHora(aux);
        } else {
            if (!aux.substring(0, 1).equals("-")) {
                System.out.println("linea " + linea);
                numeroLineas++;
            }
        }
    }

    private void obtenerHora(String aux) {
        int hora = 0;
        String horaaux=aux.substring(0, 2); 
        hora = Integer.parseInt(horaaux);
        if (hora >= 0 && hora < 1) {
            ventasAM++;
        }
        if (hora >= 1 && hora < 20) {
            ventasPM++;
        }
    }

}
