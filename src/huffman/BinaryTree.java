/*
 * HuffmanTree.java
 *
 * Created on May 21, 2007, 2:16 PM
 */

package huffman;
import java.util.*;
/**
 * binary tree for Huffman coding
 * @author pbladek
 */
public class HuffmanTree<T extends Comparable<? super T>>
        extends BinaryTree<HuffmanData<T>>
{
    private final T MARKER = null;
    SortedMap<T, String> codeMap;
    SortedMap<String, T> keyMap;
    
    private int leafCount = 0;
    
    /**
     * Creates a new instance of HuffmanTree
     */
    public HuffmanTree() 
    {
        super();
    }
   
    /**
     * Creates a new instance of HuffmanTree
     * from an array of Huffman Data
     * @param dataArray n array of Huffman Data
     */
    public HuffmanTree(HuffmanData<T>[] dataArray) 
    {
        // your code here
        //testing
        ///*
//        for(int i = 0; i < dataArray.length; i++) {
//            System.out.println(dataArray[i].getData() + " occured " +
//                    dataArray[i].getOccurances() + " times");
//        }
        //*/
        BinaryNodeInterface<HuffmanData<T>>[] nodes = new BinaryNode[dataArray.length];
        for (int i = 0; i < dataArray.length; i++) {
            nodes[i] = new BinaryNode(dataArray[i]);
        }
        
        //constructing tree -- not finished
        //*UPDATE* tree finished
        for(int i = 0; i < nodes.length - 1; i ++) {
            BinaryNode temp = new BinaryNode(new HuffmanData(null, 
                    nodes[i].getData().getOccurances() + 
                            nodes[i + 1].getData().getOccurances()));
            //takes off two lowest and adds to temp
            temp.setLeftChild(nodes[i]);
            temp.setRightChild(nodes[i + 1]);
            leafCount += 2;
            //just get rid of the the i'th node just for organizational purposes
            nodes[i] = null;
            nodes[i + 1] = null;
            //holder will compare frequencies of temp and node after i + 1
            int holder = i + 2;
            //will move nodes the appropriate amount
            while(holder < nodes.length &&((HuffmanData)temp.getData()).getOccurances() 
                    > nodes[holder].getData().getOccurances()) {
                nodes[holder - 1] = nodes[holder];
                holder++;
            }
            //place the new subtree with the [i] and [i + 1] nodes into appropriate place
            nodes[holder - 1] = temp;
            //at the end it will be a 128 array and at the last index contatin the root
        }
        /*So now the array contains one node, at the last index, 
        holding everything else*/
        this.setRootNode(nodes[nodes.length - 1]);
        //hell yeah! got the HuffmanTree working, now just character representation
        //now the HuffmanTree contains a root node which contatins everything else.
        
        keyMap = new TreeMap<String, T>();
        codeMap = new TreeMap<T,String>();
        setMaps(getRootNode(), "");
    }
    
     /** 
      * set up the 2 maps
      * @param node
      * @param codeString
      */
     private void setMaps(BinaryNodeInterface<HuffmanData<T>> node,
             String codeString)
     { 
         if(node.hasLeftChild()){
            setMaps(node.getLeftChild(), codeString + "0");
         }
         if(node.hasRightChild()) {
            setMaps(node.getRightChild(), codeString + "1");
         }
         if(node.isLeaf()) {
            keyMap.put(codeString, node.getData().getData());
            codeMap.put(node.getData().getData(), codeString);
            //System.out.println(node.getData().getData() + " is " + codeString);
            codeString = codeString.substring(0, codeString.length() - 1);
         }
     }
    /** 
     * creates two new HuffmanTrees and adds them to the root of this tree
     * @param left 
     * @param rightt
     */
    private void add(BinaryNode<HuffmanData<T>> left,
            BinaryNode<HuffmanData<T>> right)
    {
         HuffmanTree<T> leftTree = new HuffmanTree<T>();
         leftTree.setRootNode(left); 
         HuffmanTree<T> rightTree = new HuffmanTree<T>();
         rightTree.setRootNode(right);
         setTree(new HuffmanData<T>
                 (MARKER, left.getData().getOccurances()
                 + right.getData().getOccurances()), leftTree, rightTree);
    }
    
    /** 
     * adds 2 new elements to this tree<br>
     *  smaller on the left
     * @param element1
     * @param element2
     */
    private void firstAdd(HuffmanData<T> element1, HuffmanData<T> element2)
    {

    }
    
    /** 
     * add a single element to the tree
     *  smaller on the left
     * @param element1
     */
     private void add(HuffmanData<T> element1)
     {
         
     }
    
    /*
     * accessor for codeMap
     * @ return codeMap
     */
    public SortedMap<T, String> getCodeMap()
    {
        return codeMap;
    }
    
    /*
     * accessor for keyMap
     * @ return keyMap
     */
    public SortedMap<String, T> getKeyMap()
    {
        return keyMap;
    }

}
