package huffman;

/*
 * Huffman.java
 *
 * Created on May 21, 2007, 1:01 PM
 */
import java.util.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    int byteLength;

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
            int amountCompressed = 0;
            while (fileInputScanner.hasNextLine()) {
                char[] oneLine = (fileInputScanner.nextLine() + "\n").toCharArray();
                for (char c : oneLine) {
                    sb.append(keyMap.get(c));
                    amountCompressed += keyMap.get(c).length();
                }
            }
            byteArray = new byte[amountCompressed / 8 + 1];
            int track = 0;
            
            //created the byte array with numbers which will represent the path
            while(track < byteArray.length) {
                byteArray[track] = Byte.parseByte(sb.substring(track * 
                        CHARBITS, (track + 1) * CHARBITS), 2);
                //System.out.println(byteArray[track]);
                track++;
            }
            //System.out.println(sb);
            System.out.println(size + " - Amount of bites in original");
            System.out.println(amountCompressed + " - Amount of bites in compressed file");
            System.out.println(100.0 * amountCompressed / size + " percentage");
        } catch (IOException e) {
            System.out.println("Something about your file is borked");
        }
        writeEncodedFile(byteArray, fileName);
        writeKeyFile(fileName,list);
        System.out.println(Arrays.toString(list));
        decode("alice.txt");
    }

    /*
     * decode
     * @param inFileName the file to decode
     */
    public void decode(String inFileName) {
        FileInputStream encoded = null;
        int[] list = new int[CHARMAX];
        try {
            encoded = new FileInputStream(new File(inFileName.split("\\.")[0]+".cod"));
            int place = 0;
            int occurances = 0;
            while(encoded.available() != 0) {
                System.out.print(place = encoded.read());
                byte one = (byte)encoded.read();
                byte two = (byte)encoded.read();
                System.out.print(" " + one);
                System.out.print(" " + two);
                System.out.print(" or " + (occurances = (Integer.parseInt(properBinary(two) + properBinary(one), 2))) + "\n");
                list[place] = occurances;
            }
            //created 
            charCountArray = new HuffmanChar[CHARMAX];
            for (int i = 0; i < CHARMAX; i++) {
                charCountArray[i] = new HuffmanChar((char) i, list[i]);
            }
           theTree = new HuffmanTree();
           //now we have  ahuffman tree and need to find characters based
           //on the path of the 0s and 1s from the encoded file
           encoded = new FileInputStream(new File(inFileName.split("\\.")[0]+".huf"));
           byte[] encodedString = new byte[byteLength];
           int track = 0;
           while(encoded.available() != 0) {
               int temp = encoded.read();
               encodedString[track + 3] = (byte)temp;
               encodedString[track + 2] = (byte)(temp = temp >> 8);
               encodedString[track + 1] = (byte)(temp = temp >> 8);
               encodedString[track] = (byte)(temp = temp >> 8);
            }
           int atByte = 0;
           int atPos = 0;
           while(atByte < byteLength) {
               
           }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Huffman.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Huffman.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Adds the two bytes to form the amount
     * @param num   int to get path
     * @return  path
     */
    private static String properBinary(int num) {
        String temp = "";
        temp = Integer.toBinaryString(num);
        if(num < 0) {
            temp = temp.substring(24);
        }
        if(temp.length() < 8) {
            int initial = temp.length();
            for(int i = 0; i < 8 - initial; i++) {
                temp = "0" + temp;
            }
        }
        return temp;
    }
    
    /**
     * writeEncodedFile
     *
     * @param bytes bytes for file
     * @param fileName file input
     */
    public void writeEncodedFile(byte[] bytes, String fileName) {
        //I hope your file doesn't have multiple periods
        byteLength = bytes.length;
        System.out.println(bytes.length + " is length");
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
            byte[] listArray=new byte[list.length * 3];
            for(int i=0;i<list.length;i++){
                listArray[i*3]=(byte)i;//first byte is the character
                listArray[i*3+1]=(byte)list[i];//second byte is the first half of the int
                listArray[i*3+2]=(byte)(list[i] >> 8);//this shifts the integer 
                //over bitwise, deleting half the bits and accessing the other half
                System.out.println(i + " occured " + list[i] + " times " + (byte)list[i] + " " + (byte)(list[i] >> 8));
            }
            output.write(listArray);
            output.close();
        }catch(IOException e){
            
        }
    }
}
