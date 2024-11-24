package llamacpp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class CheckDataset {

	public static void main(String[] args) {
		try {

			// URL of the endpoint
			URL url = new URI("http://localhost:8088/completion").toURL();
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

			// JSON payload
			String jsonInputString = "{" + "\"stream\": true," + "\"n_predict\": 400," + "\"temperature\": 0.7,"
					+ "\"stop\": [\"</s>\", \"Llama:\", \"User:\"]," + "\"repeat_last_n\": 256,"
					+ "\"repeat_penalty\": 1.18," + "\"penalize_nl\": false," + "\"dry_multiplier\": 0,"
					+ "\"dry_base\": 1.75," + "\"dry_allowed_length\": 2," + "\"dry_penalty_last_n\": -1,"
					+ "\"top_k\": 40," + "\"top_p\": 0.95," + "\"min_p\": 0.05," + "\"xtc_probability\": 0,"
					+ "\"xtc_threshold\": 0.1," + "\"typical_p\": 1," + "\"presence_penalty\": 0,"
					+ "\"frequency_penalty\": 0," + "\"mirostat\": 0," + "\"mirostat_tau\": 5,"
					+ "\"mirostat_eta\": 0.1," + "\"grammar\": \"\"," + "\"n_probs\": 0," + "\"min_keep\": 0,"
					+ "\"image_data\": []," + "\"cache_prompt\": true," + "\"api_key\": \"\","
					+ "\"prompt\": \"This is a conversation between User and Llama, a friendly chatbot. Llama is helpful, kind, honest, good at writing, and never fails to answer any requests immediately and with precision.\\n\\nUser: In what country lies the city of Amsterdam?\\nLlama:\""
					+ "}";

			// Sending the request
			try (OutputStream os = connection.getOutputStream()) {
				byte[] input = jsonInputString.getBytes("utf-8");
				os.write(input, 0, input.length);
			}

			// Reading the response
			int status = connection.getResponseCode();
			if (status == 200) {
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
				StringBuilder buffer = new StringBuilder();
				String line;
				while ((line = in.readLine()) != null) {
					if (line.startsWith("data: ")) {
						String jsonResponse = line.substring(6);
						String content = extract("content", jsonResponse);
						String stop = extract("stop", jsonResponse);
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
				String response = buffer.toString().replaceAll("\\\\n", System.lineSeparator());
				System.out.println();
				System.out.println();
				System.out.println("Response: " + response.toString());
			} else {
				System.out.println("Error: " + status + " - " + connection.getResponseMessage());
			}

			connection.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String extract(String field, String jsonResponse) {
		String contentKey = "\"" + field + "\":\"";
		int startIndex = jsonResponse.indexOf(contentKey) + contentKey.length();
		int endIndex = jsonResponse.indexOf("\"", startIndex);
		return jsonResponse.substring(startIndex, endIndex);
	}

}
