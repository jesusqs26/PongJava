package game;

import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 * Clase hija de JPanel que representa a la pantalla de la aplicacion
 * @author Jesus
 */
public class Renderer extends JPanel {

    /**
     * Dibuja los componentes en nuestra pantalla
     * @param g Componentes graficos a dibujar
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Main.pong.render((Graphics2D) g);
    }

}
