package com.workshare.msnos.usvc_client.ui;

import com.workshare.msnos.usvc_client.Command;


public class Menu {

    private final Command[] commands;

    public Menu(Command... commands) {
        this.commands = commands;
    }

    public void show() {
        SysConsole.out.println();
        SysConsole.out.println("Action? ");
        for (int i = 0; i < commands.length; i++) {
            SysConsole.out.printf("%d) %s\n", i, commands[i].description());
        }

        SysConsole.out.flush();
    }

    public Command selection() {

        Command result;
        try {
            String line = SysConsole.in.readLine();
            int index = Integer.parseInt(line);
            result = commands[index];
        } catch (Exception ignore) {
            result = commands[0];
        }

        return result;
    }


}
