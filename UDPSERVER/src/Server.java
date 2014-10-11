/**
 * Created by doshirak on 15.09.14.
 */

import com.sun.xml.internal.ws.client.SenderException;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

class Server
{
    public static void main(String args[])
    {
        if (2 != args.length)
        {
            System.out.println("usage: java Server [filename] [port]");
            System.exit(0);
        }
        String filename = args[0];
        Integer port = Integer.parseInt(args[1]);
        File file = new File(filename);
        ArrayList<String> quotes = new ArrayList<String>();
        int n = 0;
        try
        {
            BufferedReader fileReader = new BufferedReader(new FileReader(file));
            while (fileReader.ready())
            {
                quotes.add(fileReader.readLine());
            }
            DatagramSocket serverSocket = new DatagramSocket(port);
            byte[] receiveData = new byte[1024];
            while (n < quotes.size())
            {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                InetAddress IPAddress = receivePacket.getAddress();
                int senderPort = receivePacket.getPort();
                System.out.println("sender address:" + IPAddress + " " + senderPort);
                String quote = quotes.get(n++);
                DatagramPacket sendPacket = new DatagramPacket(quote.getBytes(), quote.getBytes().length, IPAddress, senderPort);
                serverSocket.send(sendPacket);
            }
        } catch (FileNotFoundException e)
        {
            System.out.println("File '" + filename + "' not found");
        } catch (SocketException e)
        {
            System.out.println("Can't bind socket: port " + port + " already bind");
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}