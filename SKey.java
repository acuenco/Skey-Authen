//Adriene Cuenco
//CS460.01
//Final Project (S/key)
//Due: Monday, March 16, 2015

 
//import javax.crypto.*;

import javax.xml.bind.DatatypeConverter;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

public class SKey {
	//Master database
	static Hashtable< String, String > database = new Hashtable< String,String >();
	static Hashtable<String,String[]> skeykeys = new Hashtable<String, String[]>();
	static MessageDigest md;

	public static void main(String[] args)throws Exception {
		System.out.println("\n********************************************");
		System.out.println("*  Welcome to the S/Key simulation server  *");
		System.out.println("*  Written by Adriene Cuenco               *");
		System.out.println("*  CS460.01 (W15)                          *");
		System.out.println("********************************************\n");
		while(true){
			//Set encryption method to SHA-256
			md = MessageDigest.getInstance("SHA-256");
			mainMenu();

			System.out.println();
			System.out.println("Enter ID name: ");
			
			//Get ID info from user
			Scanner scanner = new Scanner(System.in);
			Scanner sc = new Scanner(System.in);
			String id = scanner.nextLine();
			int index =0;
			//if user exists in the database
			if(database.containsKey(id)){ 
				//Calculate password chain position
				for(int i = 0; i < skeykeys.get(id).length;i++) {
					if(skeykeys.get(id)[i].equals(database.get(id)))
						index = i+1;
				}
				System.out.println("Enter password (i="+ index  +"): ");
				boolean isPassCorrect=false;
				//challenge response
				int challenge = 1;
				while(!isPassCorrect){
					if(challenge>3){
						System.out.println("3 failed attmepts. Logging out.");
						mainMenu();
						break;
					}
					sc = new Scanner(System.in);
					String passwordAttempt = sc.nextLine();
					
					if(authenticate(id,passwordAttempt)){
						isPassCorrect =true;
						System.out.println("Authenticated");
						System.out.println("Welcome back " + id +" to the simulated database");
						System.out.println("logging out...");
						if(database.get(id).equals(getSeed(id))){
							System.out.println("\nNote: You have no more authentifications remaining. Please Re-register.");
							database.remove(id);
							skeykeys.remove(id);
							mainMenu();
							continue;
						}
					} 
					else{
						System.out.println("\nIncorrect password, Try again."); 
						System.out.println("Attempt #: " + challenge + " out of 3");
						challenge++;
						System.out.println("Enter password (i="+ index  +"): \n");
					}
					
				}//end while
			}else{
				//register(id);
				System.out.println("ID does not exist. Please Register.");
				mainMenu();
			}
		}	
	}//end main 
	
	public static void register(String id) throws Exception{
		Scanner sc = new Scanner(System.in);
		MessageDigest md = MessageDigest.getInstance("SHA-256");
	
		System.out.println("\nWould you like to register with this new ID name?");
		System.out.println("Enter (1) for yes or (2) for no");

		boolean isValid = false;
		
		//Get users response
		
		while(!isValid){
			int toRegister = -1;
			boolean bool = true;
			while(bool){
				
				sc = new Scanner(System.in);
				String str = sc.nextLine();
				if(isInteger(str)){ toRegister = Integer.parseInt(str); bool = false;}
				else{
					System.out.println("Invalid response, Try again.\n");
					System.out.println("You are a non-registered user. Would you like to register?");
					System.out.println("Enter (1) for yes or (2) for no");
				}	
			}
			if(toRegister == 1){
				isValid = true;
				boolean validCheck = true;
				int n=0;
				while(validCheck){
					System.out.println("Enter the amount of authentifications(n) needed: ");
					String n_str = sc.nextLine();
					
					if(isInteger(n_str)){
						n = Integer.parseInt(n_str);
						validCheck=false;
					}
					else {
						System.out.println("\nInvalid response. Try Agian.\n");
					}
				}
				
				
				System.out.println("Enter secret seed: ");
				sc = new Scanner(System.in);
				String seed = sc.nextLine();
				ArrayList<String> pList = new ArrayList<String>();
				for(int i = 0; i < n; i++){
					pList.add(seed);
					seed = secretHashAlgo(seed);	
				}
				
			    //reverse password chain
				Object[] pChain_raw =  pList.toArray();
				pChain_raw = reverse(pChain_raw);
				String pChain = (String) pChain_raw[0];
				database.put(id,pChain);
				skeykeys.put(id, (String[]) pChain_raw);
				System.out.println("You are now registered. Remember your password chain.\n");
				//Print password chain for debug purpose. This would not be needed in real life applications
				for(int i = 1; i < pChain_raw.length;i++) System.out.println("(i = "+ i +") "+pChain_raw[i]);
				mainMenu();
			}
			else if(toRegister == 2){
				mainMenu();
				break;
			}
			else{
				System.out.println("Invalid response.Try again");	
			}
		}//end while isValid
	}// end method register
	
	public static boolean isInteger(String s) {
		for(int i =0; i < s.length();i++){
			if(!Character.isDigit(s.charAt(i))) return false;
		}
	    return true;
	}// end Function isInteger
	
	public static void mainMenu()throws Exception{
		boolean flag = false;
		Scanner sc = new Scanner(System.in);
		while(!flag){
			System.out.println("\n-----------------------Main Menu--------------------------");
			System.out.println("| Enter (1) to Log in or (2) to Register or (3) to exit. |");
			System.out.println("----------------------------------------------------------\n");
			
			String menu = sc.nextLine();
			if(menu.equals("")) menu = "0";
			int menuInt = 0;

			sc = new Scanner(System.in);
			if(isInteger(menu)){
				menuInt = Integer.parseInt(menu);
				if(menuInt == 1){
					break;
				}
				else if(menuInt == 2){
					System.out.println("\nWelcome to registration.\n");
					System.out.println("Enter new ID name: ");
					@SuppressWarnings("resource")
					Scanner scanner = new Scanner(System.in);
					String id = scanner.nextLine();
					
					if(database.containsKey(id)){
						System.out.println("Error: ID is taken.");
						//continue;
					}else {register(id); break;}
				}
				else if(menuInt == 3){
					System.out.println("\nThank you for using S/Key service.");
					System.out.println("Simulation terminating.");
					System.out.println("Goodbye\n");
					System.exit(0);
					
				}
				else{
					System.out.println("\nInvalid response.Try again\n");
					//flag = true;
				}
			}
		}
	}//End method mainMenu
	
	public static String secretHashAlgo(String plain) throws Exception{
		String result="";
		byte[] hashBytes = md.digest(plain.getBytes("UTF-8"));
		result = DatatypeConverter.printHexBinary(hashBytes);
		return result;
	}//end method secretHashAlgo
	
	public static Object[] reverse(Object[] str){
		Object[] result = new String[str.length];
		int ctr = 0;
		for( int i = str.length-1; i >= 0; i-- ){
			result[ctr] = str[i];
			ctr++;
		}
		return result;
	}// end method reverse
	
	public static boolean authenticate(String id, String password) throws Exception{
		if(secretHashAlgo(password).equals(database.get(id))){
			database.replace(id, password);
			return true;
		}
		return false;
	}//end method authenticate
	
	public static String getSeed(String id){
		int size = skeykeys.get(id).length;
		return skeykeys.get(id)[size-1];
	}// end method getSeed
	
}//end class Skey 