package main;

import java.io.*;

public class ScriptExecutor {


    public static String execScript(String script)  {
        FileWriter fw = null;
        try {
            fw = new FileWriter("script.sh");
        } catch (IOException e) {
            e.printStackTrace();
        }

        PrintWriter pw = new PrintWriter(fw);

        pw.println("#!/bin/bash");
        pw.println(script);
        pw.close();

        Process proc = null;

        try {
            proc = Runtime.getRuntime().exec("script.sh");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));
        StringBuilder result = new StringBuilder();
        String line;
        try {
            while( (line = stdInput.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}
