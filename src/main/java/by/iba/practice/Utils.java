package by.iba.practice;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class Utils {
    public static String sendGETRequest(String path, Map<String, String> params) throws IOException {
        StringBuilder sb = new StringBuilder(path);
        sb.append("?");

        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(URLEncoder.encode(entry.getKey()))
                    .append("=")
                    .append(URLEncoder.encode(entry.getValue()))
                    .append("&");
        }

        URL url = new URL(sb.toString().substring(0, sb.length() - 1));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder result = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            result.append(inputLine);
        }
        in.close();
        return result.toString();
    }

    public static void saveStringToFile(String path, String str) throws FileNotFoundException {
        OutputStream os = new FileOutputStream(path);
        PrintWriter out = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
        out.print(str);
        out.close();
    }
}
