/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ctpv;

import java.io.DataInputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author chovi_000
 */
//Esta es la internalframe que aparecerá en el terminal CTPV
public class VentanaInterna extends javax.swing.JInternalFrame {

    private String nombre;
    private int indice;
    private Socket socket;
    private ObjectInputStream InputDatoRecibido;
    private HiloEscuchaTPV hiloescucha;
    
    private DefaultTableModel modelo;
    /**
     * Para lectura de datos en el socket
     */
    private DataInputStream dataInput;

    /**
     * Creates new form VentanaInterna
     */
    public VentanaInterna() {
        initComponents();
        //setFrameIcon(new ImageIcon(this.getClass().getResource("..\\TPV\\src\\Iconos\\Productos.png")));
       
        setVisible(true);
    }

    public VentanaInterna(int indice) {
        this();
        this.indice = indice;
        ponerNombre();
        modelo = new DefaultTableModel();
   modelo=(DefaultTableModel) this.jTablePedidos.getModel();
        jTablePedidos.setModel(this.jTablePedidos.getModel());
        setVisible(true);
    }



    private void ponerNombre() {
        int aux = this.indice + 1;
        this.nombre = "Terminal TPV " + aux;
        setTitle("Terminal TPV " + aux);
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelTotalTicket = new javax.swing.JLabel();
        jScrollPanePedidos = new javax.swing.JScrollPane();
        jTablePedidos = new javax.swing.JTable();

        setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        setForeground(new java.awt.Color(204, 204, 255));
        setMaximumSize(new java.awt.Dimension(800, 600));

        jLabelTotalTicket.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabelTotalTicket.setText("TOTAL");
        jLabelTotalTicket.setBorder(javax.swing.BorderFactory.createTitledBorder("Total Ticket"));

        jTablePedidos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Cantidad", "Producto", "Sub Total", "Total"
            }
        ));
        jScrollPanePedidos.setViewportView(jTablePedidos);
        if (jTablePedidos.getColumnModel().getColumnCount() > 0) {
            jTablePedidos.getColumnModel().getColumn(0).setPreferredWidth(60);
        }

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jScrollPanePedidos, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
                .addGap(75, 75, 75)
                .addComponent(jLabelTotalTicket, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPanePedidos, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelTotalTicket, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(176, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelTotalTicket;
    private javax.swing.JScrollPane jScrollPanePedidos;
    private javax.swing.JTable jTablePedidos;
    // End of variables declaration//GEN-END:variables

    
    // ----------- Getters y Setters -------------

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public ObjectInputStream getInputDatoRecibido() {
        return InputDatoRecibido;
    }

    public void setInputDatoRecibido(ObjectInputStream InputDatoRecibido) {
        this.InputDatoRecibido = InputDatoRecibido;
    }

    public HiloEscuchaTPV getHiloescucha() {
        return hiloescucha;
    }

    public void setHiloescucha(HiloEscuchaTPV hiloescucha) {
        this.hiloescucha = hiloescucha;
    }

    public DataInputStream getDataInput() {
        return dataInput;
    }

    public void setDataInput(DataInputStream dataInput) {
        this.dataInput = dataInput;
    }

    public javax.swing.JLabel getjLabelTotalTicket() {
        return jLabelTotalTicket;
    }

    public void setjLabelTotalTicket(javax.swing.JLabel jLabelTotalTicket) {
        this.jLabelTotalTicket = jLabelTotalTicket;
    }

    public javax.swing.JScrollPane getjScrollPanePedidos() {
        return jScrollPanePedidos;
    }

    public void setjScrollPanePedidos(javax.swing.JScrollPane jScrollPanePedidos) {
        this.jScrollPanePedidos = jScrollPanePedidos;
    }

    public javax.swing.JTable getjTablePedidos() {
        return jTablePedidos;
    }

    public void setjTablePedidos(javax.swing.JTable jTablePedidos) {
        this.jTablePedidos = jTablePedidos;
    }

  
    
}
