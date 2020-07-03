package com.infileconsole.eval;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Evaluator implements Evaluable, Runnable {
    Path path;

    public Evaluator(Path path) {
        this.path = path;
    }
    
    public void evalaute() throws IOException {
        System.out.println("eval");
        BufferedReader reader = Files.newBufferedReader(path);
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        reader.close();
    }

    @Override
    public void run() {
        try {
            evalaute();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}