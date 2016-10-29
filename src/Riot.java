import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Francois Melkonian
 *         on 24/10/2016
 */
class Riot {
	private static Map<Integer, String> champions;
	private static String base = "https://euw.api.pvp.net/";
	private static int numAPI = 0;

	/**
	 * Download file from Riot API
	 *
	 * @param url complete url to Riot API
	 * @param b   true if the url is complete, false otherwise
	 * @return the response server
	 */
	static String call(String url, boolean b) {

		String ret = "";
		try {
			URL myURL;
			String apiKeys = "api_key=RGAPI-e98415f2-4cfe-4fcc-81e7-d7046580ce57";
			myURL = new URL(url + (b ? "?" : "&") + apiKeys);
			URLConnection connect = myURL.openConnection();
			respectLimitation();
			BufferedReader in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null)
				ret += inputLine + "\n";
			in.close();
			return ret;
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Create complete url to use call
	 *
	 * @param url a partial URL Riot
	 * @return the response server
	 */
	static String call(String url) {
		return call(base + url, true);
	}

	/**
	 * Prevent Riot API limitation : 10 requests on 10 secondes max !
	 */
	private static void respectLimitation() {
		numAPI++;
		if (numAPI > 9) {
			long s = System.currentTimeMillis();
			System.out.println("RESPECTE !");
			while (System.currentTimeMillis() - s < 5000) ;
			numAPI = 0;
		}
	}

	/**
	 * Create a list of ChampionEnJeu which contains ingame infos already started.
	 *
	 * @param id the in-game summoner id
	 * @return list contains informations about player
	 */
	private static List<ChampionEnJeu> getGame(Integer id) {
		List<ChampionEnJeu> champ = new ArrayList<>();
		try {
			JSONObject obj = new JSONObject(Riot.call("observer-mode/rest/consumer/getSpectatorGameInfo/EUW1/" + id));
			JSONArray arr = obj.getJSONArray("participants");
			for (int i = 0; i < arr.length(); i++) {
				champ.add(new ChampionEnJeu(arr.getJSONObject(i)));
			}
			return champ;
		} catch (JSONException e) {
			System.err.println("getChampionEnJeu : Problème d'utilisation");
		}
		return null;
	}

	/**
	 * Create a list of ChampionEnJeu which contains ingame infos already started.
	 * Overload getGame(int) with String name.
	 *
	 * @param query ingame pseudo
	 * @return list of ingame summoners
	 */
	static List<ChampionEnJeu> getGame(String query) {
		return getGame(getIdByName(query));
	}

	/**
	 * Check if a summoners is ingame.
	 *
	 * @param id summoner id
	 * @return true if summoner is ingame, false otherwise
	 */
	private static boolean isOnGame(Integer id) {
		return Riot.call("observer-mode/rest/consumer/getSpectatorGameInfo/EUW1/" + id) != null;
	}

	/**
	 * Check if a summoners is ingame.
	 * Overload getGame(int) with String name.
	 *
	 * @param pseudo summoner name
	 * @return true if summoner is ingame, false otherwise
	 */
	static boolean isOnGame(String pseudo) {
		return isOnGame(getIdByName(pseudo));
	}


	/**
	 * Ask Riot an id to a summoner
	 *
	 * @param name the summonerName
	 * @return summonerId
	 */
	static int getIdByName(String name) {
		String go = Riot.call("api/lol/euw/v1.4/summoner/by-name/" + name);
		if (go == null) {
			System.err.println("Pseudo introuvable !");
			return -1;
		}
		name = name.toLowerCase();
		try {
			JSONObject obj = new JSONObject(go);
			return obj.getJSONObject(name).getInt("id");
		} catch (JSONException e) {
			System.err.println("Erreur de l'API dans GetIdByName");
			e.printStackTrace();
			return -1;
		}
	}


	/**
	 * Get Champion Name with his Id
	 *
	 * @param id The champion Id
	 * @return The champion name
	 */
	static String getChampionName(int id) {
		return champions.get(id);
	}

	/**
	 * Create Map with Id champ as key, Name champ as value.
	 * With Riot API.
	 */
	static void createChampMap() {
		String data = Riot.call("https://global.api.pvp.net/api/lol/static-data/euw/v1.2/champion?locale=fr_FR", false);
		try {
			champions = new HashMap<>();
			JSONObject json = new JSONObject(data);
			json = json.getJSONObject("data");
			JSONArray arr = json.names();
			for (int i = 0; i < arr.length(); i++) {
				JSONObject temp = (JSONObject) json.get(arr.getString(i));
				champions.put(temp.getInt("id"), temp.getString("name"));
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
