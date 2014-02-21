import java.io.*;
import java.util.Scanner;

public class Client extends AbstractClient {

	private ClientMain cm;

	public Client(String host, int port, ClientMain cm) throws IOException {
		super(host, port);

		this.cm = cm;
		try {
			openConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void handleMessageFromServer(Object msg) {
		String message = msg.toString();
		// System.out.println(message);
		cm.handleMessageFromClient(message);
		/*
		 * //System.out.println("message received"); if (msg == null){
		 * System.out.println("Client Error: msg from Server empty"); return; }
		 * 
		 * if (msg instanceof String) { System.out.println("FAILURE");
		 * MainActivity.changeCreds(); MainActivity.changeWaiting(); } else {
		 * 
		 * user = (User) msg; userName = user.getUsername(); currentBalance =
		 * user.getBalance(); userTransactions = user.getTransactions();
		 * numTransactions = user.getNumTransactions();
		 * 
		 * //System.out.println(userName + " " + currentBalance); for (int i =
		 * 0; i < 5; i++) { System.out.println(userTransactions[i].getDate() +
		 * " " + userTransactions[i].getLocation() + " " +
		 * userTransactions[i].getAmount()); } MainActivity.changeWaiting(); /*
		 * try { closeConnection(); } catch (IOException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
		// setNewLastFiveTransactions();
		// }
	}

	/*
	 * public void sendUserCredentials(String user, String password) { String
	 * together = user + " " + password; handleMessageFromUI(together); }
	 */

	public void handleMessageFromUI(String message) {
		try {
			sendToServer((Object) message);
		} catch (IOException e) {
			System.out
					.println("Client Error: Could not send message to server : "
							+ e.toString());
//			System.exit(-1);
		}
	}


}
