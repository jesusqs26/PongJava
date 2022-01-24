package game;

import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

/**
 * Clase encargada de las paletas de los jugadores
 * @author Jesus
 */
public class Paleta {

    /**
     * Numero de paletas que jugaran
     */
    public int numeroPaleta;

    /**
     * Coordenadas que indican la posicion de la paleta
     */
    public int x, y, width = 20, height = 250;

    /**
     * Puntaje del jugador
     */
    public int puntaje;

    /**
     * Metodo que crea una paleta
     * @param pongo Frame principal de la aplicacion
     * @param numeroPaleta Numero de jugador (1 o 2)
     * @param tamañoRaqueta Tamaño que tendra la raqueta en el juego
     */
    public Paleta(Main pongo, int numeroPaleta, int tamañoRaqueta) {
        this.numeroPaleta = numeroPaleta;

        if (numeroPaleta == 1) {
            this.x = 0;
        }

        if (numeroPaleta == 2) {
            this.x = pongo.width - width;
        }
        switch (tamañoRaqueta) {
            case 0:
                height = 150;
                this.y = (pongo.height) / 2 - (this.height) / 2;
                break;
            case 1:
                height = 200;
                this.y = (pongo.height) / 2  - (this.height) / 2 ;
                break;
            case 2:  
                
                break;
            case 3 :
                height = 100;
                this.y = (pongo.height) / 2  - (this.height) / 2;
                break;
            default:
                break;
        }
    }

    /**
     * Metodo que hace las paletas graficas
     * @param g 
     */
    public void render(Graphics g) {
        if (numeroPaleta ==1) {
            g.setColor(Color.blue);
        g.fillRect(x, y, width, height);
        
        }
        
        if (numeroPaleta ==2) {
            g.setColor(Color.red);
        g.fillRect(x, y, width, height);
        
        }
        
    }

    /**
     * Metodo que realiza el movimiento de la paleta
     * @param arriba True si se esta presionando la tecla arriba,
     *              de lo contrario False.
     */
    public void mover(boolean arriba) {
        int veloc = 15;

        if (arriba) {
            if (y - veloc > 0) {
                y -= veloc;
            } else {
                y = 0;
            }
        } else if (y + height + veloc < Main.pong.height) {
            y += veloc;
        } else {
            y = Main.pong.height - height;
        }
    }

}
