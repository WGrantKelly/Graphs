package P4;
import java.util.*;
import java.io.*;
import java.lang.Integer;
public class NetworkAnalysis{

	public static int numVertices = 0;
    public static int numEdges = 0;
    public static int numCopperVertices = 0;
    public static int numCopperEdges = 0;
    public static EdgeWeightedGraph graph;
    public static String filename;


	public static void main(String[] args){

		filename=args[0];
		boolean notDone = true;
		System.out.println("Welcome to 'My Networking Client!'\n\n");

		Scanner keyboard = new Scanner(System.in);
		/*System.out.print("Enter the filename to pull the graph from: ");
		filename = keyboard.nextLine();*/
        scan();
        graph = new EdgeWeightedGraph(filename, numVertices, numEdges);
        while(notDone){
        	System.out.println("\n\nHere are your options: \n\n");
	        System.out.println("1. Lowest Latency Path");
	        System.out.println("2. Copper Only Connected");
	        System.out.println("3. Maximum Amount of Data");
	        System.out.println("4. lowest average latency spanning tree");
	        System.out.println("5. Two-Vertex Failure Test");
	        System.out.println("6. Quit");
	        System.out.println(" \n\n");

	        System.out.print("Enter your selection(1..2..3..etc): ");
			int choice = keyboard.nextInt();

			switch(choice){
				case 1: lowLatPath();
					break;
				case 2: coppConn();
					break;
				case 3: maxData();
					break;
				case 4: lowAvgSpan();
					break;
				case 5: 
					if(twoFailureTest())
						System.out.println("\n\nThis graph will not fail after removing any two vertices\n\n");
					else
						System.out.println("\n\nThis graph will fail if the wrong two vertices fail\n\n");
					break;
				case 6: notDone=false;
					break;

			}
        }
        


	}

	public static void scan(){
        String line = null;
        
        try {

            FileReader fileReader = new FileReader(filename);

            BufferedReader bufferedReader = new BufferedReader(fileReader);
            numVertices = Integer.parseInt(bufferedReader.readLine());

            while((line = bufferedReader.readLine()) != null) {
                String[] ln = line.split(" ");
                assert(ln.length==5);
               	numEdges++;
            }   
            bufferedReader.close();         
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + filename + "'");                
        }
        catch(IOException ex) {
            System.out.println("Error reading file '" + filename + "'");                  
        }
	}
	public static void copperScan(){
        String line = null;
        ArrayList<Integer> vertices = new ArrayList<Integer>();
        
        try {

            FileReader fileReader = new FileReader(filename);

            BufferedReader bufferedReader = new BufferedReader(fileReader);
            bufferedReader.readLine();
            while((line = bufferedReader.readLine()) != null) {
                //PARSE EDGE LINE
                String[] ln = line.split(" ");
                assert(ln.length==5);
                if(ln[2].equals("copper")){
                	if(!vertices.contains(Integer.parseInt(ln[0]))){
	               		vertices.add(Integer.parseInt(ln[0]));
               		}
	               	if(!vertices.contains(Integer.parseInt(ln[1]))){
	               		vertices.add(Integer.parseInt(ln[1]));
	               	}
               		numCopperEdges++;
                }

               	
            }   
            numCopperVertices=vertices.size();
            bufferedReader.close();         
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + filename + "'");                
        }
        catch(IOException ex) {
            System.out.println("Error reading file '" + filename + "'");                  
        }
	}
	public static void lowLatPath(){

		int lowestFoundBandwidth = 0;

		Scanner keyboard = new Scanner(System.in);
		System.out.print("Enter the first vertex: ");
		int v1 = keyboard.nextInt();
		System.out.print("\nEnter the second vertex: ");
		int v2 = keyboard.nextInt();


		System.out.print("\n\nThe following path will give you the lowest latency: \n\n");

		DijkstraSP shortestPath = new DijkstraSP(graph, v1);
		Iterable<Edge> path;

		if(shortestPath.hasPathTo(v2)){
			path = shortestPath.pathTo(v2);
			for(Edge e: path){
				if(lowestFoundBandwidth==0)
					lowestFoundBandwidth = e.getBandwidth();
				else{
					if(e.getBandwidth()<lowestFoundBandwidth)
						lowestFoundBandwidth=e.getBandwidth();
				}
				System.out.println(e.toString());
			}
			System.out.println("Bottleneck Bandwidth for this path is "+lowestFoundBandwidth+" megabits per second.");
		}
		else{
			shortestPath = new DijkstraSP(graph, v2);
			if(shortestPath.hasPathTo(v1)){
				path = shortestPath.pathTo(v1);
				for(Edge e: path){
					if(lowestFoundBandwidth==0)
						lowestFoundBandwidth = e.getBandwidth();
					else{
						if(e.getBandwidth()<lowestFoundBandwidth)
							lowestFoundBandwidth=e.getBandwidth();
					}
					System.out.println(e.toString());
				}
				System.out.println("Bottleneck Bandwidth for this path is "+lowestFoundBandwidth+" megabits per second.");
		
			}
		}

	}
				
	public static void coppConn(){

		//REGULAR CC
        CC cc = new CC(graph);

        // number of connected components
        int m = cc.count();

        /*// compute list of vertices in each connected component
        Queue<Integer>[] components = (Queue<Integer>[]) new Queue[m];
        for (int i = 0; i < m; i++) {
            components[i] = new Queue<Integer>();
        }
        for (int v = 0; v < graph.V(); v++) {
            components[cc.id(v)].enqueue(v);
        }

        // print results
        for (int i = 0; i < m; i++) {
            for (int v : components[i]) {
                count++;
            }
        }*/

        //COPPER CC
        copperScan();
        EdgeWeightedGraph copperGraph = new EdgeWeightedGraph(filename, numVertices, numCopperEdges);
        CC coppercc = new CC(copperGraph);

        // number of connected components
        int n = coppercc.count();

        /*// compute list of vertices in each connected component
        Queue<Integer>[] copperComponents = (Queue<Integer>[]) new Queue[n];
        for (int i = 0; i < n; i++) {
            copperComponents[i] = new Queue<Integer>();
        }
        for (int v = 0; v < copperGraph.V(); v++) {
            copperComponents[coppercc.id(v)].enqueue(v);
        }

        // print results
        for (int i = 0; i < m; i++) {
            for (int v : copperComponents[i]) {
                copperCount++;
            }
        }*/
        if(m==n)
			System.out.println("\n\nThis graph can support copper-only data transactions\n\n");
		else
			System.out.println("\n\nThis graph cannot support copper-only data transactions\n\n");

	}
		
	public static void maxData(){

		Scanner keyboard = new Scanner(System.in);
		System.out.print("Enter the first vertex: ");
		int v1 = keyboard.nextInt();
		System.out.print("\nEnter the second vertex: ");
		int v2 = keyboard.nextInt();

        FlowNetwork G = new FlowNetwork(graph, numVertices, numEdges);
        StdOut.println(G);


        // compute maximum flow and minimum cut
        FordFulkerson maxflow = new FordFulkerson(G, v1, v2);
        StdOut.println("Max flow from " + v1 + " to " + v2);
        for (int v = 0; v < G.V(); v++) {
            for (FlowEdge e : G.adj(v)) {
                if ((v == e.to() && e.flow() > 0))
                    StdOut.println("   " + e);
            }
        }

        // print min-cut
        StdOut.print("Min cut: ");
        for (int v = 0; v < G.V(); v++) {
            if (maxflow.inCut(v)) StdOut.print(v + " ");
        }
        StdOut.println();

        StdOut.println("Max flow value = " +  maxflow.value());
	}
		
	public static void lowAvgSpan(){

		System.out.print("\n\nThe following list of edges represent the lowest latency spanning tree: \n\n");
		LazyPrimMST mst = new LazyPrimMST(graph);
        for (Edge e : mst.edges()) {
            StdOut.println(e);
        }
	}
		
	public static boolean twoFailureTest(){
		//Tests if any 1 edge is removed the graph will fail
		for(int i = 0; i<graph.E(); i++){
			for(int j = 0; j<graph.E(); j++){
				EdgeWeightedGraph failureGraph = new EdgeWeightedGraph(graph.edges(), i, j, graph.V(), graph.E());
				CC cc = new CC(failureGraph);
		        if(cc.count()!=1)
	        		return false;
			}
	    }
	    return true;
	}
	
}