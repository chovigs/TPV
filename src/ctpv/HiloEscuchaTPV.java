/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ctpv;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

/**
 *
 * @author chovi_000
 */
//Hilo que escucha la apertura y el cierre de los terminales
public class HiloEscuchaTPV extends Thread {

    String nombreTPV;
    VentanaServidor ventanaservidor; //Indica la VentanaServidor sobre la que actuamos
    Factura factura; //Definimos el elemento factura que aparecerá
    
    VentanaInterna[] arrayventanas;//array donde guardaré las ventanas internas
    static int contador;//contador para controlar el número de ventanas
    static final int PUERTO = 5000;
    ServerSocket server;
    ObjectInputStream InputDatoRecibido;
    Socket socket;
    

    //Constructor. Le pasamos la ventanaservidor correspondiente
    public HiloEscuchaTPV(VentanaServidor ventanaservidor) {
        this.ventanaservidor = ventanaservidor;
         contador = 0;
        try {
            server = new ServerSocket(5000);
        } catch (IOException ex) {
            Logger.getLogger(HiloEscuchaTPV.class.getName()).log(Level.SEVERE, null, ex);
        }
        start();
    }

    @Override
    public void run() {
        System.out.println("Escuchando");        

        arrayventanas = new VentanaInterna[7];
        
        while (contador < 7) { //controlo una petición de más
            try {
                
                socket = server.accept();
               //asigno el indice
                int indiceaux=0;
                    indiceaux=asignarindice();
                    enviarIndice(socket,indiceaux); //envío el indice
                 if (indiceaux!=6){ //solo permito 6 ventanas
                    VentanaInterna vi = new VentanaInterna(indiceaux);
                    ventanaservidor.add(vi); //añadimos la internalframe en la ventana principal
                    arrayventanas[indiceaux]=vi; //añadimos la ventana al array de ventanas
                    contador++; //aumentamos el contador
                    //Añadimos el hilo de ventas
                     Ventas ventas = new Ventas(this, socket, indiceaux);
                     ventas.start();
                     this.ventanaservidor.repaint();
                    }
                
                System.out.println("contador "+contador);
            } catch (IOException ex) {
                Logger.getLogger(HiloEscuchaTPV.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    //método para enviar el índice correspondiente
    private void enviarIndice(Socket socket, int indice){       
        OutputStream aux = null;
        try {
            aux = socket.getOutputStream();
            DataOutputStream flujo = new DataOutputStream(aux);
            flujo.writeUTF(""+indice );
            
        } catch (IOException ex) {
            Logger.getLogger(HiloEscuchaTPV.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("indice "+indice);
    }
    
    //asignamos el índice que esté libre en el array
    private int asignarindice(){        
        int i=0;
        while(arrayventanas[i]!=null&&i<6){
            i++;
        }
        return i;
    }
    
    //método que elimina la ventana  
    public void eliminarventana(int indice){
        JOptionPane.showMessageDialog(arrayventanas[indice], "Cliente Servido","Información", JOptionPane.INFORMATION_MESSAGE);
        ventanaservidor.remove(arrayventanas[indice]); //eliminamos de la ventana
        ventanaservidor.repaint();//actualizamos
        arrayventanas[indice]=null;
        contador--;
    }
    

    //método sincronizado  que escribe en el fichero
    //le pasamos el fichero, la lista, el total y el nombre del terminal
    public synchronized void escribirVentas(String ficherofactura,DefaultListModel modelo,BigDecimal big, String terminal){
        Factura factura= new Factura(ficherofactura, modelo, big, terminal);
        //Si queremos un mensaje con resultado
       // JOptionPane.showMessageDialog(ventanaservidor, factura.option(),"Información", JOptionPane.INFORMATION_MESSAGE);
    }
    
    //Getters y Setters

    public String getNombreTPV() {
        return nombreTPV;
    }

    public void setNombreTPV(String nombreTPV) {
        this.nombreTPV = nombreTPV;
    }

    public VentanaServidor getVentanaservidor() {
        return ventanaservidor;
    }

    public void setVentanaservidor(VentanaServidor ventanaservidor) {
        this.ventanaservidor = ventanaservidor;
    }

    public VentanaInterna[] getArrayventanas() {
        return arrayventanas;
    }

    public void setArrayventanas(VentanaInterna[] arrayventanas) {
        this.arrayventanas = arrayventanas;
    }

    public static int getContador() {
        return contador;
    }

    public static void setContador(int contador) {
        HiloEscuchaTPV.contador = contador;
    }

    public ServerSocket getServer() {
        return server;
    }

    public void setServer(ServerSocket server) {
        this.server = server;
    }

    public ObjectInputStream getInputDatoRecibido() {
        return InputDatoRecibido;
    }

    public void setInputDatoRecibido(ObjectInputStream InputDatoRecibido) {
        this.InputDatoRecibido = InputDatoRecibido;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    
}
