package com.workshare.msnos.usvc_client.ui;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.apache.poi.ss.formula.eval.NotImplementedException;

public class BufferingOutputConsole implements Console {

    private final ByteArrayOutputStream buffer;
    private final PrintStream output;
    
    public BufferingOutputConsole() {
        buffer = new ByteArrayOutputStream();
        output = new PrintStream(buffer);
    }
    
    @Override
    public BufferedReader in() {
        throw new NotImplementedException("No reading from an output buffer, sorry :)");
    }

    @Override
    public PrintStream out() {
        return output;
    }

    @Override
    public PrintStream err() {
        return output;
    }
    
    public String text() {
        return buffer.toString();
    }

    public byte[] bytes() {
        return buffer.toByteArray();
    }
}
