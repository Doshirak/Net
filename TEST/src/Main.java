import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.HashMap;

public class Main {
    private static final int BUFSIZE = 1024;
    public static final String USAGE = "Usage:<name> <subnet address> <port>";

    private static final String IDEAD = "IDEAD";
    private static final String ILIVE = "ILIVE";
    private static final String IBORN = "IBORN";



    private static boolean quit = false;
    public static void main(String[] args) {



        HashMap<InetAddress,String> map = new HashMap<InetAddress,String>();

        if (args.length != 3)
            throw new IllegalArgumentException(USAGE);
        DatagramSocket dsocket = null;

        //int port = 0;
        //InetAddress broad = null;

        try {
            String name = new String(args[0]);
            int port = Integer.parseInt(args[2]);
            dsocket = new DatagramSocket(port);
            dsocket.setBroadcast(true);
            InetAddress broad = InetAddress.getByName(args[1]);


            ShutdownHook shutDown = new ShutdownHook(dsocket, port, broad);
            Runtime.getRuntime().addShutdownHook(shutDown);


            byte[] buffer = IBORN.getBytes("UTF8");
            byte[] receiveBuffer = new byte[BUFSIZE];
            //String deleteStr = new String("delete");
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

            DatagramPacket packet = new DatagramPacket(buffer,buffer.length, broad, port);
            dsocket.send(packet);

            while(true){
                dsocket.receive(receivePacket);
                String message = new String(receiveBuffer, 0, receivePacket.getLength(), "UTF-8");

                switch(message.intern()) {
                    case IDEAD:
                        map.remove(receivePacket.getAddress());

                        break;
                    case IBORN:
                        map.put(receivePacket.getAddress(), message);
                        dsocket.send(new DatagramPacket(ILIVE.getBytes("UTF-8"), ILIVE.getBytes("UTF-8").length, receivePacket.getAddress(), receivePacket.getPort()));
                        break;
                    case ILIVE:
                        map.put(receivePacket.getAddress(), message);
                        break;
                }
                print(map);
            }

        } catch (UnknownHostException e ) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dsocket.close();

    }

    private static void print(HashMap<InetAddress, String> map){

        for(InetAddress addr : map.keySet()){
            System.out.println(map.get(addr)+ ' ' + addr.getHostAddress());
        }
        System.out.println("--------------------------------------------------------------------");
    }

}
class ShutdownHook extends Thread {


    private DatagramSocket dsocket;
    private int port;
    private InetAddress address;
    ShutdownHook(DatagramSocket dsocket,int port, InetAddress address) {
        this.dsocket = dsocket;
        this.port = port;
        this.address = address;

    }

    public void run() {
        try {
            byte[] buffer = new String("delete").getBytes("UTF-8");
            dsocket.setBroadcast(true);
            DatagramPacket request = new DatagramPacket(buffer, buffer.length, address, port);
            dsocket.send(request);

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Shutting down");
    }
}

