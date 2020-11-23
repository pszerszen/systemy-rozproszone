import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Piotr Szerszeń
 */
public class CustomizerPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = -3374697524824156971L;
	private ButtonGroup btnGroupProtocol = new ButtonGroup();
	private ButtonGroup btnGroupStart = new ButtonGroup();
	private JButton btnLaunch;
	private JComboBox<String> comboBoxApp;
	private JRadioButton rdbtnJoin;
	private JRadioButton rdbtnStart;
	private JRadioButton rdbtnTcp;
	private JRadioButton rdbtnUdp;
	private JTextField textFieldHost;
	private JTextField textFieldName;

	/**
	 * Create the panel.
	 */
	public CustomizerPanel() {
		setSize(new Dimension(265, 260));
		setPreferredSize(new Dimension(265, 260));
		setLayout(null);

		JLabel lblName = new JLabel("Nazwa:");
		lblName.setFont(new Font("Calibri", Font.PLAIN, 12));
		lblName.setBounds(37, 29, 60, 14);
		add(lblName);

		JLabel lblHost = new JLabel("Host:");
		lblHost.setFont(new Font("Calibri", Font.PLAIN, 12));
		lblHost.setBounds(37, 58, 60, 14);
		add(lblHost);

		comboBoxApp = new JComboBox<>();
		comboBoxApp.setFocusable(false);
		comboBoxApp.setModel(new DefaultComboBoxModel<>(
				new String[]{"Komunikator", "Tanki 2013"}));
		comboBoxApp.setSelectedIndex(0);
		comboBoxApp.setBounds(107, 83, 103, 20);
		add(comboBoxApp);

		JLabel lblAplikacja = new JLabel("Aplikacja:");
		lblAplikacja.setFont(new Font("Calibri", Font.PLAIN, 12));
		lblAplikacja.setBounds(37, 83, 60, 18);
		add(lblAplikacja);

		textFieldName = new JTextField();
		textFieldName.setBounds(105, 25, 105, 20);
		textFieldName.setEnabled(false);
		add(textFieldName);
		textFieldName.setColumns(10);

		textFieldHost = new JTextField("127.0.0.1");
		textFieldHost.setBounds(105, 54, 105, 20);
		textFieldHost.setEnabled(false);
		add(textFieldHost);
		textFieldHost.setColumns(10);

		rdbtnStart = new JRadioButton("Zacznij");
		rdbtnStart.setActionCommand("Start");
		rdbtnStart.setAlignmentX(Component.CENTER_ALIGNMENT);
		rdbtnStart.setFont(new Font("Calibri", Font.PLAIN, 12));
		rdbtnStart.setFocusable(false);
		rdbtnStart.setSelected(true);
		rdbtnStart.setBounds(37, 129, 83, 23);
		rdbtnStart.addActionListener(this);
		btnGroupStart.add(rdbtnStart);
		add(rdbtnStart);

		rdbtnJoin = new JRadioButton("Przyłącz się");
		rdbtnJoin.setActionCommand("Join");
		rdbtnJoin.setFocusable(false);
		rdbtnJoin.setAlignmentX(Component.CENTER_ALIGNMENT);
		rdbtnJoin.setFont(new Font("Calibri", Font.PLAIN, 12));
		rdbtnJoin.setBounds(37, 159, 83, 23);
		rdbtnJoin.addActionListener(this);
		btnGroupStart.add(rdbtnJoin);
		add(rdbtnJoin);

		btnLaunch = new JButton(
				"<html><center>Odpal<br/>Aplikację!</center></html>");
		btnLaunch.setFont(new Font("Calibri", Font.BOLD, 16));
		btnLaunch.setHorizontalTextPosition(SwingConstants.CENTER);
		btnLaunch.setActionCommand("Launch");
		btnLaunch.setFocusable(false);
		btnLaunch.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnLaunch.setBounds(10, 201, 245, 48);
		add(btnLaunch);

		rdbtnTcp = new JRadioButton("TCP");
		rdbtnTcp.setActionCommand("TCP");
		btnGroupProtocol.add(rdbtnTcp);
		rdbtnTcp.setSelected(true);
		rdbtnTcp.setAlignmentX(Component.CENTER_ALIGNMENT);
		rdbtnTcp.setFocusable(false);
		rdbtnTcp.setFont(new Font("Calibri", Font.PLAIN, 12));
		rdbtnTcp.setBounds(146, 129, 109, 23);
		add(rdbtnTcp);

		rdbtnUdp = new JRadioButton("UDP");
		rdbtnUdp.setActionCommand("UDP");
		btnGroupProtocol.add(rdbtnUdp);
		rdbtnUdp.setAlignmentX(Component.CENTER_ALIGNMENT);
		rdbtnUdp.setFocusable(false);
		rdbtnUdp.setFont(new Font("Calibri", Font.PLAIN, 12));
		rdbtnUdp.setBounds(146, 159, 109, 23);
		add(rdbtnUdp);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Join")) {
			getTextFieldHost().setEnabled(true);
			getTextFieldName().setEnabled(true);
		} else if (e.getActionCommand().equals("Start")) {
			getTextFieldHost().setEnabled(false);
			getTextFieldName().setEnabled(false);
		}
	}

	/**
	 * @param a
	 */
	public void addActionListener(ActionListener a) {
		btnLaunch.addActionListener(a);
		rdbtnJoin.addActionListener(a);
		rdbtnStart.addActionListener(a);
		rdbtnTcp.addActionListener(a);
		rdbtnUdp.addActionListener(a);
	}

	/**
	 * Gives the value of btnGroupProtocol object.
	 *
	 * @return The btnGroupProtocol.
	 */
	public ButtonGroup getBtnGroupProtocol() {
		return btnGroupProtocol;
	}

	/**
	 * Gives the value of btnGroupStart object.
	 *
	 * @return The btnGroupStart.
	 */
	public ButtonGroup getBtnGroupStart() {
		return btnGroupStart;
	}

	/**
	 * Gives the value of comboBoxApp object.
	 *
	 * @return The comboBoxApp.
	 */
	public JComboBox<String> getComboBoxApp() {
		return comboBoxApp;
	}

	/**
	 * Gives the value of textFieldHost object.
	 *
	 * @return The textFieldHost.
	 */
	public JTextField getTextFieldHost() {
		return textFieldHost;
	}

	/**
	 * Gives the value of textFieldName object.
	 *
	 * @return The textFieldName.
	 */
	public JTextField getTextFieldName() {
		return textFieldName;
	}
}
