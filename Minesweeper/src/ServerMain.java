import java.io.IOException;

public class ServerMain {

	public static void main(String[] args) {
		int port = 8043;
		if(args.length < 0) {
			try {
				port = Integer.parseInt(args[0]);
			} catch(Exception e) {}
		}
		Server newServer = new Server(port);
		try {
			newServer.listen();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
