package presentacion;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** Pide los nombres de los dos jugadores. Enter pasa de un campo al siguiente y, en el último, acepta. */
class DialogoNombres extends JDialog {

    private static final long serialVersionUID = 1L;

    private final JTextField nombre1 = new JTextField(16);
    private final JTextField nombre2 = new JTextField(16);
    private String[] resultado;

    DialogoNombres(Frame ventana, int ranura) {
        super(ventana, "Nuevo juego en la ranura " + ranura, true);
        JPanel formulario = new JPanel(new GridLayout(2, 2, 10, 10));
        formulario.add(new JLabel("Nombre del jugador 1:"));
        formulario.add(nombre1);
        formulario.add(new JLabel("Nombre del jugador 2:"));
        formulario.add(nombre2);

        JButton aceptar = new JButton("Aceptar");
        JButton cancelar = new JButton("Cancelar");
        aceptar.addActionListener(e -> aceptar());
        cancelar.addActionListener(e -> dispose());
        nombre1.addActionListener(e -> nombre2.requestFocusInWindow());
        nombre2.addActionListener(e -> aceptar());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.add(aceptar);
        botones.add(cancelar);

        JPanel contenido = new JPanel(new BorderLayout(0, 14));
        contenido.setBorder(BorderFactory.createEmptyBorder(16, 20, 14, 20));
        contenido.add(new JLabel("Escribe los nombres y presiona Enter."), BorderLayout.NORTH);
        contenido.add(formulario, BorderLayout.CENTER);
        contenido.add(botones, BorderLayout.SOUTH);
        setContentPane(contenido);
        pack();
        setLocationRelativeTo(ventana);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                nombre1.requestFocusInWindow();
            }
        });
    }

    /** @return los dos nombres, o null si se canceló */
    String[] pedir() {
        setVisible(true);
        return resultado;
    }

    private void aceptar() {
        resultado = new String[] {nombre1.getText(), nombre2.getText()};
        dispose();
    }
}
