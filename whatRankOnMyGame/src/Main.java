import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author user
 * @date 23/10/2016
 */
public class Main {
	public static void main(String[] args) throws IOException {
		URL myURL = new URL("https://na.api.pvp.net/api/lol/na/v1.4/summoner/by-name/RiotSchmick?api_key=<key>");
		URLConnection connect = myURL.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				connect.getInputStream()));
		String inputLine;
		while ((inputLine = in.readLine()) != null)
			System.out.println(inputLine);
		in.close();
	}
}
