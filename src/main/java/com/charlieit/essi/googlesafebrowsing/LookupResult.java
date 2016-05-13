
package com.charlieit.essi.googlesafebrowsing;

/**
 * @author Luís Oliveira
 * charlieIT@github.com
 */
public class LookupResult {
    
    private final String acessDate;
    private final String url;
    private final int httpCode;
    private final String data;
    private final boolean isTrusted;
    private final String[] urls;

    /**
     *
     * @param url = full encoded Request URL
     * @param urls = the array of urls to check
     * @param returnCode = http response code
     * @param date = http connection timestamp
     * @param data = retrieved http response data
     * @param trusted = whether website is trusted (true = trusted, false = untrusted or error http response code
     */
    public LookupResult(String url, String[] urls, int returnCode, String date, String data, boolean trusted) {
        this.url = url;
        this.httpCode = returnCode;
        this.data = data;
        this.isTrusted = trusted;
        this.urls = urls;
        this.acessDate = date;
    }


    public String[] getUrls() {
        return urls;
    }
    
    public String getUrl() {
        return url;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public String getData() {
        return data;
    }

    public boolean isTrusted() {
        return isTrusted;
    }

    public String getAcessDate() {
        return acessDate;
    }
    
    @Override
    public String toString() {
        return "LookupResult{" + "url=" + url + ", returnCode=" + httpCode + ", data=" + data + ", trusted=" + isTrusted + '}';
    }
    
}
