package presentacion;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JMenuItem;

import dominio.Cuenta;
import dominio.DOPOSoccer;
import dominio.ExcepcionDOPOSoccer;
import dominio.Personaje;
import dominio.Resolucion;

/**
 * Pantalla de preparación del partido: la imagen de fondo y, abajo, cuatro casillas: Tienda, Modo de juego,
 * Personaje y Jugar. Modo de juego y Personaje abren un menú desplegable sobre su casilla.
 */
class PanelConfiguracion extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int[] MINUTOS = {1, 2, 3, 5};
    private static final int[] GOLES = {1, 3, 5, 10};

    private final DOPOSoccerGUI ventana;
    private final DOPOSoccer soccer;

    private final Casilla tienda = new Casilla("Tienda", false);
    private final Casilla modo = new Casilla("Modo de juego", false);
    private final Casilla personaje = new Casilla("Personaje", false);
    private final Casilla jugar = new Casilla("Jugar", true);

    private Resolucion resolucion = Resolucion.BAJA;
    private boolean contraMaquina;
    private String perfilMaquina = "Ofensiva";
    private boolean porTiempo = true;
    private int limite = 2;
    private String pais1 = Personaje.COREA_DEL_SUR;
    private String pais2 = Personaje.CAMERUN;

    PanelConfiguracion(DOPOSoccerGUI ventana, DOPOSoccer soccer) {
        super(new BorderLayout());
        this.ventana = ventana;
        this.soccer = soccer;
        setOpaque(true);

        JButton volver = Estilo.boton("← Volver");
        volver.addActionListener(e -> ventana.mostrarInicio());
        JPanel arriba = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 14));
        arriba.setOpaque(false);
        arriba.add(volver);
        add(arriba, BorderLayout.NORTH);

        JPanel casillas = new JPanel(new GridLayout(1, 4, 16, 0));
        casillas.setOpaque(false);
        casillas.setBorder(BorderFactory.createEmptyBorder(0, 28, 22, 28));
        casillas.add(tienda);
        casillas.add(modo);
        casillas.add(personaje);
        casillas.add(jugar);
        add(casillas, BorderLayout.SOUTH);

        tienda.addActionListener(e -> abrirTienda());
        modo.addActionListener(e -> mostrar(menuModo(), modo));
        personaje.addActionListener(e -> mostrar(menuPersonaje(), personaje));
        jugar.addActionListener(e -> jugar());
    }

    /** Actualiza lo que dice cada casilla con la ranura en juego y las opciones elegidas. */
    void refrescar() {
        try {
            Cuenta c1 = soccer.getRanura(soccer.getRanuraActual()).getCuenta(1);
            Cuenta c2 = soccer.getRanura(soccer.getRanuraActual()).getCuenta(2);
            String nombre2 = contraMaquina ? "Máquina" : c2.getNombre();
            tienda.detalle(c1.getNombre() + ": " + c1.getPuntos() + " pts", c2.getNombre() + ": " + c2.getPuntos() + " pts");
            modo.detalle(contraMaquina ? "Jugador vs máquina (" + perfilMaquina.toLowerCase() + ")" : "Jugador vs jugador",
                descripcionLimite() + " · " + nombreResolucion(resolucion));
            personaje.detalle(c1.getNombre() + ": " + pais1, nombre2 + ": " + pais2);
            jugar.detalle(c1.getNombre() + " vs " + nombre2, "¡A jugar!");
        } catch (ExcepcionDOPOSoccer e) {
            ventana.manejarError(e);
            ventana.mostrarInicio();
        }
    }

    // ------------------------------------------------------------------ acciones

    private void abrirTienda() {
        new DialogoMejoras(ventana, soccer).setVisible(true);
        refrescar();
    }

    private void jugar() {
        try {
            soccer.nuevoPartido(resolucion, pais1, pais2, contraMaquina ? perfilMaquina : null, porTiempo, limite);
            ventana.mostrarJuego(resolucion, contraMaquina, contraMaquina ? perfilMaquina : null);
        } catch (Exception e) {
            ventana.manejarError(e);
        }
    }

    // ------------------------------------------------------------------ menús desplegables

    private JPopupMenu menuModo() {
        JPopupMenu menu = new JPopupMenu();

        ButtonGroup grupo = new ButtonGroup();
        JRadioButtonMenuItem jugadorVsJugador = radio("Jugador vs jugador", !contraMaquina, grupo);
        jugadorVsJugador.addActionListener(e -> {
            contraMaquina = false;
            refrescar();
        });
        menu.add(jugadorVsJugador);

        JMenu maquina = new JMenu("Jugador vs máquina");
        maquina.setFont(Estilo.letra(Font.PLAIN, 14));
        ButtonGroup perfiles = new ButtonGroup();
        for (String perfil : DOPOSoccer.PERFILES_MAQUINA) {
            JRadioButtonMenuItem item = radio("Máquina " + perfil.toLowerCase(), contraMaquina && perfil.equals(perfilMaquina), perfiles);
            item.addActionListener(e -> {
                contraMaquina = true;
                perfilMaquina = perfil;
                refrescar();
            });
            maquina.add(item);
        }
        menu.add(maquina);
        menu.addSeparator();

        JMenu tipo = new JMenu("Tipo de partido");
        tipo.setFont(Estilo.letra(Font.PLAIN, 14));
        JMenu tiempo = new JMenu("Por tiempo");
        tiempo.setFont(Estilo.letra(Font.PLAIN, 14));
        ButtonGroup limites = new ButtonGroup();
        for (int minutos : MINUTOS) {
            JRadioButtonMenuItem item = radio(minutos + (minutos == 1 ? " minuto" : " minutos"), porTiempo && limite == minutos, limites);
            item.addActionListener(e -> elegirLimite(true, minutos));
            tiempo.add(item);
        }
        tiempo.add(otro(true));
        JMenu goles = new JMenu("Por goles");
        goles.setFont(Estilo.letra(Font.PLAIN, 14));
        for (int cantidad : GOLES) {
            JRadioButtonMenuItem item = radio(cantidad + (cantidad == 1 ? " gol" : " goles"), !porTiempo && limite == cantidad, limites);
            item.addActionListener(e -> elegirLimite(false, cantidad));
            goles.add(item);
        }
        goles.add(otro(false));
        tipo.add(tiempo);
        tipo.add(goles);
        menu.add(tipo);

        JMenu cancha = new JMenu("Resolución de la cancha");
        cancha.setFont(Estilo.letra(Font.PLAIN, 14));
        ButtonGroup resoluciones = new ButtonGroup();
        for (Resolucion r : Resolucion.values()) {
            JRadioButtonMenuItem item = radio(nombreResolucion(r) + " (" + r.getAncho() + " x " + r.getAlto() + ")", r == resolucion, resoluciones);
            item.addActionListener(e -> {
                resolucion = r;
                refrescar();
            });
            cancha.add(item);
        }
        menu.add(cancha);
        return menu;
    }

    private JPopupMenu menuPersonaje() {
        JPopupMenu menu = new JPopupMenu();
        String nombre1 = "Jugador 1";
        String nombre2 = contraMaquina ? "Máquina" : "Jugador 2";
        try {
            nombre1 = soccer.getRanura(soccer.getRanuraActual()).getCuenta(1).getNombre();
            if (!contraMaquina) nombre2 = soccer.getRanura(soccer.getRanuraActual()).getCuenta(2).getNombre();
        } catch (ExcepcionDOPOSoccer e) {
            // sin ranura: se dejan los nombres genéricos
        }
        menu.add(menuPaises(nombre1, pais1, true));
        menu.add(menuPaises(nombre2, pais2, false));
        return menu;
    }

    private JMenu menuPaises(String titulo, String actual, boolean primero) {
        JMenu menu = new JMenu(titulo + "  (" + actual + ")");
        menu.setFont(Estilo.letra(Font.PLAIN, 14));
        ButtonGroup grupo = new ButtonGroup();
        for (String pais : Personaje.paisesDisponibles()) {
            JRadioButtonMenuItem item = radio(pais, pais.equals(actual), grupo);
            item.addActionListener(e -> {
                if (primero) pais1 = pais;
                else pais2 = pais;
                refrescar();
            });
            menu.add(item);
        }
        return menu;
    }

    private JMenuItem otro(boolean minutos) {
        JMenuItem item = new JMenuItem("Otro...");
        item.setFont(Estilo.letra(Font.PLAIN, 14));
        item.addActionListener(e -> {
            String texto = JOptionPane.showInputDialog(ventana, minutos ? "¿Cuántos minutos dura el partido?" : "¿A cuántos goles se gana?",
                "Tipo de partido", JOptionPane.QUESTION_MESSAGE);
            if (texto == null) return;
            try {
                int valor = Integer.parseInt(texto.trim());
                if (valor < 1 || valor > (minutos ? 60 : 99)) throw new NumberFormatException();
                elegirLimite(minutos, valor);
            } catch (NumberFormatException ex) {
                ventana.manejarError(new ExcepcionDOPOSoccer(ExcepcionDOPOSoccer.CONFIGURACION_INVALIDA));
            }
        });
        return item;
    }

    private void elegirLimite(boolean tiempo, int valor) {
        porTiempo = tiempo;
        limite = valor;
        refrescar();
    }

    private void mostrar(JPopupMenu menu, Casilla casilla) {
        menu.show(casilla, 0, -menu.getPreferredSize().height - 6);
    }

    private static JRadioButtonMenuItem radio(String texto, boolean elegido, ButtonGroup grupo) {
        JRadioButtonMenuItem item = new JRadioButtonMenuItem(texto, elegido);
        item.setFont(Estilo.letra(Font.PLAIN, 14));
        grupo.add(item);
        return item;
    }

    private String descripcionLimite() {
        if (porTiempo) return limite + (limite == 1 ? " minuto" : " min");
        return "a " + limite + (limite == 1 ? " gol" : " goles");
    }

    static String nombreResolucion(Resolucion r) {
        switch (r) {
            case BAJA:
                return "Baja";
            case MEDIA:
                return "Media";
            default:
                return "Alta";
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Estilo.pintarFondoImagen(g, this);
    }

    /** Casilla de la parte baja: un título grande y hasta dos líneas con la opción elegida. */
    private static class Casilla extends JButton {

        private static final long serialVersionUID = 1L;

        private final String titulo;
        private final boolean destacada;
        private String linea1 = "";
        private String linea2 = "";

        Casilla(String titulo, boolean destacada) {
            this.titulo = titulo;
            this.destacada = destacada;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setRolloverEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(200, 104));
        }

        void detalle(String linea1, String linea2) {
            this.linea1 = linea1;
            this.linea2 = linea2;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            boolean encima = getModel().isRollover() || getModel().isPressed();
            int w = getWidth();
            int h = getHeight();
            Color fondo = destacada ? new Color(255, 196, 0, encima ? 245 : 225) : new Color(10, 20, 45, encima ? 238 : 205);
            g2.setColor(fondo);
            g2.fillRoundRect(0, 0, w - 1, h - 1, 22, 22);
            g2.setColor(destacada ? new Color(120, 70, 0) : (encima ? Color.WHITE : Estilo.ACENTO));
            g2.setStroke(new BasicStroke(3f));
            g2.drawRoundRect(1, 1, w - 3, h - 3, 20, 20);

            Color colorTitulo = destacada ? new Color(40, 25, 0) : Estilo.ACENTO;
            Color colorDetalle = destacada ? new Color(60, 40, 0) : Color.WHITE;
            centrado(g2, titulo, Estilo.letra(Font.BOLD, 23), colorTitulo, w, 38);
            centrado(g2, linea1, Estilo.letra(Font.PLAIN, 13), colorDetalle, w, 64);
            centrado(g2, linea2, Estilo.letra(Font.PLAIN, 13), colorDetalle, w, 84);
            g2.dispose();
        }

        private static void centrado(Graphics2D g2, String texto, Font letra, Color color, int ancho, int y) {
            g2.setFont(letra);
            FontMetrics m = g2.getFontMetrics();
            String mostrado = texto;
            while (m.stringWidth(mostrado) > ancho - 16 && mostrado.length() > 4) {
                mostrado = mostrado.substring(0, mostrado.length() - 2).trim() + "…";
            }
            g2.setColor(color);
            g2.drawString(mostrado, (ancho - m.stringWidth(mostrado)) / 2, y);
        }
    }
}
