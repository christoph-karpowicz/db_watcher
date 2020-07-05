package com.infileconsole.eval;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.infileconsole.cmd.Executable;
import com.infileconsole.cmd.Notes;
import com.infileconsole.cmd.Snap;
import com.infileconsole.cmd.CmdType;

public class Line {
    private final String CMD_PATTERN = "\\s*\\/\\/\\s*@ifc(\\s+)?([a-z]+)?(\\s+)?(.+)?\\s*";

    private final String content;
    private final int number;
    private Optional<Executable> cmd = Optional.empty();
    
    public Line(String content, int number) {
        this.content = content;
        this.number = number;
    }

    public void findCmd() {
        Pattern ptrn = Pattern.compile(CMD_PATTERN, Pattern.CASE_INSENSITIVE);
        Matcher matcher = ptrn.matcher(content);

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

            if (hasCmd()) {
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