package presentacion;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import dominio.Cuenta;
import dominio.DOPOSoccer;
import dominio.Estadisticas;
import dominio.ExcepcionDOPOSoccer;
import dominio.TipoEstadistica;

/** Tienda de mejoras: cada jugador gasta sus puntos para subir de nivel sus estadísticas. */
class DialogoMejoras extends JDialog {

    private static final long serialVersionUID = 1L;

    private final DOPOSoccerGUI ventana;
    private final DOPOSoccer soccer;
    private final JPanel columnas = new JPanel(new GridLayout(1, 2, 24, 0));

    DialogoMejoras(DOPOSoccerGUI ventana, DOPOSoccer soccer) {
        super(ventana, "Mejorar estadísticas", true);
        this.ventana = ventana;
        this.soccer = soccer;
        JPanel contenido = new JPanel(new BorderLayout(0, 14));
        contenido.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        contenido.add(new JLabel("<html>Cada nivel suma un 10 % sobre el valor base. El precio se duplica cada vez que "
            + "compras esa estadística: 500, 1000, 2000... Las estadísticas van de 1 a 10.</html>"), BorderLayout.NORTH);
        contenido.add(columnas, BorderLayout.CENTER);
        JButton cerrar = new JButton("Cerrar");
        cerrar.addActionListener(e -> dispose());
        JPanel abajo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        abajo.add(cerrar);
        contenido.add(abajo, BorderLayout.SOUTH);
        setContentPane(contenido);
        refrescar();
        setSize(760, 360);
        setLocationRelativeTo(ventana);
    }

    private void refrescar() {
        columnas.removeAll();
        try {
            for (int j = 1; j <= 2; j++) columnas.add(columna(j, soccer.getRanura(soccer.getRanuraActual()).getCuenta(j)));
        } catch (ExcepcionDOPOSoccer e) {
            ventana.manejarError(e);
        }
        columnas.revalidate();
        columnas.repaint();
    }

    private JPanel columna(int jugador, Cuenta cuenta) throws ExcepcionDOPOSoccer {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(cuenta.getNombre() + "  —  " + cuenta.getPuntos() + " puntos"));
        Estadisticas estadisticas = cuenta.getEstadisticas();
        int fila = 0;
        for (TipoEstadistica tipo : TipoEstadistica.values()) {
            int nivel = estadisticas.getNivel(tipo);
            GridBagConstraints c = new GridBagConstraints();
            c.gridy = fila++;
            c.insets = new Insets(5, 6, 5, 6);
            c.anchor = GridBagConstraints.WEST;
            JLabel nombre = new JLabel(nombre(tipo));
            nombre.setFont(nombre.getFont().deriveFont(Font.BOLD));
            panel.add(nombre, c);
            c.gridx = 1;
            panel.add(new JLabel("Nivel " + nivel + " / " + Estadisticas.NIVEL_MAXIMO), c);
            c.gridx = 2;
            c.weightx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            JButton comprar;
            if (nivel >= Estadisticas.NIVEL_MAXIMO) {
                comprar = new JButton("Nivel máximo");
                comprar.setEnabled(false);
            } else {
                comprar = new JButton("Mejorar (" + estadisticas.precioMejora(tipo) + " pts)");
                comprar.addActionListener(e -> comprar(jugador, tipo));
            }
            panel.add(comprar, c);
        }
        return panel;
    }

    private void comprar(int jugador, TipoEstadistica tipo) {
        try {
            soccer.mejorarEstadistica(jugador, tipo);
        } catch (Exception e) {
            ventana.manejarError(e);
        }
        refrescar();
    }

    private static String nombre(TipoEstadistica tipo) {
        switch (tipo) {
            case VELOCIDAD:
                return "Velocidad (speed)";
            case SALTO:
                return "Salto (jump)";
            case PATADA:
                return "Patada (kick)";
            default:
                return "Supervivencia (vida)";
        }
    }
}
