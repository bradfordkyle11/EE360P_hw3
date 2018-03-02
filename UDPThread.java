import java.net.*;
import java.util.*;

public class UDPThread extends Thread
{
  BookServer bs;
  DatagramSocket s;
  DatagramPacket request;
  public static final int BLOCK_SIZE = 1024;
  public static final int HEADER_SIZE = 1 + 4 + 1 + 4;
  public static final int CONTENT_SIZE = BLOCK_SIZE - HEADER_SIZE;
  static boolean verbose = Config.verbose;

  public UDPThread (BookServer bs, DatagramPacket request) throws Exception
  {
    this.bs = bs;
    this.s = new DatagramSocket ();
    this.request = request;
  }

  public void run ()
  {
    String result = "\n";
    String command = new String (request.getData (), 0, request.getLength ());

    if (verbose)
      System.out.println ("Server Received: " + command);

    Scanner cmdScanner = new Scanner (command);
    String tag = cmdScanner.next ();

    try
    {
      // borrow <student-name> <book-name>
      if (tag.equals("setmode"))
      {}
      else if (tag.equals ("borrow"))
      {
        result = bs.borrowBook (cmdScanner) + "\n";
      }
      else if (tag.equals ("return"))
      {
        result = bs.returnBook (cmdScanner) + "\n";
      }
      // list <student-name>
      else if (tag.equals ("list"))
      {
        StringBuilder sb = new StringBuilder ();
        for (String each : bs.listBooks (cmdScanner))
          sb.append (each + "\n");
        result = sb.toString ();
      }
      // inventory
      else if (tag.equals ("inventory"))
      {
        StringBuilder sb = new StringBuilder ();
        for (String each : bs.inventory (cmdScanner))
          sb.append (each + "\n");
        result = sb.toString ();
      }
      // exit
      else if (tag.equals ("exit"))
      {
        bs.checkpoint ();
      }
      else
      {
        throw new Exception ("Bad Input!");
      }
    }
    catch (Exception e)
    {
      e.printStackTrace ();
    }
    finally
    {
      cmdScanner.close ();
    }


    int numPackets = (result.length () + CONTENT_SIZE - 1) / (CONTENT_SIZE);

    for (int i=0; i<result.length (); i+=CONTENT_SIZE)
    {
      String stamp = String.format ("%04d %04d\n", numPackets, i/CONTENT_SIZE);
      int length = Math.min (result.length () - i, CONTENT_SIZE);
      byte buf[] = (stamp + result.substring (i, i + length)).getBytes ();

      DatagramPacket response = new DatagramPacket (
          buf,
          length + HEADER_SIZE,
          request.getAddress (),
          request.getPort ());

      try
      {
        s.send (response);
      }
      catch (Exception e)
      {
        e.printStackTrace ();
      }
    }

    s.close ();
  }
}
