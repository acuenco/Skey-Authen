import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;


public class SkeyClient {

	public static void main(String[] args)throws Exception {
		Socket socket = new Socket("localhost", 22224);
		//InputStream fromServer = socket.getInputStream();
		BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		OutputStream toServer = socket.getOutputStream();
		
		
		
			System.out.println("server> " + fromServer.readLine()); //print prompt
			fromServer.readLine();
			System.out.println("server> " + fromServer.readLine());//ask for username
			
			Scanner sc = new Scanner(System.in);
			String id = sc.nextLine(); 
			
			//serialize ID and send to server
			ByteArrayOutputStream serializeID = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(serializeID);
			oos.writeObject(id);
			byte[] id_raw = serializeID.toByteArray();
			toServer.write(id_raw); 
			//System.out.println("server> " + fromServer.readLine()); //Debug: echo ID
			
			//Non registered prompts
			System.out.println("server> " + fromServer.readLine()); 
			System.out.println("server> " + fromServer.readLine());
			
			int reg = sc.nextInt();
			
			
		

	}// end main

}// end SkeyClient
