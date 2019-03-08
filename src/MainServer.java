/**
 * Group: 01
 * Assignment: Iteration02 - Match Makind
 * Course: CS390 - Spring 2018
 * Instructor: George Hauser
 * Source consulted: 
 * 	-	https://docs.oracle.com/javase/8/docs/api/
 * 	-	https://stackoverflow.com
 * To Run: 
 * 	- Have the text file "AccountDatabase.txt" in the same folder.
 *  - Compile MainServer.java and run it.
 */	

public class MainServer {

	public static void main(String[] args) {
		//Create a new ServerView Object and set the frame to visible.
		ServerView gui = new ServerView();
		gui.setFrameVisible();
		
		//Create a new Server object with port 12345
		Server server = new Server();
		
		//gui can access to server
		gui.access(server);
		
		//server can access to
		server.access(gui);	

	}

}
