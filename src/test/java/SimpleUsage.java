
import com.charlieit.essi.googlesafebrowsing.Lookup;
import com.charlieit.essi.googlesafebrowsing.LookupResult;
import com.charlieit.essi.googlesafebrowsing.Utils;

/**
 *
 * @author Luís Oliveira 
 * charlieIT@github.com
 */
public class SimpleUsage {

    public static void main(String[] args) {

        /*
    Simple usage examples, test it in your Main class
        
         */
        
        /*GET Request Example
        * using a know safe website
        */
        
        Lookup look = new Lookup("<YOUR API KEY />", "GET");
        
        LookupResult result = look.lookupURL("https://www.google.pt/");
        
        //Let's print the trusted status 
        System.out.println(result.isTrusted());
        
        //A known risky website taken from malc0de.com/database/
        LookupResult res = new Lookup("<YOUR API KEY />","GET").lookupURL("wt8.52zsoft.com/hhbqxgq.exe");
        
        //Build a compact report in JSON format
        System.out.println(Utils.prettyJson(Utils.buildCompactReport(res)));
        
        /*
        * POST Request Examples
        * Websites taken from phishtank.com/ and malc0de.com/database/
        * Used only as an example :) 
        */
        
        //POST #1 
        LookupResult post_res = 
                new Lookup("<YOUR API KEY />", "POST")
                        .lookupURL(
                                new String[]{"https://www.google.pt/",
                                    "wt8.52zsoft.com/hhbqxgq.exe",
                                    "http://www.win-install.info/dropbox/"});
        
        //Build a bigger threat report with a random term
        String report = new Utils().buildThreatReport(post_res, null);
        System.out.println(report);
        
        //POST #2 - acessing remote input file
        LookupResult postRes = 
                new Lookup("<YOUR API KEY />", "POST")
                        .lookupURL("https://raw.githubusercontent.com/charlieIT/java-googlesafebrowsing/master/test_urls.txt");
        System.out.println(Utils.prettyJson(Utils.buildCompactReport(postRes)));
        /*
        Other available output display options:
        System.out.println(Utils.prettyJson(Utils.buildCompactReport(post_res));
        System.out.println(result.isTrusted());
        System.out.println(new Utils().buildThreatReport(postRes, "potentially"));
        System.out.println(result.getData());
        System.out.println(result.toString());  
        */
    }
    

}

