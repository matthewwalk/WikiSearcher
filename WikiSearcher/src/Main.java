import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Main {

	public static void main(String[] args) {
		
		try {
			FrequencyCounter.freq(750, 250, "apple");
		} catch (InterruptedException | ExecutionException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}//end of main

}
