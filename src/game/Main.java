package game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.Timer;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/**
 * Clase que se encarga de crear una instancia de la aplicacion
 *
 * @author Jesus
 */
public class Main implements ActionListener, KeyListener {

    public static Main pong;
    public int width = 800, height = 720;
    public Renderer renderer;

    public Paleta jug1;
    public Paleta jug2;
    public Pelota pelota;

    public boolean bot = false, seleccionaDificultad, musicaOn = false;
    public boolean w, s, arriba, abajo;

    //0 = menú, 1 = pausado, 2 = en juego, 3 = pantalla de ganador
    public int ESTADOJUEGO = 0;
    public int puntajeLimite = 7, ganador;
    public int dificultadBot, movimientoBot, retrasoBot = 0;
    public int tamañoRaqueta = 0;

    public Random random;
    public JFrame jframe;

    String sonido = "Sonido\\Tetrisw.wav";
    InputStream in;
    AudioStream audio;

    FileWriter puntajes;
    BufferedWriter bw;

    /**
     * Metodo que crea una instancia de la pantalla principal de la aplicacion
     */
    public Main() {
        Timer timer = new Timer(20, this);
        random = new Random();

        jframe = new JFrame("Pong");

        renderer = new Renderer();

        jframe.setSize(width + 15, height + 35);
        jframe.setLocationRelativeTo(jframe);
        jframe.setVisible(true);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.add(renderer);
        jframe.addKeyListener(this);

        timer.start();
    }

    /**
     * Metodo que inicia el juego
     *
     * @throws Exception
     */
    public void inicio() throws Exception {

        ESTADOJUEGO = 2;
        jug1 = new Paleta(this, 1, tamañoRaqueta);
        jug2 = new Paleta(this, 2, tamañoRaqueta);
        pelota = new Pelota(this);

    }

    /**
     * Metodo que se encarga en actualizar lo que sucede en el juego
     */
    public void actualiza() {
        if (jug1.puntaje >= puntajeLimite) {
            ganador = 1;
            ESTADOJUEGO = 3;
            try {
                puntajes = new FileWriter(new File("Puntajes.txt"), true);
                bw = new BufferedWriter(puntajes);
                bw.write("- RESULTADOS DEL JUEGO -");
                bw.newLine();
                bw.write("Jugador 1: " + jug1.puntaje);
                bw.newLine();
                bw.write("Jugador 2: " + jug2.puntaje);
                bw.newLine();
                bw.close();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "No se logro guardar el puntaje.", "ERROR DE ESCRITURA", JOptionPane.WARNING_MESSAGE);
            }
        }
        if (jug2.puntaje >= puntajeLimite) {
            ganador = 2;
            ESTADOJUEGO = 3;
            try {
                puntajes = new FileWriter(new File("Puntajes.txt"), true);
                bw = new BufferedWriter(puntajes);
                bw.write("- RESULTADOS DEL JUEGO -");
                bw.newLine();
                bw.write("Jugador 1: " + jug1.puntaje);
                bw.newLine();
                bw.write("Jugador 2: " + jug2.puntaje);
                bw.newLine();
                bw.close();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "No se logro guardar el puntaje.", "ERROR DE ESCRITURA", JOptionPane.WARNING_MESSAGE);
            }
        }

        if (w) {
            jug1.mover(true);
        }
        if (s) {
            jug1.mover(false);
        }
        if (!bot) {
            if (arriba) {
                jug2.mover(true);
            }
            if (abajo) {
                jug2.mover(false);
            }
        } else {
            if (retrasoBot > 0) {
                retrasoBot--;
                if (retrasoBot == 0) {
                    movimientoBot = 0;
                }
            }

            if (movimientoBot < 10) {
                if (jug2.y + jug2.height / 2 < pelota.y) {
                    jug2.mover(false);
                    movimientoBot++;
                }
                if (jug2.y + jug2.height / 2 > pelota.y) {
                    jug2.mover(true);
                    movimientoBot++;
                }
                if (dificultadBot == 0) {
                    retrasoBot = 25;
                }
                if (dificultadBot == 1) {
                    retrasoBot = 15;
                }
                if (dificultadBot == 2) {
                    retrasoBot = 7;
                }
                if (dificultadBot == 3) {
                    retrasoBot = 3;
                }
            }
        }
        pelota.actualiza(jug1, jug2);
    }

    /**
     * Metodo que se encarga de dibujar la interfaz del juego
     *
     * @param g Elementos graficos que aparecen en la pantalla
     */
    public void render(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (ESTADOJUEGO == 0) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", 1, 50));

            g.drawString("PONG", width / 2 - 75, 50);

            if (!seleccionaDificultad) {
                String string = tamañoRaqueta == 0 ? "Pequeña" : tamañoRaqueta == 3 ? "Muy pequeña" : (tamañoRaqueta == 1 ? "Mediana" : "Grande");
                g.setFont(new Font("Arial", 1, 30));

                g.drawString("Presiona 'p' para ver los puntajes.", width / 2 - 225, height / 2 - 75);
                g.drawString("Presiona 'espacio' para jugar (2 jug).", width / 2 - 240, height / 2 - 25);
                g.drawString("Presiona 'shift' para jugar (1 jug).", width / 2 - 210, height / 2 + 25);
                g.drawString("Presiona 't' para cambiar el tamaño de las ", width / 2 - 280, height / 2 + 125);
                g.drawString("raquetas.", width / 2 - 55, height / 2 + 150);
                g.drawString("<< Puntaje límite: " + puntajeLimite + " >>", width / 2 - 150, height / 2 + 75);
                g.drawString("Tamaño de la raqueta : " + string, width / 2 - 200, height / 2 + 300);
                g.drawString("Presiona 'm' para activar la música", width / 2 - 225, height / 2 + 350);

            }
        }

        if (seleccionaDificultad) {
            String string = dificultadBot == 0 ? "Fácil" : dificultadBot == 3 ? "Muy difícil" : (dificultadBot == 1 ? "Medio" : "Difícil");

            g.setFont(new Font("Arial", 1, 30));

            g.drawString("<< Nivel del bot: " + string + " >>", width / 2 - 180, height / 2 - 25);
            g.drawString("Presiona 'espacio' para jugar", width / 2 - 150, height / 2 + 25);
        }

        if (ESTADOJUEGO == 1) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", 1, 50));
            g.drawString("Pausado", width / 2 - 103, height / 2 - 25);
        }

        if (ESTADOJUEGO == 1 || ESTADOJUEGO == 2) {
            g.setColor(Color.WHITE);

            g.setStroke(new BasicStroke(5f));

            g.drawLine(width / 2, 0, width / 2, height);

            g.setStroke(new BasicStroke(2f));

            g.drawOval(width / 2 - 150, height / 2 - 150, 300, 300);

            g.setFont(new Font("Arial", 1, 50));

            g.drawString(String.valueOf(jug1.puntaje), width / 2 - 90, 50);
            g.drawString(String.valueOf(jug2.puntaje), width / 2 + 65, 50);

            jug1.render(g);
            jug2.render(g);
            pelota.render(g);
        }

        if (ESTADOJUEGO == 3) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", 1, 50));

            g.drawString("¡PONG!", width / 2 - 75, 50);

            if (bot && ganador == 2) {
                g.drawString("¡¡¡Perdiste!!!", width / 2 - 200, 200);
            } else {
                g.drawString("¡Jugador " + ganador + " ha ganado!", width / 2 - 275, 200);
            }

            g.setFont(new Font("Arial", 1, 30));

            g.drawString("Presiona 'espacio' para volver a jugar.", width / 2 - 250, height / 2 - 25);
            g.drawString("Presiona 'ESC' para volver al menú.", width / 2 - 250, height / 2 + 25);
        }

    }

    /**
     * Metodo que actualiza si se esta jugando
     *
     * @param e Evento
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (ESTADOJUEGO == 2) {
            actualiza();
        }
        renderer.repaint();
    }

    /**
     * Metodo main que crea una instancia de la aplicacion
     *
     * @param args
     */
    public static void main(String[] args) {
        pong = new Main();
        
    }

    /**
     * Metodo que espera a que se presionen las teclas para efectuar movimientos
     * en el juego
     *
     * @param e Evento en el que se presiona una tecla
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int id = e.getKeyCode();
        if (id == KeyEvent.VK_W) {
            w = true;
        } else if (id == KeyEvent.VK_S) {
            s = true;
        } else if (id == KeyEvent.VK_UP) {
            arriba = true;
        } else if (id == KeyEvent.VK_DOWN) {
            abajo = true;
        } else if (id == KeyEvent.VK_P) {
            try (BufferedReader br = new BufferedReader(new FileReader("puntajes.txt"))) {
                String line = "";
                while ((br.readLine()) != null) {
                    line = line + br.readLine();
                }
                JOptionPane.showMessageDialog(null, leerTexto("puntajes.txt"), "PUNTAJES", JOptionPane.DEFAULT_OPTION);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "No se logro leer los puntajes.", "ERROR DE LECTURA", JOptionPane.WARNING_MESSAGE);
            }
        } else if (id == KeyEvent.VK_M) {
            if (!musicaOn) {
                try {
                    in = new FileInputStream(sonido);
                    audio = new AudioStream(in);
                    AudioPlayer.player.start(audio);
                    musicaOn = true;
                    System.out.println("musicaOn" + musicaOn);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (musicaOn) {
                try {
                    in = new FileInputStream(sonido);
                    audio = new AudioStream(in);
                    AudioPlayer.player.stop(audio);
                    musicaOn = false;
                    System.out.println("musicaOn" + musicaOn);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        } else if (id == KeyEvent.VK_RIGHT) {

            if (seleccionaDificultad) {
                System.out.println("Derecha dificultad.");
                if (dificultadBot < 3) {
                    dificultadBot++;
                } else {
                    dificultadBot = 0;
                }
            } else if (ESTADOJUEGO == 0) {
                puntajeLimite++;
                System.out.println("Puntaje +1");
            }
        } else if (id == KeyEvent.VK_LEFT) {

            if (seleccionaDificultad) {
                System.out.println("Izquierda dificultad");
                if (dificultadBot > 0) {
                    dificultadBot--;
                } else {
                    dificultadBot = 3;
                }
            } else if (ESTADOJUEGO == 0 && puntajeLimite > 1) {
                puntajeLimite--;
                System.out.println("Puntaje -1");
            }
        } else if (id == KeyEvent.VK_ESCAPE && (ESTADOJUEGO == 2 || ESTADOJUEGO == 3)) {
            ESTADOJUEGO = 0;
        } else if (id == KeyEvent.VK_SHIFT && ESTADOJUEGO == 0) {
            bot = true;
            seleccionaDificultad = true;
        } else if (id == KeyEvent.VK_T) {
            if (tamañoRaqueta == 0) {
                tamañoRaqueta++;
            } else if (tamañoRaqueta == 1) {
                tamañoRaqueta++;
            } else if (tamañoRaqueta == 2) {
                tamañoRaqueta++;
            } else if (tamañoRaqueta == 3) {
                tamañoRaqueta = 0;
            }

        } else if (id == KeyEvent.VK_SPACE) {
            if (ESTADOJUEGO == 0 || ESTADOJUEGO == 3) {
                if (!seleccionaDificultad) {
                    bot = false;
                } else {
                    seleccionaDificultad = false;
                }
                try {
                    inicio();
                } catch (Exception ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (ESTADOJUEGO == 1) {
                ESTADOJUEGO = 2;
            } else if (ESTADOJUEGO == 2) {
                ESTADOJUEGO = 1;
            }
        }
    }

    /**
     * Metodo que define las acciones que se realizaran si se deja de presionar
     * una tecla
     *
     * @param e evento en el que se deja de presionar la tecla
     */
    @Override
    public void keyReleased(KeyEvent e) {
        int id = e.getKeyCode();

        switch (id) {
            case KeyEvent.VK_W:
                w = false;
                break;
            case KeyEvent.VK_S:
                s = false;
                break;
            case KeyEvent.VK_UP:
                arriba = false;
                break;
            case KeyEvent.VK_DOWN:
                abajo = false;
                break;
            default:
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    /**
     * Metodo Encargado de leer texto de un archivo txt
     * @param ruta Ruta del archivo del que se desea leer texto
     * @return Texto leido
     */
    public static String leerTexto(String ruta) {

        //Representa el texto dentro del archivo, por ahora, vacío.
        String texto = "";

        //El archivo de donde sacamos el texto.
        File archivo = new File(ruta);

        try {

            //Le ponemos al lector de archivo 'fr' el atributo 'archivo' en su constructor para leerlo después.
            FileReader fr = new FileReader(archivo);

            //Esta parte ayuda a la eficiencia de la lectura.
            BufferedReader buffer = new BufferedReader(fr);

            //Nos indica cuando es el fin del archivo.
            boolean fin = false;

            //Búfer para guardar las líneas de texto.
            StringBuffer sbf = new StringBuffer();

            //Salto de línea para que el texto salga en líneas separadas (opcional)
            String nl = System.getProperty("line.separator");

            //Mientras no sea el fin del archivo:
            while (fin == false) {

                //Leemos una línea del archivo.
                String linea = buffer.readLine();

                //Si la línea no está vacía...
                if (linea != null) {

                    //Agregamos al búfer la línea y el salto de línea 'nl' (opcional)
                    sbf.append(linea + nl);

                } else {

                    //De lo contrario damos fin al ciclo while.
                    fin = true;

                }

            }

            //Asignamos a 'texto' lo que se agregó al búfer:
            texto = sbf.toString();

        } catch (FileNotFoundException e) {

            //Esto se ejecuta si el archivo no fue encontrado.
            e.printStackTrace();

        } catch (IOException e) {

            //Esto se ejecuta si hay un error en la lectura del archivo.
            e.printStackTrace();

        }

        //Devolvemos el texto.
        return texto;
    }

}
