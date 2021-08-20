/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.srttransl;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 *
 * @author Marcin
 */
public class MainClass {
    
    public static void main(String[] args) throws IOException, JSAPException{
        
        String srtfile, filepath, inputString, filepathPL, plString;
        
        String outputfile = "";
        
        String outputString = "";
        
        String tmpString = "";
        
        
        JSAP jsap = new JSAP();             /* Used for command line inputs */
       
        jsap = initializeJSAP(jsap);        /* Function initializing input flags */
        
        JSAPResult config = jsap.parse(args);  /* Encapsulates the results of JSAP's parse() methods. */
        
        
        if(!config.success()){
            System.out.println("\nThere was an error found within command line arguments, try again\n");        
            helpInfo();         
        } else {
            if(config.getBoolean("help")){
                    helpInfo();
                }
            else {  
                if (!config.getString("FILE_INPUT").equals("none") && config.getString("IN").equals("none")){

                    srtfile = config.getString("FILE_INPUT");

                    filepath = "./" + srtfile;

                    inputString = readFile(filepath, StandardCharsets.UTF_8);

                    parseDialogue(srtfile, outputfile, inputString, tmpString, outputString, config);

                } else if (!config.getString("FILE_INPUT").equals("none") && !config.getString("IN").equals("none")){

                    srtfile = config.getString("FILE_INPUT");

                    filepath = "./" + srtfile;

                    inputString = readFile(filepath, StandardCharsets.UTF_8);

                    filepathPL = "./" + config.getString("IN");

                    plString = readFile(filepathPL, StandardCharsets.UTF_8);

                    insertDifferentDialogue(srtfile, outputfile, inputString, tmpString, outputString, plString, config);

                } else {
                    System.out.println ("Wrong command line arguments");

                    helpInfo();
                }
        }
        }
        

        
    }
    
    /*****************************************************************************************************************************************************************/
    
    private static JSAP initializeJSAP(JSAP jsap) throws JSAPException{
        
        FlaggedOption opt1 = new FlaggedOption("OUT")         
                                .setStringParser(JSAP.STRING_PARSER)
                                .setDefault("none") 
                                .setRequired(true) 
                                .setShortFlag('o') 
                                .setLongFlag("out");
        
        jsap.registerParameter(opt1);
        
        FlaggedOption opt2 = new FlaggedOption("IN")             
                                .setStringParser(JSAP.STRING_PARSER)
                                .setDefault("none") 
                                .setRequired(true) 
                                .setShortFlag('i') 
                                .setLongFlag("in");
        
        jsap.registerParameter(opt2);
        
        FlaggedOption opt3 = new FlaggedOption("FILE_INPUT")             
                                .setStringParser(JSAP.STRING_PARSER)
                                .setDefault("none") 
                                .setRequired(true) 
                                .setShortFlag('f') 
                                .setLongFlag("file");
        
        jsap.registerParameter(opt3);
        
        Switch sw1 = new Switch("help")                     /* help information flag */
                        .setShortFlag('h')
                        .setLongFlag("help");

        jsap.registerParameter(sw1);
        
        
        return jsap;
    }
    
    /*****************************************************************************************************************************************************************/
    
    public static void parseDialogue(String srtfile, String outputfile, String inputString, String tmpstring, String outputString, JSAPResult config){
        
        /* Create an intermediate file */
        
        /*String of = srtfile.substring(0,srtfile.indexOf(".")+".".length());
        of = of.substring(0, of.length() - 1);
        outputfile = "./" + "OUTPUT_" + of + ".txt";
        */
        if (config.getString("OUT").equals("none")){
            outputfile = "./" + "OUTPUT_PL_" + srtfile + ".srt";
        } else {
            outputfile = config.getString("OUT");
        }
        
        
        try (Scanner sc = new Scanner(inputString))
        {
            
            while (sc.hasNext()){
                sc.nextLine();
                sc.nextLine();
                
                while (sc.hasNext()){
                    
                    tmpstring = sc.nextLine();
                    
                    if (tmpstring.length()>0){
                    
                        outputString += (tmpstring+"\n");
                    
                    } else {
                        
                        break;
                        
                    }
                    
                }
                
            }
            
        }
        
        
        //System.out.println(outputString);
        
        try {
            File myObj = new File(outputfile);                              /* Writing the content of outputString into the output file */
            if (myObj.createNewFile()) {
              System.out.println("File created: " + myObj.getName());
         } else {
                System.out.println("File already exists.");
         }
         } catch (IOException e) {
             System.out.println("An error occurred.");
                //e.printStackTrace();
         }
        
            try {
            try (FileWriter myWriter = new FileWriter(outputfile)) {
                myWriter.write(outputString);
            }
                System.out.println("Successfully wrote to the file " + outputfile);
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                   // e.printStackTrace();
                }
        
    }
    
    
    /*****************************************************************************************************************************************************************/
    
    public static void insertDifferentDialogue(String srtfile, String outputfile, String inputString, String tmpstring, String outputString, String plString, JSAPResult config) throws UnsupportedEncodingException, FileNotFoundException{
        
        /* Replace the dialog text with a new processed translation */
        
        /*String of = srtfile.substring(0,srtfile.indexOf(".")+".".length());
        of = of.substring(0, of.length() - 1);
        outputfile = "./" + "OUTPUT_PL_" + of + ".srt";*/
        
        
        if (config.getString("OUT").equals("none")){
            outputfile = "./" + "OUTPUT_PL_" + srtfile + ".srt";
        } else {
            outputfile = config.getString("OUT");
        }
        
        Scanner sc2 = new Scanner(plString);
        
        
        try (Scanner sc = new Scanner(inputString))
        {
            
            while (sc.hasNext()){
                outputString += sc.nextLine() + "\n";
                outputString += sc.nextLine() + "\n";
                
                while (sc.hasNext()){
                    
                    String tmp2 = sc.nextLine();
                                      
                    if (tmp2.length()>0){
                        
                        tmpstring = sc2.nextLine();
                        
                        //System.out.println(tmpstring);

                        outputString += (tmpstring+"\n");
                    
                    } else {
                        
                        outputString+="\n";
                        break;
                        
                    }
                    
                }
                
            }
        }
        
        sc2.close();

        
            try {
                Writer out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(outputfile), StandardCharsets.UTF_8));
                try {
                    out.write(outputString);
                } finally {
                    System.out.println("Successfully wrote to the file " + outputfile);
                    out.close();
                }
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                   // e.printStackTrace();
                }
        
        
    }
    
    /*****************************************************************************************************************************************************************/
    
    public static String readFile(String path, Charset encoding)           /*Simple filereader with charset encoding, need UTF-8*/
    throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
    
    /*****************************************************************************************************************************************************************/
    
    private static void helpInfo(){
        
        System.out.println();
        System.out.println("SRT Translation Helper:");
        System.out.println("-f     input .srt file");
        System.out.println("-i     different language input file");
        System.out.println("-o     output file name");
        System.out.println("Example: java -jar srttransl.jar -f test.srt -o OUT_test.srt ");
        System.out.println("Example: java -jar srttransl.jar -f test.srt -i PLtest.txt -o OUT_PL_test.srt ");
        System.out.println();

    }
    
}
