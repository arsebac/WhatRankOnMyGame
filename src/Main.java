import org.json.JSONException;

import javax.swing.*;
import java.io.IOException;
import java.util.List;


/**
 * @author Francois Melkonian
 *         on  23/10/2016
 */
public class Main {

	public static void main(String[] args) throws IOException, JSONException {
		Riot.createChampMap();
		String query = JOptionPane.showInputDialog(null, "Pseudo :");
		if (Riot.isOnGame(query)) {
			Riot.createChampMap();
			List<ChampionEnJeu> r = Riot.getGame(query);
			ActiveWindow g = new ActiveWindow(r);
			g.setVisible(true);
		} else {

			JOptionPane.showMessageDialog(null, "Le champion n'est actuellement pas en game !");
		}
		System.out.println(Riot.isOnGame("HASAGHL"));
	}


}
