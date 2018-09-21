/* TRABALHO DE LP2 */
/* BRUNO ADRIANO E MARCELO CARBONO*/

package chat.lp2;

import java.io.IOException;
import java.io.PrintStream;
import static java.lang.Thread.sleep;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bruno
 */
public class Servidor {

    ServerSocket servidor;
    ArrayList<ServerConnection> connections = new ArrayList<ServerConnection>();
    boolean emOperacao = true;

    public Servidor() throws IOException {

        /* INICIALIZA SERVIDOR NA PORTA X */
        servidor = new ServerSocket(6666);
        System.out.println("Servidor inicializado!");

        /* LOOP INFINITO PARA ACEITAR NOVOS CLIENTES*/
        while (emOperacao) {
            Socket socket = servidor.accept();
            /* VERIFICAR SE USUÁRIO É ACEITAVEL */
            Scanner entrada = new Scanner(socket.getInputStream());
            while (entrada.hasNext()) {
                try {
                    sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }
                String result = entrada.nextLine();
                /* RETORNA STRING COM NOME DO USUARIO*/
                String nome = obterNome(result);

                /* VERIFICA SE NOME É VALIDO */
                if (verificaNome(nome) == true) {
                    PrintStream saida = new PrintStream(socket.getOutputStream());
                    saida.println("login:true");
                    System.out.println("Usuário " + nome + ", login realizado com sucesso!");

                    /* LANÇA THREAD DA CONEXÃO COM O CLIENTE */
                    ServerConnection sc = new ServerConnection(socket, this, nome);
                    sc.start();
                    /* ADICIONA THREAD A LISTA DE CONEXÕES */
                    connections.add(sc);
                    try {
                        sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    listarUsuarios();
                    break;
                } else {
                    PrintStream saida = new PrintStream(socket.getOutputStream());
                    saida.println("login:false");
                    break;
                }

            }
        }
    }

    public void listarUsuarios() throws IOException {
        String result = "";
        if (connections.size() == 1) {
            result += connections.get(0).nome_usuario;
        } else {
            int i = 0;
            for (; i < connections.size()-1; i++) {
                result += connections.get(i).nome_usuario + ";";
            }
            result += connections.get(i).nome_usuario;
            
        }
        for (int i = 0; i < connections.size(); i++) {
            ServerConnection sc = connections.get(i);
            sc.enviaMsg("lista_usuarios:"+result);
        }

    }

    public boolean verificaNome(String nome) {
        for(int i = 0; i < connections.size(); i++){
            //SE O NOME JA EXISTIR RETORNA FALSO
            if(connections.get(i).nome_usuario.equals(nome))
                return false;
            //SE O NOME POSSUIR ALGUM DOS SEGUINTES CARACTERES RETORNA FALSO
            if(nome.contains("*") || nome.contains("/") || nome.contains(";") || nome.contains(":"))
                return false;
        }

        return true;
    }

    /* VERIFICA OPERAÇÃO E RETORNA NOME */
    public String obterNome(String result) {
        int i;
        String operacao = "";
        String nome = "";
        for (i = 0; result.charAt(i) != ':'; i++) {
            operacao += result.charAt(i);
        }
        i++;
        for (; i < result.length(); i++) {
            nome += result.charAt(i);
        }

        return nome;
    }

}
