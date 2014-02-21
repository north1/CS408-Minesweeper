import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.lang.*;
import java.io.*;

public class GameServer {

    public static String[] nameList = new String[256];
    public static int ind = 0;
    public static Socket[] clientList = new Socket[256];

    public static void main(String[] args) throws Exception {

        int clientNumber = 0;
        ServerSocket listener = new ServerSocket(9898);
        try {
            while (true) {
		Socket s = listener.accept();
                Game g = new Game(s, clientNumber++);
		g.start();
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
	    
            log("New connection with client# " + clientNumber + " at " + socket);
        }


        public void run() {
            try {

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

		//out.println("Looking for opponent...");
		//out.println("----------------------------------");
                while (true) {
                    String input = in.readLine();
                    if (input == null || input.equals(".")) {
                        break;
                    }
			//out.println(input);
			//log(input);
			nameList[ind] = input;
			
			if(checkForDups(nameList,ind+1)){
				out.println("name in use");
				nameList[ind] = "";
				//ind--;
			}
			else{
				//log(input);
				out.println("test "+nameList[ind]);
				clientList[ind] = socket;
				ind++;
				/*for(int i = 0; i < ind; i++){
					log("list["+i+"] = "+nameList[i]);	
				}*/
			}
			
      if(ind >= 2){
        //more than 2 in queue, so match 2 random people together
        //connect 2 clients
	PrintWriter os = new PrintWriter(clientList[0].getOutputStream(), true);
	BufferedReader is = new BufferedReader(
                        new InputStreamReader(clientList[1].getInputStream()));
	while(true){
		String b = is.readLine();
		os.println(b);
	}
	//send board info
	
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

        /*public boolean checkForDups(String[] nameList){
          boolean duplicates = false;
          for(int j = 0; j < nameList.Length; j++)
            for(int k = j+1; k<nameList.Length;k++)
              if(k!=j && nameList[k] == nameList[j])
                duplicates = true;
          return duplicates;
        }*/
	private synchronized boolean checkForDups(String[] list, int ind){

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
	}
    }
}