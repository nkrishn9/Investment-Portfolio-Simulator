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
	
	public double getCurRevenue() throws JSONException, IOException{
		if(!isShort)
			return (infoGather.getCurrentPrice() - this.purchasePrice) * this.numShares;
		else
			return (this.purchasePrice - infoGather.getCurrentPrice()) * this.numShares;
	}
	
	public double sell(int numSold) throws JSONException, IOException{
		this.numShares = this.numShares - numSold;
		return this.infoGather.getCurrentPrice() * numSold;
	}
	
	public double cover(int numCovered) throws JSONException, IOException{
		this.numShares = this.numShares - numCovered;
		return (2 * this.purchasePrice * numCovered) - (this.infoGather.getCurrentPrice() * numCovered);
	}
}
