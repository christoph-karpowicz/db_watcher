package com.infileconsole.cmd;

public class Cmd implements Executable {
    private final CmdType type;
    private String arg;

    public Cmd(CmdType type) {
        this.type = type;
    }

    public String getArg() {
        return arg;
    }
    
    public void setArg(String arg) {
        this.arg = arg;
    }

    public void execute() {
    }
}