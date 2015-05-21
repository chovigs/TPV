/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package generadorClaves;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author chovi_000
 */
public class GeneradorClaves {
    private KeyPairGenerator generadorClaves;
    private SecureRandom generadorDeAleatorios;
    private KeyPair par;
    private PrivateKey clavePrivada;
    private PublicKey clavePublica;

    public GeneradorClaves() {

    }

    //Constructor
    public void generarClaves(String algoritmo1, String algoritmo2) throws NoSuchAlgorithmException {
        generadorClaves = KeyPairGenerator.getInstance(algoritmo1); 
        generadorDeAleatorios = SecureRandom.getInstance(algoritmo2);
        generadorClaves.initialize(1024, generadorDeAleatorios);
        System.out.println("entro en generador");
        crearParejaDeClaves(); //aquí asignamos las claves creadas a las variables declaradas

        try {
            guardarclave();
        } catch (IOException ex) {
            Logger.getLogger(GeneradorClaves.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Metodo que genera la pareja de claves
    private void crearParejaDeClaves() {
        par = generadorClaves.generateKeyPair();
        clavePrivada = par.getPrivate();
        clavePublica = par.getPublic();
    }

//Metodo para guardar las claves
    public void guardarclave() throws IOException {
        File ficheroprivado = new File("privado.cfg");
        ficheroprivado.delete(); //si existe, lo elimino para hacer uno nuevo
        File ficheropublico = new File("publico.cfg");
        ficheropublico.delete(); //si existe, lo elimino para hacer uno nuevo
        System.out.println("entro en guardar clave");
        try {
            
            //Codificamos las claves
            PKCS8EncodedKeySpec prk = new PKCS8EncodedKeySpec(clavePrivada.getEncoded());
            X509EncodedKeySpec puk = new X509EncodedKeySpec(clavePublica.getEncoded());
            //escribimos la clave privada
            byte[] bprk = prk.getEncoded();
            long lprk = bprk.length;
            RandomAccessFile ras = new RandomAccessFile(ficheroprivado, "rw");
            ras.write(bprk);
            ras.close();

            //escribimos la clave pública
            byte[] bpuk = puk.getEncoded();
            long lpuk = bpuk.length;
            ras = new RandomAccessFile(ficheropublico, "rw");
            ras.write(bpuk);
            ras.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(GeneradorClaves.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GeneradorClaves.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Método para leer las claves
    public void leerclaves() {

        File ficheropublico = new File("publico.cfg");
        File ficheroprivado = new File("privado.cfg");

        if (ficheroprivado.exists() && ficheropublico.exists()) {

            try {
                RandomAccessFile ras = new RandomAccessFile(ficheroprivado, "rw");
                int tamano = (int) ras.length();
                byte[] cpri = new byte[tamano];
                //guardo clave privada de fichero
                ras.read(cpri);
//MUY IMPORTANTE CERRAR LOS RANDOM ACCESFILE SINO NO FUNCIONA BIEN
                ras.close();

                ras = new RandomAccessFile(ficheropublico, "rw");
                tamano = (int) ras.length();
                byte[] cpub = new byte[tamano];
                //guardo clave privada de fichero
                ras.read(cpub);
//MUY IMPORTANTE CERRAR LOS RANDOM ACCESFILE SINO NO FUNCIONA BIEN
                ras.close();
                
                // extraigo las claves codificadas
                KeyFactory keyfactory = KeyFactory.getInstance("RSA");
                PKCS8EncodedKeySpec pkcx8 = new PKCS8EncodedKeySpec(cpri);
                X509EncodedKeySpec x509 = new X509EncodedKeySpec(cpub);

                //almaceno las dos claves publica y privada
                clavePublica = keyfactory.generatePublic(x509);
                clavePrivada = keyfactory.generatePrivate(pkcx8);

            } catch (FileNotFoundException ex) {
                Logger.getLogger(GeneradorClaves.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(GeneradorClaves.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(GeneradorClaves.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidKeySpecException ex) {
                Logger.getLogger(GeneradorClaves.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    //  SETTERS Y GETTERS
     public PrivateKey getClavePrivada() {
        return clavePrivada;
    }

    public PublicKey getClavePublica() {
        return clavePublica;
    }

    public void setClavePrivada(PrivateKey clavePrivada) {
        this.clavePrivada = clavePrivada;
    }

    public void setClavePublica(PublicKey clavePublica) {
        this.clavePublica = clavePublica;
    }
}


