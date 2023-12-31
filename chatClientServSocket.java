import java.io.IOException;
import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;




class ChatServer{

    public static final int PORT = 4000;
    private ServerSocket serverSocket;
    private final List<clientSocket> clients = new LinkedList<>();


    public void start() throws IOException{
        System.out.println("Servidor iniciado na porta: " + PORT);
        // a função irá iniciar um socket na porta definida
        serverSocket = new ServerSocket(PORT);
        /*O loop é chamado no start para que inicie a espera da solitação dos clientes */
        clientConnectionLoop();
    }

    /*O servidor deve possuir um método para ficar em escuta o tempo inteiro esperando a solitação do cliente 
     * Ele fica aguardando as conexões dos clientes. É um loop infinito
    */
    private void clientConnectionLoop() throws IOException{
        while(true){
            /*O metodo accept retorna um socket do cliente  */
            /*O método accept é uma operação bloqueante. O servidor fica parado
             * Ao terminar o loop o método fica esperando uma outra conexão
             */
            clientSocket clientSocket = new clientSocket(serverSocket.accept());
            /*apos criar o socket iremos armazenalo em uma lista */
            clients.add(clientSocket);
            /*Aqui estamos obtendo o endereço remoto do cliente conectado
             * A mensagem irá aparecer a porta que o cliente está utilizando para receber o retorno do server
             * O cliente conecta na porta 4000 
             * O servidor irá  utiizar a porta passada pelo getRemoteSocketAdress, essa porta é definida pelo Sistema Operacional
             */

            // o método na função lambda () nao precisa de definição de acesso e nem mesmo o tipo de retorno.
            // o Java possui inferencia de tipo ele identifica por conta propria o retorno
            // a -> é uma arrow function. O método na função lambda não pode receber funções que retorno Throws Exception
            new Thread(() -> clientMessageLoop(clientSocket)).start(); 
        }
    }

    /*
     * O método abaixo realiza o recebimento de mensagem pelo server
     * No bloco try, estamos utilizadno a mensagem transmitida pelo socket do cliente
     * Temos uma verificação para o caso da mensagem recebida seja de escape
     * Após receber mensagens de um determinado cliente, é importante  fechar o socket e os 
     * objetos de entrada e saida que foram criados, para que o sistema libere memoria e recurso que estava sendo utilizado
     */

    private void clientMessageLoop(clientSocket clientesocket){
        String msg;
        try{
            while((msg = clientesocket.getMessage())!=null){
                if("sair".equalsIgnoreCase(msg))
                    return;
                System.out.printf("Msg recebida do cliente %s: %s\n", clientesocket.getRemoteSocketAddress(),msg);
                sendMessageToAll(clientesocket, msg);
        }} finally{
            clientesocket.close();
        }
    }

    private void sendMessageToAll(clientSocket sender,String msg){
        final Iterator<clientSocket> iterator=clients.iterator();
        while(iterator.hasNext()){
            clientSocket clientSocket = iterator.next();
            if(!sender.equals(clientSocket)){
                if(!clientSocket.sendMessage(msg)){
                    iterator.remove();
                }
            }
        }
    }
    public static void main(String[] args){
        ChatServer server = new ChatServer();
        try {
            server.start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("erro ao iniciar ao servidor: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Servidor finalizado!");

        
    }
}
