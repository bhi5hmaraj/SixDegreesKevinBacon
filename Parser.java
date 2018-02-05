import java.util.*;
import java.io.*;
public class Parser {
	
public static HashMap<String,ArrayList<String>> parse()
{
	String line="";
	String location = "C:/Users/bhisR/Downloads/Compressed/cast.G.txt";
	HashMap<String,ArrayList<String>> mp =new HashMap<>();
	ArrayList<String> arl;
	try
	{
		BufferedReader br = new BufferedReader(new FileReader(location));
		StringTokenizer st;
		while((line=br.readLine()) != null)
		{
			st = new StringTokenizer(line,"/");
			arl = new ArrayList<>();
			String movie = st.nextToken();
			while(st.hasMoreElements())
			{
				arl.add(st.nextToken());
			}
			mp.put(movie, arl);
		}
		br.close();
	}
	catch(IOException e)
	{
		System.out.println("Error occured at this line\n"+line);		
	}
	return mp;
}
//022537
public static void main(String[] args)throws IOException {
	File file = new File("movies.txt");
	file.createNewFile();
	PrintWriter out = new PrintWriter(file);
	for(Map.Entry<String, ArrayList<String>> entry:parse().entrySet())
	{
		out.println("Movie :"+entry.getKey()+"\nActors:");
		out.println(entry.getValue()+"\n");
	}
	out.close();
}
}
