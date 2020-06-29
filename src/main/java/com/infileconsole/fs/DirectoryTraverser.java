package com.infileconsole.fs;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class DirectoryTraverser {
    private Path path;
    private File root;
    private ArrayList<Path> dirs;

    public DirectoryTraverser(Path path) {
        this.path = path;
        this.dirs = new ArrayList<Path>();
    }

    public void init() {
        root = path.toFile();
    }
    
	public ArrayList<Path> traverse(){
        visit(root);
        return dirs;
    }
    
    private void visit(File node) {
		if (node.isDirectory()) {
            dirs.add(Paths.get(node.getAbsolutePath()));
            
			String[] subNodes = node.list();
			for (String fileName : subNodes) {
				visit(new File(node, fileName));
			}
		}
    }
}