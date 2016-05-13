package com.charlieit.essi.googlesafebrowsing;

import java.util.ArrayList;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author Luís Oliveira charlieIT@github.com
 */
public class Main {

    public static void main(String[] args) {
        
        /**
         * Example class for parsing command line input
         */

        String key = "";
        String dest_url = "";
        String request_method = "GET";
        boolean compact_report = false;
        boolean isPost = false;
        String input_dir = null;
        Lookup lookup = null;
        LookupResult result = null;

        if (args.length < 2) {
            System.out.println(Utils.help());
            System.exit(0);
        } else {

            switch (args[0]) {
                case "-help":
                case "-h": {
                    System.out.println(Utils.help());
                    System.exit(-1);
                }

                case "-GET":
                case "-get": {
                    String help_get_method = Utils.help().split("\r\n")[0];
                    if (args.length < 5) {
                        System.out.println(help_get_method);
                        System.exit(-1);
                    } else {
                        request_method = "GET";
                        isPost = false;
                        break;
                    }
                }
                case "-POST":
                case "-post": {
                    if (args.length < 5) {
                        String help_post_method = Utils.help().split("\r\n")[1];
                        System.out.println(help_post_method);
                        System.exit(0);
                    } else {
                        request_method = "POST";
                        isPost = true;
                        break;
                    }
                }
            }

            key = args[ArrayUtils.indexOf(args, "-k") + 1];
            /*
                    Debug for the key input
                    System.out.println(key);
             */

            //Check whether the compact output is requested
            compact_report = ArrayUtils.indexOf(args, "-compact") != -1;

            //Check whether method is POST and input file is given
            int index = ArrayUtils.indexOf(args, "-in");
            isPost = ArrayUtils.indexOf(args, "-post") != -1;

            if (index != -1) {
                input_dir = args[index + 1];
                isPost = true;

            } else if (ArrayUtils.indexOf(args, "-URL") != -1) {
                dest_url = args[ArrayUtils.indexOf(args, "-URL") + 1];
            } else {
                dest_url = args[ArrayUtils.indexOf(args, "-u") + 1];
            }
            if (ArrayUtils.indexOf(args, "-get") != -1) {
                isPost = false;
                /*
                    Debug for the url input
                    System.out.println(dest_url);
                 */

            }
        }

        if (isPost) {           
            lookup = new Lookup(key, "POST");
            //if it's POST method and local input file was passed as argument
            if (null != input_dir) {

                FileHandler handler = new FileHandler(input_dir);
                try {
                    ArrayList<String> urls = null;
                    handler.openInFile();
                    if ((urls = handler.getUrls()) != null) {                     
                        result = lookup.lookupURL(urls.toArray(new String[urls.size()]));

                    }
                } catch (NullPointerException ex) {
                    // System.out.println("<Make it not null?/>");
                    ex.printStackTrace();
                } catch (Exception ex) {
                    // System.out.println("<Do Stuff/>");
                    ex.printStackTrace();
                }

            } else {
                //POST request whith remote input file
                result = lookup.lookupURL(dest_url);
            }
        } else {
            //GET request with single input url
            lookup = new Lookup(key, "GET");
            result = lookup.lookupURL(dest_url);
        }

        /*
        * Various options for output and output format
        * .isTrusted(), .getData(), .toString() 
        * .buildThreatReport() returns full text report along with ANSI colors and indentation
        * .buildCompactReport returns a valid JSON object
        *   using .prettyJSON() bellow for the visual presentation
         */
        if (!compact_report) {
            System.out.println(new Utils().buildThreatReport(result, null));
            // System.out.println(new Utils().buildThreatReport(result, "potentially"));
            // System.out.println(result.isTrusted());
            // (...)

        } else {
            System.out.println(Utils.prettyJson(Utils.buildCompactReport(result)));
            // System.out.println(result.isTrusted());
            // System.out.println(result.getData());
            // System.out.println(result.toString());
        }

    }

}
