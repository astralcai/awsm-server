import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static final int MAX_CLIENT_COUNT = 10;

    public static HashMap<String, String> filenames = new HashMap<>();

    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.err.println("Usage: java Server <port number>");
            System.exit(1);
        }

        File directory = new File("data");
        if (!directory.exists()) {
            directory.mkdir();
        }

        // get port number from command line
        int portNumber = Integer.parseInt(args[0]);

        // create pool of client threads
        ExecutorService pool = Executors.newFixedThreadPool(MAX_CLIENT_COUNT);

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {

            // log server status
            System.out.println("Server Running on Port " + portNumber);

            // while loop to handle all client sockets
            while (true) {

                // create new client
                pool.execute(new ClientHandler(serverSocket.accept()));

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler extends Thread {

    Socket clientSocket;

    ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {

        String input;
        PrintWriter writer;

        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            // listen for client input
            while ((input = in.readLine()) != null) {
                System.out.println("Received: " + input);
                String[] tokens = input.split(" ");
                if (tokens[1].equals("new")) {
                    Server.filenames.put(tokens[0], "data/" + tokens[0] + "-" + tokens[2]);
                } else {
                    writer = new PrintWriter(new FileWriter(Server.filenames.get(tokens[0]), true));
                    writer.println(tokens[1]);
                    writer.close();
                }
            }

            // close client socket
            this.clientSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
