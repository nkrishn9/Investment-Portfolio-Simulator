import org.json.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class Portfolio {
	
	public ArrayList<Stock> stockList;
	public double cash;
	public Portfolio(int startingCash){
		this.stockList = new ArrayList<Stock>();
		this.cash = startingCash;
	}
	
	public boolean buyStock(Scanner reader, boolean isShort){
		try{
			System.out.print("Enter a valid stock symbol: ");
			String symbol = reader.next();
			if(!isShort)
				System.out.print("Enter the number of shares you wish to purchase: ");
			else
				System.out.print("Enter the number of shares you wish to short: ");
			int numShares = reader.nextInt();
			Ticker priceCheck = new Ticker(symbol);
			Stock toAdd = new Stock(symbol, priceCheck.getCurrentPrice(), numShares, isShort);
			if(toAdd.infoGather.getCurrentPrice() * numShares > cash){
				System.out.println("You do not have enough funds to purchase this.\n");
				return false;
			}
			stockList.add(toAdd);
			cash -= toAdd.infoGather.getCurrentPrice() * numShares;
			System.out.println("Your transaction was successful.\n");
			return true;
		}catch(JSONException exception){
			System.out.println("Invalid Stock. Please try again.\n");
			return false;
		}catch(Exception exception1){
			System.out.println("Invalid input. Please try again.\n");
			return false;
		}
	}
	

	public boolean sellStock(Scanner reader){
		try{
			System.out.print("Enter which stock you wish to sell (by reference number): ");
			int index = reader.nextInt() - 1;
			if(index > this.stockList.size() || index < 0){
				System.out.println("Invalid input; please try again.\n");
				return false;
			}else{
				if(this.stockList.get(index).isShort){
					System.out.println("You cannot sell a stock that has been shorted; you must cover it.\n");
					return false;
				}else{
					System.out.print("How many shares of " + this.stockList.get(index).corpName + " do you wish to sell? ");
					int numShares = reader.nextInt();
					this.cash += this.stockList.get(index).sell(numShares);
					System.out.println("The sale was successful.\n");
					if(stockList.get(index).numShares == 0)
						stockList.remove(index);
					return true;
				}
			}
		}catch(JSONException e){
			System.out.println("There was an issue connecting to the server. Please check your internet connection and restart the program.");
			return false;
		}catch(Exception e){
			System.out.println("Invalid; please try again.\n");
			return false;
		}
	}
	
	public boolean coverStock(Scanner reader){
		try{
			System.out.print("Enter which stock you wish to cover (by reference number): ");
			int index = reader.nextInt() - 1;
			if(index > this.stockList.size() || index < 0){
				System.out.println("Invalid input; please try again.\n");
				return false;
			}else{
				if(!this.stockList.get(index).isShort){
					System.out.println("You cannot cover a stock that has been bought; you must sell it.\n");
					return false;
				}else{
					System.out.print("How many shares of " + this.stockList.get(index).corpName + " do you wish to cover? ");
					int numShares = reader.nextInt();
					this.cash += this.stockList.get(index).cover(numShares);
					System.out.println("The cover was successful.\n");
					if(stockList.get(index).numShares == 0)
						stockList.remove(index);
					return true;
				}
			}
		}catch(JSONException e){
			System.out.println("There was an issue connecting to the server. Please check your internet connection and restart the program.");
			return false;
		}catch(Exception e){
			System.out.println("Invalid; please try again.\n");
			return false;
		}
	}
	
	public boolean deleteStock(int index){
		if(index > this.stockList.size() || index < 0)
			return false;
		this.stockList.remove(index);
		return true;
	}

	public double getStockValue() throws JSONException, IOException{
		double totalRevenue = 0;
		for(int i = 0; i < this.stockList.size(); i++){
			if(!this.stockList.get(i).isShort)
				totalRevenue += (this.stockList.get(i).purchasePrice * this.stockList.get(i).numShares) +  ((this.stockList.get(i).infoGather.getCurrentPrice() - this.stockList.get(i).purchasePrice) * this.stockList.get(i).numShares);
			else
				totalRevenue += (this.stockList.get(i).purchasePrice * this.stockList.get(i).numShares) +  ((this.stockList.get(i).purchasePrice - this.stockList.get(i).infoGather.getCurrentPrice()) * this.stockList.get(i).numShares);
		}
		return Math.round(totalRevenue * 100.0) / 100.0;
	}
	
	public void displayHeader(){
		System.out.format("  %-12s     %-16s     %-9s     %-14s     %-13s     %-21s\n", "Company Name", "Number of Shares", "Buy/Short", "Purchase Price", "Current Price", "Revenue Gained/Lossed");
		for(int i = 0; i < 112; i++){
			System.out.print("-");
		}
		System.out.println();
	}
	
	public void displayStocks() throws IOException{
		if(this.stockList.isEmpty())
			System.out.println("No Stock Investments");
		for(int i = 0; i < this.stockList.size(); i++){
			System.out.print(i + 1 + ")");
			String buyShort;
			if(this.stockList.get(i).isShort)
				buyShort = "Short";
			else
				buyShort = "Buy";
			if(this.stockList.get(i).corpName.length() <= 12){
				try{
					System.out.format("%12s     %,16d     %9s     %,14.2f     %,13.2f     %+,21.2f\n", this.stockList.get(i).corpName, this.stockList.get(i).numShares, buyShort, this.stockList.get(i).purchasePrice, this.stockList.get(i).infoGather.getCurrentPrice(), this.stockList.get(i).getCurRevenue());
				}catch(JSONException e){
					System.out.println("There was an issue connecting to the server. Please check your internet connection and restart the program.");
				}
			}else{
				try{
					System.out.format("%12s     %,16d     %9s     %,14.2f     %,13.2f     %+,21.2f\n", this.stockList.get(i).corpName.substring(0, 11), this.stockList.get(i).numShares, buyShort, this.stockList.get(i).purchasePrice, this.stockList.get(i).infoGather.getCurrentPrice(), this.stockList.get(i).getCurRevenue());
				}catch(JSONException e){
					System.out.println("There was an issue connecting to the server. Please check your internet connection and restart the program.");
				}
			}
		}
	}
	
	public void displayFooter() throws IOException{
		for(int i = 0; i < 112; i++){
			System.out.print("-");
		}
		System.out.println();
		for(int i = 0; i < 74; i++){
			System.out.print(" ");
		}
		double stockAssets;
		try {
			stockAssets = this.getStockValue();
		} catch (JSONException e) {
			stockAssets = 0;
		}
		System.out.format("%12s:    %,21.2f\n", "Stock Assets", stockAssets);
		for(int i = 0; i < 74; i++){
			System.out.print(" ");
		}
		System.out.format("%12s:    %+,21.2f\n", "Cash", this.cash);
		for(int i = 0; i < 74; i++){
			System.out.print(" ");
		}
		System.out.format("%12s:    %+,21.2f\n", "Total Assets", stockAssets + this.cash);
	}
	
	public void printInfo() throws IOException{
		this.displayHeader();
		this.displayStocks();
		this.displayFooter();
	}
	
	public void userInterface(Scanner reader) throws IOException{
		boolean toQuit = false;
		do{
			System.out.println();
			this.printInfo();
			System.out.print("Enter a command (buy/sell/short/cover/refresh/quit): ");
			String choice = reader.next();
			switch(choice){
			case "buy":
				this.buyStock(reader, false);
				break;
			case "sell":
				this.sellStock(reader);
				break;
			case "refresh":
				System.out.println("Refreshing your stocks now...");
				break;
			case "quit":
				toQuit = true;
				break;
			case "short":
				this.buyStock(reader, true);
				break;
			case "cover":
				this.coverStock(reader);
				break;
			default:
				System.out.println("Invalid command; please try again.\n");
				break;
			}
		}while(!toQuit);
		this.savePortfolio();
		reader.close();
	}
	
	public void savePortfolio() throws FileNotFoundException, UnsupportedEncodingException{
		PrintWriter writer = new PrintWriter("saveFile.txt", "UTF-8");
		writer.println(this.cash);
		writer.println(this.stockList.size());
		for(int i = 0; i < this.stockList.size(); i++){
			Stock toWrite = this.stockList.get(i);
			writer.println(toWrite.infoGather.symbol + " " + toWrite.purchasePrice + " " + toWrite.numShares + " " + toWrite.isShort);
		}
		writer.close();
	}
	
	public boolean loadPortfolio(){
		try {
			FileReader inputFile = new FileReader("saveFile.txt");
			BufferedReader reader = new BufferedReader(inputFile);
			this.cash = Double.parseDouble(reader.readLine());
			String numStocksString = reader.readLine();
			int numStocks = Integer.parseInt(numStocksString);
			for(int i = 0; i < numStocks; i++){
				String line = reader.readLine();
				Scanner liner = new Scanner(line);
				String symbol = liner.next();
				double purchasePrice = liner.nextDouble();
				int numShares = liner.nextInt();
				boolean isShort = liner.nextBoolean();
				Stock toAdd = new Stock(symbol, purchasePrice, numShares, isShort);
				this.stockList.add(toAdd);
				liner.close();
			}
			reader.close();
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		} catch (JSONException e) {
			return false;
		}	
		return true;
	}
}
