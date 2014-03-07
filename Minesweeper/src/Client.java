import java.io.IOException;

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
