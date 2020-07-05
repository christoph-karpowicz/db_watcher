package com.infileconsole.cmd;

public class Cmd implements Executable {
    private String arg;
    private final CmdType type;

    public Cmd(CmdType type) {
        this.type = type;
    }

    public String getArg() {
        return arg;
    }
    
    public void setArg(String arg) {
        this.arg = arg;
    }

    public CmdType getType() {
        return type;
    }
    
    public void execute() {
    }
}