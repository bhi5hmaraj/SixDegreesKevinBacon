import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.Queue;
import java.util.LinkedList;

 class DepthFirstPaths {
    private boolean[] marked;    // marked[v] = is there an s-v path?
    private int[] edgeTo;        // edgeTo[v] = last edge on s-v path
    private int id[];
    private int ct,count;
    private HashMap<Integer,Integer> comp_size;
    /**
     * Computes a path between <tt>s</tt> and every other vertex in graph <tt>G</tt>.
     * @param G the graph
     * @param s the source vertex
     */
    public DepthFirstPaths(Graph G) {
        edgeTo = new int[G.V()];
        marked = new boolean[G.V()];
        comp_size = new HashMap<>();
        id = new int[G.V()];
        ct =0;        
        count =0;
        
        for(int i =0 ;i<G.V();i++)
        {
            if(!marked[i])
            {
            dfs(G, i);
            comp_size.put(ct, count);
            count = 0;
            ct++;
            }
        }
        System.out.println("number of components "+(ct));
    }

    // depth first search from v
    private void dfs(Graph G, int v) {
	//System.out.println(v);
	if(!marked[v])
	{
        marked[v] = true;
        count++;
        id[v]=ct;
        for (int w : G.adj(v)) {
            if (!marked[w]) {
        	//System.out.println(ActorGraph.name(v)+","+ActorGraph.name(w));
                edgeTo[w] = v;                
                dfs(G, w);

            }
        }
	}
    }
	private void bfs(Graph G,int u)
	{
	    Queue<Integer> q = new LinkedList<>();
	    q.add(u);
	    int v;
	    while(!q.isEmpty())
	    {			
		 //System.out.println(q);
		 v = q.remove();
		 if(!marked[v])
		 {		 
		     marked[v]  = true;
		     id[v]  = ct;
		     count++;
		     for(int w:G.adj(v))
		     {
			 if(!marked[w])
			     q.add(w);
		     }
		 }
	    }			
	}
    /**
     * Is there a path between the source vertex <tt>s</tt> and vertex <tt>v</tt>?
     * @param v the vertex
     * @return <tt>true</tt> if there is a path, <tt>false</tt> otherwise
     */
    public boolean hasPathTo(int v,int w) {
        return id[v] == id[w];
    }

    /**
     * Returns a path between the source vertex <tt>s</tt> and vertex <tt>v</tt>, or
     * <tt>null</tt> if no such path.
     * @param v the vertex
     * @return the sequence of vertices on a path between the source vertex
     *   <tt>s</tt> and vertex <tt>v</tt>, as an Iterable
     */
    public Iterable<Integer> pathTo(int v,int w) {
        if (!hasPathTo(v,w)) return null;
        Stack<Integer> path = new Stack<Integer>();
        for (int x = v; x != w; x = edgeTo[x])
            path.push(x);
        path.push(w);
        return path;
    }
    
    public void components()
    {
	int max_id=-1,big=0;
	for(Map.Entry<Integer, Integer> e:comp_size.entrySet())
	{
	    System.out.println(e.getKey() +"---"+e.getValue());
	    if(e.getValue() > big)
	    {
		big = e.getValue();
		max_id = e.getKey();
	    }
	}
	System.out.println("big comp id ="+max_id+" size "+big);
    }
 }
public class ActorGraph {

    private HashMap<String, Integer> st;  // string -> index
    private static  String[] keys;           // index  -> string
    public Graph G;
    private int V,E;
    double avg_degree;
    /**  
     * Initializes a graph from a file using the specified delimiter.
     * Each line in the file contains
     * the name of a vertex, followed by a list of the names
     * of the vertices adjacent to that vertex, separated by the delimiter.
     * @param filename the name of the file
     * @param delimiter the delimiter between fields
     */
    public ActorGraph(String filename, String delimiter) {
        st = new HashMap<String, Integer>();
        BufferedReader br = null;
        // First pass builds the index by reading strings to associate
        // distinct strings with an index
        // while (in.hasNextLine()) {
        try
        {
            long start= System.nanoTime();
            br = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = br.readLine()) != null )  {
        	String[] a = line.split(delimiter);
        	for (int i = 1; i < a.length; i++) {
        	    if (!st.containsKey(a[i]))
        		st.put(a[i], st.size());
        	}
            }
            br.close();
            System.out.println("Done reading " + filename);

            // inverted index to get string keys in an aray
            keys = new String[st.size()];
            for (Map.Entry<String,Integer> entry:st.entrySet()) {
        	keys[entry.getValue()] = entry.getKey();
            }

            // second pass builds the graph by connecting first vertex on each
            // line to all others
            G = new Graph(st.size());            
            br = new BufferedReader(new FileReader(filename));
            while ((line = br.readLine()) != null ) {
        	String[] a = line.split(delimiter);
        	for(int i=1;i<a.length-1;i++)
        	{
        	    int v = st.get(a[i]);
        	    for (int j = i+1; j < a.length; j++) {
        		int w = st.get(a[j]);
        		if(!G.adj(v).contains(w))
        		    G.addEdge(v, w);
        	    }
        	}
            }
            br.close();
            V = G.V();
            E = G.E();
            avg_degree = ((double)2*E)/V;
            long end = System.nanoTime()-start;            
            System.out.println("Time taken to build graph:"+end/1e9+"s");
        }
    
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Does the graph contain the vertex named <tt>s</tt>?
     * @param s the name of a vertex
     * @return <tt>true</tt> if <tt>s</tt> is the name of a vertex, and <tt>false</tt> otherwise
     */
    public boolean contains(String s) {
        return st.containsKey(s);
    }
    
    public String[] keys()
    {
	return keys;
    }
    /**
     * Returns the integer associated with the vertex named <tt>s</tt>.
     * @param s the name of a vertex
     * @return the integer (between 0 and <em>V</em> - 1) associated with the vertex named <tt>s</tt>
     */
    public int index(String s) {
        return st.get(s);
    }

    /**
     * Returns the name of the vertex associated with the integer <tt>v</tt>.
     * @param v the integer corresponding to a vertex (between 0 and <em>V</em> - 1) 
     * @return the name of the vertex associated with the integer <tt>v</tt>
     */
    public static String name(int v) {
        return keys[v];
    }

    /**
     * Returns the graph assoicated with the symbol graph. It is the client's responsibility
     * not to mutate the graph.
     * @return the graph associated with the symbol graph
     */
    public Graph G() {
        return G;
    }
    public int V()
    {
	return V;
    }
    public int E()
    {
	return E;
    }
    public String toString()
    {
	StringBuilder s = new StringBuilder();
	s.append(V + " vertices, "+E+" edges\n");
	/*
	for (int v = 0; v < V; v++) {
	    s.append(name(v) + "--> ");
	    for (int w : G.adj(v)) {
		s.append(name(w) + " ");
	    }
	    s.append("\n");
	}
	*/
	return s.toString();
    }
    public void printtoFile(PrintWriter out)
    {	
	//out.println("Source,Target,Type,Weight,Average Degree");
	for (int v = 0; v < V; v++) {
	    
	    //out.print(v + "--> ");
	    for (int w : G.edges(v)) {
		out.println(v+","+w);
	    }
	}
	out.flush();
    }

    /**
     * Unit tests the <tt>SymbolGraph</tt> data type.
     */
    public static void main(String[] args) {
	//System.out.println(System.getProperty("sun.arch.data.model"));
	String file ="movies-top-grossing.txt";
        String filename  = "C:/Users/bhisR/OneDrive/Network Project/"+file;
        String delimiter = "/";
        ActorGraph ag = new ActorGraph(filename, delimiter);
        PrintWriter out = null;
        try
        {
        long start= System.nanoTime();    
        out = new PrintWriter(new File("D:/test_gephi_grossing.csv"));
        ag.printtoFile(out);
        //out.println(ag); 
        long end = System.nanoTime()-start;
        System.out.println("Time to print into file "+end/1e9+" s"); 
        System.out.println(ag);
        start = System.nanoTime();
        DepthFirstPaths dfs = new DepthFirstPaths(ag.G());
        end = System.nanoTime() - start;
        System.out.println("Time for dfs "+end/1e9+"s");
        dfs.components();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
           // out.close();
        }    
    }
}