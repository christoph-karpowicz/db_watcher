package com.infileconsole.cmd;

public interface Executable {
    public void execute();
    public String getArg();
    public void setArg(String arg);
}