import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

//Word and corresponding frequency assigning using constructors later used in the heap operations
final class WordFreq implements Comparable<WordFreq> {
    String word;
    int freq;

    public WordFreq(final String w, final int c) {
        word = w;
        freq = c;
    }
    public WordFreq(WordFreq wordfreq){
    	word = wordfreq.word;
    	freq = wordfreq.freq;
    }

    @Override
    public int compareTo(final WordFreq other) {
        return Integer.compare(this.freq, other.freq);
    }
}
/* External Sorting Technique is used as the file in being read in this case is really huge */

public class ExternalMemorySort {	
		static int N = 10;    /* size of our log file in the disk. A very minimal size is used for testing purposes. 
								          This could be assigned a huge number to handle large files. */
		static int M = 2;     /* size of temporary buffer being used. A very minimal size is used for testing purposes.
								          This could be assigned a huge number but a number lesser than N */
		
		public static void externalMemorySort(String filename){
			int size = 0;
			if(M<N) size = M;
			else size = N;
			
			String tfile = "tempFile";
			
			String[] buffer = new String[size];
			
			 try
			  {
			   FileReader fr = new FileReader(filename);
			   BufferedReader br = new BufferedReader(fr);
			   int slices = (int) Math.ceil((double) N/M);
			   
			   int i, j;
			   i = j = 0;
			   // Iterate through the elements in the file
			   for (i = 0; i < slices; i++)
			   {
			    // Read M-element chunk at a time from the file
			    for (j = 0; j < size; j++)
			    {
			     String textLine = br.readLine();
			     if (textLine != null){
			     String parts[] = textLine.split("\t");   
			      buffer[j] = parts[1]; //Second column in the file consists of the IP address values
			     }
			     else
			    	 break;
			    }
			    
			    // Sort M elements - individual files are being sorted
			    Arrays.sort(buffer);
			
			    // Write the sorted IPs from the buffer to a temporary file
			    FileWriter fw = new FileWriter(tfile + i + ".txt");
			    PrintWriter pw = new PrintWriter(fw);
			    for (int k = 0; k < j; k++)
			     pw.println(buffer[k]);
			    
			    pw.close();
			    fw.close();
			   }
			   
			   br.close();
			   fr.close();
			   
			   // Now open each temporary file and merge them, then write back to disk
			   String[] topNums = new String[slices];//first number of each temporary file
			   BufferedReader[] brs = new BufferedReader[slices]; //file pointers array to every temporary file
			   
			   for (i = 0; i < slices; i++)
			   {
			    brs[i] = new BufferedReader(new FileReader(tfile + i + ".txt"));
			    String t = brs[i].readLine();
			    if (t != null)
			     topNums[i] = t;
			   }
			   
			   
			   FileWriter fw = new FileWriter("E:\\test\\externalSorted.txt");//Creating an output file to write the sorted IP addresses
			   PrintWriter pw = new PrintWriter(fw);
			   
			   for (i = 0; i < N; i++)
			   {
			    String min = topNums[0];
			    int minFile = 0;
			    
			    for (j = 0; j < slices; j++)         
			    {
			     if ((min.compareTo(topNums[j]) > 0))
			     {
			      min = topNums[j];
			      minFile = j;
			     }
			    }
			    
			    pw.println(min);
			    String t = brs[minFile].readLine();
			    if (t != null)
			     topNums[minFile] = t;
			    else
			     topNums[minFile] = String.valueOf(Integer.MAX_VALUE);
			    
			   }
			   for (i = 0; i < slices; i++)
			    brs[i].close();
			   
			   pw.close();
			   fw.close();
			  }
			  catch (FileNotFoundException e)
			  {
			   e.printStackTrace();
			  } catch (IOException e) {
			   e.printStackTrace();
			  }  
			 }
		
		// A min heap of size 10 is being maintained in order to obtain the top 10 IP addresses and the corresponding count
		public static void Heapify() throws IOException{

			 int heapTop = 3;
			 final PriorityQueue<WordFreq> topKHeap = new PriorityQueue<WordFreq>(heapTop);
			 BufferedReader in = new BufferedReader(new FileReader("E:\\test\\externalSorted.txt"));
		     String line = "";
		     line = in.readLine();		/*The externalSorted file would contain the repeating IP addresses 
		     								            one after the other. Same IP address is read until it is repeated and thus 
		     								            the corresponding count is obtained and that is populated to a heap */
		        while ((line != null)) {
		        	String IP = line;
		        	int counter = 1;
		        	while((line = in.readLine()) != null && line.compareTo(IP)== 0)
		        	{
		        		counter++;
		        	}
		        	
					if (topKHeap.size() < heapTop) {
						topKHeap.add(new WordFreq(IP, counter));
					} else if (counter > topKHeap.peek().freq) {
		            topKHeap.remove();
		            topKHeap.add(new WordFreq(IP, counter));
		        }
			}
				
				
			final String[] topK = new String[heapTop];
		    int i = 0;
		    while (topKHeap.size() > 0) {
		        WordFreq result = new WordFreq(topKHeap.remove());
		        System.out.println(result.word + "->" + result.freq);
		    }
		}
		  
		
		public static void main(String[] args) throws IOException {
			
			//Read the filename as a command line argument 
			if (0 < args.length) {
				externalMemorySort(args[0]); 
				Heapify();
				
			} else {
			   System.err.println("Invalid arguments count:" + args.length);
			   
			}
			 
		}

}
