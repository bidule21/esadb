package view.settings.weapons;


import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;

import controller.Controller;
import model.SettingsChangeListener;
import model.SettingsModel;
import model.Unit;
import model.Weapon;
import view.IconButton;
import view.TableEditor;


@SuppressWarnings("serial")
public class SettingsWeapons extends JPanel implements ActionListener, TableModelListener {

	private SettingsChangeListener scl;

	private JTable table;
	private WeaponTableModel wtm;

	public SettingsWeapons(SettingsModel config, SettingsChangeListener scl) {
		this.scl = scl;

		this.setSize(735, 420);
		this.setLayout(null);
		
		wtm = new WeaponTableModel(Arrays.asList(config.getWeapons()));
		wtm.addTableModelListener(this);
		table = new JTable(wtm);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setRowHeight(20);
		table.getTableHeader().setReorderingAllowed(false);

		table.getColumnModel().getColumn(0).setMinWidth(100);
		table.getColumnModel().getColumn(1).setMinWidth(200);
		table.getColumnModel().getColumn(2).setMinWidth(80);
		table.getColumnModel().getColumn(3).setMinWidth(70);
		table.getColumnModel().getColumn(4).setMinWidth(80);

		table.getColumnModel().getColumn(0).setPreferredWidth(100);
		table.getColumnModel().getColumn(1).setPreferredWidth(500);
		table.getColumnModel().getColumn(2).setPreferredWidth(80);
		table.getColumnModel().getColumn(3).setPreferredWidth(70);
		table.getColumnModel().getColumn(4).setPreferredWidth(80);

		table.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
			{setHorizontalAlignment(SwingConstants.RIGHT);}
		});
		table.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
			{setHorizontalAlignment(SwingConstants.RIGHT);}
	
			@Override
			protected void setValue(Object o) {
				super.setValue(String.format("%.3f", (Double) o));
			}
		});

		table.getColumnModel().getColumn(2).setCellEditor(new TableEditor(new JSpinner(), 1));
		table.getColumnModel().getColumn(3).setCellEditor(new TableEditor(new JComboBox<Unit>(Unit.values()), 0));
		JComboBox<Integer> comboBox = new JComboBox<Integer>(new Integer[] {1, 2, 3, 4, 5, 6});
		((JLabel) comboBox.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		table.getColumnModel().getColumn(4).setCellEditor(new TableEditor(comboBox, 0));

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(15, 15, 705, 355);
		scrollPane.setViewportView(table);
		add(scrollPane);		

		JButton button_minus = new IconButton(IconButton.REMOVE, "-", this);
		button_minus.setBounds(15, 385, 45, 20);
		add(button_minus);

		JButton button_plus = new IconButton(IconButton.ADD, "+", this);
		button_plus.setBounds(675, 385, 45, 20);
		add(button_plus);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		switch (arg0.getActionCommand()) {
			case "+":
				Controller.get().getConfig().newWeapon();
				wtm.setWeapons(Arrays.asList(Controller.get().getConfig().getWeapons()));
				for (int i = 0; i < table.getRowCount(); i++) {
					if (((Weapon) table.getValueAt(i, 1)).toString().equals("Neue Waffe")) {
						table.setRowSelectionInterval(i, i);
						table.scrollRectToVisible(new Rectangle(table.getCellRect(i, 0, true)));
					}
				}
				break;
			case "-":
				int index = table.getSelectedRow();
				if (index < 0) break;

				Weapon w = (Weapon) wtm.getValueAt(table.getSelectedRow(), 1);
				if (Controller.get().getConfig().removeWeapon(w)) {
					wtm.setWeapons(Arrays.asList(Controller.get().getConfig().getWeapons()));
					if (index >= table.getRowCount()) index = table.getRowCount() - 1;
					if (table.getRowCount() > 0) {
						table.setRowSelectionInterval(index, index);
						table.scrollRectToVisible(new Rectangle(table.getCellRect(index, 0, true)));
					}
				} else {
					JOptionPane.showMessageDialog(
						this,
						"Die Waffe kann nicht gelöscht werden, da sie\nnoch mindestens einer Regel zugeordnet ist.",
						"Löschen nicht möglich",
						JOptionPane.INFORMATION_MESSAGE
					);
				}
				break;
		}
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		scl.settingsChanged();
	}
}