import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalTime;
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

    public DatagramPacket enviarPacket(String texto) throws IOException {
        byte[] sendData;
        byte[] receiveData = new byte[1024];

        sendData = texto.getBytes();

        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, porta);

        LocalTime tempoIncial = java.time.LocalTime.now();

        try {
            socket.send(sendPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return receberPacket(receiveData, tempoIncial.getNano());
    }

    public DatagramPacket receberPacket(byte[] receiveData, int tempoInicial) throws IOException {
        DatagramPacket receivePacket = new DatagramPacket(receiveData,
                receiveData.length);

        LocalTime tempoFinal = java.time.LocalTime.now();

        int diferencaTempo = tempoFinal.getNano() - tempoInicial;

        ArrayList<Integer> lista = new ArrayList<Integer>();

        lista.add(diferencaTempo);

        for (int i = 0; i < lista.size(); i++) {
            criarArquivoTXT(lista.get(i));
            System.out.println("Tempo de resposta: " + lista.get(i));
        }

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

    public void criarArquivoTXT(int tempoResposta) throws IOException {
        FileWriter arq = new FileWriter("C:\\Users\\matheus_hohmann\\Desktop\\tempo_resposta.txt", true);
        BufferedWriter gravarArq = new BufferedWriter(arq);

        Date dataAtual = new Date();

        gravarArq.write("Data Atual -  " + dataAtual + " - Tempo de resposta (ms): " + tempoResposta);
        gravarArq.newLine();

        gravarArq.close();
        arq.close();
    }

}
