//Ria Galanos 12.7.2016
//Tom Rudwick 11.29.2017
   import twitter4j.*;       //set the classpath to lib\twitter4j-core-4.0.4.jar
   import java.util.List;
   import java.io.*;
   import java.util.Collections;
   import java.util.ArrayList;
   import java.util.Scanner;
   import java.util.Date;
   import java.util.Random;
   import java.util.Calendar;
   import java.util.Date;
   import java.text.DateFormat;
   import java.text.SimpleDateFormat;
   
   public class Twitter_Driver
   {
      private static PrintStream consolePrint;
   
      public static void main (String []args) throws TwitterException, IOException
      {
         consolePrint = System.out; // this preserves the standard output so we can get to it later      
         TJTwitter bigBird = new TJTwitter(consolePrint);
         String message = "Testing twitter bot";
         //bigBird.tweetOut(message);
      /*         while (!twitter_handle.equals("done"))
         {
            bigBird.queryHandle(twitter_handle);
            consolePrint.println("The most common word from @" + twitter_handle + " is: " + bigBird.mostPopularWord()+ ".");
				consolePrint.println("The word appears " + bigBird.getFrequencyMax() + " times.");
            consolePrint.println();
            consolePrint.print("Please enter a Twitter handle, do not include the @ symbol --> ");
            twitter_handle = scan.next();
         }
         */
         bigBird.investigate();
      }           
   }              
   class TJTwitter 
   {
      private Twitter twitter;
      private PrintStream consolePrint;
      private List<Status> statuses;
      private List<String> terms;
      private String popularWord;
      private int frequencyMax;
     
      public TJTwitter(PrintStream console)
      {
         twitter = TwitterFactory.getSingleton(); 
         consolePrint = console;
         statuses = new ArrayList<Status>();
         terms = new ArrayList<String>();
      }
      public void tweetOut(String message) throws TwitterException, IOException
      {
            twitter.updateStatus(message);
      }

      @SuppressWarnings("unchecked")
      public void queryHandle(String handle) throws TwitterException, IOException
      {
         statuses.clear();
         terms.clear();
         fetchTweets(handle);
         splitIntoWords();	
     	   removeCommonEnglishWords();
         sortAndRemoveEmpties();
      }
   	
      public void fetchTweets(String handle) throws TwitterException, IOException
      {
         PrintStream fileout = new PrintStream(new FileOutputStream("tweets.txt")); 
         Paging page = new Paging (1,200);
         int p = 1;
         while (p <= 10)
         {
            page.setPage(p);
            statuses.addAll(twitter.getUserTimeline(handle,page)); 
            p++;        
         }
         int numberTweets = statuses.size();
         fileout.println("Number of tweets = " + numberTweets);
         int count=1;
         for (Status j: statuses)
         {
            fileout.println(count+".  "+j.getText());
            count++;
         }
      }   
      public void splitIntoWords() //throws TwitterException, IOException
      {
         for (Status j: statuses)
         {
            String[] stringStore = removePunctuation(j.getText()).split(" |\n");
            for (int i = 0; i < stringStore.length; i++){
               terms.add(stringStore[i].toLowerCase());
            }
         }
      }
      private String removePunctuation( String s )
      {
         String sCopy = "";
         String punctuation = "!.,?{}[]/():;\"";
         for (int i = 0; i< s.length(); i++){
            if (punctuation.indexOf(s.charAt(i)) == -1){
            sCopy = sCopy + s.charAt(i);
            }         
         }
         return sCopy;
      }
      @SuppressWarnings("unchecked")
      private void removeCommonEnglishWords() throws TwitterException, IOException
      {	
          List<String> commonWords;
          commonWords = new ArrayList<String>();
          Scanner infile = new Scanner(new File("commonWords.txt"));
          try
          {
            infile = new Scanner(new FileInputStream("commonWords.txt")); 
          }
          catch(IOException e)
            {
               System.out.println("oops");
            }
          while (infile.hasNextLine()){
            commonWords.add(infile.next());
          }
          PrintStream fileout = new PrintStream(new FileOutputStream("newTerms.txt"));
          System.out.println(commonWords.size());
          for (int i = 0; i < terms.size(); i++){
            if (commonWords.contains(terms.get(i)) || terms.get(i).equals(" ") || terms.get(i).equals("\n") || terms.get(i).equals(",")){
               terms.remove(i);
               i--;
            }
          }
          System.out.println("Done");
          fileout.println(terms);
      }
      @SuppressWarnings("unchecked")
      public void sortAndRemoveEmpties() throws TwitterException, IOException
      {
         sort(terms);
         for (int i = 0; i < terms.size(); i++){
            if (terms.get(i).equals(" ") || terms.get(i).equals("\n") || terms.get(i).isEmpty() || terms.get(i).equals(null)){
               terms.remove(i);
               i--;
            }
          }
         terms.removeAll(Collections.singleton(null));
         PrintStream fileout = new PrintStream(new FileOutputStream("sortedTerms.txt"));
         fileout.println(terms);
         System.out.println("Done2");
      }
      
      public static void sort(List<String> array)
      { 
        System.out.println("Happens");
        String[] copyBuffer = new String[array.size()];
        mergeSortHelper(array, copyBuffer, 0, array.size() - 1);
      }
      private static void mergeSortHelper(List<String> array, String[] copyBuffer,
                                                      int low, int high)
      {  
      if (low < high)
      {
         int middle = (low + high) / 2;
         mergeSortHelper(array, copyBuffer, low, middle);
         mergeSortHelper(array, copyBuffer, middle + 1, high);
         merge(array, copyBuffer, low, middle, high);
      }
      }
      public static void merge(List<String> array, String[] copyBuffer,
                                   int low, int middle, int high)
   
   {
      int i1 = low;
      int i2 = middle + 1;
      int currPlace = low;
      while (currPlace <= high){
         if (i1 > middle){
            copyBuffer[currPlace] = array.get(i2);
            i2++;
            currPlace++;
         }
         else if (i2 > high){
            copyBuffer[currPlace] = array.get(i1);
            i1++;
            currPlace++;
         }
         else if (array.get(i1).compareTo(array.get(i2)) >= 0){
            copyBuffer[currPlace] = array.get(i2);
            i2++;
            currPlace++;    
         }
         else if (array.get(i1).compareTo(array.get(i2)) <= -1){
            copyBuffer[currPlace] = array.get(i1);
            i1++;
            currPlace++;
         }     
      }
      for (int i = low; i <= high; i++){
         array.set(i, copyBuffer[i]);
      }
   }	
      @SuppressWarnings("unchecked")
      public String mostPopularWord() throws TwitterException, IOException
      {
          PrintStream fileout = new PrintStream(new FileOutputStream("freqTerms.txt"));
          frequencyMax = 0;
          String maxWord = terms.get(0);
          String currentWord = terms.get(0);
          int currentCount = 0;
          for (int i = 0; i < terms.size(); i++){
            if (terms.get(i).equals(currentWord)){
               currentCount++;
            }
            else{
               if (currentCount > frequencyMax){
                  maxWord = currentWord;
                  frequencyMax = currentCount;
               }
               currentWord = terms.get(i);
               currentCount = 0;
            }
          }
          fileout.println(frequencyMax + " " + maxWord);
          terms.removeAll(Collections.singleton(maxWord));
          while (terms.size() >= 1){
          int newfrequencyMax = 0;
          String newMaxWord = terms.get(0);
          String newCurrentWord = terms.get(0);
          int newCurrentCount = 0;
          for (int i = 0; i < terms.size(); i++){
            if (terms.get(i).equals(newCurrentWord)){
               newCurrentCount++;
            }
            else{
               if (newCurrentCount > newfrequencyMax){
                  newMaxWord = newCurrentWord;
                  newfrequencyMax = newCurrentCount;
               }
               newCurrentWord = terms.get(i);
               newCurrentCount = 0;
               }
            }
            fileout.println(newfrequencyMax + " " + newMaxWord);
            terms.removeAll(Collections.singleton(newMaxWord));
          }

          return maxWord;
      }
   	public int getFrequencyMax()
		{
			return frequencyMax;
		}
   
      public void investigate () throws TwitterException, IOException
      {
         Query query = new Query("Apple");
         query.lang("en");
         query.setCount(100);
         query.setSince("2018-11-26");
         query.setUntil("2018-11-27");
         QueryResult result = twitter.search(query);
         SentiWordNetDemoCode sentiwordnet = new SentiWordNetDemoCode();
         //System.out.println(getPrevDate(9));
         //for (Status tweet : result.getTweets()){
            //System.out.println("@"+tweet.getUser().getName()+": " + tweet.getText());
            //System.out.println(sentiwordnet.sentenceScore(tweet.getText()));
            //System.out.println(tweet.getCreatedAt());
         //}
         double[] fOpenPrices = getPastOpens("aapl");
         double[] fClosePrices = getPastCloses("aapl");
         String[] fDateArray = getDateArray();
         int emptyCount = 0;
         for (int i = 0; i < 8; i++){
            System.out.println(fDateArray[i] + ": Open: " + fOpenPrices[i] + " Close: " + fClosePrices[i]);
            if (fOpenPrices[i] == -1.0){
               emptyCount++;
            }
         }
         System.out.println(emptyCount);
         int pos = 0;
         double[] openPrices = new double[8 - emptyCount];
         double[] closePrices = new double[8 - emptyCount]; 
         String[] dateArray = new String[8 - emptyCount];
         for (int i = 0; i < 8; i++){
            if (fOpenPrices[i] != -1){
               openPrices[pos] = fOpenPrices[i];
               closePrices[pos] = fClosePrices[i];
               dateArray[pos] = fDateArray[i];
               pos++;
            }
         }      
         for (int i = 0; i < openPrices.length; i++){
            System.out.println(dateArray[i] + ": Open: " + openPrices[i] + " Close: " + closePrices[i]);
         }
      }
      public static String getPrevDate(int prev) {
         Calendar cal = Calendar.getInstance();
         cal.add(Calendar.DAY_OF_MONTH, -prev);
         Date date = cal.getTime();
         DateFormat dateFormat = new SimpleDateFormat("M/dd/Y");
         String formattedDate=dateFormat.format(date);
         //System.out.println("Current time of the day using Calendar month: "+ formattedDate);
         return formattedDate;
         }
      public static String[] getDateArray(){
         String[] dateArray = new String[9];
         for (int i = 9; i > 1; i--){
            dateArray[9-i] = getPrevDate(i);
         }
         return dateArray;
      }
      public double[] getPastOpens(String sym) throws IOException{
         StockReader stockreader = new StockReader();
         double[] openPrices = new double[9];
         for (int i = 9; i > 1; i--){
            //System.out.println(getPrevDate(i) + ": " + stockreader.returnOpen(sym, getPrevDate(i)));
            openPrices[9-i] = stockreader.returnOpen(sym, getPrevDate(i));
         }
         return openPrices;
      }
      public double[] getPastCloses(String sym) throws IOException{
         StockReader stockreader = new StockReader();
         double[] closePrices = new double[9];
         for (int i = 9; i > 1; i--){
            //System.out.println(getPrevDate(i)+ ": " + stockreader.returnClose(sym, getPrevDate(i)));
            closePrices[9-i] = stockreader.returnClose(sym, getPrevDate(i));
         }
         return closePrices; 
      }
      public double neuralNetworkCalc(double open, int favorites, int retweets, double score, double[] weights1, double[] weights2, double[] biases1, double[] biases2){
         return 2.0;
      }
     /** 
      * This method determines how many people in Arlington, VA 
      * tweet about the Miami Dolphins.  Hint:  not many. :(
      */
      public void sampleInvestigate ()
      {
         Query query = new Query("Hawker");
         query.setCount(100);
         query.setGeoCode(new GeoLocation(1.423844, 103.776921), 5, Query.MILES);
         query.setSince("2016-12-1");
         try {
            QueryResult result = twitter.search(query);
            System.out.println("Count : " + result.getTweets().size()) ;
            for (Status tweet : result.getTweets()) {
               System.out.println("@"+tweet.getUser().getName()+ ": " + tweet.getText());  
            }
         } 
            catch (TwitterException e) {
               e.printStackTrace();
            } 
         System.out.println(); 
      }  
   
   }  

