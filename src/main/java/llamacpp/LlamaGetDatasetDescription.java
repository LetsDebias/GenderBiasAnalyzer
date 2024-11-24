package llamacpp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import etc.JsonTool;
import jakarta.servlet.http.HttpServletResponse;

public class LlamaGetDatasetDescription {

    static LlamaGetDatasetDescription myself = new LlamaGetDatasetDescription();

    public static DatasetDescription evaluate(String datadescription, HttpServletResponse servletResponse)
            throws URISyntaxException, IOException {

        // URL of the endpoint
        URL url = new URI("http://localhost:8088/completion").toURL();

        String grammar = "root ::= \\\"{\\\" fields \\\"}\\\"\\nfields ::= field (\\\",\\\" field)*\\nfield ::= string \\\":\\\" value\\nstring ::= \\\"\\\\\\\"\\\" [^\\\\n\\\\r\\\\t\\\\\\\"]* \\\"\\\\\\\"\\\"\\nvalue ::= string | number | object | array\\nnumber ::= [0-9]+\\nobject ::= \\\"{\\\" fields \\\"}\\\"\\narray ::= \\\"[\\\" values \\\"]\\\"\\nvalues ::= value (\\\",\\\" value)*";

        StringBuffer systemPrompt = new StringBuffer();
        systemPrompt.append("You are an AI language model helping a user. ");
        systemPrompt.append(
                "Your task is to identify and describe the variables in a dataset and generate responses in JSON format. ");
        systemPrompt.append("Make sure every column is included in the nested segment. ");
        systemPrompt.append("Include in your response the title of the dataset. ");
        systemPrompt.append("Use the context field to describe the context of the dataset, ");
        systemPrompt.append(
                "which may include the source of the data and the intended purpose if that is explained in the given description.");
        systemPrompt.append("Your response must be complete and in valid JSON. ");
        systemPrompt.append("Ensure that all responses strictly follow the JSON structure provided. ");
        systemPrompt.append("Here is the structure you should use:\\\\r\\\\n\\\\r\\\\n");
        systemPrompt.append(
                "{\\\"dataset\\\":{\\\"title\\\":\\\"string\\\", \\\"context\\\":\\\"string\\\"},\\\"columns\\\":[{\\\"column\\\":\\\"string\\\",\\\"description\\\":\\\"string\\\"},{\\\"column\\\":\\\"string\\\",\\\"description\\\":\\\"string\\\"},{\\\"column\\\":\\\"string\\\",\\\"description\\\":\\\"string\\\"}]}");

        StringBuffer prompt = new StringBuffer();
        prompt.append(
                "USER: Give me JSON that describes the title, context and all fields from this dataset description. ");
        prompt.append("Here follows the description of the dataset:\\r\\n\\r\\n");
        prompt.append("\\\"" + datadescription + "\\\"");
        prompt.append("\\r\\n\\r\\nAI LANGUAGE MODEL: ");

        // JSON payload
        StringBuffer jsonInputString = new StringBuffer();
        jsonInputString.append("{");
        jsonInputString.append("\"stream\": true,");
        jsonInputString.append("\"n_predict\": 2048,");
        jsonInputString.append("\"temperature\": 0.7,");
        jsonInputString.append("\"stop\": [\"</s>\", \"AI LANGUAGE MODEL:\", \"USER:\"],");
        jsonInputString.append("\"repeat_last_n\": 256,");
        jsonInputString.append("\"repeat_penalty\": 1,");
        jsonInputString.append("\"penalize_nl\": false,");
        jsonInputString.append("\"dry_multiplier\": 0,");
        jsonInputString.append("\"dry_base\": 1.75,");
        jsonInputString.append("\"dry_allowed_length\": 2,");
        jsonInputString.append("\"dry_penalty_last_n\": -1,");
        jsonInputString.append("\"top_k\": 40,");
        jsonInputString.append("\"top_p\": 1,");
        jsonInputString.append("\"min_p\": 0.05,");
        jsonInputString.append("\"xtc_probability\": 0,");
        jsonInputString.append("\"xtc_threshold\": 0.1,");
        jsonInputString.append("\"typical_p\": 1,");
        jsonInputString.append("\"presence_penalty\": 0,");
        jsonInputString.append("\"frequency_penalty\": 0,");
        jsonInputString.append("\"mirostat\": 0,");
        jsonInputString.append("\"mirostat_tau\": 5,");
        jsonInputString.append("\"mirostat_eta\": 0.1,");
        jsonInputString.append("\"grammar\": \"" + grammar + "\",");
        jsonInputString.append("\"n_probs\": 0,");
        jsonInputString.append("\"min_keep\": 0,");
        jsonInputString.append("\"image_data\": [],");
        jsonInputString.append("\"cache_prompt\": true,");
        jsonInputString.append("\"api_key\": \"\",");
        jsonInputString.append("\"prompt\": \"" + systemPrompt + prompt + "\"}");

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
            byte[] input = jsonInputString.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Reading the response
        int status = connection.getResponseCode();
        if (status == 200) {
            boolean titleShown = false;
            String lastVariableShown = "";
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
                        if (!buffer.toString().isBlank()) {
                            List<String> elements = JsonTool.extractAllArrayElements("columns", buffer.toString());
                            if (!elements.isEmpty()) {
                                String lastColumn = elements.getLast();
                                if (lastColumn != null) {
                                    String variableName = JsonTool.extract("column", lastColumn);
                                    if (!lastVariableShown.equals(variableName)) {
                                        servletResponse.getWriter().write("<script>setMessage(\"Identified variable: "
                                                + variableName + "\");</script>" + System.lineSeparator());
                                        servletResponse.flushBuffer();
                                        lastVariableShown = variableName;
                                    }
                                }
                            } else {
                                String title = JsonTool.extract("title", buffer.toString());
                                String context = JsonTool.extract("context", buffer.toString());
                                if (!titleShown && !title.isBlank() && !context.isBlank()) {
                                    servletResponse.getWriter()
                                            .write("<script>setMessage(\"Dataset: " + title
                                                    + "\");</script>");
                                    servletResponse.flushBuffer();
                                    titleShown = true;
                                }
                            }
                        }
                    }
                }
            }
            in.close();

            // Parsing the JSON response
        } else {
            System.out.println("Error: " + status + " - " + connection.getResponseMessage());
        }
        connection.disconnect();
        String response = buffer.toString().replaceAll("\\\\n", System.lineSeparator());

        DatasetDescription datasetDescription = new DatasetDescription();
        datasetDescription.title = JsonTool.extract("title", response);
        datasetDescription.context = JsonTool.extract("context", response);
        for (String element : JsonTool.extractAllArrayElements("columns", response)) {
            datasetDescription.variableNames.add(JsonTool.extract("column", element));
            datasetDescription.variableDescriptions.add(JsonTool.extract("description", element));
            servletResponse.flushBuffer();
        }
        System.out.println(datasetDescription.toString());

        return datasetDescription;
    }

}
