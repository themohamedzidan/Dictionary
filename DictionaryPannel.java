package ex2;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.text.BreakIterator;
import java.text.StringCharacterIterator;
import java.util.Map;
import java.util.Scanner;


public class DictionaryPannel extends JPanel {
    private JButton addTerm, deleteTerm, updateTerm, importButton, export;
    private JTable table;
    private JLabel searchLabel;
    private JTextField searchTxt;
    private JScrollPane scrollPane;
    private Dictionary book;
    private DefaultTableModel tbm;

    public DictionaryPannel() {
        // Buttons
        addTerm = new JButton("Add term");
        deleteTerm = new JButton("Delete term");
        updateTerm = new JButton("Update term");
        importButton = new JButton("Import");
        export = new JButton("Export");

        // Label
        searchLabel = new JLabel("search: ");

        // Text-box
        searchTxt = new JTextField(10);

        // Create table
        table = new JTable() {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setFont(new Font("Serif", Font.BOLD, 20));

        table.setFillsViewportHeight(true);
        scrollPane = new JScrollPane(table);

        book = new Dictionary();
        buildPannel();
    }

    private void buildPannel() {
        // Add columns to table
        String[] columnNames = {"Term", "Meanning"};
        tbm = (DefaultTableModel) this.table.getModel();
        for (String col : columnNames) {
            tbm.addColumn(col);
        }
        // Add the buttons to controller
        JPanel controls = new JPanel();
        GridLayout experimentLayout = new GridLayout(2, 0);
        controls.setLayout(experimentLayout);
        add(searchLabel);
        add(searchTxt);
        add(addTerm);
        add(deleteTerm);
        add(updateTerm);
        add(importButton);
        add(export);
        add(scrollPane);

        // Control listener
        searchTxt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchTxtKeyReleased();
            }
        });
        ControlsListener l = new ControlsListener();
        addTerm.addActionListener(l);
        deleteTerm.addActionListener(l);
        updateTerm.addActionListener(l);
        export.addActionListener(l);
        importButton.addActionListener(l);
        this.setBackground(Color.WHITE);
    }

    private void searchTxtKeyReleased() {
        Dictionary newBook = new Dictionary(book.Search(searchTxt.getText()));
        UpdateTable(newBook, tbm);
    }

    private void UpdateTable(Dictionary map, DefaultTableModel dtm) {
        if (dtm.getRowCount() > 0) {
            for (int i = dtm.getRowCount() - 1; i > -1; i--) {
                dtm.removeRow(i);
            }
        }

        String[][] data = new String[map.getBook().size()][3];
        int i = 0;
        for (Map.Entry<String, String> entry : map.getBook().entrySet()) {
            data[i][0] = entry.getKey().split(" ")[0];
            data[i][1] = entry.getValue();
            dtm.insertRow(i, data[i]);
            i++;
        }

        this.table.setModel(dtm);
        dtm.fireTableDataChanged();
    }

    private String getSelectedTerm() {
        int row = this.table.getSelectedRow();
        if (row == -1) {
            return null;
        }
        return (String) this.table.getValueAt(row, 0);
    }

    private String getSelectedMeanning() {
        int row = this.table.getSelectedRow();
        if (row == -1) {
            return null;
        }
        return (String) this.table.getValueAt(row, 1);
    }

    private class ControlsListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            searchTxt.setText("");
            if (e.getSource() == addTerm) {
                addAction();
            } else if (e.getSource() == deleteTerm) {
                deleteAction();
            } else if (e.getSource() == updateTerm) {
                updateAction();
            } else if (e.getSource() == importButton) {
                importAction();
            } else if (e.getSource() == export) {
                exportAction();
            }
        }

        private void importAction() {
            IOactions io = new IOactions();
            JFileChooser chooser = new JFileChooser(new File(String.valueOf(FileSystems.getDefault().getPath("."))));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("txt", "txt");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(getParent());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String filepath = chooser.getSelectedFile().getName();

                try {
                    book.setBook(io.Import(filepath));
                    UpdateTable(book, tbm);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(getParent(), "File not found", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (Error ex) {
                    JOptionPane.showMessageDialog(getParent(), ex, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private void exportAction() {
            IOactions io = new IOactions();
            JFileChooser fileChooser = new JFileChooser(new File(String.valueOf(FileSystems.getDefault().getPath("."))));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("txt", "txt");
            fileChooser.setFileFilter(filter);
            fileChooser.setDialogTitle("Specify a file to save");
            int userSelection = fileChooser.showSaveDialog(getParent());
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                String saveAs = fileToSave.getAbsolutePath();
                try {
                    io.Export(book.getBook(), saveAs);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(getParent(), "File not found", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private void updateAction() {
            String oldTerm = getSelectedTerm();
            if (oldTerm == null) {
                JOptionPane.showMessageDialog(getParent(), "You should select a term to edit", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                String term = getSelectedTerm();
                String meanning = getSelectedMeanning();
                String oldTerm1 = String.format("%s %s", term, meanning);
                JTextField termTxt = new JTextField(term);
                JTextField meanningTxt = new JTextField(meanning);
                Object[] message = {
                        "Term: ", termTxt,
                        "Meanning: ", meanningTxt,
                };
                int option = JOptionPane.showConfirmDialog(getParent(), message, "Add a new contact", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    term = termTxt.getText();
                    meanning = meanningTxt.getText();
                    String fullTerm = String.format("%s %s", term, meanning);
                   
                    if (book.isExist(fullTerm) && !fullTerm.equals(oldTerm1)) {
                        JOptionPane.showMessageDialog(getParent(), String.format("%s already exist in the Dictionary", fullTerm), "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        if (!oldTerm1.equals(fullTerm)) {
                            book.DeleteTerm(oldTerm1);
                        }
                        try {
                            book.AddTerm( term, meanning);
                            UpdateTable(book, tbm);
                        } catch (Error ex) {
                            JOptionPane.showMessageDialog(getParent(), ex, "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        }

        private void deleteAction() {
        	
            if (getSelectedTerm() == null) {
                JOptionPane.showMessageDialog(getParent(), "You should select a term to delete", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                book.DeleteTerm(getSelectedTerm());
                UpdateTable(book, tbm);
            }
        }

        private void addAction() {
            JTextField termTxt = new JTextField();
            JTextField meanningTxt = new JTextField();
            Object[] message = {
                    "term: ", termTxt,
                    "term meanning: ", meanningTxt,
            };
            int option = JOptionPane.showConfirmDialog(getParent(), message, "Add a new term", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String term = termTxt.getText();
                String meanning = meanningTxt.getText();
                if (book.isExist(term)) {
                    JOptionPane.showMessageDialog(null, String.format("term %s  already exists", term), "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    try {
                        book.AddTerm(term, meanning);
                        UpdateTable(book, tbm);
                    } catch (Error ex) {
                        JOptionPane.showMessageDialog(null, ex, "Error", JOptionPane.ERROR_MESSAGE);
                    }

                }
            }
        }
    }
}


