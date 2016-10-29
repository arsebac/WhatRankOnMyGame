import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Francois Melkonian
 */
public class ActiveWindow extends JDialog {
	public JLabel b0, b1, b2, b3, b4;
	public JLabel r0, r1, r2, r3, r4;
	public JButton afficherMaitriseButton;
	public JButton afficherRankButton;
	private JPanel contentPane;
	private boolean showMaitrise = false;
	private boolean showRank = false;
	private List<ChampionEnJeu> champs;
	private List<JLabel> blueTeam = new ArrayList<>();
	private List<JLabel> redTeam = new ArrayList<>();
	private Map<Integer, String> ranks;

	/**
	 * Create the window and assign an action listener to afficherMaitriseButton and afficherRankButton.
	 *
	 * @param champs Summoners who are ingame.
	 */
	ActiveWindow(List<ChampionEnJeu> champs) {
		setContentPane(contentPane);
		setModal(true);
		blueTeam.add(b0);
		blueTeam.add(b1);
		blueTeam.add(b2);
		blueTeam.add(b3);
		blueTeam.add(b4);
		redTeam.add(r0);
		redTeam.add(r1);
		redTeam.add(r2);
		redTeam.add(r3);
		redTeam.add(r4);
		this.champs = champs;
		showChamps();
		afficherRankButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showRank = !showRank;
				if (showRank) {
					ranks = createRank();
					System.out.println(ranks);
					showChamps();
				}
				afficherRankButton.setText("Masquer les rank");
			}
		});
		afficherMaitriseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showMaitrise = !showMaitrise;
				showChamps();
				afficherMaitriseButton.setText("Masquer les maitrises");
			}
		});
	}

	/**
	 * Modify Labels b[0..4] aka blue team and r[0..4] aka red team .
	 * Considering showMaitrise and showRank to show needed information to show to the user.
	 */
	private void showChamps() {
		int blueP = 0, redP = 0;
		for (ChampionEnJeu entry : champs) {
			String aff = entry.getPseudo() + " : " + entry.getChampion();
			if (showMaitrise) {
				aff += " " + entry.getMasteries();
			}
			if (showRank) {
				aff = ranks.get(entry.getIdPseudo()) + " " + aff;
			}
			(entry.isBlueTeam() ? blueTeam.get(blueP++) : redTeam.get(redP++)).setText(aff);

		}
		contentPane.updateUI();

	}

	/**
	 * Use API to create a map with id of each summoners and their rank.
	 *
	 * @return the map created with rank
	 */
	private Map<Integer, String> createRank() {
		Map<Integer, String> idRankMap = new HashMap<>();
		String ids = "";
		for (ChampionEnJeu championEnJeu : champs) {
			ids += championEnJeu.getIdPseudo() + ",";
		}

		String json = Riot.call("api/lol/euw/v2.5/league/by-summoner/" + ids + "/entry");
		for (ChampionEnJeu championEnJeu : champs) {
			try {
				JSONArray jsonArray = new JSONObject(json).getJSONArray("" + championEnJeu.getIdPseudo());
				JSONObject jsonObject = (JSONObject) jsonArray.get(0);
				idRankMap.put(championEnJeu.getIdPseudo(), jsonObject.getString("tier") + " " + ((JSONObject) jsonObject.getJSONArray("entries").get(0)).getString("division"));
			} catch (JSONException e) {
				System.err.println("Erreur createRank: " + e.getMessage());
				e.printStackTrace();
			}
		}
		return idRankMap;

	}
}
