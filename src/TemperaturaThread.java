import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;

public class TemperaturaThread extends Thread implements Runnable {

    private Controlador controlador;
    public InetAddress ipAddress;
    public DatagramSocket socket;

    public int porta;
    public double[] arrayTempoResposta;

    public TemperaturaThread(Controlador controlador) {
        this.controlador = controlador;
        this.porta = 9090;
        try {
            this.socket = new DatagramSocket();
            this.ipAddress = InetAddress.getByName("localhost");
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.arrayTempoResposta = new double[10];
    }

    public void run() {
        while (true) {
            try {
                mudarTemperatura(enviarPacket("st-0"));

                Thread.sleep(50);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public DatagramPacket enviarPacket(String texto) {
        byte[] sendData;
        byte[] receiveData = new byte[1024];

        sendData = texto.getBytes();

        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, porta);

        Long tempoInicial = new Date().getTime();

        try {
            socket.send(sendPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return receberPacket(receiveData, tempoInicial);
    }

    public DatagramPacket receberPacket(byte[] receiveData, Long tempoInicial) {
        DatagramPacket receivePacket = new DatagramPacket(receiveData,
                receiveData.length);

        Long tempoFinal = new Date().getTime();

        Long diferencaTempo = tempoInicial - tempoFinal;

        ArrayList<Long> lista = new ArrayList<Long>();

        lista.add(diferencaTempo);

        System.out.println("Tempo de resposta: " + lista.get(0));

        try {
            socket.receive(receivePacket);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return receivePacket;
    }

    public void mudarTemperatura(DatagramPacket packet) {
        String resposta = new String(packet.getData());
        resposta = resposta.substring(3);

        Double novaTemperatura = Double.parseDouble(resposta);
        this.controlador.temperatura.setText(String.format("%.2f", novaTemperatura));
    }

}
