import java.io.*;
import org.json.*;

public class Stock {
	
	public String corpName;
	public double purchasePrice;
	public int numShares;
	public Ticker infoGather;
	public boolean isShort;
	
	public Stock(String corpSymbol, double price, int shares, boolean shorting) throws IOException, JSONException{
		this.infoGather = new Ticker(corpSymbol);
		this.corpName = infoGather.getCorpName();
		this.purchasePrice = price;
		this.numShares = shares;
		this.isShort = shorting;
	}
	
	public double getCurRevenue() throws JSONException{
		if(!isShort)
			return (infoGather.getCurrentPrice() - this.purchasePrice) * this.numShares;
		else
			return (this.purchasePrice - infoGather.getCurrentPrice()) * this.numShares;
	}
	
	public double sell(int numberSold) throws JSONException{
		this.numShares = this.numShares - numberSold;
		return this.infoGather.getCurrentPrice() * numberSold;
	}
	
}
