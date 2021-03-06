package view.teamedit;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ScrollPaneConstants;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import controller.Controller;
import model.Gender;
import model.Group;
import model.Single;
import model.Team;
import view.FilterBox;
import view.IconButton;

import javax.swing.JLabel;
import javax.swing.JSeparator;

import java.awt.CardLayout;
import java.util.Vector;

import javax.swing.JSplitPane;


@SuppressWarnings("serial")
public class TeamEdit extends JDialog implements ActionListener {

	private Controller controller;

	private ResultTable table_team;
	private ResultTable table_single;
	private ResultTable table_member;

	private JPanel card_panel;
	private CardLayout cards;

	public TeamEdit(Frame parent) {
		super(parent, "Mannschaften bearbeiten");

		this.controller = Controller.get();

		setModal(true);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int row = table_team.getSelectedRow();
				if (row >= 0) {
					Team team = (Team) table_team.getValueAt(row, -1);
					if (team.getDisziplin() == null || team.getGroup(false) == null) {
						controller.remove(team);
					}
				}
				controller.save();
			}
		});
		setMinimumSize(new Dimension(500, 340));

		getContentPane().setLayout(new BorderLayout());
		{
			card_panel = new JPanel();
			getContentPane().add(card_panel, BorderLayout.CENTER);
			card_panel.setBorder(new EmptyBorder(5, 5, 5, 5));
			cards = new CardLayout(0, 0);
			card_panel.setLayout(cards);
			{
				FilterBox discipline;
				FilterBox group;

				JPanel panel = new JPanel();
				card_panel.add(panel, "TEAM_LIST");
				panel.setBorder(null);
				panel.setLayout(new BorderLayout(5, 5));
				{
					JPanel panel_1 = new JPanel();
					panel.add(panel_1, BorderLayout.NORTH);
					panel_1.setBorder(null);
					panel_1.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
					{
						JLabel label = new JLabel("Filter:");
						panel_1.add(label);
					}
					{
						discipline = new FilterBox("Alle Disziplinen", Controller.get().getDisziplinen());
						panel_1.add(discipline);
						discipline.setActionCommand("DISCIPLINE_TEAM");
						discipline.addActionListener(this);
					}
					{
						group = new FilterBox("Alle Gruppen", Controller.get().getConfig().getGroups());
						panel_1.add(group);
						group.setActionCommand("GROUP_TEAM");
						group.addActionListener(this);
					}
				}
				{
					JScrollPane scrollPane = new JScrollPane();
					scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
					scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
					scrollPane.setMinimumSize(new Dimension(460, 100));
					scrollPane.setPreferredSize(new Dimension(600, 400));
					panel.add(scrollPane, BorderLayout.CENTER);
					table_team = new ResultTable(true, controller.getModel().getErgebnisse(), discipline, group, null, false);
					scrollPane.setViewportView(table_team);
				}
				{
					JPanel panel_1 = new JPanel();
					panel.add(panel_1, BorderLayout.SOUTH);
					panel_1.setBorder(null);
					panel_1.setLayout(new BorderLayout(5, 5));
					{
						JButton button = new IconButton(IconButton.REMOVE, "REMOVE_TEAM", this);
						panel_1.add(button, BorderLayout.WEST);
					}
					{
						JPanel panel_1_1 = new JPanel();
						panel_1.add(panel_1_1, BorderLayout.CENTER);
						panel_1_1.setBorder(null);
						panel_1_1.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
						{
							JButton button = new JButton("Bearbeiten");
							button.setActionCommand("EDIT");
							button.addActionListener(this);
							panel_1_1.add(button);
						}
					}
					{
						JButton button = new IconButton(IconButton.ADD, "ADD_TEAM", this);
						panel_1.add(button, BorderLayout.EAST);
					}
				}
			}
			{
				FilterBox discipline;
				FilterBox group;

				JPanel panel = new JPanel();
				panel.setBorder(null);
				panel.setLayout(new BorderLayout(5, 5));
				{
					JPanel panel_1 = new JPanel();
					panel.add(panel_1, BorderLayout.NORTH);
					panel_1.setBorder(null);
					panel_1.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
					{
						JLabel label = new JLabel("Filter:");
						panel_1.add(label);
					}
					{
						discipline = new FilterBox("Alle Disziplinen", Controller.get().getDisziplinen());
						panel_1.add(discipline);
						discipline.setActionCommand("DISCIPLINE_SINGLE");
						discipline.addActionListener(this);
					}
					{
						Vector<Object> filterGroups = new Vector<Object>();
						for (Group g : Controller.get().getConfig().getGroups()) {
							if (g.getGender() != Gender.ANY) filterGroups.add(g);
						}
						group = new FilterBox("Alle Gruppen", filterGroups);
						panel_1.add(group);
						group.setActionCommand("GROUP_SINGLE");
						group.addActionListener(this);
					}
				}
				{
					JScrollPane scrollPane = new JScrollPane();
					scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
					scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
					scrollPane.setMinimumSize(new Dimension(460, 100));
					scrollPane.setPreferredSize(new Dimension(600, 300));
					panel.add(scrollPane, BorderLayout.CENTER);
					table_single = new ResultTable(false, controller.getModel().getErgebnisse(), discipline, group, table_team, false);
					scrollPane.setViewportView(table_single);
				}
				{
					JPanel panel_1 = new JPanel();
					panel.add(panel_1, BorderLayout.SOUTH);
					panel_1.setBorder(null);
					panel_1.setLayout(new BorderLayout(5, 5));
					{
						JButton button = new IconButton(IconButton.UP, "REMOVE_SINGLE", this);
						panel_1.add(button, BorderLayout.WEST);
					}
					{
						JPanel panel_1_1 = new JPanel();
						panel_1.add(panel_1_1, BorderLayout.CENTER);
						panel_1_1.setBorder(null);
						panel_1_1.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
						{
							JButton button = new JButton("Mannschaften");
							button.setActionCommand("LIST");
							button.addActionListener(this);
							panel_1_1.add(button);
						}
					}
					{
						JButton button = new IconButton(IconButton.DOWN, "ADD_SINGLE", this);
						panel_1.add(button, BorderLayout.EAST);
					}
				}

				JScrollPane scrollPane = new JScrollPane();
				scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
				scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
				scrollPane.setMinimumSize(new Dimension(460, 100));
				scrollPane.setPreferredSize(new Dimension(600, 100));
				table_member = new ResultTable(false, controller.getModel().getErgebnisse(), null, null, table_team, true);
				scrollPane.setViewportView(table_member);

				JSplitPane split_panel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panel, scrollPane);
				split_panel.setBorder(null);
				card_panel.add(split_panel, "TEAM_MEMBER");
			}
		}
		{
			JPanel panel = new JPanel();
			getContentPane().add(panel, BorderLayout.SOUTH);
			panel.setBorder(new EmptyBorder(0, 5, 5, 5));
			panel.setLayout(new BorderLayout(5, 5));
			{
				JSeparator separator = new JSeparator();
				panel.add(separator, BorderLayout.NORTH);
			}
			{
				JButton button = new JButton("Schließen");
				panel.add(button, BorderLayout.EAST);
				button.setActionCommand("CANCEL");
				button.addActionListener(this);
				getRootPane().setDefaultButton(button);
			}
		}

		pack();
		setLocationRelativeTo(parent);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Team team = null;
		Single single = null;
		Single member = null;
		int row = -1;
		
		row = table_team.getSelectedRow();
		if (row >= 0) team = (Team) table_team.getValueAt(row, -1);

		row = table_single.getSelectedRow();
		if (row >= 0) single = (Single) table_single.getValueAt(row, -1);

		row = table_member.getSelectedRow();
		if (row >= 0) member = (Single) table_member.getValueAt(row, -1);

		switch (e.getActionCommand()) {
			case "CANCEL":
				dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
				setVisible(false);
				dispose();
				break;
			case "DISCIPLINE_TEAM":
			case "GROUP_TEAM":
				table_team.fireTableDataChanged(team);
				break;
			case "REMOVE_TEAM":
				if (team != null) {
					controller.remove(team);
					table_team.fireTableDataChanged(null);
				}
				break;
			case "EDIT":
				if (team != null) {
					table_single.fireTableDataChanged(null);
					table_member.fireTableDataChanged(null);
					cards.show(card_panel, "TEAM_MEMBER");
				}
				break;
			case "ADD_TEAM":
				Team t = new Team(null, null);
				controller.add(t);
				table_team.fireTableDataChanged(t);
				table_single.fireTableDataChanged(null);
				table_member.fireTableDataChanged(null);
				cards.show(card_panel, "TEAM_MEMBER");
				break;
			case "DISCIPLINE_SINGLE":
			case "GROUP_SINGLE":
				table_single.fireTableDataChanged(single);
				break;
			case "REMOVE_SINGLE":
				if (team != null && member != null) {
					team.removeMember(member);
					table_single.fireTableDataChanged(member);
					table_member.fireTableDataChanged(null);
				}
				break;
			case "LIST":
				if (team != null && (team.getDisziplin() == null || team.getGroup(false) == null)) {
					controller.remove(team);
					table_team.fireTableDataChanged(null);
				}
				cards.show(card_panel, "TEAM_LIST");
				break;
			case "ADD_SINGLE":
				if (team != null && single != null) {
					if (team.getDisziplin() == null) team.setDisziplin(single.getDisziplin());
					if (team.getGroup(false) == null) team.setGroup(single.getGroup(false));
					team.addMember(single);
					table_single.fireTableDataChanged(null);
					table_member.fireTableDataChanged(single);
				}
				break;
		}
	}
}