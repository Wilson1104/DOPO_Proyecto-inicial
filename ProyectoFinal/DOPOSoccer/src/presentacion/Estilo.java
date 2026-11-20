package presentacion;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;

import dominio.Personaje;
import dominio.RegistroDeErrores;
import dominio.TiroDePoder;

/** Colores, letras y piezas visuales que comparten las pantallas. */
final class Estilo {

    static final Color FONDO_ARRIBA = new Color(16, 32, 64);
    static final Color FONDO_ABAJO = new Color(20, 92, 60);
    static final Color ACENTO = new Color(255, 196, 0);
    static final Color TEXTO = Color.WHITE;
    static final Color TEXTO_SUAVE = new Color(205, 215, 230);

    private static final String[] RUTAS_FONDO = {"recursos/fondo.jpg", "DOPOSoccer/recursos/fondo.jpg"};
    private static BufferedImage fondo;
    private static boolean fondoIntentado;

    private Estilo() {
    }

    static Font letra(int estilo, float tamanio) {
        return new Font("SansSerif", estilo, Math.round(tamanio));
    }

    static JLabel etiqueta(String texto, int estilo, float tamanio) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(letra(estilo, tamanio));
        etiqueta.setForeground(TEXTO);
        return etiqueta;
    }

    static JButton boton(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(letra(Font.BOLD, 14));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(boton.getBorder(), BorderFactory.createEmptyBorder(4, 12, 4, 12)));
        return boton;
    }

    static void pintarFondo(Graphics g, Component c) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(new GradientPaint(0, 0, FONDO_ARRIBA, 0, c.getHeight(), FONDO_ABAJO));
        g2.fillRect(0, 0, c.getWidth(), c.getHeight());
    }

    /** Dibuja la imagen de fondo cubriendo todo el componente (recorta lo que sobre). Sin imagen, usa el degradado. */
    static void pintarFondoImagen(Graphics g, Component c) {
        BufferedImage imagen = fondo();
        if (imagen == null) {
            pintarFondo(g, c);
            return;
        }
        double escala = Math.max(c.getWidth() / (double) imagen.getWidth(), c.getHeight() / (double) imagen.getHeight());
        int ancho = (int) Math.ceil(imagen.getWidth() * escala);
        int alto = (int) Math.ceil(imagen.getHeight() * escala);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.drawImage(imagen, (c.getWidth() - ancho) / 2, (c.getHeight() - alto) / 2, ancho, alto, null);
    }

    private static BufferedImage fondo() {
        if (!fondoIntentado) {
            fondoIntentado = true;
            for (String ruta : RUTAS_FONDO) {
                File archivo = new File(ruta);
                if (!archivo.isFile()) continue;
                try {
                    fondo = ImageIO.read(archivo);
                    break;
                } catch (IOException e) {
                    RegistroDeErrores.registrar(e);
                }
            }
        }
        return fondo;
    }

    /** Color del cuerpo del personaje de cada país. */
    static Color colorPrincipal(String pais) {
        if (Personaje.COREA_DEL_SUR.equals(pais)) return new Color(215, 50, 60);
        if (Personaje.CAMERUN.equals(pais)) return new Color(245, 190, 20);
        if (Personaje.NIGERIA.equals(pais)) return new Color(40, 165, 75);
        if (Personaje.ESTADOS_UNIDOS.equals(pais)) return new Color(35, 80, 190);
        return Color.GRAY;
    }

    /** Color de la franja del cuerpo. */
    static Color colorSecundario(String pais) {
        if (Personaje.COREA_DEL_SUR.equals(pais)) return new Color(0, 71, 160);
        if (Personaje.CAMERUN.equals(pais)) return new Color(0, 130, 70);
        if (Personaje.NIGERIA.equals(pais)) return Color.WHITE;
        if (Personaje.ESTADOS_UNIDOS.equals(pais)) return new Color(200, 30, 60);
        return Color.DARK_GRAY;
    }

    /** Color del aura y del balón cargado con cada tiro de poder. */
    static Color colorTiro(TiroDePoder tiro) {
        if (tiro == null) return Color.WHITE;
        switch (tiro.getNombre()) {
            case "Aura Azul":
                return new Color(60, 150, 255);
            case "Rayo":
                return new Color(255, 225, 40);
            case "Tornado":
                return new Color(215, 215, 225);
            default:
                return new Color(235, 70, 200);
        }
    }
}
