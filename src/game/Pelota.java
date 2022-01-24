package game;

import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/**
 * Clase de la pelota del juego
 * @author Jesus
 */
public class Pelota {

    public int x, y, width = 25, height = 25;

    public int motionX, motionY;

    public Random random;
    private Color color = Color.white;
    private boolean reb1 = false, reb2 = false;
    private Main pong;
    public int cantidadGolpes;
    FileInputStream fis;
    Player player;
    String pip = "Sonido\\pip.wav";
    String pop = "Sonido\\pop.wav";

    public Pelota(Main pong) {
        
        this.pong = pong;

        this.random = new Random();

        spawn();
    }

    /**
     * Actualiza la velocidad y direccion de la pelota
     * @param raq1 Paleta del jugador 1
     * @param raq2 Paleta del jugador 2
     */
    public void actualiza(Paleta raq1, Paleta raq2) {
        int veloc = 5;

        this.x += motionX * veloc;
        this.y += motionY * veloc;

        if (this.y + height - motionY > pong.height || this.y + motionY < 0) {
            if (this.motionY < 0) {
                this.y = 0;
                this.motionY = random.nextInt(4);

                if (motionY == 0) {
                    motionY = 1;
                }
            } else {
                this.motionY = -random.nextInt(4);
                this.y = pong.height - height;

                if (motionY == 0) {
                    motionY = -1;
                }
            }
        }

        if (Colisiones(raq1) == 1) {
            reb1 = true;
            reb2 = false;
            this.motionX = 1 + (cantidadGolpes / 5);
            this.motionY = -2 + random.nextInt(4);

            if (motionY == 0) {
                motionY = 1;
            }

            cantidadGolpes++;
        } else if (Colisiones(raq2) == 1) {
            reb2 = true;
            reb1 = false;
            this.motionX = -1 - (cantidadGolpes / 5);
            this.motionY = -2 + random.nextInt(4);

            if (motionY == 0) {
                motionY = 1;
            }

            cantidadGolpes++;
        }

        if (Colisiones(raq1) == 2) {
            raq2.puntaje++;
            spawn();
        } else if (Colisiones(raq2) == 2) {
            raq1.puntaje++;
            spawn();
        }
    }

    /**
     * Metodo para generar la pelota
     */
    public void spawn() {
        this.cantidadGolpes = 0;
        this.x = pong.width / 2 - this.width / 2;
        this.y = pong.height / 2 - this.height / 2;

        this.motionY = -2 + random.nextInt(4);

        if (motionY == 0) {
            motionY = 1;
        }

        if (random.nextBoolean()) {
            motionX = 1;
        } else {
            motionX = -1;
        }
    }

    /**
     * Metodo que se encarga de regresar el numero de colisiones con cierta paleta
     * @param raq Paleta especifica
     * @return Numero de colisiones
     */
    public int Colisiones(Paleta raq) {
        if (this.x < raq.x + raq.width && this.x + width > raq.x && this.y < raq.y + raq.height && this.y + height > raq.y) {
            if (reb1) {
                color = Color.BLUE;
                reb1 = false;

                try {
                    InputStream in;
                    in = new FileInputStream(pip);

                    AudioStream audio = new AudioStream(in);
                    AudioPlayer.player.start(audio);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Pelota.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Pelota.class.getName()).log(Level.SEVERE, null, ex);
                }


            } else if (reb2) {
                color = Color.red;
                try {
                    InputStream in;
                    in = new FileInputStream(pop);

                    AudioStream audio = new AudioStream(in);
                    AudioPlayer.player.start(audio);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Pelota.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Pelota.class.getName()).log(Level.SEVERE, null, ex);
                }
                reb2 = false;
            } else {
                color = Color.WHITE;
            }
            return 1; //rebote
        } else if ((raq.x > x && raq.numeroPaleta == 1) || (raq.x < x - width && raq.numeroPaleta == 2)) {
            reb1 = false;
            reb2 = false;
            color = Color.white;
            return 2; //puntaje
        }

        return 0; //nada
    }

    /**
     * Metodo que se encarga de producir la secuencia de imagenes
     * @param g Graficos
     */
    public void render(Graphics g) {
        g.setColor(color);
        g.fillOval(x, y, width, height);
    }

}
