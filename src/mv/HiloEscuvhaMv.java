/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mv;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author chovi_000
 */
public class HiloEscuvhaMv extends Thread{
    
    public void run(){
        try {
                DatagramSocket socket = new DatagramSocket(5000);
                
                while(true){
                    byte[] bytes = new byte[1];
                     DatagramPacket datagrama = new DatagramPacket(bytes, 1);                    
                     socket.receive(datagrama);
                     
                     String orden= new String(bytes);
                     System.out.println(""+orden);
                    
                                         }
                
             
    }   catch (SocketException ex) {
            Logger.getLogger(HiloEscuvhaMv.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HiloEscuvhaMv.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
