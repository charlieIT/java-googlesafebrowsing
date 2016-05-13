package com.charlieit.essi.googlesafebrowsing;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author Luís Oliveira charlieIT@github.com
 */
public class Lookup {

    private final static String DEFAULT_API_URL = "https://sb-ssl.google.com/safebrowsing/api/lookup?";
    private final static String URL_OPTS = "client=%s&key=%s&appver=%s&pver=%s";
 
    private String httpRequestMethod;
    private final String client;
    private final String apiKey;
    private final String appver;
    private final String pver;
    private String targetURL;
    private String[] urls;
    private ArrayList<String> queryUrls;

    public String getTargetURL() {
        return targetURL;
    }

    public void setPostUrls(ArrayList<String> postUrl) {
        this.queryUrls = postUrl;
    }

    /**
     *
     * @param apiKey - The Google Safe Browsing API KEY for this application
     *  detailed information @
     *  https://developers.google.com/safe-browsing/lookup_guide#GettingStarted
     * @param requestMethod
     */
    public Lookup(String apiKey, String requestMethod) {
        this("api-client", apiKey, "1.0.0", "3.1", requestMethod);
    }

    /**
     * @param client - the type of client
     * @param apiKey - the API KEY for the application
     * @param appver - the client app version
     * @param pver - protocol version supported by the client
     * @param requestMethod
     * @builds targetURL - the UTF-8 encoded URL with mandatory arguments
     */
    public Lookup(String client, String apiKey, String appver, String pver, String requestMethod) {
        this.client = client;
        this.apiKey = apiKey;
        this.appver = appver;
        this.pver = pver;
        this.httpRequestMethod = requestMethod;
        try {
            this.targetURL = DEFAULT_API_URL + String.format(URL_OPTS,
                    URLEncoder.encode(client, "UTF-8"),
                    URLEncoder.encode(apiKey, "UTF-8"),
                    URLEncoder.encode(appver, "UTF-8"),
                    URLEncoder.encode(pver, "UTF-8"),
                    "%s");
        } catch (Exception ex) {
            java.lang.System.err.println("System does not support utf-8");
            java.lang.System.err.println(new AssertionError(ex));
        }
    }

    /**
     *
     * @param arg = url trusted status
     * @return
     */
    public boolean isTrusted(String arg) {
        return lookupURL(arg).isTrusted();
    }

    /**
     *
     * @param arg = urls trusted status
     *              false if, at least, one URL is not trusted 
     * @return
     */
    public boolean isTrusted(String[] arg) {
        return lookupURL(arg).isTrusted();
    }

    private LookupResult LookupParser(HttpURLConnection connection) throws IOException {
        //Retrieve connection date <WeekDay Month Day Hh:Min:Sec WEST Year> 
        Date date =  Calendar.getInstance().getTime();
        int respondeCode = connection.getResponseCode();
        boolean trusted = false;
        int rsp = new Utils().verifyResponseCode(respondeCode);
        String data = "";
        switch (rsp) {
            case 0:
                trusted = true;
                break;
            case 1:
                trusted = false;
                break;
            case 2:
                System.out.println(new Utils().searchResponseCode(respondeCode));
                break;
            default:
                System.out.println("Did not see this coming :o");
                break;
        }

        if (connection.getContentLength() > 0) {
            Scanner scanner = new Scanner(connection.getInputStream());
            while (scanner.hasNext()) {
                data += scanner.nextLine() + "\n";
            }
        }     
        connection.disconnect();
        /*
        *Debug for the received params
        *@responseCode -> http response code
        *@data         -> retrieved request information
        *@trusted      
        System.out.println(Integer.toString(respondeCode) + data + trusted);
        */
        return new LookupResult(connection.getURL().toString(), 
                this.urls, respondeCode, date.toString(), data, trusted);
    }

    /**
     *
     * @param search_urls - array of Strings containing the URLs to check via POST
     * @return LookupResult containing the results
     * @exception IOException
     * @exception UnsupportedEncodingException
     * @exception MalformedURLException
     */
    public LookupResult lookupURL(String[] search_urls) {

        this.urls = search_urls;
        this.queryUrls = new ArrayList<>(Arrays.asList(this.urls));
        
        try {
            HttpURLConnection connection = null;
            if (this.httpRequestMethod.equals("POST")) {
                connection = (HttpURLConnection) new URL(this.targetURL).openConnection();
                connection.setRequestMethod(this.httpRequestMethod);

                //Lookup API POST Request requires number of URLs as first argument
                int size = this.queryUrls.size();
                connection.setDoOutput(true);
                try (DataOutputStream dos = new DataOutputStream(connection.getOutputStream())) {
                    String postParams = "" + size;
                    Iterator it = this.queryUrls.iterator();

                    while (it.hasNext()) {
                        postParams += "\n" + it.next();
                    }
                    /*
                    Debug for POST request Params
                    System.out.println(postParams);
                    */
                    dos.writeBytes(postParams);
                    dos.flush();
                    dos.close();
                }
            }
            LookupResult result = LookupParser(connection);
            return result;                  

        } catch (UnsupportedEncodingException ex) {
            System.err.println("System may not support utf-8 encoding\n" + ex.getMessage());
        } catch (MalformedURLException ex) {
            System.err.println(ex.getMessage());
        } catch (IOException ex) {
            System.err.println(ex.getClass() + "\n"
                    + "Could not open connection");
        }

        return null;
    }

    /**
     *
     * @param search_url = Unique URL to be checked via GET 
     * @param search_url = remote file containing URLs to be checked via POST
     * @return
     */
    public LookupResult lookupURL(String search_url) {
        this.urls = new String[]{search_url};
        URL url;
        try {
            HttpURLConnection connection = null;
            switch (this.httpRequestMethod) {
                case "GET":
                    url = new URL(String.format(this.targetURL + 
                            "&url=%s", URLEncoder.encode(search_url, "UTF-8"), "%s"));
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod(this.httpRequestMethod);
                    LookupResult result = this.LookupParser(connection);
                    return result;
                    
                case "POST":                    
                    FileHandler handler = new FileHandler(new URL(search_url));
                    handler.openURL();
                    ArrayList<String> temp = handler.getUrls();                                   
                    return this.lookupURL(temp.toArray(new String[temp.size()]));
                    
                default:
                    System.err.println("Could not build GET/POST Request");
                    break;
            }         
            
            
        } catch (UnsupportedEncodingException ex) {
            System.err.println("System may not support utf-8 encoding\n"
                    + ex.getMessage());
        } catch (MalformedURLException ex) {
            System.err.println(ex.getMessage());
        } catch (IOException ex) {
            System.err.println(ex.getClass() + "\n"
                    + "Could not open connection");
        }

        return null;
    }

}
