// ===============================================================================
// Authors: AFRL/RQQA
// Organization: Air Force Research Laboratory, Aerospace Systems Directorate, Power and Control Division
// 
// Copyright (c) 2017 Government of the United State of America, as represented by
// the Secretary of the Air Force.  No copyright is claimed in the United States under
// Title 17, U.S. Code.  All Other Rights Reserved.
// ===============================================================================


package avtas.lmcp.lmcpgen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;


public class MethodBuilder {
    
    public static String ws = "    ";
    
    public static void buildClass(File templateFile, File methodClass) {

        try {

            BufferedReader reader = new BufferedReader( new FileReader(templateFile) );

            String outString = "";
            ArrayList<String> list = new ArrayList<String> ();

            outString += "/* This file was autogenerated from LmcpGen */\n\n";
            outString += "import avtas.lmcp.MDMInfo;\n";
            outString += "import avtas.lmcp.Message;\n";
            outString += "import avtas.lmcp.Field;\n";
            outString += "java.io.File;\n\n";
            outString += "class " + methodClass.getName().substring(0, methodClass.getName().indexOf(".")) + "{\n";

            while(reader.ready()) {
                String line = reader.readLine();
                
                if(line.startsWith("#")) {
                    continue;
                }
                
                String[] splits = line.split("\\s+");
                
                if (splits.length >= 3 ) { 
                    File f = new File(templateFile.getParent(), splits[1]);
                    getMethodNames(splits[2], list);
                    String fileStr = LmcpGen.readFile( f.toURI().toURL() );
                    getMethodNames(fileStr, list);
                }
            }

            for( String s : list) {
                outString += ws + "public static String " + s + "(MDMInfo[] infos, MDMInfo info, final File outfile, " +
                        "Struct st, String ws) throws Exception {\n";
                outString += ws + ws + "return \"\";\n";
                outString += ws + "}\n\n";
            }

            outString += "}\n";

            LmcpGen.writeFile(methodClass, outString);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getMethodNames(String inputString, List<String> list) {
        String[] strs = inputString.split("-<");
        for(int i=1; i<strs.length; i++) {
            String str = strs[i].substring(0, strs[i].indexOf(">-"));
            if (!list.contains(str))
                list.add(str);
        }
    }

    public static void main(String[] args) {
        JFileChooser chooser = new JFileChooser();
        File openFile = null;
        File saveFile = null;
        chooser.setDialogTitle("Open a template listing file");
        int ans = chooser.showOpenDialog(null);
        if (ans == JFileChooser.APPROVE_OPTION) {
            openFile = chooser.getSelectedFile();
        }
        else {
            return;
        }
        chooser.setDialogTitle("Save a methods file");
        ans = chooser.showSaveDialog(null);
        if (ans == JFileChooser.APPROVE_OPTION) {
            saveFile = chooser.getSelectedFile();
        }
        else {
            return;
        }

        buildClass(openFile, saveFile);
    }

}

