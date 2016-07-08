package gui.actions.dialog;

import exceptions.InvalidExportException;
import gui.MainFrame;
import gui.dialogs.generic.AnalitikaIzvodaDialog;
import gui.tasks.ExportNalogZaPlacanje;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLException;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ExportNalogZaPlacanjeAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AnalitikaIzvodaDialog dialog;
	private JFileChooser fileChooser;

	public ExportNalogZaPlacanjeAction(AnalitikaIzvodaDialog dialog) {
		this.dialog = dialog;
		putValue(NAME, "Export");
		File defaultLocation = new File("./testExportResults");
		fileChooser = new JFileChooser(defaultLocation);
		fileChooser.setMultiSelectionEnabled(false);
		FileNameExtensionFilter jksFilter = new FileNameExtensionFilter(
				"XML file", "xml");
		fileChooser.addChoosableFileFilter(jksFilter);
		fileChooser.setFileFilter(jksFilter);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (fileChooser.showSaveDialog(dialog) == JFileChooser.APPROVE_OPTION) {
			dialog.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			File selectedFile = fileChooser.getSelectedFile();
			if (!selectedFile.getPath().toLowerCase().endsWith(".xml")) {
				selectedFile = new File(selectedFile.getPath() + ".xml");
			}
			JTable tableGrid = dialog.getTableGrid();
			int selectedRow = tableGrid.getSelectedRow();
			try {
				String successMessage = null;
				ExportNalogZaPlacanje.exportNalog(new FileOutputStream(selectedFile));
					successMessage = "Uspešno eksportovanje naloga";
				
				if(successMessage != null){
					JOptionPane.showMessageDialog(dialog, successMessage, 
							"Uspeh", JOptionPane.INFORMATION_MESSAGE);
					dialog.getGenTableModel().open();
					tableGrid.setRowSelectionInterval(selectedRow, selectedRow);
				}
					
			
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(MainFrame.getInstance(), 
						"Desila se neočekivana greška sa bazom podataka, molimo vas da kontaktirate administratora sistema: \n" +
							e1.getMessage(), 
						"Greška sa bazom podataka", JOptionPane.ERROR_MESSAGE);
			} catch (InvalidExportException e1) {
				JOptionPane.showMessageDialog(MainFrame.getInstance(), 
						"Dogodila se greška prilikom eksportovanja naloga za medjubankarsko plaćanje: \n" +
							e1.getMessage(), 
						"Greška", JOptionPane.ERROR_MESSAGE);
			} catch(Exception e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(MainFrame.getInstance(), 
						"Exception has occured: \n" +
							e1.getMessage(), 
						"Exception", JOptionPane.ERROR_MESSAGE);
			}
			dialog.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

}
