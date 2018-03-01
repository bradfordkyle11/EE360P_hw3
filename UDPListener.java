import java.net.*;

public class UDPListener extends Thread
{
  BookServer bs;
  int port;

  public UDPListener (BookServer bs, int port)
  {
    this.bs = bs;
    this.port = port;
  }


  public void start ()
  {
    // DatagramSocket listener = new DatagramSocket (UDP_PORT);
    // try
    // {

    // }
    // catch (IOException e)
    // {
    //   System.err.println ("Server aborted:" + e);
    // }
  }
}
