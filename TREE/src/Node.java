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
    public boolean check() throws IOException;
    static String child = "CHD";
    static String parent = "PAR";
    static String removeParent = "RMP";
    static String removeChild = "RMC";
    static String message = "MSG";
    static String check = "CHK";
    static String answer = "ANS";
    static int commandLength = 3;
}