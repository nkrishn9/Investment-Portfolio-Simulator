import java.io.IOException;
import java.util.Scanner;

public class PortfolioDriver {

	public static void main(String[] args) throws IOException {
		PortfolioDriver.fullSim();
	}

	
	public static void fullSim() throws IOException{
		boolean isDone = false;
		Scanner reader = new Scanner(System.in);
		Portfolio portfolio = new Portfolio(1000000);
		do{
			try{
				System.out.print("Would you like to start a new simulation or load your previous session? (new/load) ");
				String newLoad = reader.next();
				if(newLoad.equalsIgnoreCase("new")){
					System.out.print("Are you sure you want to start a new simulation? All previous progress will be deleted. (yes/no) ");
					String yesNo = reader.next();
					if(yesNo.equalsIgnoreCase("yes"))
						isDone = true;
					else if(yesNo.equalsIgnoreCase("no")){
						System.out.println("Okay, taking you back to the start menu...");
					}else{
						System.out.println("Invalid input. Please try again.");
					}
				}else if(newLoad.equalsIgnoreCase("load")){
					isDone = true;
					if(!portfolio.loadPortfolio())
						System.out.println("No valid save file found. Creating a new simulation now...\n");
				}
			}catch(Exception e){
				System.out.println("Invalid input. Please try again.");
			}
		}while(!isDone);
		portfolio.userInterface(reader);
		reader.close();
	}
}
