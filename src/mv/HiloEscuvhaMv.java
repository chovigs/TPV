/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mv;

import comunes.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author chovi_000
 */
public class HiloEscuvhaMv extends Thread {

    int  numeroLineas;
    float ventasAM, ventasPM;
    
    VentanaControl ventanacont;
    
    public HiloEscuvhaMv(){
        ventanacont = new VentanaControl();
        ventanacont.setVisible(true);        
    }

    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket(4455);

            while (true) {
                byte[] bytes = new byte[1000];
                DatagramPacket datagrama = new DatagramPacket(bytes, 1000);
                socket.receive(datagrama);
                //ResultadoRecibido resultadorec = new ResultadoRecibido(0, 0, 0);
                
                comunes.Resultado resultadorec = new comunes.Resultado(0, 0, 0);
                try {
                    resultadorec = convertirByteArrayToDatoUDP(datagrama.getData());
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(HiloEscuvhaMv.class.getName()).log(Level.SEVERE, null, ex);
                }

                ventasAM=resultadorec.getVentasAM();
                ventasPM=resultadorec.getVentasPM();
                numeroLineas=resultadorec.getNumeroLineas();
                
                actualizarlabels();
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
       
        comunes.Resultado aux = (comunes.Resultado)is.readObject();
        is.close();
        return aux;
    }
    
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
    
    public static void main(String[] args) {
        HiloEscuvhaMv hiloMV =new HiloEscuvhaMv();
        new Thread(hiloMV).start();
    }
}
