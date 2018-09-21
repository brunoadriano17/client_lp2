/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cliente;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author Bruno
 */
public class ClientConnection extends Thread {

    Socket socket;
    InputStream in;
    OutputStream out;
    boolean emOperacao = true;
    Cliente c;

    public ClientConnection(Socket socket, Cliente c) throws IOException {
        this.socket = socket;
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
        this.c = c;
    }

    public void sendMsgServer(String msg) {
        PrintStream saida = new PrintStream(out);
        System.out.println("out: " + out);
        saida.println("msg");
    }

    public void run() {
        while (emOperacao) {
            Scanner entrada;
            try {
                System.out.println("xD");
                entrada = new Scanner(socket.getInputStream());

                while (entrada.hasNext()) {
                    String msg = entrada.nextLine();
                    System.out.println(msg);
                }
            } catch (IOException ex) {
                System.out.println("Erro: "+ex);
            }
        }
    }
}
