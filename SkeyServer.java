import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.Key;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;


public class SkeyServer {
	
	//Hashtable supports synchronization for multiple users. Hashmap does not.
	static Hashtable< String, Queue<String> > database = new Hashtable< String, Queue<String> >();
	
	public static void main(String[] args) throws Exception {
		//Create Server socket with port # 22224
		ServerSocket serSocket = new ServerSocket(22224);
		
		//Accept incoming request
		try(Socket socket = serSocket.accept()){
			String address = socket.getInetAddress().getHostAddress();
			System.out.printf("Client connected: %s%n", address);
			InputStream fromClient = ((Socket) socket).getInputStream();
			//BufferedReader fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter toClient = new PrintWriter(socket.getOutputStream(),true);
			//OutputStream toClient_oStream = socket.getOutputStream();
			
			//Display initial prompt for client
			toClient.println("***Welcome to the S/Key simulation server***");
			toClient.println();
			
			//Retrieve Client username
			toClient.println("Please enter username: ");
			ObjectInputStream serializedUser = new ObjectInputStream(fromClient);
			String id = (String) serializedUser.readObject();
			//toClient.println("id:" + id);

			//Check if user is in the database
			if (database.containsKey(id)){    //user exists in the database
				toClient.println("Enter Password");
				ObjectInputStream serializedObject = new ObjectInputStream(fromClient);
				String passwordAttempt = (String) serializedObject.readObject();
				boolean isPassCorrect = false;
				while(!isPassCorrect){
					if(passwordAttempt.equals(database.get(id).peek())){
						database.get(id).poll();
						isPassCorrect = true;
						toClient.println("************Authenticated.*********");
						socket.close();
						
					}
					else{
						toClient.println("Password incorrect. Please try again");
					}
				}//end while isPassCorrect
			}
			//else user must register
			else{
				toClient.println("You are a non-registered user. Would you like to register?");
				toClient.println("Enter (1) for yes or (2) for no");
				
				//Receive User's response 
				byte[] registerResponse = new byte[4];
				fromClient.read(registerResponse);
				ByteBuffer registerResponse_buf = ByteBuffer.wrap(registerResponse);
				int register = registerResponse_buf.getInt();
				boolean isValid = false;
				while(!isValid){
					if(register == 2){
						toClient.println("Connection closing...");
						socket.close();
						serSocket.close();
					}
					else if(register == 1){
						isValid = true;
						toClient.println("\nPlease enter passwordChain: ");
						
						/*
						byte[] pChain = new byte[1024];  
						fromClient.read(pChain);
						ByteArrayInputStream rawObject = socket.getInputStream().;
						*/
						
						//De-serialize password chain from client.
						ObjectInputStream serializedObject = new ObjectInputStream(fromClient);
						
						@SuppressWarnings("unchecked")
						Queue<String> pChain = (Queue<String>) serializedObject.readObject();
						
						//save id and password chain to the database
						database.put(id,pChain);
						
					}//end else If
					else System.out.println("Invalid response. Try agian");
				}// end while_isValid
			}// end else
			
			
			/*
			//Generate AES key
			Cipher cipher = Cipher.getInstance("AES");
			Key key = KeyGenerator.getInstance("AES").generateKey();
			
			//Activate EncryptMode for generated AES key
			cipher.init(Cipher.ENCRYPT_MODE, key);
			*/
			

			
			
			
			//Hashmap to store ID and password
			
			socket.close();
		}catch(Exception e){e.getStackTrace(); return;}
		
	}//End main
	
	//Generates Random Strings based off length
	public static String generateString(int length){
		Random r = new Random();
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	    char[] text = new char[length];
	    for (int i = 0; i < length; i++){
	        text[i] = characters.charAt(r.nextInt(characters.length()));
	    }//end for
	    return new String(text);
	}// End Method generateString
}//End Class Skey Server
