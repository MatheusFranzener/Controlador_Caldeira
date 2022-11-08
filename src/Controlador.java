
import javax.swing.*;

public class Controlador {
    private JPanel panel1;
    public JLabel temperatura;
    public JLabel tanque;
    public JLabel agua;

    public Controlador(){
        JFrame frame = new JFrame("Controlador");
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        TemperaturaThread temperaturaThread = new TemperaturaThread(this);
        temperaturaThread.start();
        SaidaAguaThread saidaAguaThread = new SaidaAguaThread(this);
        saidaAguaThread.start();
        TanqueThread tanqueThread = new TanqueThread(this);
        tanqueThread.start();
    }

    public static void main(String[] args) {
        new Controlador();
    }

}
