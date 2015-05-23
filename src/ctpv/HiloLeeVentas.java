/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ctpv;

import comunes.*;
import generadorClaves.GeneradorClaves;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author chovi_000
 * 
 * Hilo que envia lee los datos de las ventas y los envía al MV
 */
public class HiloLeeVentas extends Thread {

    String ficherofactura;
    static int  numeroLineas;
    static float ventasAM, ventasPM;
    int AM;//Para saber si la venta ha sido por la mañana o por la tarde
    //Variables para la conexion UDP
    DatagramSocket socket = null;
    String ip = "127.0.0.1"; //ip par la conexion UDP
    int port = 4455;//puerto para la conexion UDP
    
    private PublicKey clavePublica; //Para la clave privada

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
        //leerclavePublica(); //obtenemos la clave pública
        while (true) {
            try {
                Thread.sleep(500);
                leerclavePublica(); //obtenemos la clave pública por si se ha generado una nueva
                leerNumeroLineas();//obtenemos los datos requeridos
                
                if(clavePublica!=null){
                    enviarResultados(); //enviamos la información
                }
                
            } catch (InterruptedException ex) {
                Logger.getLogger(HiloLeeVentas.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    //metodo para obtener el número de líneas de venta y comprueba la hora de la venta
    private void leerNumeroLineas() {
        ventasAM = 0;
        ventasPM = 0;
        numeroLineas = 0;
        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;
        archivo = new File(ficherofactura);       
        String linea;

        long tamanioArchivo = archivo.length();//Longitud del fichero
        //Si el tamaño del fichero es mayor a 0, es decir si no esta vacio
        if (tamanioArchivo > 0) {
            try {
                fr = new FileReader(archivo);
                br = new BufferedReader(fr);                
                //leo el archivo linea a linea
                while ((linea = br.readLine()) != null) { //mientras haya líneas
                    linea = "" + linea;
                    comprobarlinea(linea);//examinamos la linea
                }
                br.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(HiloLeeVentas.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(HiloLeeVentas.class.getName()).log(Level.SEVERE, null, ex);
            } finally {//cerramos el file reader
                try {
                    if (fr != null) {
                        fr.close();
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    //metodo que examina la linea que se lee del archivo de ventas
    private void comprobarlinea(String linea) {
        StringTokenizer stk = new StringTokenizer(linea);
        String aux = stk.nextToken();
        if (aux.equals("Terminal")) {//comprobamos si es "Teminal" o datos
            for (int i = 0; i < 7; i++) {
                aux = stk.nextToken();
            }
            AM = obtenerHora(aux);//comprobamos la hora de la venta
        } else {
            if (!aux.substring(0, 1).equals("-")) {//si no es línea de separación                
                numeroLineas++;//aumentamos el número de lineas
                while(stk.hasMoreTokens()) {//recorremos la línea
                        aux = stk.nextToken();
                        System.out.println(" "+ aux);
                    }
                float parcial=Float.parseFloat(aux);//obtenemos el subtotal y lo añadimos al total correspondiente
                if (AM == 1) {
                    ventasAM=ventasAM+parcial;                    
                }else{
                    ventasPM=ventasPM+parcial;
                }
            }
        }
    }
//Metodo obtenemos la hora de la venta y devuelve si ha sido por la mañana o por la tarde
    private int obtenerHora(String aux) {
        int am = 0;//indica si la venta es por la mañana o por la tarde
        int hora = 0;
        String horaaux = aux.substring(0, 2);
        hora = Integer.parseInt(horaaux);
        if (hora >= 8 && hora < 14) {
            am = 1;            
        }
        if (hora >= 16 && hora < 20) {
            am = -1;            
        }
        return am;
    }

    private void enviarResultados() {//enviamos los resultados al MV
        try {
            //creamos al objeto que guarda los resultados
            Resultado resultado = new Resultado(ventasAM, ventasPM, numeroLineas);            
            byte[] bytes = resultado.getBytes();
            
            //Ciframos con clave pública el resultado utilizando RSA
            Cipher cifrador = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cifrador.init(Cipher.ENCRYPT_MODE, clavePublica);
            byte[] bufferCifrado = cifrador.doFinal(bytes);
           //enviamos el resultado cifrado
            InetAddress inetaddress = InetAddress.getByName(ip);
            socket = new DatagramSocket();
            DatagramPacket datagrama = new DatagramPacket(bufferCifrado, bufferCifrado.length, inetaddress, port);
            System.out.println("enviando datagrama");
            socket.send(datagrama);
            socket.close();
        } catch (UnknownHostException ex) {
            Logger.getLogger(HiloLeeVentas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(HiloLeeVentas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HiloLeeVentas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(HiloLeeVentas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(HiloLeeVentas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(HiloLeeVentas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(HiloLeeVentas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(HiloLeeVentas.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //metodo paraleer el archivo con la clave publica
     public void leerclavePublica() {
        File ficheropublico = new File("publico.cfg");
        if (ficheropublico.exists()) {
            System.out.println(" existe ");
            try {
                
                RandomAccessFile ras = new RandomAccessFile(ficheropublico, "rw");
                int tamano = (int) ras.length();
                byte[] cpub = new byte[tamano];
                //guardo clave publica de fichero
                ras.read(cpub);
//MUY IMPORTANTE CERRAR LOS RANDOM ACCESFILE SINO NO FUNCIONA BIEN
                ras.close();

                //descodificamos la clave
                KeyFactory keyfactory = KeyFactory.getInstance("RSA");
                X509EncodedKeySpec x509 = new X509EncodedKeySpec(cpub);
                
                //almaceno la clave publica
                clavePublica = keyfactory.generatePublic(x509);
              
            } catch (FileNotFoundException ex) {
                Logger.getLogger(GeneradorClaves.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(GeneradorClaves.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(GeneradorClaves.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidKeySpecException ex) {
                System.out.println("clave publica "+clavePublica);
                Logger.getLogger(GeneradorClaves.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
//        else{
//            JOptionPane.showMessageDialog(null,"No se han generado las claves. No se enviaran los datos", "Error", JOptionPane.ERROR_MESSAGE);
//        }
    }

}
