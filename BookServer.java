import java.util.*;
import java.util.concurrent.*;
import java.io.*;
import java.net.*;

public class BookServer {
  private ConcurrentHashMap<String, Integer> library;
  private ConcurrentHashMap<Integer, Tuple<String, String>> history;
  
  public class Tuple<X, Y> { 
    public final X x; 
    public final Y y; 
    public Tuple(X x, Y y) { 
      this.x = x; 
      this.y = y; 
    } 
  }

  public BookServer()
  {
    library = new ConcurrentHashMap<>();
    history = new ConcurrentHashMap<>();
  }
  public static void main (String[] args) {
    final int TCP_PORT = 7000;
    final int UDP_PORT = 8000;
    char mode = 'U';
    if (args.length != 1) {
      System.out.println("ERROR: Provide 1 argument: input file containing initial inventory");
      System.exit(-1);
    }
    String fileName = args[0];
    String absPath = new File("").getAbsolutePath();
    
    BookServer bs = new BookServer();

    // parse the inventory file
    try
    {
      Scanner sc = new Scanner(new FileReader(absPath + fileName));
  
      while(sc.hasNextLine()) {
        String cmd = sc.nextLine();
        //input format: "Book Name" numCopies
        String[] tokens = cmd.split("\" ");
        tokens[0] = tokens[0].replace("\"", "");
        bs.library.put(tokens[0], Integer.valueOf(tokens[1]));
      }

      sc.close();
    } 
    catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    //handle connections
    try
    {
      ServerSocket tcpSocket = new ServerSocket(TCP_PORT);
      DatagramSocket udpSocket = new DatagramSocket(UDP_PORT);
    }
    catch (IOException e)
    {
      System.err.println("Server aborted:" + e);
    }
  }
}
