package com.infileconsole.eval;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.infileconsole.cmd.Executable;
import com.infileconsole.cmd.Notes;
import com.infileconsole.cmd.Snap;
import com.infileconsole.cmd.CmdType;

public class Line {
    private final Pattern initCmdRgxp = Pattern.compile("\\s*\\/\\/\\s*@ifc(\\s+)?([a-z]+)?(\\s+)?(.+)?\\s*", Pattern.CASE_INSENSITIVE);
    private final String content;
    private final int number;
    private Optional<Executable> cmd = Optional.empty();
    
    public Line(String content, int number) {
        this.content = content;
        this.number = number;
    }

    public void findCmd() {
        Matcher matcher = initCmdRgxp.matcher(content);

        while (matcher.find()) {
            String cmdStr = matcher.group(2);
            switch (cmdStr) {
                case "notes":
                    Executable notesCmd = new Notes(CmdType.NOTES);
                    cmd = Optional.of(notesCmd);
                    break;
                case "snap":
                    Executable snapCmd = new Snap(CmdType.NOTES);
                    cmd = Optional.of(snapCmd);
                    break;
            }

            if (cmd.isPresent()) {
                String arg = matcher.group(4);
                if (arg != null && !arg.isEmpty()) {
                    cmd.get().setArg(arg);
                }
                System.out.println(cmd.get().getArg());
            }
        }
    }

    public boolean hasCmd() {
        return cmd.isPresent();
    }

    public int getNumber() {
        return number;
    }
}