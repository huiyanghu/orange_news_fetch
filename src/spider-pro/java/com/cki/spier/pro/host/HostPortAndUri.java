   
  
  
  
  

package com.cki.spier.pro.host;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

   
  
 
  
  
public class HostPortAndUri {

                                                                                                                       

    private static final Pattern URL_PATTERN = Pattern.compile("(http(s)?)://([\\w\\d\\-\\.]+)(:([0-9]+))?(/.*)*");

                                                                                                                       

    private String scheme;
    private String host;
    private int port;
    private String uri;

                                                                                                                       

    public HostPortAndUri(String scheme, String host, int port, String uri) {

        this.scheme = scheme;
        this.host = host;
        this.port = port;
        this.uri = uri;
    }

    public HostPortAndUri(HostPortAndUri that) {

        this.scheme = that.scheme;
        this.host = that.host;
        this.port = that.port;
        this.uri = that.uri;
    }

                                                                                                                       

    public static HostPortAndUri splitUrl(String url) {

        Matcher m = URL_PATTERN.matcher(url);

        if (m.find()) {
            return new HostPortAndUri(m.group(1), m.group(3), m.group(4) == null ? 80 : Integer.parseInt(m.group(5)),
                                      m.group(6) == null ? "/" : m.group(6));
        }

        return null;
    }

                                                                                                                       

    public String asHostAndPort() {

        StringBuilder sb = new StringBuilder().append(this.host);

        if (this.port != 80) {

            sb.append(':').append(this.port);

        }

        return sb.toString();
    }

    public String asUrl() {

        return new StringBuilder().append(this.scheme).append("://").append(this.host).append(':').append(
            this.port).append(this.uri).toString();
    }

    public boolean isHttps() {
        return this.scheme.equals("https");
    }

                                                                                                                       

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

                                                                                                                       

    @Override
    public String toString() {

        return new StringBuilder().append("HostPortAndUri{").append(host).append(':').append(port).append(uri == null
                                          ? "" : uri).append('}').toString();
    }
}
