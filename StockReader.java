import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class StockReader{

   public static double returnOpen(String sym, String date) throws IOException{
      URL url = new URL("https://www.nasdaq.com/symbol/"+ sym +"/historical");
      URLConnection urlConn = url.openConnection();
      InputStreamReader inStream = new InputStreamReader(urlConn.getInputStream());
      BufferedReader buff = new BufferedReader(inStream);
      String line = buff.readLine();
      while (line != null){
         if (line.contains(date)){
            buff.readLine();
            buff.readLine();
            return Double.parseDouble(buff.readLine().replaceAll("\\s+",""));
         }
         line = buff.readLine();
      }
      return -1.0;
   }
   public static double returnClose(String sym, String date) throws IOException{
      URL url = new URL("https://www.nasdaq.com/symbol/"+ sym +"/historical");
      URLConnection urlConn = url.openConnection();
      InputStreamReader inStream = new InputStreamReader(urlConn.getInputStream());
      BufferedReader buff = new BufferedReader(inStream);
      String line = buff.readLine();
      while (line != null){
         if (line.contains(date)){
            for (int i = 0; i < 11; i++){
            buff.readLine();
            }
            return Double.parseDouble(buff.readLine().replaceAll("\\s+",""));
         }
         line = buff.readLine();
      }
      return -1.0;
   }
   public static void main(String[] args) throws IOException
   {
      //final String sym = "APPL";
      System.out.println(returnOpen("aapl", "12/03/2018"));
      System.out.println(returnClose("aapl", "12/03/2018"));
   }
}