package llamarag;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import etc.JsonTool;
import llamacpp.LlamaHelper;

public class LlamaRAG {

    ContextInformationStorage cis;
    String model;
    TreeSet<String> bibtexSegments = new TreeSet<String>();
    StringBuffer citations = new StringBuffer();

    public LlamaRAG() {
        try {
            model = LlamaHelper.getModelInfo("http://localhost:8086");
            cis = new ContextInformationStorage(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getCitations() {
        return citations.toString();
    }

    public HttpURLConnection evaluate(String datadescription) throws URISyntaxException, IOException {

        float embeddings[] = LlamaEmbeddings.getEmbeddings(datadescription, false);
        int context[] = cis.findClosestContext(embeddings, 3, "gender");
        StringBuffer contextmaterial = new StringBuffer();
        for (int i = 0; i < context.length; i++) {
            int index = context[i];
            StringBuffer paragraph = new StringBuffer();
            for (int j = Math.max(0, index - 2); j < Math.min(index + 2, cis.size()); j++) {
                if (cis.getContextFile(j).equals(cis.getContextFile(index))) {
                    paragraph.append(cis.getContextText(j) + " ");
                }
            }
            if (!cis.getBibtex(index).isBlank()) {
                System.out.println(i + ": " + cis.getContextFile(index) + " --- " + cis.getContextText(index));
                System.out.println(parseBibTeX(cis.getBibtex(index)));
                String bibtexSegment = cis.getBibtex(index);
                if (!bibtexSegments.contains(bibtexSegment)) {
                    bibtexSegments.add(bibtexSegment);
                    citations.append(formatBibTeXToHTML(bibtexSegment));
                }
            }
            contextmaterial.append(paragraph.toString().replaceAll("[\\n\\t\\r]+", " ").replaceAll("\\s+", " ") + " ");
        }

        // System.exit(0);
        System.out.println("context: " + contextmaterial.toString().replaceAll("\\s+", " "));

        // URL of the endpoint
        URL url = new URI("http://localhost:8088/completion").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        String grammar = "root ::= \\\"{\\\" fields \\\"}\\\"\\nfields ::= field (\\\",\\\" field)*\\nfield ::= string \\\":\\\" value\\nstring ::= \\\"\\\\\\\"\\\" [^\\\\n\\\\r\\\\t\\\\\\\"]* \\\"\\\\\\\"\\\"\\nvalue ::= string | number | object | array\\nnumber ::= [0-9]+\\nobject ::= \\\"{\\\" fields \\\"}\\\"\\narray ::= \\\"[\\\" values \\\"]\\\"\\nvalues ::= value (\\\",\\\" value)*";

        StringBuffer systemPrompt = new StringBuffer();
        systemPrompt.append("You are an AI language model helping a user. ");
        systemPrompt.append(
                "Your task is to identify variables in a dataset that may introduce a gender bias and generate responses in JSON format. ");
        systemPrompt.append("Make sure every column is included in the nested segment. ");
        systemPrompt.append("Your response must be complete and in valid JSON. ");
        systemPrompt.append(
                "Always respond with a rationale, whenever a variable is considered a risk for creating a gender bias. ");
        systemPrompt.append("\\\\r\\\\n\\\\r\\\\n");
        systemPrompt.append(
                "You have the following quoted context information, that you may optionally use for helping the user:\\r\\n");
        systemPrompt.append("“" + JsonTool.escapeJsonString(contextmaterial.toString()) + "”");
        systemPrompt.append("Ensure that all responses strictly follow the JSON structure provided. ");
        systemPrompt.append("Here is the structure you should use:\\\\r\\\\n\\\\r\\\\n");
        systemPrompt.append(
                "{\\\"dataset\\\":{\\\"title\\\":\\\"\\\"},\\\"columns\\\":[{\\\"column\\\":\\\"string\\\",\\\"description\\\":\\\"string\\\",\\\"genderbias\\\":\\\"string\\\",\\\"rationale\\\":\\\"string\\\"},{\\\"column\\\":\\\"string\\\",\\\"description\\\":\\\"string\\\",\\\"genderbias\\\":\\\"string\\\",\\\"rationale\\\":\\\"string\\\"},{\\\"column\\\":\\\"string\\\",\\\"description\\\":\\\"string\\\",\\\"genderbias\\\":\\\"string\\\",\\\"rationale\\\":\\\"string\\\"}]}");

        StringBuffer prompt = new StringBuffer();
        prompt.append("USER: Give me JSON that describes all fields from this dataset description, ");
        prompt.append(
                "and assess for each column whether it may cause a gender bias when used for automated decision making. ");
        prompt.append(
                "Use the “genderbias” field to state whether the variable either has “no risk for gender bias”, ");
        prompt.append("“low risk for gender bias”, “moderate risk for gender bias”, “high risk for gender bias”, ");
        prompt.append("or “is a direct representation” for gender. ");
        prompt.append(
                "Use the “rationale” field to explain why or how the variable could cause a gender bias, or not. ");
        prompt.append("Here follows the description of the dataset:\\r\\n\\r\\n");
        prompt.append("\\\"" + datadescription + "\\\"");
        prompt.append("\\r\\n\\r\\nAI LANGUAGE MODEL: ");

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

        System.out.println(jsonInputString.toString());

        // Sending the request
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        return connection;
    }

    public static String parseBibTeX(String bibtex) {
        Map<String, String> fields = new HashMap<>();

        // Remove the starting and ending braces
        bibtex = bibtex.substring(bibtex.indexOf('{') + 1, bibtex.lastIndexOf('}'));

        // Split into key-value pairs
        String[] pairs = bibtex.split(",\\s*(?=[^{}]*\\{[^{}]*})");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim().replaceAll("[{}]", "");
                fields.put(key, value);
            }
        }

        // Create a formatted citation string
        String author = fields.get("author");
        String title = fields.get("title");
        String journal = fields.get("journal");
        String year = fields.get("year");
        String doi = fields.get("doi");

        return String.format("%s. %s. %s. %s. DOI: %s", author, title, journal, year, doi);
    }

    // Converts Bibtex to APA style citation in HTML
    public static String formatBibTeXToHTML(String bibtex) {
        Map<String, String> fields = new HashMap<>();

        // Remove the starting and ending braces
        bibtex = bibtex.substring(bibtex.indexOf('{') + 1, bibtex.lastIndexOf('}'));

        // Split into key-value pairs
        String[] pairs = bibtex.split(",\\s*(?=[^{}]*\\{[^{}]*})");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim().replaceAll("[{}]", "");
                fields.put(key, value);
            }
        }

        // Extract the fields
        String author = fields.containsKey("author") ? fields.get("author") : "";
        String title = fields.containsKey("title") ? fields.get("title") : "";
        String journal = fields.containsKey("journal") ? " <i>" + fields.get("journal") + "</i>." : "";
        String year = fields.containsKey("year") ? " (" + fields.get("year") + ")." : "";
        author = formatAuthorsForAPA(author);
        if (fields.containsKey("doi")) {
            String doi = fields.get("doi");
            String url = "https://doi.org/" + doi;
            return String.format("<p>%s%s <i>%s</i>.%s DOI: <a href=\"%s\">%s</a></p>", author, year, title, journal,
                    url, doi);
        } else {
            return String.format("<p>%s%s <i>%s</i>.%s</p>", author, year, title, journal);
        }
    }

    private static String formatAuthorsForAPA(String authors) {
        // Split authors and format them
        String[] authorArray = authors.split(" and ");
        StringBuilder formattedAuthors = new StringBuilder();

        for (int i = 0; i < authorArray.length; i++) {
            String[] nameParts = authorArray[i].split(", ");
            if (nameParts.length == 2) {
                formattedAuthors.append(nameParts[1]).append(" ").append(nameParts[0]);
            } else {
                formattedAuthors.append(nameParts[0]);
            }
            if (i < authorArray.length - 1) {
                formattedAuthors.append(", ");
            }
        }

        return formattedAuthors.toString();
    }

}
