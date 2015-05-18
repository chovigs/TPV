/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package comunes;


import mv.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 *
 * @author chovi_000
 */
public class Resultado implements Serializable{
  // static final long serialVersionUID = 42L;
    int  numeroLineas;
    float ventasAM, ventasPM;
    
    public Resultado(float ventasAM,float ventasPM, int numeroLineas ){
        this.ventasAM=ventasAM;
        this.ventasPM=ventasPM;
        this.numeroLineas=numeroLineas;
    }

    public float getVentasAM() {
        return ventasAM;
    }

    public void setVentasAM(int ventasAM) {
        this.ventasAM = ventasAM;
    }

    public float getVentasPM() {
        return ventasPM;
    }

    public void setVentasPM(int ventasPM) {
        this.ventasPM = ventasPM;
    }

    public int getNumeroLineas() {
        return numeroLineas;
    }

    public void setNumeroLineas(int numeroLineas) {
        this.numeroLineas = numeroLineas;
    }
    
     //método para pasar el Resultado a byte[] 
    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bs);
        os.writeObject(this);  // this es de tipo Resultado
        os.close();
        byte[] bytes = bs.toByteArray(); // devuelve byte[]
        
        return bytes;
    }
   
    
}
