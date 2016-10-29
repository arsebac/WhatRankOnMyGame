import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represent a champion ingame.
 *
 * @author Francois Melkonian
 *         on  24/10/2016
 */
class ChampionEnJeu {
	private String pseudo;
	private int idPseudo;
	private int champion;
	private int masteries;
	/**
	 * team equals true if blue team, false if red.
	 */
	private boolean team;

	ChampionEnJeu(JSONObject champ) {
		try {
			this.pseudo = champ.getString("summonerName");
			this.idPseudo = champ.getInt("summonerId");
			this.champion = champ.getInt("championId");
			this.team = champ.getInt("teamId") == 100;

		} catch (JSONException e) {
			System.err.println("Erreur dans la construction de champion en jeu : " + champ);
			this.pseudo = null;
		}

	}

	/**
	 * Getter of idPseudo
	 *
	 * @return idPseudo
	 */
	int getIdPseudo() {
		return idPseudo;
	}

	/**
	 * Getter of Pseudo
	 *
	 * @return Pseudo
	 */
	String getPseudo() {
		return pseudo;
	}

	/**
	 * Getter of Champion
	 *
	 * @return Champion
	 */
	String getChampion() {
		return Riot.getChampionName(champion);
	}

	/**
	 * Ask to Riot API the champion's mastery on summonerName
	 *
	 * @return the mastery of the summoner on this champ
	 */
	int getMasteries() {
		String temp;
		do {
			temp = Riot.call("championmastery/location/EUW1/player/" + idPseudo + "/champion/" + champion);
			if (temp == null) {
				long s = System.currentTimeMillis();
				while (System.currentTimeMillis() - s < 100) ;
			}
		} while (temp == null);
		try {
			masteries = new JSONObject(temp).getInt("championLevel");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return masteries;
	}

	/**
	 * Getter of team
	 *
	 * @return true if blue team false else
	 */
	boolean isBlueTeam() {
		return team;
	}

	@Override
	public String toString() {

		return Riot.getChampionName(champion) + " Mas." + getMasteries() + "     " + String.format("%20s", pseudo) + "\n";
	}
}
