package com.workshare.msnos.usvc_client.ui;

import java.io.BufferedReader;
import java.io.PrintStream;

public interface Console {

    public BufferedReader in()
    ;

    public PrintStream out()
    ;

    public PrintStream err()
    ;

}