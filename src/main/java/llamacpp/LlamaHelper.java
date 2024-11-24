package llamacpp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;

import etc.JsonTool;

public class LlamaHelper {

    public static String getModelInfo(String serverUrl) throws Exception {
        URL url = new URI(serverUrl + "/props").toURL();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        int status = con.getResponseCode();
        if (status != 200) {
            throw new RuntimeException("Failed to get model info: HTTP error code : " + status);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        con.disconnect();
        String model = etc.JsonTool.extract("model", content.toString());
        return model;
    }

    public static String prompt(URL url, String prompt) throws IOException {

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept", "text/event-stream");
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br, zstd");
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.9,nl;q=0.8,zh-CN;q=0.7,zh;q=0.6");
        connection.setRequestProperty("Connection", "keep-alive");
        connection.setRequestProperty("Content-Length", "755");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Host", "localhost:8088");
        connection.setRequestProperty("Origin", "http://localhost:8088");
        connection.setRequestProperty("Referer", "http://localhost:8088/");
        connection.setRequestProperty("Sec-Fetch-Dest", "empty");
        connection.setRequestProperty("Sec-Fetch-Mode", "cors");
        connection.setRequestProperty("Sec-Fetch-Site", "same-origin");
        connection.setDoOutput(true);

        StringBuilder buffer = new StringBuilder();

        // Sending the request
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = prompt.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Reading the response
        int status = connection.getResponseCode();
        if (status == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("data: ")) {
                    String jsonResponse = line.substring(6);
                    String content = JsonTool.extract("content", jsonResponse);
                    String stop = JsonTool.extract("stop", jsonResponse);
                    System.out.print(content);
                    if (Boolean.parseBoolean(stop)) {
                        break;
                    } else {
                        buffer.append(content);
                    }
                }
            }
            in.close();

            // Parsing the JSON response
        } else {
            System.out.println("Error: " + status + " - " + connection.getResponseMessage());
        }
        connection.disconnect();
        return buffer.toString().replaceAll("\\\\n", System.lineSeparator());
    }

    public static void main(String[] args) {
        // Example usage
        String jsonResponse = "{\"columns\":[{\"column\":\"Name\"}, {\"column\":\"Age\"}]}";
        List<String> columns = JsonTool.extractAllArrayElements("columns", jsonResponse);

        for (String column : columns) {
            System.out.println(column);
        }
    }
}
