package llamarag;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import etc.JsonTool;
import etc.StringTool;

public class LlamaEmbeddings {

    public static void main(String[] args) {
        String context = "Why is the sky blue?";
        float embeddings[];
        try {
            embeddings = LlamaEmbeddings.getEmbeddings(context, true);
            System.out.println("Vector size: " + embeddings.length);
            System.out.println("Response: " + StringTool.asString(embeddings, ", "));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static float[] getEmbeddings(String context, boolean cleanup) throws URISyntaxException, IOException {

        // URL of the endpoint
        URL url = new URI("http://localhost:8086/embedding").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Setting the request method and headers
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

        if (cleanup) {
            context = JsonTool.escapeJsonString(context);
        }
        StringBuffer content = new StringBuffer(context);

        // JSON payload
        StringBuffer jsonInputString = new StringBuffer();
        jsonInputString.append("{\"content\": \"");
        jsonInputString.append(content);
        jsonInputString.append("\"}");
        System.out.println(jsonInputString.toString());

        // Sending the request
        OutputStream os = connection.getOutputStream();
        byte[] input = jsonInputString.toString().getBytes("utf-8");
        os.write(input, 0, input.length);

        // Reading the response
        int status = connection.getResponseCode();
        if (status == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            String line = in.readLine();
            if (line.startsWith("{\"embedding\":")) {
                String contentValue = extract("embedding", line);
                float array[] = parseEmbedding(contentValue);
                return array;
            }
            in.close();
        } else {
            System.err.println("Error: " + status + " - " + connection.getResponseMessage());
        }

        connection.disconnect();

        return new float[] {};
    }

    private static String extract(String field, String jsonResponse) {
        String contentKey = "\"" + field + "\":\"";
        int startIndex = jsonResponse.indexOf(contentKey) + contentKey.length();
        StringBuilder result = new StringBuilder();

        boolean qouted = false;
        for (int i = startIndex + 1; i < jsonResponse.length(); i++) {
            char currentChar = jsonResponse.charAt(i);

            // Check if we've found the start or the end of the value
            if (currentChar == '"' && jsonResponse.charAt(i - 1) != '\\') {
                if (qouted) {
                    break;
                } else {
                    qouted = true;
                }
            }

            // Append the character to the result
            if (currentChar == '\\' && i + 1 < jsonResponse.length()) {
                char nextChar = jsonResponse.charAt(i + 1);
                if (nextChar == '"') {
                    result.append('"');
                    i++; // Skip the next character since it is an escaped quote
                } else if (nextChar == '\\') {
                    result.append('\\');
                    i++; // Skip the next character since it is an escaped backslash
                } else {
                    result.append(currentChar);
                }
            } else {
                result.append(currentChar);
            }
        }

        return result.toString();
    }

    public static String getBetween(String text, char left, char right) {
        int i = text.indexOf(left) + 1;
        if (i < 0)
            return "";
        int j = text.indexOf(right, i + 1);
        if (j < 0 || j < i)
            return "";
        return text.substring(i, j);
    }

    public static float[] parseEmbedding(String embeddingString) {
        // Remove the brackets and split the string by commas
        String[] stringValues = getBetween(embeddingString, '[', ']').split(",");

        // Create a float array to hold the parsed values
        float[] embeddingArray = new float[stringValues.length];

        // Parse each string value to a float and store it in the array
        for (int i = 0; i < stringValues.length; i++) {
            embeddingArray[i] = Float.parseFloat(stringValues[i].trim());
        }

        return embeddingArray;
    }

}
