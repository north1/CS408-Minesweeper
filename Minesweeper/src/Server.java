import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

//package com.example.boilerbanker;


public class Server extends AbstractServer {

	private ArrayList<User> users; // list of users in the database

	private String usageString = "To connect to another player, type ::connect <username>.  To disconnect, type ::disconnect";

	public Server(int port) {
		super(port);
		users = new ArrayList<User>();
		try {
			Scanner userScan = new Scanner(new File("userDB.txt"));
			while (userScan.hasNextLine()) {
				String line = userScan.nextLine();
				StringTokenizer st = new StringTokenizer(line);
				User user = new User(st.nextToken(), Integer.parseInt(st
						.nextToken()));
				users.add(user);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Thread userlistThread = new Thread(userListRunnable);
		userlistThread.start();
	}

	/**
	 * This method overrides the one in the superclass. Called when the server
	 * starts listening for connections.
	 */
	protected void serverStarted() {
		System.out.println("Server listening for connections on port "
				+ getPort());
	}

	/**
	 * This method overrides the one in the superclass. Called when the server
	 * stops listening for connections.
	 */
	protected void serverStopped() {
		System.out.println("Server has stopped listening for connections.");
	}

	synchronized protected void clientDisconnected(ConnectionToClient client) {
		String toString = client.toString();
		for (int i = 0; i < currentUsers.size(); i++) {
			User usr = currentUsers.get(i);
			if (client.user != null
					&& usr.getUsername().equals(client.user.getUsername())
					&& usr.getDescription().equals(toString)) {
				currentUsers.remove(i);
				sendUserlist();
				break;
			}
		}
	}

	private void sendUserlist() {
		String userList = "userlist";
                //edit made here
		for (int i = 0; i < 5; i++) {
			if (currentUsers.get(i) != null)
				userList += " " + currentUsers.get(i).getUsername();
		}
		this.sendToAllClients(userList);
	}

	public void handleMessageFromClient(Object msg, ConnectionToClient client) {
		System.out.println("Message received: " + msg + " from " + client);
		if (msg.toString().startsWith("UsernameLogon")) {
			StringTokenizer st = new StringTokenizer(msg.toString());
			st.nextToken();
			// get username
			User user = new User(st.nextToken(), client.toString());
			// get password
			int password = Integer.parseInt(st.nextToken());
			boolean foundUser = false;
			int i = 0;
			for (; i < users.size(); i++) {
				if (users.get(i).getUsername().equals(user.getUsername())) {
					foundUser = true;
					break;
				}
			}

			if (foundUser) { // check password
				System.out.println(users.get(i).getUsername()
						+ " was found in file");
				if (password != users.get(i).getPassword()) {
					try {
						System.out.println("Auth failed");
						client.sendToClient("Auth failed");
						return;
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					try {
						System.out.println("Auth success");
						client.sendToClient("Auth success");
						client.sendToClient(usageString);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else { // enter user into list and db
				System.out.println(user.getUsername()
						+ " was not found in file");
				User user2 = new User(user.getUsername(), password);
				users.add(user2);
				user2.setDescription(client.toString());
				writeUsers();
				user = user2;
				try {
					System.out.println("Auth success");
					client.sendToClient("Auth success");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			client.user = user;
			currentUsers.add(user);
			sendUserlist();
			String loginString = user.getUsername() + " has logged on from "
					+ client.getInetAddress();
			this.sendToAllClients(loginString);
		} else if (msg.toString().equals("userlist request")) {
			sendUserlist();
		} else if (msg.toString().contains("gamedata")) {
			// System.out.println("gamedata");
			// Get sending user
			User user1 = users.get(0);
			for (User user : users) {
				if (user.getUsername().equals(client.user.getUsername())) {
					user1 = user;
					break;
				}
			}

			ConnectionToClient ctc = (ConnectionToClient) this
					.getClientConnections()[0];
			boolean foundPair = false;
			for (User user : users) {
				// Find the paired user
				if (user1.getPaired().getUsername().equals(user.getUsername())) {
					// Find connection for paired user
					Thread[] connections = this.getClientConnections();
					for (int i = 0; i < connections.length; i++) {
						if (((ConnectionToClient) connections[i]).user
								.getUsername().equals(user.getUsername())) {
							// Set ctc to paired user's CTC
							ctc = (ConnectionToClient) connections[i];
							foundPair = true;
						}

					}
				} else {
					
				}
			}

			// Send gamedata message to paired user
			try {
				if (ctc != null && foundPair) {
					ctc.sendToClient(msg);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
                else if(msg.toString().length() > 50){
                    msg = msg.toString().substring(0, 49);
                        
                        
                }
                
                else if (msg.toString().contains("::connect")) { // connect 2 users
			// Get sending user
			User u1 = users.get(0);
			for (User user : currentUsers) {
				if (user.getUsername().equals(client.user.getUsername())) {
					u1 = user;
					break;
				}
			}

			// do the connects
			StringTokenizer st = new StringTokenizer(msg.toString());
			st.nextToken();
			User u2 = users.get(0);
			String uname = "";
			boolean foundUser = false;
			while (st.hasMoreTokens()) {
				uname = st.nextToken();
				if (uname.contains("::connect")) {
					uname = st.nextToken();
					// Make sure you don't pair with yourself
					if (!uname.equals(u1.getUsername())) {
						// Find correct user to pair with
						for (User user : currentUsers) {
							if (user.getUsername().equals(uname)) {
								u2 = user;
								foundUser = true;
								break;
							}
						}
					}
				}
			}

			if (foundUser) {
				// Both users are available to be paired
				//if (u1.getPaired() == null && u2.getPaired() == null) {
					if (Math.random() < .9) {
						u1.setPaired(u2);
					} else {
						u1.setPaired(u1);
					}
					u2.setPaired(u1);
					System.out.println(u1.getPaired().getUsername());
					System.out.println(u2.getPaired().getUsername());
					// add to list of pairs
					this.sendToAllClients("connecting " + u1.getUsername()
							+ " and " + u2.getUsername());

					swapBoards(u1, u2);
					if (Math.random() > .9) {
						try {
							client.sendToClient("connect failed");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				/*} else if (u1.getPaired() != null) { // Send failure messages
					try {
						client.sendToClient("You are already paired with a user.  Type ::disconnect to unpair yourself");
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else if (u2.getPaired() != null) {
					try {
						client.sendToClient(u2.getUsername()
								+ " is already paired with a user.  They must disconnect before you can pair with them");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				*/
			} else {
				try {
					client.sendToClient("connect failed");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else if (msg.toString().contains("::disconnect")) {
			// disconnect a user from their pair
			// Get sending user
			User u1 = users.get(0);
			boolean foundUser = false;
			for (User user : users) {
				if (user.getUsername().equals(client.user.getUsername())) {
					u1 = user;
					foundUser = true;
					break;
				}
			}

			// Search for paired user
			// This is so both users can receive a disconnect message
			ConnectionToClient ctc = (ConnectionToClient) this
					.getClientConnections()[0];
			System.out.println("ctc = " + ctc.toString());
			boolean foundPair = false;
			for (User user : users) {
				// Find the paired user
				if (u1.getPaired().getUsername().equals(user.getUsername())) {
					// Find connection for paired user
					Thread[] connections = this.getClientConnections();
					for (int i = 0; i < connections.length; i++) {
						if (((ConnectionToClient) connections[i]).user
								.getUsername().equals(user.getUsername())) {
							System.out.println("connection found");
							// Set ctc to paired user's CTC
							ctc = (ConnectionToClient) connections[i];
							foundPair = true;
						}

					}
				}
			}

			if (foundUser) {
				if (u1.getPaired() != null) {
					u1.getPaired().setPaired(null);
				}
				u1.setPaired(null);
				try {
					client.sendToClient("disconnect");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (foundPair) {
				try {
					ctc.sendToClient("disconnect");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		} else { // send the message to everyone
			if(Math.random() > .1) {
				StringTokenizer st = new StringTokenizer(msg.toString());
				st.nextToken();
				String message = st.nextToken();
				while(st.hasMoreTokens()) {
					message.concat(st.nextToken());
				}
				this.sendToAllClients(message);
			}
		}

	}

	/**
	 * Swaps boards between 2 players
	 * 
	 * @param user1
	 * @param user2
	 */
	private void swapBoards(User user1, User user2) {
		System.out.println("boardswap");

		Board board1 = new Board();
		Board board2 = new Board();
		board1.setupBoardRandom(15);
		board2.setupBoardRandom(15);

		ConnectionToClient ctc1 = (ConnectionToClient) this
				.getClientConnections()[0];
		ConnectionToClient ctc2 = (ConnectionToClient) this
				.getClientConnections()[0];
		boolean founduser1 = false;
		boolean founduser2 = false;

		Thread[] connections = this.getClientConnections();
		for (int i = 0; i < connections.length; i++) {
			// Check for user1
			if (((ConnectionToClient) connections[i]).user.getUsername()
					.equals(user1.getUsername())) {
				System.out.println("connection found");
				ctc1 = (ConnectionToClient) connections[i];
				founduser1 = true;
			}

			// Check for user2
			if (((ConnectionToClient) connections[i]).user.getUsername()
					.equals(user2.getUsername())) {
				System.out.println("connection found");
				ctc2 = (ConnectionToClient) connections[i];
				founduser2 = true;
			}

		}

		// Send gamedata message to paired user
		try {
			// Send boards to user1
			if (ctc1 != null && founduser1 && founduser2) {
				System.out.println("attempting to send yourboard to "
						+ ctc1.toString());
				String msg = "gamedata yourboard " + board1.getHeight() + " "
						+ board1.getWidth();
				for (int i = 0; i < board1.getHeight(); i++) {
					for (int j = 0; j < board1.getWidth(); j++) {
						msg += " " + board1.getSpace(j, i);
					}
				}
				ctc1.sendToClient(msg);
				System.out.println("attempting to send board to "
						+ ctc1.toString());
				msg = "gamedata board " + board2.getHeight() + " "
						+ board2.getWidth();
				for (int i = 0; i < board2.getHeight(); i++) {
					for (int j = 0; j < board2.getWidth(); j++) {
						msg += " " + board2.getSpace(j, i);
					}
				}
				ctc1.sendToClient(msg);
			}
			// Send boards to user2
			if (ctc2 != null && founduser1 && founduser2) {
				System.out.println("attempting to send yourboard to "
						+ ctc2.toString());
				String msg = "gamedata yourboard " + board2.getHeight() + " "
						+ board2.getWidth();
				for (int i = 0; i < board2.getHeight(); i++) {
					for (int j = 0; j < board2.getWidth(); j++) {
						msg += " " + board2.getSpace(j, i);
					}
				}
				ctc2.sendToClient(msg);
				System.out.println("attempting to send board to "
						+ ctc2.toString());
				msg = "gamedata board " + board1.getHeight() + " "
						+ board1.getWidth();
				for (int i = 0; i < board1.getHeight(); i++) {
					for (int j = 0; j < board1.getWidth(); j++) {
						msg += " " + board1.getSpace(j, i);
					}
				}
				ctc2.sendToClient(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void writeUsers() {
		try {
			PrintWriter out = new PrintWriter(new File("userDB.txt"));
			out.print("");
			for (int i = 0; i < users.size(); i++) {
				out.append(users.get(i).getUsername() + " "
						+ users.get(i).getPassword());
				if (i != users.size() - 1)
					out.append("\n");
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Runnable userListRunnable = new Runnable() {

		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				Thread[] clientConnections = getClientConnections();
				ArrayList<User> oldUsers = new ArrayList<User>();
				// System.out.println(System.currentTimeMillis() + "\tccSize: "
				// + clientConnections.length + "\touSize: "
				// + currentUsers.size());
				for (int i = 0; i < currentUsers.size(); i++) {
					oldUsers.add(currentUsers.get(i));
				}
				currentUsers.clear();
				for (int i = 0; i < clientConnections.length; i++) {
					ConnectionToClient ctc = (ConnectionToClient) clientConnections[i];
					currentUsers.add(ctc.user);
				}
				sendUserlist();
				for (int i = 0; i < oldUsers.size(); i++) {
					boolean found = false;
					for (int j = 0; j < currentUsers.size(); j++) {
						if (currentUsers.get(j) != null
								&& oldUsers.get(i) != null) {
							if (currentUsers.get(j).equalsUser(oldUsers.get(i))) {
								found = true;
								break;
							}
						}
					}
					if (!found) {
						if (oldUsers.get(i) != null) {
							sendToAllClients(oldUsers.get(i).getUsername()
									+ " has logged off from "
									+ oldUsers.get(i).getDescription());
						}
					}
				}
			}
		}
	};

}