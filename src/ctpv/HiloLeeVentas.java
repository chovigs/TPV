/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ctpv;

import comunes.*;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author chovi_000
 */
public class HiloLeeVentas extends Thread {

    String ficherofactura;
    static int  numeroLineas;
    static float ventasAM, ventasPM;
    
    
    DatagramSocket socket = null;
    String ip = "127.0.0.1";
    int port = 4455;

    int AM;

    public HiloLeeVentas(String ficherofactura) {
        this.ficherofactura = ficherofactura;
        try {
            socket = new DatagramSocket();
        } catch (SocketException ex) {
            Logger.getLogger(HiloLeeVentas.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(500);
                leerNumeroLineas();
                enviarResultados();
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

                while ((linea = br.readLine()) != null) {
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
            AM = obtenerHora(aux);
            
        } else {
            if (!aux.substring(0, 1).equals("-")) {
                System.out.println("linea " + linea);
                numeroLineas++;
                while(stk.hasMoreTokens()) {
                        aux = stk.nextToken();
                        System.out.println(" "+ aux);
                    }
                float parcial=Float.parseFloat(aux);
                if (AM == 1) {
                    ventasAM=ventasAM+parcial;                    
                }else{
                    ventasPM=ventasPM+parcial;
                }
            }
        }
    }

    private int obtenerHora(String aux) {
        int am = 0;
        int hora = 0;
        String horaaux = aux.substring(0, 2);
        hora = Integer.parseInt(horaaux);
        if (hora >= 8 && hora < 14) {
            am = 1;            
        }
        if (hora >= 14 && hora < 20) {
            am = -1;            
        }
        return am;
    }

    private void enviarResultados() {
        try {
            comunes.Resultado resultado = new comunes.Resultado(ventasAM, ventasPM, numeroLineas);
            byte[] bytes = resultado.getBytes();
            //InetAddress inetaddress = Inet4Address.getLocalHost();
            InetAddress inetaddress = InetAddress.getByName(ip);
            socket = new DatagramSocket();
            DatagramPacket datagrama = new DatagramPacket(bytes, bytes.length, inetaddress, port);
            System.out.println("enviando datagrama");
            socket.send(datagrama);
            socket.close();
        } catch (UnknownHostException ex) {
            Logger.getLogger(HiloLeeVentas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(HiloLeeVentas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HiloLeeVentas.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
