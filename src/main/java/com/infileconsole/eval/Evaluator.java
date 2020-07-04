package com.infileconsole.eval;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Evaluator implements Evaluable, Runnable {
    Path filePath;
    List<Line> linesWithCmd = new ArrayList<Line>();

    public Evaluator(Path filePath) {
        this.filePath = filePath;
    }
    
    public void evalauteFile() throws IOException {
        BufferedReader reader = Files.newBufferedReader(filePath);
        int lineCounter = 1;
        String rawLine;
        while ((rawLine = reader.readLine()) != null) {
            Line line = new Line(rawLine, lineCounter);
            evalauteLine(line);
            lineCounter++;
        }
        reader.close();
    }

    private void evalauteLine(Line line) {
        line.findCmd();
        if (line.hasCmd()) {
            linesWithCmd.add(line);
        }
    }

    @Override
    public void run() {
        try {
            evalauteFile();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}