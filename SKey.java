//Adriene Cuenco
//CS460.01
//Final Project (S/key)
//Due: Monday, March 16, 2015

import javax.crypto.*;

import java.security.*;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class SKey {
	
	static Hashtable< String, Queue<String> > database = new Hashtable< String, Queue<String> >();
	
	public static void main(String[] args)throws Exception {
		System.out.println("\n***Welcome to the S/Key simulation server***\n");
		while(true){
			
			System.out.println();
			System.out.println("Please enter ID name: ");
			
			//Get ID info from user
			Scanner sc = new Scanner(System.in);
			String id = sc.nextLine();
			
			//if user exists in the database
			if(database.containsKey(id)){ 
				//If no more passwords in the password chain re-register user
				//if(database.get(id).peek().isEmpty()){
				if(database.get(id).size() == 0){
					System.out.println("You have no more authentifications. Please re-register.");
					database.remove(id);
					register(id);
					continue;
				}
				
				System.out.println("Welcome back " + id);
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
					} else System.out.println("Incorrect password, Please try again\nEnter password: "); 	
				}//end while
			}else register(id);
		}
		
		
	}//end main 
	
	public static void register(String id) throws Exception{
		Scanner sc = new Scanner(System.in);
		System.out.println("You are a non-registered user. Would you like to register?");
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
				System.out.println("Enter the size of the password chain: ");
				int size = sc.nextInt();
				String[] pChain_raw = getPasswordChain(size);
				Queue<String> pChain = strArrayToQueue(pChain_raw);
				database.put(id,pChain);
				System.out.println("You are now registered");
			}
			else if(toRegister == 2){
				System.out.println("Program terminating...");
				System.exit(0);
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
	
}//end class Skey 