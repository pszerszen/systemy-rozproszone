package tanks;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

public class TanksPanel extends JPanel implements TanksGame {
	private static final long serialVersionUID = 7756834483777462484L;
	private JLabel[][] fields;
	private String[][] localMap;
	private JPanel mapPanel = new JPanel();
	private String nick;

	public TanksPanel(String nick) {
		if (nick != null) {
			this.nick = nick;
		} else {
			this.nick = "";
		}
		FlowLayout flowLayout = (FlowLayout) getLayout();
		flowLayout.setHgap(100);
		setPreferredSize(new Dimension(500, 600));
		setSize(new Dimension(500, 600));
		mapPanel.setBackground(Color.WHITE);
		mapPanel.setPreferredSize(new Dimension(360, 360));
		setLocalMap(map);
		fields = new JLabel[9][9];
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				fields[i][j] = new JLabel(new ImageIcon(
						"res/" + localMap[i][j] + ".jpg"));
				mapPanel.add(fields[i][j]);
			}
		}

		add(mapPanel);
		mapPanel.setLayout(new GridLayout(9, 9, 0, 0));
	}

	/**
	 * Refreshes graphically #fields with a new local map
	 *
	 * @see tanks.TanksGame#copyChanges(java.lang.String[][])
	 */
	@Override
	public void copyChanges(String[][] newMap) {
		setLocalMap(newMap);
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (!localMap[i][j].contains("tank")) {
					fields[i][j].setIcon(new ImageIcon(
							"res/" + localMap[i][j] + ".jpg"));
				} else {
					String[] name = localMap[i][j].split(" "); // [0] ma nazwę
					// ikonki, a [1] nick
					if (name[1].equals(nick)) {
						fields[i][j].setIcon(new ImageIcon(
								"res/" + name[0] + ".jpg"));
					} else {
						fields[i][j].setIcon(new ImageIcon(
								"res/e" + name[0] + ".jpg"));
					}
				}
				fields[i][j].repaint();
			}
		}
	}

	@Override
	public int[] getPlaceOf(String nick) {
		return new int[0];
	}

	@Override
	public void makeMove(String nick, String move) {}

	public void setLocalMap(String[][] newMap) {
		localMap = new String[newMap.length][];
		for (int i = 0; i < newMap.length; i++) {
			localMap[i] = newMap[i].clone();
		}
	}
}
