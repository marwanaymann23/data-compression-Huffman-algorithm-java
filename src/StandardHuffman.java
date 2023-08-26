import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;


class Node{
    int frequency;
    char data;
    
    Node right;
    Node left;
}


class NodeComparator implements Comparator<Node>{

    // compare while adding
    @Override
    public int compare(Node arg0, Node arg1) {
        return arg0.frequency - arg1.frequency;
    }    
}

public class StandardHuffman {
     // Creating a HashMap containing char
    // as a key and occurrences as  a value
    static HashMap<Character, Integer> charProbability = new HashMap<Character, Integer>();
    static float compressedSize = 0;
    static float uncompressedSize = 0;
    
    static void characterCount(String input)
    {
        // Converting given string to char array
        char[] strArray = input.toCharArray();
 
        // checking each char of strArray
        for (char c : strArray) {
            if (charProbability.containsKey(c)) { 
                // If char is present in charCountMap,
                // incrementing it's count by 1
                charProbability.put(c, charProbability.get(c) + 1);
            }
            else { 
                // If char is not present in charCountMap,
                // putting this char to charCountMap with 1 as it's value
                charProbability.put(c, 1);
            }
        } 
        // Printing the charCountMap
        for (Map.Entry<Character, Integer> entry : charProbability.entrySet()) {
            System.out.println(entry.getKey() + "\t" + entry.getValue());
        }
    }
    
    public static void writeDictionary(Node root, String code, File file, FileWriter writer){
    	if(root.left == null && root.right == null) {
    		try {
				writer.write(root.data + ": " + code + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	else {
    		writeDictionary(root.left, code + "0", file, writer);
    		writeDictionary(root.right, code + "1", file, writer);
    	}
    }

    static public void compress() throws IOException {
    	       
        System.out.println("Please enter the path of the file that contains the text you want to compress.");
        Scanner input = new Scanner(System.in);
        String path = input.nextLine();
        String data = "";
        input.close();
        
        File text = new File(path);
        Scanner read = new Scanner(text);
        while (read.hasNextLine()) {
          data += read.nextLine();
        }
        read.close();
        
        uncompressedSize = data.length() * 3;
        
        characterCount(data);
        
        // creates priority queue
        PriorityQueue<Node> q = new PriorityQueue<Node>(charProbability.size(), new NodeComparator());
        
        // loop to create a node for each key in the HashMap
        for(Map.Entry<Character, Integer> entry : charProbability.entrySet()) {
            
            Node newNode = new Node();
            
            newNode.right = null;
            newNode.left = null;
            
            newNode.data = (char)entry.getKey();
            newNode.frequency = (int) entry.getValue();
            
            q.add(newNode);
        }
        
        Node root = new Node();
        
        while(q.size() > 1) {
            Node firstExtract = q.poll();
            Node secondExtract = q.poll();
            
            Node newRoot = new Node();
            newRoot.frequency = firstExtract.frequency + secondExtract.frequency;
            newRoot.data = '_';
            newRoot.left = firstExtract;
            newRoot.right = secondExtract;
            root = newRoot;
            q.add(root);
        }
        
     // creating new file for the output
        File file = new File("CompressionOutput.txt");
        try {
            file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FileWriter writer = new FileWriter(file, true);
        
        writer.write("Dictionary:\n");
        writeDictionary(root, "", file, writer);
        writer.close();
        
        String compressedString = "";
        String currentLine = "";
        
        FileWriter compressedStringWriter = new FileWriter(file, true);
        for(int i = 0; i < data.length(); i++) {
        	Scanner readDictionary = new Scanner(file);
        	readDictionary.nextLine();
        	while(readDictionary.hasNextLine()) {
        		currentLine = readDictionary.nextLine();
        		if(data.charAt(i) == currentLine.charAt(0)) {
        			compressedString += currentLine.substring(3);
        			break;
        		}
        	}
        	readDictionary.close();
        }
        
        compressedSize = compressedString.length();
        compressedStringWriter.write("\n\nCompressed String:\n" + compressedString);
        compressedStringWriter.close();
        
        System.out.println("Uncompressed Size = " + uncompressedSize);
        System.out.println("Compressed Size = " + compressedSize);
        System.out.println("Compression Ratio = " + (uncompressedSize / compressedSize));
               
    }
    
    static public void decompress() throws IOException {
    	String compressedString = "";
    	String decompressedString = "";
    	String currentLine = "";
    	boolean found = false;
    	File file = new File("CompressionOutput.txt");
    	Scanner dictionaryReader = new Scanner(file); 	
    	HashMap<Character, String>dictionaryMap = new HashMap<Character, String>();
    	
    	dictionaryReader.nextLine();
    	while(dictionaryReader.hasNextLine()) {
    		currentLine = dictionaryReader.nextLine();

    		if (currentLine.equals("")) {
    			break;
    		}
    		
    		dictionaryMap.put(currentLine.charAt(0), currentLine.substring(3));
    	}

    	dictionaryReader.nextLine();
    	dictionaryReader.nextLine();
    	compressedString = dictionaryReader.nextLine();
    	dictionaryReader.close();
    	
    	for(int i = 0; i < compressedString.length(); i++) {
    		found = false;
    		for(int j = i; j < compressedString.length(); j++) {
    			if (found) {
    				break;
    			}
    			if (dictionaryMap.containsValue(compressedString.substring(i, j + 1))) {
    				for (Map.Entry<Character, String> entry : dictionaryMap.entrySet()) {
        				if(entry.getValue().equals(compressedString.substring(i, j + 1))) {
        					decompressedString += entry.getKey();
        					i = j;
        					found = true;
        					break;
        				}
        			}
    			}	
    		}
    	}
    	File output = new File("DecompressionOutput.txt");
    	output.createNewFile();
    	FileWriter outputWriter = new FileWriter(output, true);
    	outputWriter.write(decompressedString);
    	outputWriter.close();
    }

    public static void main(String[] args) throws IOException {
        
        System.out.println("Enter 1 for compression or 2 for decompression");
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        if (choice == 1) {
            compress();
        }
        else if (choice == 2) {
        	try {
        		decompress();
        	}
        	catch(IOException e) {
        		e.printStackTrace();
        	}
        }
        scanner.close();   
    }
}