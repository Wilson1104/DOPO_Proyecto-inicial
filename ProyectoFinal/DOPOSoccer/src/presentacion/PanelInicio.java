package presentacion;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import dominio.Cuenta;
import dominio.DOPOSoccer;
import dominio.ExcepcionDOPOSoccer;
import dominio.Ranura;

/** Pantalla de inicio: dos ranuras de juego, cada una con las cuentas de dos jugadores. */
class PanelInicio extends JPanel {

    private static final long serialVersionUID = 1L;

    private final DOPOSoccerGUI ventana;
    private final DOPOSoccer soccer;
    private final JPanel ranuras = new JPanel(new GridLayout(1, DOPOSoccer.RANURAS, 30, 0));

    PanelInicio(DOPOSoccerGUI ventana, DOPOSoccer soccer) {
        super(new BorderLayout(0, 20));
        this.ventana = ventana;
        this.soccer = soccer;
        setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        JPanel titulos = new JPanel(new GridLayout(2, 1));
        titulos.setOpaque(false);
        JLabel titulo = Estilo.etiqueta("DOPOSoccer", Font.BOLD, 54);
        titulo.setForeground(Estilo.ACENTO);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel subtitulo = Estilo.etiqueta("Fútbol de cabezones para dos jugadores en el mismo teclado", Font.PLAIN, 18);
        subtitulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulos.add(titulo);
        titulos.add(subtitulo);
        add(titulos, BorderLayout.NORTH);

        ranuras.setOpaque(false);
        add(ranuras, BorderLayout.CENTER);

        JLabel ayuda = Estilo.etiqueta("Elige una ranura: crea un juego nuevo con los nombres de los dos jugadores "
            + "o continúa uno que ya tenga puntos.", Font.PLAIN, 14);
        ayuda.setForeground(Estilo.TEXTO_SUAVE);
        ayuda.setHorizontalAlignment(SwingConstants.CENTER);
        add(ayuda, BorderLayout.SOUTH);
    }

    void refrescar() {
        ranuras.removeAll();
        for (int i = 1; i <= DOPOSoccer.RANURAS; i++) ranuras.add(tarjeta(i));
        ranuras.revalidate();
        ranuras.repaint();
    }

    private JPanel tarjeta(int numero) {
        JPanel tarjeta = new JPanel(new BorderLayout(0, 12)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(255, 255, 255, 30));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        tarjeta.setOpaque(false);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Estilo.ACENTO, 2, true), BorderFactory.createEmptyBorder(18, 18, 18, 18)));

        JLabel nombre = Estilo.etiqueta("Ranura " + numero, Font.BOLD, 24);
        nombre.setHorizontalAlignment(SwingConstants.CENTER);
        tarjeta.add(nombre, BorderLayout.NORTH);

        JPanel datos = new JPanel();
        datos.setOpaque(false);
        datos.setLayout(new BoxLayout(datos, BoxLayout.Y_AXIS));
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        botones.setOpaque(false);
        try {
            Ranura ranura = soccer.getRanura(numero);
            if (ranura.estaVacia()) {
                datos.add(Estilo.etiqueta("Vacía", Font.ITALIC, 18));
            } else {
                for (int j = 1; j <= 2; j++) {
                    Cuenta cuenta = ranura.getCuenta(j);
                    datos.add(Estilo.etiqueta("Jugador " + j + ": " + cuenta.getNombre(), Font.BOLD, 16));
                    JLabel puntos = Estilo.etiqueta(cuenta.getPuntos() + " puntos", Font.PLAIN, 14);
                    puntos.setForeground(Estilo.TEXTO_SUAVE);
                    datos.add(puntos);
                }
                JButton continuar = Estilo.boton("Continuar");
                continuar.addActionListener(e -> continuar(numero));
                botones.add(continuar);
            }
        } catch (ExcepcionDOPOSoccer e) {
            ventana.manejarError(e);
        }
        JButton nuevo = Estilo.boton("Nuevo juego");
        nuevo.addActionListener(e -> nuevoJuego(numero));
        botones.add(nuevo);
        tarjeta.add(datos, BorderLayout.CENTER);
        tarjeta.add(botones, BorderLayout.SOUTH);
        return tarjeta;
    }

    private void nuevoJuego(int numero) {
        try {
            if (!soccer.getRanura(numero).estaVacia()) {
                int respuesta = JOptionPane.showConfirmDialog(ventana,
                    "La ranura " + numero + " ya tiene un juego. Si creas uno nuevo se pierden sus puntos. ¿Continuar?",
                    "Nuevo juego", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (respuesta != JOptionPane.YES_OPTION) return;
            }
            String[] nombres = new DialogoNombres(ventana, numero).pedir();
            if (nombres == null) return;
            soccer.nuevoJuego(numero, nombres[0], nombres[1]);
            ventana.mostrarConfiguracion();
        } catch (Exception e) {
            ventana.manejarError(e);
        }
    }

    private void continuar(int numero) {
        try {
            soccer.seleccionarRanura(numero);
            ventana.mostrarConfiguracion();
        } catch (Exception e) {
            ventana.manejarError(e);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Estilo.pintarFondo(g, this);
    }
}
