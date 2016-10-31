package eg.edu.alexu.csd.oop.draw;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

@SuppressWarnings("serial")
public class PropertiesDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private Shape shape;
	private PropertiesTable table;

	public PropertiesDialog(Shape s) {
		this.shape = s;
		setContents();
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}

	private void setContents() throws RuntimeException {
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		table = new PropertiesTable(shape);
		contentPanel.add(table, BorderLayout.CENTER);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton okButton = new JButton("OK");
		okButton.setActionCommand("OK");
		okButton.addActionListener(new OkAction());
		buttonPane.add(okButton);

		getRootPane().setDefaultButton(okButton);

		getContentPane().add(contentPanel, BorderLayout.CENTER);
	}

	public void showDialog() {
		if (!table.isEmpty()) {
			setModal(true);
			setVisible(true);
		}
	}

	private void okAction() {
		Map<String, Double> newProperties = shape.getProperties();
		for (int i = 0; i < table.sizeOfTable(); ++i) {
			try {
				PropertiesTable.TableRow row = (PropertiesTable.TableRow) table.getRow(i);
				newProperties.put(row.propertyName, Double.parseDouble((String) row.val));
			} catch (Exception e) {
				// show error massage
				return;
			}
		}
		shape.setProperties(newProperties);
		setVisible(false);
		dispose();
	}

	private class OkAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			okAction();
		}
	}

	/*
	 * PropertiesTable
	 */
	private class PropertiesTable extends JPanel {

		private JTable table;
		private int size = 0;

		PropertiesTable(Shape shape) {
			try {
				String[] coloumnNames = { "Property", "value" };
				Map<String, Double> properties = shape.getProperties();
				Object[][] rowContent = new Object[properties.size()][2];

				for (Map.Entry<String, Double> p : properties.entrySet()) {
					String valField = p.getValue().toString();
					String txtlabel = p.getKey();
					if (txtlabel.charAt(0) != '_') {
						rowContent[size][0] = txtlabel;
						rowContent[size++][1] = valField;
					}
				}
				TableModel model = new EditableTableModel(coloumnNames, rowContent);
				table = new JTable(model);
				table.createDefaultColumnsFromModel();
				JScrollPane scrollPane = new JScrollPane(table);
				contentPanel.add(scrollPane);
			} catch (NullPointerException ex) {
			}

		}

		public int sizeOfTable() {
			return size;
		}

		public boolean isEmpty() {
			return (size == 0);
		}

		public TableRow getRow(int i) {
			TableRow row = new TableRow();
			row.propertyName = (String) getValueAt(i, 0);
			row.val = getValueAt(i, 1);
			return row;
		}

		private Object getValueAt(int row, int col) {
			return table.getValueAt(row, col);
		}

		class EditableTableModel extends AbstractTableModel {

			String[] columnTitles;
			Object[][] dataEntries;

			public EditableTableModel(String[] columnTitles, Object[][] dataEntries) {
				this.columnTitles = columnTitles;
				this.dataEntries = dataEntries;
			}

			public int getRowCount() {
				return dataEntries.length;
			}

			public int getColumnCount() {
				return columnTitles.length;
			}

			public Object getValueAt(int row, int column) {
				return dataEntries[row][column];
			}

			public String getColumnName(int column) {
				return columnTitles[column];
			}

			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int column) {
				return getValueAt(0, column).getClass();
			}

			public boolean isCellEditable(int row, int column) {
				return (column == 1);
			}

			public void setValueAt(Object value, int row, int column) {
				dataEntries[row][column] = value;
			}
		}

		public class TableRow {
			String propertyName;
			Object val;
		}

	}

}
