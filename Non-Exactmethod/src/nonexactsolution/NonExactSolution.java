package nonexactsolution;

/**
 * @author Ramitaa Loganathan
 */

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;

public class NonExactSolution {

    private static Graph graph;
    private static Graph temporaryPotentialCliqueGraph = new Graph();
    private static ArrayList<Node> newTempList = new ArrayList<>();
    private static ArrayList edgesToCheck = new ArrayList<>();
    private static ArrayList newEdges = new ArrayList<>();
    static String node = "23";
    static String k = "6";
    static int counterClique = 0;
    static int graphcounter =0;
    /**
     * Main function
     * @param args 
     */
    public static void main(String[] args) {
        for(int i = 0; i < 50; i ++) {    
        //Using user's input 
        int array[] = getUserInput();

        graph = new Graph(array[0], array[1]);
        
        generateProblemInstance();
        
        long start = System.nanoTime();
        
        generateSolution(graph);
        
        long finish = System.nanoTime();
        String s = ""+((finish - start)/1000000.00);
       FileWriter writer;
            try{
                writer = new FileWriter("C:\\Users\\Kel Den\\Desktop\\Non-ExactSolution-master\\time.txt" , true);
                BufferedWriter buffer = new BufferedWriter(writer);  
                buffer.write(s);
                buffer.newLine();
            buffer.close();
            }
            catch(IOException ex){
                Logger.getLogger(NonExactSolution.class.getName()).log(Level.SEVERE, null , ex);
            }
            System.out.println(s); 
            System.out.println("Time elapsed: " + ((finish-start)/1000000.00));
            //SyFileWriter writer2;
        }
        
        FileWriter writer2;
        try{
            writer2 = new FileWriter("C:\\Users\\Kel Den\\Desktop\\Non-ExactSolution-master\\cliqueResult.txt" , true);
            BufferedWriter buffer = new BufferedWriter(writer2);
            String temp = String.valueOf(counterClique);
            buffer.write(temp);
            buffer.newLine();
        buffer.close();
            
        }
        catch(IOException ex){
            Logger.getLogger(NonExactSolution.class.getName()).log(Level.SEVERE, null , ex);
        }
    
       
                
    }

    
    //------------------------------------------------------------------------------------------------------
    // Main Functions
    //------------------------------------------------------------------------------------------------------
    
    /**
     *  Generate and print graph details based on user input
     */
    private static void generateProblemInstance() 
    {
        System.out.print("\n");
        printSpecialLine(50, "-");
        System.out.print("\n                   GRAPH DETAILS\n");
        printSpecialLine(50, "-");
                
        // Details of the graph is printed out
        System.out.println(graph.toString());
        
        printSpecialLine(50, "-");
        System.out.print("\n                    NODE DETAILS\n");
        printSpecialLine(50, "-");
        System.out.print("\n");
        
        graph.printNodeDetails();
    }

    /**
     * Carry out Step 1 - 4 as shown below to determine if a clique exists
     * @return true if clique exists and vice versa
     */
    public static int generateSolution(Graph g) 
    {
        System.out.print("\n");
        printSpecialLine(50, "-");
        System.out.print("\n                SOLUTION DETAILS\n");
        printSpecialLine(50, "-");
        
        graph = null;
        newTempList.clear();
        temporaryPotentialCliqueGraph = null;
        edgesToCheck.clear();
        newEdges.clear();
        
        graph = g;
        
        //System.out.println("NON-EXACT SOLUTION!!!");
        //generateProblemInstance();
        
         //Step 1: Check if graph has minimum edges to form k-clique
        //if (!minimumEdgesExists())
          //  return 0;
        
        // Step 2: Check if graph has minimum number of nodes with minimum number of edges
       // if(!minimumNodesWithMinimumEdgesExists())
       //     return 0;
        
        // Step 3: Check if the top 10 edges can form a clique by randomizing the node 100 times
        return cliqueExists();
               
    }
    
    /**
     * Check whether the minimum number of edges for a k-clique to form exists or not
     * @return true if minimum number of edges exist in the graph
     */
    private static boolean minimumEdgesExists()
    {
        int min = (graph.getCliqueSize() * (graph.getCliqueSize() - 1)) / 2;
        
        if (graph.getEdgesTable().length >= min)
        {
            System.out.format("\nStage 1: Pass. %d edge(s) are suffient to form a %d-clique.\n", 
                    graph.getEdgesTable().length, graph.getCliqueSize());
            return true;
        }
        
        else
        {
            System.out.format("\nStage 1: Fail. %d edge(s) are not suffient to form a %d-clique. At least %d edges are required.\n", 
                   graph.getEdgesTable().length, graph.getCliqueSize(), min);
            return false;
        }
    }
    
    /**
     * Check whether there are enough nodes with minimum edges to form clique exists.
     * @return true if there a minimum number of nodes with required edges and vice versa
     */
    private static boolean minimumNodesWithMinimumEdgesExists()
    {
        int count = 0;
        ArrayList<Node> nodeList = graph.getNodeList();
        
        count = nodeList.stream().filter((n) -> (n.getEdgeCount() >= (graph.getCliqueSize() - 1))).map((_item) -> 1).reduce(count, Integer::sum);
        
        if (count >= graph.getCliqueSize())
        {
            System.out.format("Stage 2: Pass. %d node(s) with at least %d edges are sufficient to form %d-clique.\n", 
                    count, graph.getCliqueSize() - 1, graph.getCliqueSize());
            return true;
        }
        
        else
        {
            System.out.format("Stage 2: Fail. %d node(s) with %d edges are not sufficient to form %d-clique. At least %d nodes are required.\n", 
                    count, graph.getCliqueSize() - 1, graph.getCliqueSize(), graph.getCliqueSize());
            return false;
        }
    }
    
    /**
     * Check if clique exists based on formed combinations
     * @return true if clique exists and vice versa
     */
    public static int cliqueExists()
    {
        ArrayList<Node> oriNodeList = graph.getNodeList();
        ArrayList<Node> tempNodeList = new ArrayList<>();
        ArrayList<int[]> solutionList = new ArrayList<>();
        int[] bestSolution = new int[graph.getCliqueSize()];
        boolean bestSolutionExists = false;
        int maxClique = graph.getCliqueSize() - 3;
        int minClique = graph.getCliqueSize() - 3;
        int[] candidateSolution = new int[graph.getCliqueSize()];
        int[] candidateSolutionCopy = new int[graph.getCliqueSize()];
        int[] nonSelectedNodes = new int[graph.getGraphSize() - graph.getCliqueSize()];
        
        Collections.sort(oriNodeList, new Comparator<Node>()
        {
            public int compare(Node n1, Node n2)
            {
                return Integer.valueOf(n2.getEdgeCount()).compareTo(n1.getEdgeCount());
            }
        });
                
        int count = 0;
        
        for (Node n: oriNodeList)
        {
            
            try
            {
                tempNodeList.add((Node) n.clone());
                newTempList.add((Node) n.clone());
            }

            catch (CloneNotSupportedException ex)
            {
                Logger.getLogger(NonExactSolution.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        int loop_counter = 0;
        boolean first = true;
        
        while (loop_counter < 1000)
        {
            if (first)
            {
                //System.out.println("CANDIDATE SOLUTION 1\n\n");
                for (int i = 0; i < graph.getCliqueSize(); i++)
                {
                    candidateSolution[i] = tempNodeList.get(i).getNodeID();
                    candidateSolutionCopy[i] = tempNodeList.get(i).getNodeID();
                    bestSolution[i] = tempNodeList.get(i).getNodeID();
                }
                
                
                for(int j = 0, i = graph.getCliqueSize(); i < graph.getGraphSize(); i++, j++)
                {
                    nonSelectedNodes[j] = tempNodeList.get(i).getNodeID();
                }

                minClique = 3;
                first = false;
            }
            
            else
            {   
                boolean repeat = false;
                
                do
                {               
                    repeat = false;
                    //System.out.println("CANDIDATE SOLUTION " + (loop_counter + 1 ) + "\n\n");

                    Random rand = new Random();      
                    int min = 0;
                    int max1 = candidateSolution.length - 1;
                    int max2 = nonSelectedNodes.length - 1;

                    int randomNum1 =  rand.nextInt((max1 - min) + 1) + min;
                    int randomNum2 =  rand.nextInt((max2 - min) + 1) + min;

                    //System.out.println("Swapping " + candidateSolution[randomNum1] + " with " + nonSelectedNodes[randomNum2]);

                    int temp = candidateSolution[randomNum1];
                    candidateSolution[randomNum1] = nonSelectedNodes[randomNum2];
                    nonSelectedNodes[randomNum2] = temp;
                    
                    ListIterator<int[]> 
                    iterator = solutionList.listIterator(); 

                    while (iterator.hasNext()) 
                    { 
                        if(iterator.next().equals(candidateSolution))
                        {
                            repeat = true;
                            break;
                        }
                    } 
            
                } while(repeat == true);               
                
                minClique = maxClique;
            }
            
            solutionList.add(candidateSolution.clone());
            Pair<Integer, int[]> p = greedySearch(candidateSolution, candidateSolutionCopy, minClique);
                
            if(p.getKey() == graph.getCliqueSize())
            {
                graph.printNodeDetails();
                bestSolution = p.getValue().clone();
                System.out.println("Stage 3: " + graph.getCliqueSize() + "-Clique found!");
                System.out.println(Arrays.toString(bestSolution));
                counterClique++;
                return graph.getCliqueSize();
            }
            
            else if(p.getKey() > maxClique)
            {
                maxClique = p.getKey();
                bestSolution = p.getValue().clone();
                bestSolutionExists = true;
                System.out.println(Arrays.toString(bestSolution));
            }
            
            loop_counter++;    
        }
        
        System.out.println("Stage 3: " + graph.getCliqueSize() + "-Clique not found!.");
        System.out.print("Best solution with " + bestSolution.length + "-clique found: ");
        System.out.println(Arrays.toString(bestSolution));
    
        if (bestSolutionExists)
            return maxClique;
        else
            return 0;
    }
    
    public static Pair<Integer, int[]> greedySearch(int[] candidateSolution, int[] candidateSolutionCopy, int minClique)
    {
        // Check if candidateSolution contains k-clique
        if (checkIfKCliqueExists(candidateSolution, candidateSolutionCopy, graph.getCliqueSize()))
            return new Pair(graph.getCliqueSize(), candidateSolution);
        
        // If candidateSolutin does not contain k-clique, it is checked for other bigger possible cliques
        // For exampls, if candidateSolution does not contain a 7-clique (which is the target),
        // and a 5-clique is already found in previous solution,
        // a 6-clique solution is attempted to be found
        
        // Create combinations based on candidateSolutions in the graph
        int search_times = minClique + 1;
        int possible = 0;
        
        while (search_times < graph.getCliqueSize())
        {
            /*for (int i = 0; i < candidateSolution.length; i++)
            {
                for (int j = 0; j < candidateSolution.length; j++)
                {
                    if (candidateSolution[i] != candidateSolution[j])
                        possible += getEdgeListSizeFromNode(candidateSolution[i]);
                }
            }*/
            
            if(possible >= (search_times * search_times - 1) / 2)
                continue;
            
            createCombination(candidateSolution, candidateSolution.length, search_times);

            Iterator iter = edgesToCheck.iterator();

            // Check every combination that has been created
            while (iter.hasNext())
            {
                String line = iter.next().toString();
                String tokenline = line.substring(1, line.length()-1);

                String[] tokens = tokenline.split(", ");
                int[] array = new int[tokens.length];
                int[] array2 = new int[tokens.length];

                for (int a = 0; a < tokens.length; a++)
                {
                    array[a] = Integer.parseInt(tokens[a]); 
                    array2[a] = Integer.parseInt(tokens[a]);
                }

                if (checkIfKCliqueExists(array, array2, search_times))
                    return new Pair(search_times, array);
            }
            
            search_times++;
        }
 
        return new Pair<>(-1, candidateSolution);
    }
    
    public static boolean checkIfKCliqueExists(int[] arr1, int[] arr2, int value)
    {
        // Check if candidateSolution contains k-clique
        int count = 0;
        
        //System.out.println(Arrays.toString(arr1));
        
        for (int k = 0; k < arr1.length; k++)
        {
            //System.out.println("Checking node " + arr1[k]);
            
            for (int l = 0; l < arr2.length; l++)
            {
                if (k != l)
                {
                    //System.out.println("Checking if node " + arr1[k] + " is in node " + arr2[l]);
                    if (valueExistsInArrayList(arr1[k], getEdgeListFromNode(arr2[l])))
                        count++;
                }
            }

        }

        if(count >= (value * (value - 1)))
        {
            //System.out.println("" + value + "- clique found: " + Arrays.toString(arr1));
            return true;
        }
        
        else
            return false;
    }
    
    /**
     * This functions help to deep copy an array
     * @param <T> ArrayList
     * @param matrix
     * @return 
     */
    <T> T[][] deepCopy(T[][] matrix) {
    return java.util.Arrays.stream(matrix).map(el -> el.clone()).toArray($ -> matrix.clone());
}
    
    /**
     * This function creates combinations based on given array
     * @param arr nodeList
     * @param data temporary nodeList to store all combinations
     * @param start start of nodeList
     * @param end end of nodeList
     * @param index current index
     * @param r size of combination
     */
    public static void combinationUtil(int arr[], int data[], int start, int end, int index, int r) 
    { 
        // Current combination is ready to be printed, print it 
        if (index == r) 
        { 
            int temp[] = new int[r];          
            System.arraycopy(data, 0, temp, 0, r);         
            edgesToCheck.add(Arrays.toString(temp));
            return; 
        } 
  
        for (int i=start; i<=end && end-i+1 >= r-index; i++) 
        { 
            data[index] = arr[i]; 
            combinationUtil(arr, data, i+1, end, index+1, r); 
        } 
    } 
    
    /**
     * This function creates a temporary array to store all combinations
     * @param arr nodeList
     * @param n length of nodeList
     * @param r size of combination
     */
    public static void createCombination(int arr[], int n, int r) 
    { 
        // A temporary array to store all combination one by one 
        int data[] = new int[r]; 
  
        edgesToCheck.clear();
        // Print all combination using temprary array 'data[]' 
        combinationUtil(arr, data, 0, n-1, 0, r); 
    }
    
    
    //------------------------------------------------------------------------------------------------------
    // Supporting Functions
    //------------------------------------------------------------------------------------------------------
    
    /**
     * This function is a method to get a valid user input from users
     * @return graphSize, cliqueSize in an array
     */
    private static int[] getUserInput() {
        
        String line;
        int graphSize, cliqueSize;
        
        Scanner sc = new Scanner(System.in);
        	
        printSpecialLine(50, "*");
        System.out.print("\n          K- CLIQUE PROBLEM SOLUTION \n");
        printSpecialLine(50, "*");

        do
        {
            System.out.print("\nEnter Graph Size: ");
            //line = sc.nextLine();
            line = node;
            
        } while(isNumberValid(line, 10, 100) == false);
        
        graphSize = Integer.parseInt(line);
        
        //do
        //{
            System.out.print("\nEnter Clique Size: ");
            //line = sc.nextLine();
            line = k;
            
        //} while(isNumberValid(line, graphSize / 2, graphSize) == false);
        
        cliqueSize = Integer.parseInt(line);
        
        int array[] = {graphSize, cliqueSize};
        
        return array;
    }
    
    /**
     * This function is a method to check whether the input is valid and display error message
     * @param number input by user
     * @param min boundary check by system to ensure sizes are not too small
     * @param max boundary check by system to ensure sizes are not too large
     * @return if number is valid or not
     */
    public static boolean isNumberValid(String number, int min, int max)
    {
        try 
        {
            int intValue = Integer.parseInt(number);
            
            if (intValue > max || intValue < min)
            {
                System.out.print("Input must be a digit between " + min + " to " + max + ". Please try again!\n");
                return false;
            }
            
            else
                return true;
            
        }
        catch(NumberFormatException e) 
        {
            System.out.print("Input must an integer. Please try again!\n");
            return false;
        }   
        
    }
    
    /**
     * This function is a method to check whether a value exist in a list.
     * Further checking will be using this potential clique graph for convenience sake.
     * @param value value to be checked
     * @param list list to be checked
     * @return if value exists in array
     */
    public static boolean valueExistsInArrayList(int value, ArrayList list)
    {
        return list.contains(value);
    }
    
    /**
     * This function returns the edge list based on a given node ID
     * @param value nodeID
     * @return edgeList of node with nodeID
     */
    public static ArrayList getEdgeListFromNode(int value)
    {
        for (Node n: newTempList)
        {
            if (n.getNodeID() == value)
                return n.getEdgeList();
        }
        
        return null;
    }
    
    /**
     * This function returns the edge list based on a given node ID
     * @param value nodeID
     * @return edgeList of node with nodeID
     */
    public static int getEdgeListSizeFromNode(int value)
    {
        for (Node n: newTempList)
        {
            if (n.getNodeID() == value)
                return n.getEdgeList().size();
        }
        
        return 0;
    }
        
    /**
     * This function is to print out a special line, mainly for the use of menus
     * @param no no of characters in the line
     * @param pattern the pattern of the line (whether its *, - etc.)
     */
    public static void printSpecialLine(int no, String pattern)
    {
        for (int i = 0; i < no; i++)
        {
            System.out.print(pattern);
        }
    }
      
}