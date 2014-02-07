import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer {

    public static void main(String[] args) throws Exception {
        int clientNumber = 0;
        ServerSocket listener = new ServerSocket(9090);
        try {
            while (true) {
                new Game(listener.accept(), clientNumber++).start();
            }
        } finally {
            listener.close();
        }
    }


    private static class Game extends Thread {
        private Socket socket;
        private int clientNumber;

        public Game(Socket socket, int clientNumber) {
            this.socket = socket;
            this.clientNumber = clientNumber;
            //log("New connection with client# " + clientNumber + " at " + socket);
        }

        public void run() {
            try {

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                // Send a welcome message to the client.
                out.println("Hello, you are client #" + clientNumber + ".");
                out.println("Type in your name to be put in the list\n");

                while (true) {
                    String input = in.readLine();
                    if (input == null ) {
                        break;
                    }
		    log(input);
                }
            } catch (IOException e) {
                //log("Error handling client# " + clientNumber + ": " + e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    //log("Couldn't close a socket");
                }
                //log("Connection with client# " + clientNumber + " closed");
            }
        }

        private void log(String message) {
            System.out.println(message);
        }
    }
}
