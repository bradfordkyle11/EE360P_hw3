import java.util.Scanner;
import java.io.*;
import java.util.*;
import java.net.*;
public class BookClient
{
  final static String hostAddress = "localhost";;
  final static int TCP_PORT = 7000; // hardcoded -- must match the server's tcp port
  final static int UDP_PORT = 8000; // hardcoded -- must match the server's udp port
  static int CLIENTID;
  static char MODE = 'T';

  static boolean connectTCP (Scanner scanner) throws Exception
  {
    Socket server = new Socket (hostAddress, TCP_PORT);
    Scanner in = new Scanner (server.getInputStream ());
    PrintStream out = new PrintStream (server.getOutputStream (), true);
    PrintWriter log = new PrintWriter ("out_" + CLIENTID + ".txt");

    boolean exit = false;
    while (MODE == 'T' && scanner.hasNextLine ())
    {
      String command = scanner.nextLine ();
      Scanner cmdScanner = new Scanner (command);
      String tag = cmdScanner.next ();
      
      if (tag.equals("setmode"))
        MODE = cmdScanner.next ().charAt(0);
      out.println (command);
      exit = tag.equals("exit");
      if (exit)
        break;

      while (in.hasNextLine ())
      {
        String last = in.nextLine();
        if (last.equals("over"))
          break;
        log.println (last);
        System.out.println(last);
      }
      cmdScanner.close();
    }
    
    server.close();
    in.close();
    log.flush();
    log.close();
    return exit;
  }

  static boolean connectUDP (Scanner scanner) throws Exception
  {
    DatagramSocket server = new DatagramSocket (TCP_PORT);
    while (scanner.hasNextLine ())
    {

    }
    while (MODE == 'U')
    {

    }
    return true;
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
      if (MODE == 'T')
      {
        if (connectTCP (scanner))
          break;
      }
      else
      {
        if (connectUDP (scanner))
          break;
      }
    }

    scanner.close ();
  }
}
