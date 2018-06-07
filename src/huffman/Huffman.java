package huffman;

/*
 * Huffman.java
 *
 * Created on May 21, 2007, 1:01 PM
 */

import java.util.*;
import java.lang.*;
import java.io.*;
/**
 *
 * @author pbladek
 */
public class Huffman
{  
    public static final int CHARMAX = 128;
    public static final byte CHARBITS = 7;
    public static final short CHARBITMAX = 128;
    private HuffmanTree<Character> theTree;
    private byte[] byteArray;
    private SortedMap<Character, String> keyMap;
    private SortedMap<String, Character> codeMap;
    HuffmanChar[] charCountArray;
    byte[] saveDataArray;
    
    /**
     * Creates a new instance of Main
     */
    public Huffman() {}
    
    /**
     * main
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
//----------------------------------------------------
// used for debugging encoding
//----------------------------------------------------
//        args = new String[1];
//        args[0] = "alice.txt";
//----------------------------------------------------
// used for debugging encoding
//----------------------------------------------------
//        args = new String[2];
//        args[0] = "-d";
//        args[1] = "alice.txt";  
//----------------------------------------------------        
        boolean decode = false;
        String textFileName = "";
        if(args.length > 0)
        {
            if(args[0].substring(0,2).toLowerCase().equals("-d"))
            {
                decode = true;
                if(args.length > 1)
                    textFileName = args[1];
            }
            else
                textFileName = args[0];
        }
        Huffman coder = new Huffman();
        textFileName="alice.txt";
        if(!decode)
            coder.encode(textFileName);
        else
            coder.decode(textFileName);
    }

    /*
     * encode
     * @param fileName the file to encode
     */
    public void encode(String fileName)
    {
      // YOUR CODE HERE
        File input = new File(fileName);
        Scanner fileInputScanner;
        int[] list=new int[128];
        int size=0;
        try
        {
           fileInputScanner = new Scanner(input);
           while(fileInputScanner.hasNextLine())
           {
               char[] oneLine = (fileInputScanner.nextLine()+"\n").toCharArray();
               
               for(char c : oneLine){
                   list[c]++;
                   size++;
               }
           }
           for(int i=0;i<list.length;i++){
               System.out.println(i + " " + 100.0 * list[i] / size);
           }
       }catch(IOException e){
           System.out.println("Something about your file is borked");
       }


        writeEncodedFile(byteArray, fileName);
        writeKeyFile(fileName);
    } 
 
    /*
     * decode
     * @param inFileName the file to decode
     */   
    public void decode(String inFileName)
    { 
     
    }
      
    /**
     * writeEncodedFile
     * @param bytes bytes for file
     * @param fileName file input
     */ 
    public void writeEncodedFile(byte[] bytes, String fileName)
    {
      

    }
   
    /**
     * writeKeyFile
     * @param fileName the name of the file to write to
     */
    public void writeKeyFile(String fileName)
    {
  
    }
 
}

