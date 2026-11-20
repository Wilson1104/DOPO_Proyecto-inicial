package presentacion;

import java.awt.CardLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import dominio.DOPOSoccer;
import dominio.ExcepcionDOPOSoccer;
import dominio.Jugador;
import dominio.Partido;
import dominio.RegistroDeErrores;
import dominio.Resolucion;

/**
 * Ventana principal de DOPOSoccer. Cambia entre la pantalla de inicio (ranuras), la configuración
 * del partido y el juego. Es la única que muestra mensajes de error al usuario.
 */
public class DOPOSoccerGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final String INICIO = "inicio";
    private static final String CONFIGURACION = "configuracion";
    private static final String JUEGO = "juego";
    private static final int ANCHO_MENU = 980;
    private static final int ALTO_MENU = 650;

    private final DOPOSoccer soccer = new DOPOSoccer();
    private final CardLayout tarjetas = new CardLayout();
    private final JPanel contenido = new JPanel(tarjetas);
    private final PanelInicio panelInicio;
    private final PanelConfiguracion panelConfiguracion;
    private PanelJuego panelJuego;

    public DOPOSoccerGUI() {
        super("DOPOSoccer");
        panelInicio = new PanelInicio(this, soccer);
        panelConfiguracion = new PanelConfiguracion(this, soccer);
        contenido.add(panelInicio, INICIO);
        contenido.add(panelConfiguracion, CONFIGURACION);
        setContentPane(contenido);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                salir();
            }
        });
        mostrarInicio();
    }

    void mostrarInicio() {
        quitarJuego();
        panelInicio.refrescar();
        tarjetas.show(contenido, INICIO);
        tamanioDeMenu();
    }

    void mostrarConfiguracion() {
        quitarJuego();
        panelConfiguracion.refrescar();
        tarjetas.show(contenido, CONFIGURACION);
        tamanioDeMenu();
    }

    /** El partido ya fue creado en el dominio; aquí solo se muestra y arranca el ciclo del juego. */
    void mostrarJuego(Resolucion resolucion, boolean contraMaquina, String perfilMaquina) {
        quitarJuego();
        panelJuego = new PanelJuego(this, soccer, resolucion, contraMaquina, perfilMaquina);
        contenido.add(panelJuego, JUEGO);
        tarjetas.show(contenido, JUEGO);
        pack();
        setLocationRelativeTo(null);
        panelJuego.iniciar();
    }

    /** Anuncia el ganador y los puntos obtenidos, y vuelve a la configuración. */
    void partidoTerminado() {
        Partido partido = soccer.getPartido();
        Jugador ganador = partido.getGanador();
        StringBuilder mensaje = new StringBuilder();
        if (ganador == null) {
            mensaje.append("¡Empate!\n\n");
        } else {
            mensaje.append("¡Ganó ").append(ganador.getCuenta().getNombre())
                   .append(" con ").append(ganador.getPersonaje().getPais()).append("!\n\n");
        }
        for (int i = 1; i <= 2; i++) {
            Jugador jugador = partido.getJugador(i);
            int puntos = jugador == ganador ? Partido.PUNTOS_GANADOR : Partido.PUNTOS_PERDEDOR;
            mensaje.append(jugador.getCuenta().getNombre()).append(": ").append(jugador.getGoles())
                   .append(jugador.getGoles() == 1 ? " gol, +" : " goles, +").append(puntos)
                   .append(" puntos (total ").append(jugador.getCuenta().getPuntos()).append(")\n");
        }
        JOptionPane.showMessageDialog(this, mensaje.toString(), "Fin del partido", JOptionPane.INFORMATION_MESSAGE);
        mostrarConfiguracion();
    }

    /** Errores del juego: mensaje claro. Errores inesperados: además se guardan en el log. */
    void manejarError(Exception e) {
        if (e instanceof ExcepcionDOPOSoccer) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "DOPOSoccer", JOptionPane.WARNING_MESSAGE);
        } else {
            RegistroDeErrores.registrar(e);
            JOptionPane.showMessageDialog(this,
                "Ocurrió un error inesperado: " + e.getMessage() + "\nEl detalle quedó en " + RegistroDeErrores.ARCHIVO + ".",
                "DOPOSoccer", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void salir() {
        if (panelJuego != null) panelJuego.pausarSiJuega();
        int respuesta = JOptionPane.showConfirmDialog(this, "¿Seguro que quieres salir de DOPOSoccer?", "Salir",
            JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }

    private void quitarJuego() {
        if (panelJuego == null) return;
        panelJuego.detener();
        contenido.remove(panelJuego);
        panelJuego = null;
    }

    private void tamanioDeMenu() {
        setSize(ANCHO_MENU, ALTO_MENU);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((hilo, error) ->
            RegistroDeErrores.registrar(error instanceof Exception ? (Exception) error : new Exception(error)));
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                RegistroDeErrores.registrar(e);
            }
            new DOPOSoccerGUI().setVisible(true);
        });
    }
}
