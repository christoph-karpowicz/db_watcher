package com.infileconsole.eval;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.infileconsole.watcher.WatchedFile;

public class Evaluator implements Evaluable, Runnable {
    WatchedFile file;
    List<Line> linesWithCmd = new ArrayList<Line>();

    public Evaluator(WatchedFile file) {
        this.file = file;
    }
    
    public void evalauteFile() throws IOException {
        BufferedReader reader = Files.newBufferedReader(file.toPath());
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
            System.out.println(file.getId());
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