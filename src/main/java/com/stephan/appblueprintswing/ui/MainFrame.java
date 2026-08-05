package com.stephan.appblueprintswing.ui;

import com.stephan.appblueprintswing.application.services.ClickCountApplicationService;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

public final class MainFrame extends JFrame {
    private final ClickCountApplicationService clickCountService;
    private final JLabel countLabel;

    public MainFrame(ClickCountApplicationService clickCountService) {
        this.clickCountService = clickCountService;

        setTitle("App Blueprint Swing");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        countLabel = new JLabel("0", SwingConstants.CENTER);
        countLabel.setFont(countLabel.getFont().deriveFont(Font.BOLD, 48f));

        JButton incrementButton = new JButton("Click me");
        incrementButton.addActionListener(event -> updateCountLabel(clickCountService.incrementClickCount()));

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(event -> updateCountLabel(clickCountService.resetClickCount()));

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        buttonsPanel.add(incrementButton);
        buttonsPanel.add(resetButton);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(countLabel, BorderLayout.CENTER);
        contentPanel.add(buttonsPanel, BorderLayout.SOUTH);

        setJMenuBar(createMenuBar());
        setContentPane(contentPanel);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent event) {
                clickCountService.shutdown();
            }
        });

        updateCountLabel(clickCountService.getCurrentClickCount());
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu appMenu = new JMenu("App");

        JMenuItem settingsMenuItem = new JMenuItem("Settings");
        settingsMenuItem.addActionListener(event -> openSettingsDialog());

        JMenuItem aboutMenuItem = new JMenuItem("About");
        aboutMenuItem.addActionListener(event -> JOptionPane.showMessageDialog(
            this,
            "App Blueprint Swing\nHello World with SQLite",
            "About",
            JOptionPane.INFORMATION_MESSAGE
        ));

        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(event -> {
            clickCountService.shutdown();
            dispose();
        });

        appMenu.add(settingsMenuItem);
        appMenu.add(aboutMenuItem);
        appMenu.addSeparator();
        appMenu.add(exitMenuItem);

        menuBar.add(appMenu);
        return menuBar;
    }

    private void openSettingsDialog() {
        JDialog dialog = new JDialog(this, "Settings", true);
        dialog.setSize(640, 180);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(8, 8));

        JTextField databasePathField = new JTextField(clickCountService.getDatabasePath(), 40);
        JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(event -> chooseDatabasePath(databasePathField));

        JPanel pathPanel = new JPanel(new BorderLayout(8, 8));
        pathPanel.add(databasePathField, BorderLayout.CENTER);
        pathPanel.add(browseButton, BorderLayout.EAST);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(event -> {
            try {
                clickCountService.setDatabasePath(databasePathField.getText());
                updateCountLabel(clickCountService.getCurrentClickCount());
                dialog.dispose();
            } catch (RuntimeException exception) {
                JOptionPane.showMessageDialog(dialog, exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(event -> dialog.dispose());

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.add(saveButton);
        actionPanel.add(cancelButton);

        JPanel bodyPanel = new JPanel(new GridLayout(1, 1));
        bodyPanel.add(pathPanel);

        dialog.add(bodyPanel, BorderLayout.CENTER);
        dialog.add(actionPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void chooseDatabasePath(JTextField databasePathField) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select SQLite database file");
        fileChooser.setFileFilter(new FileNameExtensionFilter("SQLite files (*.sqlite, *.db)", "sqlite", "db"));

        String currentPath = databasePathField.getText().trim();
        if (!currentPath.isEmpty()) {
            fileChooser.setSelectedFile(new File(currentPath));
        }

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile != null) {
                SwingUtilities.invokeLater(() -> databasePathField.setText(selectedFile.getAbsolutePath()));
            }
        }
    }

    private void updateCountLabel(int value) {
        countLabel.setText(String.valueOf(value));
    }
}

