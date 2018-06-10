package huffman;

/*
 * Huffman.java
 *
 * Created on May 21, 2007, 1:01 PM
 */
import java.util.*;
import java.io.*;
import java.nio.file.Files;//used to get byte arrays from files

/**
 *
 * @author pbladek
 */
public class Huffman {

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
    public Huffman() {
    }

    /**
     * main
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
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
        decode=true;
        if (args.length > 0) {
            if (args[0].substring(0, 2).toLowerCase().equals("-d")) {
                decode = true;
                if (args.length > 1) {
                    textFileName = args[1];
                }
            } else {
                textFileName = args[0];
            }
        }
        Huffman coder = new Huffman();
        textFileName = "alice.txt";
        if (!decode) {
            coder.encode(textFileName);
        } else {
            coder.decode(textFileName);
        }
    }

    /*
     * encode
     * @param fileName the file to encode
     */
    public void encode(String fileName) {
        // YOUR CODE HERE
        File input = new File(fileName);
        Scanner fileInputScanner;
        int[] list = new int[CHARMAX];
        long size = 0;
        try {
            fileInputScanner = new Scanner(input);
            while (fileInputScanner.hasNextLine()) {
                char[] oneLine = (fileInputScanner.nextLine() + "\n").toCharArray();
                for (char c : oneLine) {
                    list[c]++;
                }
                size += oneLine.length;
            }
            
            size *= 8; //convert to bits
            /*            
            //testing
            
            for (int i = 0; i < list.length; i++) {
                System.out.println(i + " " + (char) i + " " + 100.0 * list[i] / size);
                //System.out.println(i + " " + (char) i  + " " + list[i]);
            }
            */
            
            //saving what we just got into an array containing character and occurence
            charCountArray = new HuffmanChar[CHARMAX];
            for (int i = 0; i < CHARMAX; i++) {
                charCountArray[i] = new HuffmanChar((char) i, list[i]);
            }
            //sorting array
            Arrays.sort(charCountArray);
            
            /*
            //testing
            for (int i = 0; i < charCountArray.length; i++) {
                System.out.println(charCountArray[i].toString());
            }
            */
            /*
             By now we have an array of HuffmanChar's which contain a 
             character and its reoccurences
             */
            /*Change the charCountArray to size 8 and uncomment this below for smaller test*/
//            charCountArray[0] = new HuffmanChar('a', 2);
//            charCountArray[1] = new HuffmanChar('b', 3);
//            charCountArray[2] = new HuffmanChar('c', 7);
//            charCountArray[3] = new HuffmanChar('d', 10);
//            charCountArray[4] = new HuffmanChar('f', 15);
//            charCountArray[5] = new HuffmanChar('k', 21);
//            charCountArray[6] = new HuffmanChar('l', 50);
//            charCountArray[7] = new HuffmanChar('m', 70);
            theTree = new HuffmanTree(charCountArray);
            SortedMap<Character, String> keyMap = theTree.getCodeMap();
            //so now we have ahuffman tree and two maps with a path and a character
            
            //I believe this is now what we use to encode our file
            StringBuilder sb = new StringBuilder();
            fileInputScanner = new Scanner(input);
            long amountCompressed = 0;
            while (fileInputScanner.hasNextLine()) {
                char[] oneLine = (fileInputScanner.nextLine() + "\n").toCharArray();
                for (char c : oneLine) {
                    
                    sb.append(keyMap.get(c));
                    amountCompressed += keyMap.get(c).length();
                }
            }
            
            byteArray=new byte[sb.length()/8];
            for(int i=0;i<sb.length()/8;i++){
                String byteString=sb.substring(i*8,i*8+1);
                byteString+=sb.substring(i*8+1,i*8+2);
                byteString+=sb.substring(i*8+2,i*8+3);
                byteString+=sb.substring(i*8+3,i*8+4);
                byteString+=sb.substring(i*8+4,i*8+5);
                byteString+=sb.substring(i*8+5,i*8+6);
                byteString+=sb.substring(i*8+6,i*8+7);
                byteString+=sb.substring(i*8+7,i*8+8);
                byteArray[i]=(byte)Integer.parseInt(byteString,2);
            }
           
            System.out.println(sb);
            System.out.println(size + " - Amount of bites in original");
            System.out.println(amountCompressed + " - Amount of bites in compressed file");
            System.out.println(100.0 * amountCompressed / size + " percentage");
        } catch (IOException e) {
            System.out.println("Something about your file is borked");
        }
        writeEncodedFile(byteArray, fileName);
        writeKeyFile(fileName,list);
    }

    /*
     * decode
     * @param inFileName the file to decode
     */
    public void decode(String inFileName) {
        File input = new File(inFileName.split("\\.")[0]+".huf");
        //these bytes are our encoded file
        byte[] encodedText;
        //this is our character/count array in byte form
        byte[] array;
        int[] list;
        
        try {
            encodedText = Files.readAllBytes(input.toPath());
            input=new File(inFileName.split("\\.")[0]+".cod");
            array = Files.readAllBytes(input.toPath());
            list=new int[array.length/3];
            for(int i=0;i<list.length;i++){
                int currentChar=array[i*3];
                list[currentChar]=array[i*3+1] | array[i*3+2] << 8 ;//this should get us our count number 
            }
            //we should now have our original list
            
            //saving what we just got into an array containing character and occurence
            charCountArray = new HuffmanChar[CHARMAX];
            for (int i = 0; i < CHARMAX; i++) {
                charCountArray[i] = new HuffmanChar((char) i, list[i]);
            }
            
            theTree = new HuffmanTree(charCountArray);
            
        }
        catch(IOException e){
            encodedText = new byte[0];
            array = new byte[0];
            System.out.println("Something about your file is borked");
        }
        try {
            
            PrintWriter writer = new PrintWriter(inFileName.split("\\.")[0]+"x.txt");
            SortedMap<String, Character> CodeMap = theTree.getKeyMap();//not sure why the names are reversed
            String line="";
            for(byte c : encodedText){
                //this bit should convert the bytes into binary strings
                //except the binary Strings are too big
                //Note that there may be a method where we traverse the tree instead of
                //using it's maps
                String binaryString = String.format("%8s", Integer.toBinaryString(c & 0xFF)).replace(' ', '0');
                System.out.println(binaryString);
                String s=""+CodeMap.get(binaryString);
                //s.equals("\n") use this when you can actually get characters back
                if(Math.random()>.9){//I literally randomly decide when to print a line
                    writer.println(line+"\n");//print current line to file
                    
                    line="";
                }else{
                    line+=s;//this should get us our character
                }
            }
            writer.println(line);//makes sure end text doesn't get skipped over
        }catch(IOException e){
            System.out.println("minour teqhnical diffucultys writning you're file");
        }
        
    }

    /**
     * writeEncodedFile
     *
     * @param bytes bytes for file
     * @param fileName file input
     */
    public void writeEncodedFile(byte[] bytes, String fileName) {
        //I hope your file doesn't have multiple periods
        try (FileOutputStream output = new FileOutputStream(fileName.split("\\.")[0]+".huf")) {
            output.write(bytes);
            output.close();
        }catch(IOException e){
            
        }
    }

    /**
     * writeKeyFile
     *
     * @param fileName the name of the file to write to
     */
    public void writeKeyFile(String fileName, int[] list) {
           //I hope your file doesn't have multiple periods
        try (FileOutputStream output = new FileOutputStream(fileName.split("\\.")[0]+".cod")) {
            byte[] listArray=new byte[list.length*3];
            for(int i=0;i<list.length;i++){
                listArray[i*3]=(byte)i;//first byte is the character
                listArray[i*3+1]=(byte)list[i];//second byte is the first half of the int
                listArray[i*3+2]=(byte)(list[i] >> 8);//this shifts the integer 
                //over bitwise, deleting half the bits and accessing the other half
            }
            output.write(listArray);
            output.close();
        }catch(IOException e){
            
        }
    }

}
