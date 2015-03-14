//Adriene Cuenco
//CS460.01
//Final Project (S/key)
//Due: Monday, March 16, 2015

import javax.crypto.*;
import javax.xml.bind.DatatypeConverter;

import java.nio.ByteBuffer;
import java.security.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class SKey {
	
	static Hashtable< String, Queue<String> > database = new Hashtable< String, Queue<String> >();
	static MessageDigest md;
	public static void main(String[] args)throws Exception {
		System.out.println("\n***Welcome to the S/Key simulation server***\n");
		while(true){
			md = MessageDigest.getInstance("SHA");
			mainMenu();

			System.out.println();
			System.out.println("Please enter ID name: ");
			
			//Get ID info from user
			Scanner scanner = new Scanner(System.in);
			Scanner sc = new Scanner(System.in);
			String id = scanner.nextLine();
			
			//if user exists in the database
			if(database.containsKey(id)){ 
				//If no more passwords in the password chain re-register user
				System.out.println("server> i= " + (int) (database.get(id).size() - (database.get(id).size()-1)));
				System.out.println("Enter password: ");
				boolean isPassCorrect=false;
				//challenge response
				//loop if incorrect password. Please add feature: three failed attempts compromises your registration. Must re register
				while(!isPassCorrect){
					sc = new Scanner(System.in);
					String passwordAttempt = sc.nextLine();

					if(passwordAttempt.equals(database.get(id).peek())){
						database.get(id).poll();
						isPassCorrect =true;
						System.out.println("Authenticated");
						System.out.println("Welcome back " + id +" to the simulated database");
						System.out.println("logging out...");
						if(database.get(id).size() == 0){
							System.out.println("\nNote: You have no more authentifications. Please re-register.");
							database.remove(id);
							mainMenu();
							continue;
						}
					} else System.out.println("Incorrect password, Please try again\nEnter password: "); 	
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
					System.out.println("Invalid response, Please try again.\n");
					System.out.println("You are a non-registered user. Would you like to register?");
					System.out.println("Enter (1) for yes or (2) for no");
				}
				
			}
			if(toRegister == 1){
				isValid = true;
				System.out.println("Enter the amount authentifications(n): ");
				int n = sc.nextInt();
				
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
				Queue<String> pChain = strArrayToQueue((String[]) pChain_raw);
				database.put(id,pChain);
				System.out.println("You are now registered. Please remember your password Chain.\n");
				for(Object i: pChain_raw) System.out.println(i);
				mainMenu();
			}
			else if(toRegister == 2){
				mainMenu();
				break;
			}
			else{
				System.out.println("Invalid response. Please Try again");	
			}
		}//end while isValid
	}// end method register
	
	public static String[] getPasswordChain(int size){
		Scanner sc = new Scanner(System.in);
		String[] result = new String[size];
		for(int i = 0; i < size; i++){
			System.out.println("Enter password # " + (i+1));
			result[i] = sc.nextLine();
		}
		System.out.println("Password chain complete.");
		return result;
	}//end method getPasswordChain
	
	public static Queue<String> strArrayToQueue(String[] sArr){
		Queue<String> result = new LinkedList<String>();
		for(String i : sArr){
			result.add(i);
		}
		return result;
	}// end method strArrayToQueue
	
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
			System.out.println("\n----------------------------------------");
			System.out.println("****Main Menu****");
			System.out.println("Enter (1) to Log in or (2) to Register.");
			System.out.println("----------------------------------------\n");
			
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
					System.out.println("Welcome to registration.\n");
					System.out.println("Please enter new ID name: ");
					@SuppressWarnings("resource")
					Scanner scanner = new Scanner(System.in);
					String id = scanner.nextLine();
					
					if(database.containsKey(id)){
						System.out.println("Error: ID exist.");
						//continue;
					}else {register(id); break;}
				}
				else{
					System.out.println("Invalid response.Try again\n");
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
	
	public static void authenticate(String password){
		
	}//end method authenticate
	
}//end class Skey 