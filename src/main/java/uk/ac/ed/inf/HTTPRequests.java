package uk.ac.ed.inf;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpClient;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


public class HTTPRequests {
    /**
     *doGETRequest - Retrieves the menu and returns it as a String
     *@param endpoint - String, The url which the getRequest will fetch
     *@returns textBuilder.toString() - String,The String format of the menu.json
     *@throws  Exception - Exception, thrown if there is any issues connecting to the url
     */
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    public static String doGETRequest(String endpoint) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(endpoint).openConnection();
            InputStream inputStream = connection.getInputStream();
            StringBuilder textBuilder = new StringBuilder();
            try (Reader reader = new BufferedReader(new InputStreamReader
                    (inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
                int c = 0;
                while ((c = reader.read()) != -1) {
                    textBuilder.append((char) c);
                }
            }
            return textBuilder.toString();
        }
        catch(Exception e){return "error connecting to webage";}

    }
}
