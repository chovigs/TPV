/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mv;

import comunes.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author chovi_000
 */
public class HiloEscuvhaMv extends Thread {

    int  numeroLineas;
    float ventasAM, ventasPM;
    
    VentanaControl ventanacont;
    
    PrivateKey clavePrivada;
    
    //Hilo que escucha la recepcion de datos y los visualiza en la ventana correspondiente
    public HiloEscuvhaMv(){
        ventanacont = new VentanaControl();
        ventanacont.setVisible(true);
    }

    public void run() {
        leerPrivado();//lee la clave privada del archivo correspondiente
        try {
            DatagramSocket socket = new DatagramSocket(4455);

            while (true) {
                //recibe los datos
                byte[] bufferCifrado = new byte[128];
                DatagramPacket datagrama = new DatagramPacket(bufferCifrado, 128);
                socket.receive(datagrama);
                Resultado resultadorec = new Resultado(0, 0, 0);
                try {
                    
                    //Desencriptación utilizando la clave privada
                    Cipher cifrador = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                    cifrador.init(Cipher.DECRYPT_MODE, clavePrivada);
                        
                    //Obtener y mostrar texto descifrado
                    byte[] bytes =new byte[128];
                    bytes = cifrador.doFinal(datagrama.getData());
                    resultadorec = convertirByteArrayToDatoUDP(bytes);
                    
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(HiloEscuvhaMv.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(HiloEscuvhaMv.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchPaddingException ex) {
                    Logger.getLogger(HiloEscuvhaMv.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvalidKeyException ex) {
                    Logger.getLogger(HiloEscuvhaMv.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalBlockSizeException ex) {
                    Logger.getLogger(HiloEscuvhaMv.class.getName()).log(Level.SEVERE, null, ex);
                } catch (BadPaddingException ex) {
                    Logger.getLogger(HiloEscuvhaMv.class.getName()).log(Level.SEVERE, null, ex);
                }

                //asigna el resultado
                ventasAM=resultadorec.getVentasAM();
                ventasPM=resultadorec.getVentasPM();
                numeroLineas=resultadorec.getNumeroLineas();
                
                actualizarlabels();//actualizamos los labels de la ventana
            }

        } catch (SocketException ex) {
            Logger.getLogger(HiloEscuvhaMv.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HiloEscuvhaMv.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

   // Método que convierte el array de bytes a Resultado
    protected comunes.Resultado convertirByteArrayToDatoUDP(byte[] buffer) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArray = new ByteArrayInputStream(buffer);
        ObjectInputStream is = new ObjectInputStream(byteArray);
        Resultado aux = (Resultado)is.readObject();
        is.close();
        return aux;
    }
    
    //me´todo que actualiza los labels, ayudándonos de la clase BigDecimal
    private void actualizarlabels(){
       BigDecimal bigventasAM =new BigDecimal(ventasAM);
       BigDecimal bigventasPM= new BigDecimal(ventasPM);
       bigventasAM = bigventasAM.setScale(2, RoundingMode.HALF_UP);
       bigventasPM = bigventasPM.setScale(2, RoundingMode.HALF_UP);
       ventanacont.getjTextFieldLineas().setText(""+numeroLineas);
       ventanacont.getjTextFieldVentasAM().setText(""+bigventasAM);
       ventanacont.getjTextFieldVentasPM().setText(""+bigventasPM);
       ventanacont.repaint();
    }
    
    //metodo para leer el archivo con la clave privada
    public void leerPrivado(){        
        File ficheroprivado = new File("privado.cfg");
        if (ficheroprivado.exists() ) {
            try {
                RandomAccessFile ras = new RandomAccessFile(ficheroprivado, "rw");
                int tamano = (int) ras.length();
                byte[] cpri = new byte[tamano];
                
                //guardo clave privada de fichero
                ras.read(cpri);
//MUY IMPORTANTE CERRAR LOS RANDOM ACCESFILE SINO NO FUNCIONA BIEN
                ras.close();
                //descodifico la cleve leida
                KeyFactory keyfactory = KeyFactory.getInstance("RSA");
                PKCS8EncodedKeySpec pkcx8 = new PKCS8EncodedKeySpec(cpri);
                
                //almaceno la clave privada                
                clavePrivada = keyfactory.generatePrivate(pkcx8);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(HiloEscuvhaMv.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(HiloEscuvhaMv.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(HiloEscuvhaMv.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidKeySpecException ex) {
                Logger.getLogger(HiloEscuvhaMv.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
   
    public static void main(String[] args) {
        HiloEscuvhaMv hiloMV =new HiloEscuvhaMv();
        new Thread(hiloMV).start();
    }
}
