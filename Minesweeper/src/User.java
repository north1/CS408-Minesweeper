
public class User {
	
	private String username;
	private String clientDescription;
	private int password;
	
	public User(String username, String clientDescription) {
		this.username = username;
		this.clientDescription = clientDescription;
	}
	
	public User(String username, int password) {
		this.username = username;
		this.password = password;
	}
	
	public int getPassword() {
		return password;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getDescription() {
		return clientDescription;
	}
	
	public boolean equalsUser(User user) {
		return (username.equals(user.getUsername()) && clientDescription.equals(user.getDescription()));
	}

}
