import java.io.IOException;
import java.net.InetAddress;

interface Node {
    public InetAddress getAddress();
    public int getPort();
    public void addParent(Node node) throws IOException;
    public void addChild(Node node) throws IOException;
    public void removeParent() throws IOException;
    public void removeChild(Node node) throws IOException;
    public void sendMessage(String message) throws IOException;
    static String child = "CHD";
    static String parent = "PAR";
    static String removeParent = "RMP";
    static String removeChild = "RMC";
    static String message = "MSG";
    static int commandLength = 3;
    static String hr = "--------------------------";
}