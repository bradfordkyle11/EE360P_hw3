import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.io.*;
import java.net.*;

public class BookServer
{
  static ConcurrentHashMap <String, AtomicInteger> library = new ConcurrentHashMap <> ();;
  static ConcurrentHashMap <Integer, Pair <String, String>> history = new ConcurrentHashMap <> ();;
  static AtomicInteger recordId = new AtomicInteger (1);
  static final int TCP_PORT = 7000;
  static final int UDP_PORT = 8000;
  static boolean verbose = true;

  class Pair <X, Y>
  {
    X x;
    Y y;
    public Pair (X x, Y y)
    {
      this.x = x;
      this.y = y;
    }
  }

  class ServerThread extends Thread
  {
    int clientId;
    Socket s;
    DatagramSocket ds;

    public ServerThread (Socket s)
    {
      Scanner scanner;
      try
      {
        scanner = new Scanner (s.getInputStream ());
      }
      catch (Exception e)
      {
        e.printStackTrace ();
      }
      finally
      {
        if (scanner != null)
          scanner.close ();
      }

      this.clientId = scanner.nextInt ();
      this.s = s;
    }

    public ServerThread (DatagramSocket ds)
    {
      // this.clientId = clientId;
      this.ds = ds;
    }

    public void start ()
    {
      if (s != null)
        handleSocket ();
      else
        handleDatagramSocket ();
    }

    public void handleSocket ()
    {
      Scanner scanner;
      try
      {
        Scanner scanner = new Scanner (s.getInputStream ());
        PrintStream out = new PrintStream (s.getOutputStream (), true);
        String command = scanner.nextLine ();

        if (verbose)
          System.out.println ("received:" + command);

        Scanner cmdScanner = new Scanner (command);
        String tag = cmdScanner.next ();

        // setmode T|U
        if (tag.equals ("setmode"))
        {
          synchronized (BookServer.class)
          {
            MODE = cmdScanner.next ();
          }
        }
        // borrow <student-name> <book-name>
        else if (tag.equals ("borrow"))
        {
          String student = cmdScanner.next ();
          String book = cmdScanner.next (Pattern.compile ("\"[^\"]+\""));
          AtomicInteger quantity = library.get (book);
          if (quantity == null)
            out.println ("Request Failed - We do not have this book");
          else
          {
            int value = quantity.decrementAndGet ();
            if (value < 0)
            {
              quantity.incrementAndGet ();
              out.println ("Request Failed - Book not available");
            }
            else
            {
              int id = recordId.getAndIncrement ();
              history.put (id, new Pair <String, String> (student, book));
              if (students.containsKey (student))
                out.printf ("You request has been approved, %d %s %s\n", id, student, book);
            }
          }
        }
        else if (tag.equals ("return"))
        {
          int id = cmdScanner.nextInt ();
          if (!history.containsKey (id))
          {
            out.println (id + " not found, no such borrow record");
          }
          else
          {
            String book = history.remove (id).y;

            AtomicInteger quantity = library.get (book);
            quantity.incrementAndGet ();

            out.println (id + " is returned");
          }
        }
        // list <student-name>
        else if (tag.equals ("list"))
        {
          String student = cmdScanner.next ();

          boolean found = false;
          for (Integer id : history.keySet ())
          {
            String s = history.get (id).x;
            String book = history.get (id).y;
            if (s.equals (student))
            {
              out.println (id + " " + book);
            }
          }

          if (!found)
            out.println ("No record found for " + student);
        }
        // inventory
        else if (tag.equals ("inventory"))
        {
          for (String book : library.keySet ())
          {
            int value = Math.max (library.get (book).get (), 0);
            out.println (book + " " + value);
          }
        }
        // exit
        else if (tag.equals ("exit"))
        {
          checkpoint ();
        }
        else
        {
          throw new Exception ("Bad Input!");
        }
      }
      catch (Exception e)
      {
        System.err.println (e);
      }
      finally
      {
        if (scanner != null)
          scanner.close ();
        s.close ();
      }
    }

    public void handleDatagramSocket ()
    {

    }
  }

  static synchronized void checkpoint ()
  {
    PrintWriter out = new PrintWriter ("inventory.txt");
    for (String book : library.keySet ())
      out.println (book + " " + library.get (book))
  }

  static void initLibrary (String path)
  {
    Scanner scanner;
    try
    {
      scanner = new Scanner (new FileReader (path));

      while (scanner.hasNextLine ())
      {
        String command = scanner.nextLine ();
        Scanner cmdScanner = new Scanner (command);
        String book = cmdScanner.next (Pattern.compile ("\"[^\"]+\""));
        int quantity = cmdScanner.nextInt ();
        library.put (book, new AtomicInteger (quantity));
      }
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace ();
    }
    finally
    {
      if (scanner != null)
        scanner.close ();
    }
  }

  class TCPListener extends Thread
  {
    public void start ()
    {
      ServerSocket listener = new ServerSocket (TCP_PORT);
      try
      {
        Socket s;
        while ((s = listener.accept ()) != null)
        {
          new ServerThread (s).start ();
        }
      }
      catch (IOException e)
      {
        System.err.println ("Server aborted:" + e);
      }
    }
  }

  class UDPListener extends Thread
  {
    public void start ()
    {
      DatagramSocket listener = new DatagramSocket (UDP_PORT);
      try
      {

      }
      catch (IOException e)
      {
        System.err.println ("Server aborted:" + e);
      }
    }
  }

  public static void main (String[] args)
  {
    if (args.length != 1)
    {
      System.out.println ("ERROR: Provide 1 argument: input file containing initial inventory");
      System.exit (-1);
    }

    String fileName = args[0];
    String absPath = new File ("").getAbsolutePath ();

    // parse the inventory file
    initLibrary (absPath + fileName);

    //handle connections
    new TCPListener ().start ();
    new UDPListener ().start ();
  }
}
