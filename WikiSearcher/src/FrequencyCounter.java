import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FrequencyCounter {

	static void freq(int numOfArticles, int numOfThreads, String keyword) throws InterruptedException, ExecutionException, IOException {

		HashMap<String, Integer> articles = new HashMap<String, Integer>();

		URL url = new URL("https://en.wikipedia.org/wiki/Special:Random");

		ExecutorService executor = Executors.newFixedThreadPool(numOfThreads);
		ArrayList<Future<Response>> responses = new ArrayList<Future<Response>>();

		int errorCounter = 0;
		
		long t1 = System.currentTimeMillis();
		for (int i = 0; i < numOfArticles / numOfThreads; i++) 
		{
			for (int j = 0; j < numOfThreads; j++)
				responses.add(executor.submit(new Request(url)));

			//should I sleep the thread here to give the requests time?
			//I should pause this thread until all responses are finished :/
			
			for (Future<Response> response : responses) 
			{
				try 
				{
					InputStream body = response.get().getBody();
					try (BufferedReader br = new BufferedReader(new InputStreamReader(body))) 
					{
						String line = null;
						String text = null;
						String title = null;
						while (null != (line = br.readLine())) 
						{
							if (line.startsWith("<title>"))
								title = "Article Title: " + line.substring(7, line.indexOf("-") - 1);
							text += line;
						}
						int freq = (text.length() - text.replaceAll(keyword, "").length()) / keyword.length();
						if (freq > 0)
							articles.put(title, freq);
					}
				} 
				catch (NullPointerException e) 
				{
				errorCounter++;//handle timeout by doing nothing for that article
				}
			} // end responses
			responses.clear();
		} // end of toSearch

		long t2 = System.currentTimeMillis();
		long t = (t2 - t1) / 1000;
		executor.shutdown();

		int hitCount = 0;
		for (Map.Entry<String, Integer> entry : articles.entrySet()) {
			if (entry.getValue() > 0) {
				hitCount += entry.getValue();
				System.out.println(entry.getKey() + "\nFreq: " + entry.getValue());
			}
		} // end of entry loop

		System.out.println("\nTotal Frequency of '" + keyword + "' is: " + hitCount);
		System.out.println("Total Articles to Articles Containing Keyword :  " + numOfArticles + " / " + articles.size());
		System.out.println("Total Time: " + t + "s");
		System.out.println("Number of failed connections: " + errorCounter);
	}
}
