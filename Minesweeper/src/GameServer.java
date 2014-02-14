import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.lang.*;

/**
 * A server program which accepts requests from clients to
 * Game strings.  When clients connect, a new thread is
 * started to handle an interactive dialog in which the client
 * sends in a string and the server thread sends back the
 * Gamed version of the string.
 *
 * The program is runs in an infinite loop, so shutdown in platform
 * dependent.  If you ran it from a console window with the "java"
 * interpreter, Ctrl+C generally will shut it down.
 */
public class GameServer {


    public static void main(String[] args) throws Exception {

        int clientNumber = 0;
        ServerSocket listener = new ServerSocket(9898);
        try {
            while (true) {
                new Game(listener.accept(), clientNumber++).start();
            }
        } finally {
            listener.close();
        }
    }

    /**
     * A private thread to handle capitalization requests on a particular
     * socket.  The client terminates the dialogue by sending a single line
     * containing only a period.
     */
    private static class Game extends Thread {
        private Socket socket;
        private int clientNumber;
	public String[] nameList = new String[256];
	private int ind = 0;

        public Game(Socket socket, int clientNumber) {
            this.socket = socket;
            this.clientNumber = clientNumber;
            log("New connection with client# " + clientNumber + " at " + socket);
        }

        /**
         * Services this thread's client by first sending the
         * client a welcome message then repeatedly reading strings
         * and sending back the Gamed version of the string.
         */
        public void run() {
            try {

                // Decorate the streams so we can send characters
                // and not just bytes.  Ensure output is flushed
                // after every newline.
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                // Send a welcome message to the client.
                //out.println("Hello, you are client #" + clientNumber + ".");
                //out.println("Enter a line with only a period to quit\n");

                // Get messages from the client, line by line; return them
                // Gamed
		out.println("Looking for opponent...");
		out.println("----------------------------------");
                while (true) {
                    String input = in.readLine();
                    if (input == null || input.equals(".")) {
                        break;
                    }
			//out.println(input);
			//log(input);
			nameList[ind] = input;
			ind++;
			if(checkForDups(nameList)){
				out.println("name in use");
				//nameList[ind] = "";
				//ind--;
			}
			else{
				//log(input);
				out.println(nameList[ind]);

			}
      if(ind >= 2){
        //more than 2 in queue, so match 2 random people together
        //connect 2 clients
      }
			/*out.println(input);
			log(input);
		        nameList[ind] = input;
		        out.println(nameList[ind]);
		        ind++;*/
			//log(checkForDups(nameList,ind));

                }
            } catch (IOException e) {
                log("Error handling client# " + clientNumber + ": " + e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    log("Couldn't close a socket, what's going on?");
                }
                log("Connection with client# " + clientNumber + " closed");
            }
        }


        private void log(String message) {
            System.out.println(message);
        }

        public boolean checkForDups(String[] nameList){
          boolean duplicates = false;
          for(int j = 0; j < nameList.Length; j++)
            for(int k = j+1; k<nameList.Length;k++)
              if(k!=j && nameList[k] == nameList[j])
                duplicates = true;
          return duplicates;
        }
	/*private boolean checkForDups(String[] list, int ind){

		Set<String> set = new HashSet<String>();
    		for ( int i = 0; i < ind; ++i ) {
        		if ( set.contains( list[i])) {
            			return true;
       			}
        		else {
            			set.add(list[i]);
        		}
    		}
    		return false;
	}*/
    }
}
