import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

public class ClientMain extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8511961690224884198L;
	private JTextArea chatArea;
	private JTextField msgArea;
	private JTextArea userArea;
	private Client client;
	private String username;
	private String password;
	private String server;
	private int port;
	private ClientMain object;
	private MainGUI mainGUI;

	public ClientMain(String server, int port, MainGUI mainGUI) {
		super("Minesweeper Chat");
		this.server = server;
		this.port = port;
		this.mainGUI = mainGUI;
		enterCredentials();
	}

	private void createUI() {
		setLayout(new BorderLayout(3, 3));

		// Chat area
		JPanel chatPanel = new JPanel();
		chatArea = new JTextArea();
		chatArea.setEditable(false);
		chatArea.setPreferredSize(new Dimension(450, 450));
		chatArea.setWrapStyleWord(true);
		JScrollPane jsp1 = new JScrollPane(chatArea);
		jsp1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		chatPanel.add(jsp1);

		// Message area
		JPanel msgPanel = new JPanel();
		msgArea = new JTextField();
		msgArea.setEditable(true);
		msgArea.setPreferredSize(new Dimension(565, 50));
		msgArea.addKeyListener(msgListener);
		JScrollPane jsp2 = new JScrollPane(msgArea);
		jsp2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		msgPanel.add(jsp2);

		// CurrentUsers area
		JPanel usersPanel = new JPanel();
		userArea = new JTextArea();
		userArea.setEditable(false);
		userArea.setPreferredSize(new Dimension(100, 450));
		usersPanel.add(userArea);

		add(chatPanel, BorderLayout.CENTER);
		add(msgPanel, BorderLayout.SOUTH);
		add(usersPanel, BorderLayout.EAST);

		pack();
		addWindowListener(windowListener);
		setVisible(true);
	}

	private WindowListener windowListener = new WindowListener() {
		@Override
		public void windowActivated(WindowEvent arg0) {
		}

		@Override
		public void windowClosed(WindowEvent arg0) {
			if (client != null) {
				try {
					client.closeConnection();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void windowClosing(WindowEvent arg0) {
			if (client != null) {
				try {
					client.closeConnection();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void windowDeactivated(WindowEvent arg0) {
		}

		@Override
		public void windowDeiconified(WindowEvent arg0) {
		}

		@Override
		public void windowIconified(WindowEvent arg0) {
		}

		@Override
		public void windowOpened(WindowEvent arg0) {
		}
	};

	private KeyListener msgListener = new KeyListener() {
		public void keyPressed(KeyEvent arg0) {
			if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
				if (client != null) {
					client.handleMessageFromUI(username + ": "
							+ msgArea.getText());
					msgArea.setText("");
				} else {
					try {
						client = new Client(server, port, object);
						client.handleMessageFromUI(username + ": "
								+ msgArea.getText());
						msgArea.setText("");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		public void keyReleased(KeyEvent arg0) {
		}

		public void keyTyped(KeyEvent arg0) {
		}

	};

	private void enterCredentials() {
		username = JOptionPane.showInputDialog("Enter (or create) Username");
		password = "";
		while (username.contains("%") || username.contains(" ")) {
			username = JOptionPane
					.showInputDialog("Invalid characters. Enter (or create) Username");
		}
		while (password.length() <= 0) {
			password = JOptionPane
					.showInputDialog("Enter (or create) Password");
		}
		int pwd = password.hashCode();
		object = this;
		try {
			if (client != null) {
				client.closeConnection();
			}
			client = new Client(server, port, this);
			client.handleMessageFromUI("UsernameLogon " + username + " " + pwd);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void handleMessageFromClient(String str) {
		if (str.startsWith("userlist")) {
			StringTokenizer st = new StringTokenizer(str);
			st.nextToken();
			String userList = "";
			while (st.hasMoreTokens()) {
				userList += st.nextToken();
				userList += "\n";
			}
			if (userArea != null) {
				userArea.setText(userList);
			}
		} else if (str.startsWith("Auth failed")) {
			setVisible(false);
			try {
				client.closeConnection();
			} catch (IOException e) {
				e.printStackTrace();
			}
			enterCredentials();
		} else if (str.startsWith("Auth success")) {
			createUI();
			try {
				client.sendToServer("userlist request");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (str.contains("gamedata")) {
			System.out.println("Gamedata: " + str);
			StringTokenizer st = new StringTokenizer(str);
			String token = st.nextToken();
			while (!token.equals("gamedata")) {
				token = st.nextToken();
			}
			// Get the type of gamedata
			token = st.nextToken();
			if (token.equals("board")) {
				int height = Integer.parseInt(st.nextToken());
				int width = Integer.parseInt(st.nextToken());
				Board board = new Board(height, width);
				int[][] spaces = new int[height][width];
				for (int i = 0; i < height; i++) {
					for (int j = 0; j < width; j++) {
						spaces[i][j] = Integer.parseInt(st.nextToken());
					}
				}
				board.setSpaces(spaces);
				mainGUI.initSecondPlayer(board);
			} else if (token.equals("click")) {
				int x = Integer.parseInt(st.nextToken());
				int y = Integer.parseInt(st.nextToken());
				mainGUI.secondClick(x, y);
			}
		} else {
			if (chatArea != null) {
				chatArea.append("\n" + str);
				setVisible(true);
			}
		}
	}

	private static String usageString = "Usage:\n1) admChat {no arguments}2) admChat [port]\n3) admChat [server] [port]";

	public static void main(String[] args) {
		@SuppressWarnings("unused")
		ClientMain cm = null;
		if (args.length == 0) {
			cm = new ClientMain("moore06.cs.purdue.edu", 8043, null);
		} else if (args.length == 1) {
			cm = new ClientMain("data.cs.purdue.edu",
					Integer.parseInt(args[0]), null);
		} else if (args.length == 2) {
			cm = new ClientMain(args[0], Integer.parseInt(args[1]), null);
		} else {
			System.out.println(usageString);
		}
	}

}
