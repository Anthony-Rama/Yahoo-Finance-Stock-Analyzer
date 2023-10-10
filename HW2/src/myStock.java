import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import yahoofinance.*;

// to store the stock name and price
class stockInfo {
	// instance variables to store name and price
	private String name;
	private BigDecimal price;

	// constructor to initialize name and price
	public stockInfo(String nameIn, BigDecimal priceIn) {
		name = nameIn;
		price = priceIn;
	}

	// method to convert stockInfo object to string
	public String toString() {
		return name + " " + price.toString();
	}

	// method to get the stock price
	public BigDecimal getStockPrice() {
		return price;
	}

}

public class myStock {
	// HashMap to store stock information with O(1) retrieval
	HashMap<String, stockInfo> stocksMap;

	// TreeSet to store the same stock information sorted by price for O(K) retrieval
	TreeSet<Entry<String, stockInfo>> stockTree;

	// constructor to initialize the data structures
	public myStock() {
		// initialize the HashMap
		stocksMap = new HashMap<String, stockInfo>();

		// initialize the TreeSet with a custom Comparator to sort by price
		stockTree = new TreeSet<>(new Comparator<Entry<String, stockInfo>>() {
			@Override
			public int compare(Entry<String, stockInfo> o1, Entry<String, stockInfo> o2) {
				return o2.getValue().getStockPrice().compareTo(o1.getValue().getStockPrice());
			}
		});
	}

	// method to insert or update records in the database
	public void insertOrUpdate(String symbol, stockInfo stock) {
		if(stocksMap.containsKey(symbol)) {
			// if the stock already exists in the database, remove it from the TreeSet and add the updated stock
			Entry<String, stockInfo> obj = Map.entry(symbol, stocksMap.get(symbol));
			stocksMap.replace(symbol, stock);
			stockTree.remove(obj);
			obj = Map.entry(symbol, stocksMap.get(symbol));
			stockTree.add(obj);
		} else {
			// if the stock does not exist in the database, add it to the HashMap and TreeSet
			Entry<String, stockInfo> obj = Map.entry(symbol, stock);
			stocksMap.put(symbol, stock);
			stockTree.add(obj);
		}
	}

	// method to retrieve a record from the database in O(1) time
	public stockInfo get(String symbol) {
		return stocksMap.get(symbol);
	}

	// method to return the top K stocks in the database in O(K) time
	public List<Map.Entry<String, stockInfo>> top(int k) {
		// create an iterator for the stockTree
		Iterator<Entry<String, stockInfo>> setIterator = stockTree.iterator();
		List<Entry<String, stockInfo>> list = new ArrayList<>();

		// iterate through the stockTree and add the top K stocks to the list
		while(k > 0) {
			list.add(setIterator.next());
			k--;
		}

		// return the list of top K stocks
		return list;
	}

	public static void main(String[] args) throws IOException {
		// testing code
		myStock techStock = new myStock();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader("./US-Tech-Symbols.txt"));
			String line = reader.readLine();
			while (line != null) {
				String[] var = line.split(":");

				// YahooFinance API is used and make sure the lib files are included in the
				// project build path
				Stock stock = null;
				try {
					stock = YahooFinance.get(var[0]);
				} catch (IOException e) {
					System.out.println("do nothing and skip the invalid stock");
				}

				// test the insertOrUpdate method when initializing the database
				if (stock != null && stock.getQuote().getPrice() != null) {
					techStock.insertOrUpdate(var[0], new stockInfo(var[1], stock.getQuote().getPrice()));
				}
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int i = 1;
		System.out.println("===========Top 10 stocks===========");

		// test the top method
		for (Map.Entry<String, stockInfo> element : techStock.top(10)) {
			System.out.println("[" + i + "]" + element.getKey() + " " + element.getValue());
			i++;
		}

		// test the get method
		System.out.println("===========Stock info retrieval===========");
		System.out.println("VMW" + " " + techStock.get("VMW"));
		System.out.println("BIDU" + " " + techStock.get("BIDU"));
	}
}