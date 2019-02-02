import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.concurrent.Callable;

public class Request implements Callable<Response> {
    private URL url;

    public Request(URL url) {
        this.url = url;
    }

    @Override
    public Response call() throws Exception {
    	HttpURLConnection con = (HttpURLConnection) url.openConnection();
    	con.setConnectTimeout(0);
    	try {
    		return new Response(con.getInputStream());
    	}
    	catch (SocketTimeoutException e) {
    	}
		catch (IOException e) {
			//probably an annoying 429 >:(
			//this might skip an article :(
			System.err.println("Wiki gave 429, retrying in 5s ...");
			Thread.sleep(5000);
		}
		return null;
    }//end of call
    
}//end of class