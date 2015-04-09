/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ctpv;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author chovi_000
 */
//Hilo que escucha la apertura y el cierre de los terminales
public class HiloEscuchaTPV extends Thread {

    String nombreTPV;
    VentanaServidor ventanaservidor; //Indica la VentanaServidor sobre la que actuamos
    HashMap<String, VentanaInterna> listaventanas; //Recoge las ventanas que se van abriendo con el terminal correspondiente

    public HiloEscuchaTPV() {
        start();
    }

    public HiloEscuchaTPV(String nombreTPV) {
        this.nombreTPV = nombreTPV;
        start();
    }

    public HiloEscuchaTPV(VentanaServidor ventanaservidor) {
        this.ventanaservidor = ventanaservidor;
        start();
    }

    @Override
    public void run() {
        System.out.println("Escuchando");
        listaventanas = new HashMap<String, VentanaInterna>();

        //Recibimos el terminal a tratar y laorden a ejecutar
        try {
            DatagramSocket socket = new DatagramSocket(5000);
            while (true) {

                byte[] bytes = new byte[1000];
                DatagramPacket datagrama = new DatagramPacket(bytes, 1000);
                socket.receive(datagrama);
                TerminalComando tc = new TerminalComando();

                try {
                    tc = convertirByteArrayToDatoUDP(datagrama.getData());
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(TerminalComando.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("comando " + tc.getComando());
                //Actuamos según el comando recibido
                if (tc.getComando().equals("abrir")) {
                    System.out.println("entro en abrir ventana " + tc.getTerminal());
                   
                    VentanaInterna vi = new VentanaInterna(tc.getTerminal());                    
                    ventanaservidor.add(vi); //añadimos la internalframe en la ventana principal
                     listaventanas.put(vi.getTitle(), vi);//incluimos la internalframe en el HashMap
                } else {
                    eliminarventana(tc.getTerminal()); //quitamos la ventana
                    System.out.println("entro en eliminar ventana " + tc.getTerminal());
                }

            }
        } catch (SocketException ex) {
            Logger.getLogger(HiloEscuchaTPV.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HiloEscuchaTPV.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Método que convierte el array de bytes a TerminalComando
    protected TerminalComando convertirByteArrayToDatoUDP(byte[] buffer) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArray = new ByteArrayInputStream(buffer);
        ObjectInputStream is = new ObjectInputStream(byteArray);
        TerminalComando aux = (TerminalComando) is.readObject();
        is.close();
        return aux;

    }

    //método que elimina la ventana
    private void eliminarventana(String nombre) {
        System.out.println("ventana a eliminar " + nombre);
        //buscamos el terminal a cerrar -eliminar-
        for (String string : listaventanas.keySet()) {
            if (listaventanas.get(string).getTitle().equals(nombre)) {
                JOptionPane.showMessageDialog(listaventanas.get(string), "Cliente Servido",
                        "Información", JOptionPane.INFORMATION_MESSAGE);

                ventanaservidor.remove(listaventanas.get(string)); //eliminamos de la ventana
                ventanaservidor.repaint();//actualizamos
                listaventanas.remove(string);//eliminamos del HashMap
            }
        }
    }

    
}
