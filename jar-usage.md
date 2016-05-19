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
	[client,appver,compact]
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

HELP
java -jar <jar file with dependencies> -h
java -jar <jar file with dependencies> -get -h
java -jar <jar file with dependencies> -post -h
```
