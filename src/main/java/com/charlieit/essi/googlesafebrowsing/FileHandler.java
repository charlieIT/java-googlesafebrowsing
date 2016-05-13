package com.charlieit.essi.googlesafebrowsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Luís Oliveira charlieIT@github.com
 */
public class FileHandler {

    private final String in_dir;
    private final URL in_url;
    private final ArrayList<String> urls;

    /* Still in ToDo LiSt
    
    private String out_dir;
    private String out_size;
    private String data;
    private Date acessDate;
    
     */
    public FileHandler(String in_dir) {
        this.in_dir = in_dir;
        this.urls = new ArrayList<>();
        this.in_url = null;
    }

    public FileHandler(URL in_url) {
        this.in_url = in_url;
        this.urls = new ArrayList<>();
        this.in_dir = null;
    }

    public ArrayList<String> getUrls() {      
        return urls;
    }

    public void openInFile() {
        File in = new File(this.in_dir);
        String data;

        int limit = 500;
        //Google Lookup API POST Request lines limited to 500 per request!!

        try {

            BufferedReader reader = new BufferedReader(new FileReader(in));
            while ((data = reader.readLine()) != null && limit > 0) {
                //split one or more times of either [\s , ; or \t]
                //fetch 1st column -> where URLs should be ^.^
                data = data.split("[\\s,;\\t]+")[0];
                //URLs are added to ArrayList 
                this.urls.add(data);
                limit--;
            }
        } catch (FileNotFoundException ex) {
            System.out.println("File not found, please verify path to input file");
        } catch (IOException ex) {
            System.out.println("Could not open input file, please verify reading permissions");
        }

    }

    public void openURL() {

        try {
            String data = "";
            HttpURLConnection con = (HttpURLConnection) this.in_url.openConnection();
            int limit = 500;
            //Google Lookup API POST Request lines limited to 500 per request!!

            if (con.getContentLength() > 0) {
                Scanner scanner = new Scanner(con.getInputStream());
                while (scanner.hasNext() && limit > 0) {
                    data = scanner.nextLine().split("[\\s,;\\t]+")[0];
                    this.urls.add(data);
                    limit--;
                }
            }
            con.disconnect();
        } catch (FileNotFoundException ex) {
            System.out.println("File not found, please verify url to input file");
        } catch (IOException ex) {
            System.out.println("Could not open input file, please verify reading permissions");
        }
    }

}
