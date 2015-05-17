/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ctpv;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import tpv.ProductoPedido;

/**
 *
 * @author chovi_000
 */
public class Ventas extends Thread {

    ObjectInputStream InputDatoRecibido;
    Socket socket;
    int indice;
    HiloEscuchaTPV hiloescucha;
    VentanaInterna ventana;

    private HashMap<String, ProductoPedido> listaPedidos; // Aqui se almacenan los productos pedidos
    DefaultTableModel modelo;
    BigDecimal big;
    static final String ficheroFactura ="Ventas.dat";

    public Ventas(HiloEscuchaTPV hiloescucha, Socket socket, int indice) {
        this.hiloescucha = hiloescucha;
        this.socket = socket;
        this.indice = indice;
        this.ventana = hiloescucha.getArrayventanas()[indice];
        this.ventana.setVisible(true);
    }

    @Override
    public void run() {
        //inicializamos la lista, y el DefaultListModel
        //asignamos la lista al modelo
        listaPedidos = new HashMap<>();
        modelo = new DefaultTableModel();
        modelo=(DefaultTableModel) ventana.getjTablePedidos().getModel();
        ventana.getjTablePedidos().setModel(modelo);
        
     
        try {//recibimos los datos
            InputDatoRecibido = new ObjectInputStream(socket.getInputStream());
          
        } catch (IOException ex) {
            Logger.getLogger(Ventas.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (!socket.isClosed()) { //Comprobamos el tipo de "producto" que hemos recibido
            ProductoPedido producto = new tpv.ProductoPedido("", 0, 0);
            try {//convierto el dato recibido en ProductoPedido
                producto = (ProductoPedido) InputDatoRecibido.readObject();                          
            } catch (IOException ex) {
                Logger.getLogger(Ventas.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Ventas.class.getName()).log(Level.SEVERE, null, ex);
            } 
           //Según el tipo de "producto", actualizamos o salimos
            if (!producto.getNombre().equals("salir")) {
                    actualizarTabla(producto);
                    //System.out.println(" producto "+producto.toString());
                } else {
                try {                    
                    hiloescucha.eliminarventana(indice);
                    hiloescucha.escribirVentas(ficheroFactura, modelo, big, ventana.getNombre());  
                    socket.close();
                } catch (IOException ex) {
                    Logger.getLogger(Ventas.class.getName()).log(Level.SEVERE, null, ex);
                }
                }
        }
    }

   

    //diseño el elemento lista para presentarlo
    private String elementoLista(ProductoPedido producto) {
        String elemento = "";
        String[] arrayelement = producto.getProducto();        
        //formateamos para que quede mejor
        elemento = arrayelement[1] + " x " +String.format("%-20s", arrayelement[0]) +  String.format("%-10s", producto.redondear(producto.getPrecio())) + "\t" + arrayelement[2];
        return elemento;
    }

    //método para obtener el total
    public void actualizarTotal() {
        float total = 0;
        for (String string : listaPedidos.keySet()) {
            total += listaPedidos.get(string).getTotal();
        }
        String val = total + "";
        big = new BigDecimal(val);
        big = big.setScale(2, RoundingMode.HALF_UP);
        ventana.getjLabelTotalTicket().setText(" TOTAL \n" + big + " € ");
    }
    
     /**
     * Método que borra la tabla para rellenarla nuevamente con los datos
     * actualizados de la compra.
     *
     */  
    
    public void actualizarTabla(ProductoPedido producto) {
        int a = modelo.getRowCount() - 1;
        for (int i = a; i >= 0; i--) {
            modelo.removeRow(i);
        }//para que actualice correctaente, escribimos toda la lista
        if (producto.getCantidad() != 0) {
            listaPedidos.put(producto.getNombre(), producto);
        } else {
            listaPedidos.remove(producto.getNombre());
        }
       
        for (String string : listaPedidos.keySet()) {
            modelo.addRow(listaPedidos.get(string).getProducto2());           
        }
        ventana.getjTablePedidos().repaint();
        //actualizamos el total
        actualizarTotal();
    }
}
