/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ctpv;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 *
 * @author chovi_000
 */

//Esta clase indica el terminal sobre el que se actua y el comando que hay que realizar 
public class TerminalComando implements Serializable{
    
    String terminal;
    String comando;
    
    public TerminalComando(){        
    }
    
    public TerminalComando(String terminal, String comando){
        this.terminal=terminal;
        this.comando=comando;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getComando() {
        return comando;
    }

    public void setComando(String comando) {
        this.comando = comando;
    }
    
    //método para pasar el TerminalComando a byte[] 
    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bs);
        os.writeObject(this);  // this es de tipo TerminalComando
        os.close();
        byte[] bytes = bs.toByteArray(); // devuelve byte[]
        
        return bytes;
    }
    
}
