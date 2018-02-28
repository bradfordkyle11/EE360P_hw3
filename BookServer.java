import java.util.*;
import java.io.*;

public class BookServer {
  private HashMap<String, Integer> library;
  public BookServer()
  {
    library = new HashMap<>();
  }
  public static void main (String[] args) {
    int tcpPort;
    int udpPort;
    if (args.length != 1) {
      System.out.println("ERROR: Provide 1 argument: input file containing initial inventory");
      System.exit(-1);
    }
    String fileName = args[0];
    String absPath = new File("").getAbsolutePath();
    tcpPort = 7000;
    udpPort = 8000;
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
  }
}
