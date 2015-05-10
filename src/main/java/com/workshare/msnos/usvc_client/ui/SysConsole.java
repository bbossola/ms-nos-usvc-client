package com.workshare.msnos.usvc_client.ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * A console that works, as on my jdk1.6 Runtime.getConsole() 
 * returns null and I'm now really pissed
 */
public class SysConsole implements Console  {

    private static Console instance = new SysConsole();

    public static BufferedReader in;
    public static PrintStream out;
	private static PrintStream err;
	
	static {
		SysConsole.in = new BufferedReader(new InputStreamReader(System.in));
		SysConsole.out = System.out;
		SysConsole.err = System.err;
	}
	
	private SysConsole() {}

    public static Console get() {
        return instance;
    }

    @Override
    public BufferedReader in() {
        return in;
    }

    @Override
    public PrintStream out() {
        return out;
    }

    @Override
    public PrintStream err() {
        return err;
    }
}
