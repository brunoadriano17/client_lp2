/*
    TRABALHO DE LP2
    BRUNO ADRIANO E MARCELO CARBONO

*/
package chat.lp2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bruno
 */
public class ServerConnection extends Thread {

    Socket socket;
    InputStream in;
    OutputStream out;
    boolean emOperacao = true;
    String nome_usuario;
    Servidor servidor;

    public ServerConnection(Socket socket, Servidor servidor, String nome_usuario) throws IOException {
        super("ServerConnectionThread");
        this.socket = socket;
        this.servidor = servidor;
        in = this.socket.getInputStream();
        out = this.socket.getOutputStream();
        this.nome_usuario = nome_usuario;
    }

    public void enviaMsg(String msg) throws IOException {
        PrintStream saida = new PrintStream(socket.getOutputStream());
        saida.println(msg);
    }

    public void enviarMensagem(String msg) throws IOException {
        for (int i = 0; i < servidor.connections.size(); i++) {
            ServerConnection sc = servidor.connections.get(i);
            sc.enviaMsg(msg);
        }
    }

    public void run() {
        Scanner entrada;
        try {
            entrada = new Scanner(socket.getInputStream());
            while (emOperacao) {
                while (entrada.hasNext()) {
                    /* LE MENSAGEM ENVIADA PELO CLIENTE */
                    String msg = entrada.nextLine();
                    decodifica(msg);
                }
            }
            in.close();
            out.close();
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

    }  
   
    public void listarUsuarios() throws IOException {
        String result = "";
        if (servidor.connections.size() == 1) {
            result += servidor.connections.get(0).nome_usuario;
        } else {
            int i = 0;
            for (; i < servidor.connections.size()-1; i++) {
                result += servidor.connections.get(i).nome_usuario + ";";
            }
            result += servidor.connections.get(i).nome_usuario;
            
        }
        for (int i = 0; i < servidor.connections.size(); i++) {
            ServerConnection sc = servidor.connections.get(i);
            sc.enviaMsg("lista_usuarios:"+result);
        }

    }    
    
    public void decodifica(String msg) throws IOException {
        int i = 0;
        String operacao = "";
        String nomes = "";
        String mensagem = "";
        for (; msg.charAt(i) != ':'; i++) {
            operacao += msg.charAt(i);
        }
        i++;

        if (operacao.equals("mensagem")) {
            /* SE FOR PARA TODOS */
            if (msg.charAt(i) == '*') {
                i++;
                i++;
                for (; i < msg.length(); i++) {
                    mensagem += msg.charAt(i);
                }
                /* ENVIA MENSAGEM PARA TODOS USUARIOS */
                enviarMensagem("transmitir:" + nome_usuario + ":*:" + mensagem);
            } else {
                for (; msg.charAt(i) != ':'; i++) {
                    nomes += msg.charAt(i);
                }
                i++;
                for (; i < msg.length(); i++) {
                    mensagem += msg.charAt(i);
                }

                ArrayList<String> nomesD = new ArrayList<String>();
                String nUsuario = "";
                for (int j = 0; j < nomes.length(); j++) {
                    if (nomes.charAt(j) == ';') {
                        nomesD.add(nUsuario);
                        nUsuario = "";
                    } else {
                        nUsuario += nomes.charAt(j);
                    }
                }
                nomesD.add(nUsuario);
                int verifica = 0;
                for (int j = 0; j < nomesD.size(); j++) {
                    for (int m = 0; m < servidor.connections.size(); m++) {
                        ServerConnection sc = servidor.connections.get(m);
                        if (sc.nome_usuario.equals(nomesD.get(j))) {
                            sc.enviaMsg("transmitir:" + nome_usuario + ":" + nomes + ":" + mensagem);
                            verifica = 1;
                        }
                    }
                }
                if (verifica == 1) {
                    enviaMsg("transmitir:" + nome_usuario + ":" + nomes + ":" + mensagem);
                } else {
                    enviaMsg("transmitir:Sistema:"+nome_usuario+":Usuário não encontrado");
                }
            }
        }

    }

}
