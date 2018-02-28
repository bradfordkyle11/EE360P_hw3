import java.util.Scanner;
import java.io.*;
import java.util.*;
public class BookClient
{
  final String hostAddress = "localhost";;
  final int TCP_PORT = 7000; // hardcoded -- must match the server's tcp port
  final int UDP_PORT = 8000; // hardcoded -- must match the server's udp port
  static int CLIENTID;
  static char MODE = 'U';

  static void connectTCP (Scanner scanner) throws Exception
  {
    Socket server = new Socket (hostAddress, TCP_PORT);
    Scanner in = new Scanner (server.getInputStream ());
    PrintStream out = new PrintStream (server.getOutputStream (), true);
    PrintWriter log = new PrintWriter ("out_" + clientId + ".txt");

    while (mode == 'T' && scanner.hasNextLine ())
    {
      String command = scanner.nextLine ();
      Scanner cmdScanner = new Scanner (command);
      String tag = cmdScanner.next ();

      if (tag.equals ("setmode"))
        MODE = cmdScanner.next ()[0];
      else
      {
        out.println (command);
        while (in.hasNextLine ())
          log.println (in.nextLine ());
      }
    }
  }

  static void connectTCP (Scanner scanner) throws Exception
  {
    DatagramSocket server = new DatagramSocket (hostAddress, TCP_PORT);
    while (scanner.hasNextLine ())
    {

    }
    while (mode == 'U')
    {

    }
  }

  public static void main (String[] args) throws Exception
  {

    if (args.length != 2)
    {
      System.out.println ("ERROR: Provide 2 arguments: commandFile, clientId");
      System.out.println ("\t (1) <command-file>: file with commands to the server");
      System.out.println ("\t (2) client id: an integer between 1..9");
      System.exit (-1);
    }

    String commandFile = args[0];
    String absPath = new File ("").getAbsolutePath ();
    Scanner scanner = new Scanner (new FileReader (absPath + commandFile));

    CLIENTID = Integer.parseInt (args[1]);

    while (true)
    {
      if (mode == 'T')
        connectTCP (scanner);
      else
        connectUDP (scanner);
    }

    scanner.close ();
  }
}
