# Google Safebrowsing API v3.1 for Java

### This project implements de Google Safe Browsing Lookup API v3.1 in Java.

Features as of v1.0:
  - Check URL via GET method
  - Check multiple URLs via POST method
  - Check multiple URLs from local or remote file via POST method
  - Build a report in plain text or JSON format
  
### Simple usage:

```java
        /*GET Request Example
        * using a know safe website
        */
        String apiKey = "<Your API KEY />";
        
        Lookup look = new Lookup(apiKey, "GET");
        
        LookupResult result = look.lookupURL("https://www.google.pt/");
        
        //Let's print the trusted status 
        System.out.println(result.isTrusted());
        
        //A known risky website taken from malc0de.com/database/
        LookupResult res = new Lookup(apiKey,"GET").lookupURL("wt8.52zsoft.com/hhbqxgq.exe");
        
        //Build a compact report in JSON format
        System.out.println(Utils.prettyJson(Utils.buildCompactReport(res)));
        
        /*
        * POST Request Examples
        * Websites taken from phishtank.com/ and malc0de.com/database/
        * Used only as an example :) 
        */
        
        //POST Request ex 1 - using an array of strings
        
        LookupResult post_res = 
                new Lookup(apiKey, "POST")
                        .lookupURL(
                                new String[]{"https://www.google.pt/",
                                    "wt8.52zsoft.com/hhbqxgq.exe",
                                    "http://www.win-install.info/dropbox/"});
        
        //Build a bigger threat report with a random term
        String report = new Utils().buildThreatReport(post_res, null);
        System.out.println(report);
        
        //POST Request ex 2 - using a remote file containing the urls to be checked
        LookupResult post_2_res = 
                new Lookup(apiKey, "POST")
                    .lookupURL("https://raw.githubusercontent.com/charlieIT/java-googlesafebrowsing/master/test_urls.txt");
        System.out.println(Utils.prettyJson(Utils.buildCompactReport(post_2_res)));
```
###OUTPUT:
```java
        /*
        * Various options for output and output format
        * .isTrusted(), .getData(), .toString() 
        * .buildThreatReport() returns full text report along with ANSI colors and indentation
        * .buildCompactReport returns a valid JSON object
        *   using .prettyJSON() will indent the JSON output and make it visually pleasant
        */
```
```bash
Usage: java -jar <jarfile with dependencies> [-args]

Usage with GET Method: -get,
	--- Mandatory ---
	-k <APIKEY>
	-u , -URL <Target URL>

Usage with POST Method: -post,
Note: Post Method usage is limited to 500 lines per request
	--- Mandatory ---
	-k <APIKEY>
	-u , -URL <remote input file>
	-in <input file>
		 <input file> should contain the hostnames/ips in the first column
		 
	--- Optional ---
	[client,appver] cmd line arguments Not implemented in v1.0
	-client <client info>
	-appver <application version>
	-compact
		generates JSON output
	to create new output file simply pipe the output: <command> '>' <destination file>
```
```bash
POST
- java -jar <jar file with dependencies> -post -k <key> -u <url or path to remote file>
- java -jar <jar file with dependencies> -post -k <key> -in <local file>

GET
- java -jar <jar file with dependencies> -get -k <key> -u <url>

HELP:
java -jar <jar file with dependencies> -h
java -jar <jar file with dependencies> -get -h
java -jar <jar file with dependencies> -post -h
```
##Dependencies
- Apache Commons: commons-lang3-3.1
- JSON: json-20160212

##Attribution
This project is based of @kivibot work with [Java-SafeBrowsing](https://github.com/kivibot/Java-SafeBrowsing)
