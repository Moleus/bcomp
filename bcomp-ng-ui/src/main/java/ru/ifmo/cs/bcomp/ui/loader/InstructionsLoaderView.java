package ru.ifmo.cs.bcomp.ui.loader;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class InstructionsLoaderView {
    private final ResourceBundle res = ResourceBundle.getBundle("ru.ifmo.cs.bcomp.ui.components.loc", Locale.getDefault());

    private File prevDir = FileSystemView.getFileSystemView().getHomeDirectory();
    private final InstructionsLoader loader;
    private final JFileChooser fileChooser = new JFileChooser(prevDir);

    public InstructionsLoaderView(final InstructionsLoader loader) {
        this.loader = loader;
        initLoader();
    }

    private void initLoader() {
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("TXT file program", "txt");
        fileChooser.addChoosableFileFilter(filter);
    }

    public void load() {
        int selectedOption = fileChooser.showOpenDialog(null);

        if (selectedOption == JFileChooser.APPROVE_OPTION) {
            prevDir = fileChooser.getCurrentDirectory();

            File selected = fileChooser.getSelectedFile();
            processSelectedFile(selected);
        }
    }

    private void processSelectedFile(File selected) {
        try {
            List<String> lines = Files.readAllLines(selected.getAbsoluteFile().toPath());
            loader.loadInstructions(lines);
        } catch (IOException e) {
            System.err.println("Failed to read file [" + selected.getAbsolutePath() + "] with err: " + e.getMessage());
        }
    }
}