package com.charlieit.essi.googlesafebrowsing;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 *
 * @author Luís Oliveira charlieIT@github.com
 */
public class Utils {

    protected HashMap<Integer, String> responde_codes;
    protected ArrayList<String> threat_terms;
    protected ArrayList<String> googleSpecs;
    protected HashMap<String, String> warning_language;

    /*
     * response_codes:  known Google Lookup API response codes 
     * threat_terms:    Google suggested risk classifying terms 
     * google_specs :   Google specifications
     *      For more detailed information visit:
     *      https://developers.google.com/safe-browsing/lookup_guide#UserWarnings
     * 
     * warning_language: Google specification
     *     For more detailed information visit:
     *     https://developers.google.com/safe-browsing/lookup_guide#suggested-warning-language
     * 
     * 
     */
    public Utils() {
        this.responde_codes = new HashMap<>();
        this.responde_codes.put(200, "false");
        this.responde_codes.put(204, "true");
        this.responde_codes.put(400, "Error 400: Bad Request");
        this.responde_codes.put(401, "Error 401: Not Authorized - API key not authorized");
        this.responde_codes.put(503, "Error 503: Service Unavailable");
        this.responde_codes.put(0, "Unknown response code");

        this.threat_terms = new ArrayList<>();
        this.threat_terms.add("suspected");
        this.threat_terms.add("potentially");
        this.threat_terms.add("likely");
        this.threat_terms.add("possibly");

        this.googleSpecs = new ArrayList<>();
        this.googleSpecs.add("Advisory provided by Google");
        this.googleSpecs.add("No page result is 100% certain, for more information visit:");
        this.googleSpecs.add("https://www.google.com/about/company/unwanted-software-policy.html");
        this.googleSpecs.add("http://code.google.com/apis/safebrowsing/safebrowsing_faq.html#whyAdvisory");
        this.googleSpecs.add("http://www.antiphishing.org/ ");
        this.googleSpecs.add("http://www.stopbadware.org/");

        this.warning_language = new HashMap<>();
        this.warning_language.put("phishing", "Warning - This page may be a forgery or imitation of another website, designed to trick users into sharing personal or financial information. Entering any personal information on this page may result in identity theft or other abuse. You can find out more about phishing from www.antiphishing.org.");
        this.warning_language.put("malware", "Warning - Visiting this web site may harm your computer. This page appears to contain malicious code that could be downloaded to your computer without your consent. You can learn more about harmful web content including viruses and other malicious code and how to protect your computer at StopBadware.org.");
        this.warning_language.put("unwanted", "Warning - The site ahead may contain harmful programs. Attackers might attempt to trick you into installing programs that harm your browsing experience (for example, by changing your homepage or showing extra ads on sites you visit). You can learn more about unwanted software at https://www.google.com/about/company/unwanted-software-policy.html");

    }

    /**
     *
     * @param code = http response code
     * @return = the message associated with http request
     */
    public String searchResponseCode(int code) {

        Set<Map.Entry<Integer, String>> set = this.responde_codes.entrySet();
        for (Map.Entry<Integer, String> tmp : set) {
            if (Objects.equals(tmp.getKey(), code)) {
                //return message associated with the http response code
                return tmp.getValue();
            }
        }
        return "Unknown response code: " + Integer.toString(code);
    }

    /**
     *
     * @param code = http response code
     * @return int
     */
    public int verifyResponseCode(int code) {

        Set<Map.Entry<Integer, String>> set = this.responde_codes.entrySet();
        for (Map.Entry<Integer, String> tmp : set) {
            if (Objects.equals(tmp.getKey(), code)) {
                // if website is trusted return 1
                if (tmp.getValue().equals("false")) {
                    return 1;
                }
                //if website is likely risky return 0
                if (tmp.getValue().equals("true")) {
                    return 0;
                }

            }
        }
        return 2;
    }

    /**
     *
     * @param result = LookupResult object
     * @param term = arbitrary term for the output risk message - accepts null
     * value
     * @return full report String
     */
    public String buildThreatReport(LookupResult result, String term) {
        
        //if no term is given one will be picked from the Google sugested terms
        if (term == null || term.equals("")) {
            //generate a random int in range [0, ArraySize[
            term = this.threat_terms.get(new Random().nextInt(this.threat_terms.size()));
        }
        int i = 0;
        String sb = new StringBuilder("\t\t\t\t\t\u001B[34;1mThe lookup returned the following:\u001B[0m\n"
                + "Request URL: " + result.getUrl() + "\n"
                + "Code: " + result.getHttpCode() + "\n").toString();
        String[] data = result.getData().split("\n");

        for (String d : data) {
            //if the data String is non-empty (GET) or !contains param 'ok' (POST) --> deemed risky website
            //code 200 : AT LEAST ONE of the queried URLs are matched in either the phishing, malware, or unwanted software lists.
            if (!(d.isEmpty() || d.contains("ok"))) {
                sb += "URL: \u001B[1m" + result.getUrls()[i] + "\u001B[0m --> ";
                sb += ("\033[31;1m" + term + " " + d + "\u001B[0m");
                String[] tmp = d.split(",");
                Set<Map.Entry<String, String>> set = this.warning_language.entrySet();
                for (String tmp1 : tmp) {
                    for (Map.Entry<String, String> s : set) {
                        if (s.getKey().equals(tmp1)) {
                            sb += "\n" + s.getKey() + ": " + s.getValue() + "\n";
                        }
                    }
                }
                //Response code 204 = true / safe (GET)
                //Response code 200 (POST) = at least one page is unsafe, still some maybe safe
                //Responde code 204 (GET) = all pages are deemed safe
            } else if (result.getHttpCode() == 200 || result.getHttpCode() == 204) {
                sb += "URL: \u001B[1m" + result.getUrls()[i] + "\u001B[0m --> ";
                sb += "\033[32;1m" + term + " safe\u001B[0m\n";
            } else {
                //Any other code = error or unknown
                sb += "\033[31;1m" + searchResponseCode(result.getHttpCode()) + "\u001B[0m";
            }
            i++;

        }
        /*
        *Google Advisory info will be included in the output
        *to comply with google policy for displaying warnings 
        *@ https://developers.google.com/safe-browsing/lookup_guide#UserWarnings
         */
        sb += "\n" + this.getGoogleSpecs();

        return sb;
    }

    /**
     *
     * @param result = LookupResult object
     * @return valid JSON file, promise (: 
     */
    public static JSONObject buildCompactReport(LookupResult result) {

        JSONObject object = new JSONObject();
        JSONArray array = new JSONArray();
        JSONObject hosts = new JSONObject();

        String[] data = result.getData().split("\n");
        int i = 0;
        for (String s : result.getUrls()) {

            hosts.put("host" + i + ":", (new JSONObject().put("url", s)).put("risk", data[i]).put("trusted", result.isTrusted()));
            i++;
        }
        object.put("timestamp", result.getAcessDate()).put("base_url", result.getUrl());
        array.put(hosts);

        /*
        *Google Advisory info will be included in the json output
        *to comply with google policy for warnings @ https://developers.google.com/safe-browsing/lookup_guide#UserWarnings
         */
        object.put("advisory", new Utils().getGoogleSpecs());

        object.put("hosts", array);
        return object;
    }

    /**
     *
     * @param json
     * @return indented JSON string
     */
    public static String prettyJson(JSONObject json) {
        int space = 2;
        return json.toString(space);
    }

    public String getGoogleSpecs() {
        return this.googleSpecs.toString();
    }

    /**
     * The default help String for -h/-help command
     *
     * @return = help text
     */
    public static String help() {
        return new StringBuilder(""
                + "Usage: java -jar <jarfile> [-args]\n"
                + "Where available arguments are:\n"
                + "Simple Usage with GET Method: -get,\n"
                + "\t--- Mandatory ---\n"
                + "\t-k <APIKEY>\n"
                + "\t-u , -URL <Target URL>\n"
                + "\t--- Optional ---\n"
                + "\t[client,appver]\n"
                + "\t-client <client info>\n"
                + "\t-appver <application version>\n"
                + "\t-compact\n\t\tgenerates JSON output\n"
                + "\tto create new output file simply pipe the output: <command> '>' <destination file>\n"
                + "\r\n"
                + "Simple Usage with POST Method: -post,\n"
                + "Note: Post Method usage is limited to 500 lines per request\n"
                + "\t--- Mandatory ---\n"
                + "\t-k <APIKEY>\n"
                + "\t-u , -URL <remote input file>\n"
                + "\t-in <input file>\n"
                + "\t\t <input file> should contain the hostnames/ips in the first column\n"
                + "\t--- Optional ---\n"
                + "\t[client,appver]\n"
                + "\t-client <client info>\n"
                + "\t-appver <application version>\n"
                + "\t-compact\n\t\tgenerates JSON output\n"
                + "\tto create new file simply pipe the output: <command> '>' <destination file>\n"
                + "\r\n").toString();
    }

}
