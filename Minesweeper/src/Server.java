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
		for (int i = 0; i < currentUsers.size(); i++) {
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
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else { // enter user into list and db
				System.out.println(user.getUsername()
						+ " was not found in file");
				User user2 = new User(user.getUsername(), password);
				users.add(user2);
				writeUsers();
			}

			client.user = user;
			currentUsers.add(user);
			sendUserlist();
			String loginString = user.getUsername() + " has logged on from "
					+ client.getInetAddress();
			this.sendToAllClients(loginString);
		} else if (msg.toString().equals("userlist request")) {
			sendUserlist();
		} else { // send the message to everyone
			this.sendToAllClients(msg);
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
				System.out.println(System.currentTimeMillis() + "\tccSize: "
						+ clientConnections.length + "\touSize: "
						+ currentUsers.size());
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
