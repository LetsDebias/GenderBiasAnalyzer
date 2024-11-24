package llamacpp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class LlamaPrompter {

	public static void main(String[] args) {
		String datadescription = "Detailed data description of Credit Card Approval dataset:\\n| **Feature Name** | **Description** | **Remarks** | \\n| --- | --- |\\n| ID | Client Number | | \\n| CODE_GENDER | Gender | | \\n| FLAG_OWN_CAR | Is there a car | | \\n| FLAG_OWN_REALTY | Is there a property | | \\n| CNT_CHILDREN | Number of Children | | \\n| AMT_INCOME_TOTAL | Annual Income | | \\n| NAME_EDUCATION_TYPE | Education Level | | \\n| NAME_FAMILY_STATUS | Marital Status | | \\n| NAME_HOUSING_TYPE | Way of Living | | \\n| DAYS_BIRTH | Age in days | | \\n| DAYS_EMPLOYED | Duration of work in days | | \\n| FLAG_MOBIL | Is there a mobile phone | | \\n| FLAG_WORK_PHONE | Is there a work phone | | \\n| FLAG_PHONE | Is there a phone | | \\n| FLAG_EMAIL | Is there an email | | \\n| JOB | Job | | \\n| BEGIN_MONTHS | Record month | The month of the extracted data is the starting point, backwards, 0 is the current month, -1 is the previous month, and so on | \\n| STATUS | Status | 0: 1-29 days past due 1: 30-59 days past due 2: 60-89 days overdue 3: 90-119 days overdue 4: 120-149 days overdue 5: Overdue or bad debts, write-offs for more than 150 days C: paid off that month X: No loan for the month | |\\n| TARGET | Target | Risk user are marked as '1', else are '0' | | ";
		String response = LlamaPrompter.evaluate(datadescription);
		System.out.println("Response: " + response);
	}

	public static String evaluate(String datadescription) {
		String response = "";
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

			String grammar = "root ::= \\\"{\\\" fields \\\"}\\\"\\nfields ::= field (\\\",\\\" field)*\\nfield ::= string \\\":\\\" value\\nstring ::= \\\"\\\\\\\"\\\" [^\\\\n\\\\r\\\\t\\\\\\\"]* \\\"\\\\\\\"\\\"\\nvalue ::= string | number | object | array\\nnumber ::= [0-9]+\\nobject ::= \\\"{\\\" fields \\\"}\\\"\\narray ::= \\\"[\\\" values \\\"]\\\"\\nvalues ::= value (\\\",\\\" value)*";

			StringBuffer systemPrompt = new StringBuffer();
			systemPrompt.append("You are an AI language model helping a user. ");
			systemPrompt.append(
					"Your task is to identify proxy variables in a dataset and generate responses in JSON format. ");
			systemPrompt.append("Make sure every column is included in the nested segment. ");
			systemPrompt.append("Your response must be complete and in valid JSON. ");
			systemPrompt
					.append("Always respond with a rationale, whenever a variable is considered a proxy variable. ");
			systemPrompt.append("Ensure that all responses strictly follow the JSON structure provided. ");
			systemPrompt.append("Here is the structure you should use:\\\\r\\\\n\\\\r\\\\n");
			systemPrompt.append(
					"{\\\"dataset\\\":{\\\"title\\\":\\\"\\\"},\\\"columns\\\":[{\\\"column\\\":\\\"string\\\",\\\"description\\\":\\\"string\\\",\\\"genderproxy\\\":\\\"string\\\",\\\"rationale\\\":\\\"string\\\"},{\\\"column\\\":\\\"string\\\",\\\"description\\\":\\\"string\\\",\\\"genderproxy\\\":\\\"string\\\",\\\"rationale\\\":\\\"string\\\"},{\\\"column\\\":\\\"string\\\",\\\"description\\\":\\\"string\\\",\\\"genderproxy\\\":\\\"string\\\",\\\"rationale\\\":\\\"string\\\"}]}");

			StringBuffer prompt = new StringBuffer();
			prompt.append(systemPrompt);
			prompt.append("USER: Give me JSON that describes all fields from this dataset description, ");
			prompt.append("and assess for each column whether it may be a proxy variable for bias. ");
			prompt.append(
					"Use the “genderproxy” field to state whether the variable either has “no risk for being a proxy”, ");
			prompt.append(
					"“low risk for being a proxy”, “moderate risk for being a proxy”, “high risk for being a proxy”, ");
			prompt.append("or “is a direct representation” for gender. ");
			prompt.append(
					"Use the “rationale” field to explain why the variable could function as proxy for gender, or not. ");
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
			jsonInputString.append("\"prompt\": \"" + prompt + "\"}");

			System.out.println(jsonInputString.toString());

			// Sending the request
			try (OutputStream os = connection.getOutputStream()) {
				byte[] input = jsonInputString.toString().getBytes("utf-8");
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
				response = buffer.toString().replaceAll("\\\\n", System.lineSeparator());
			} else {
				System.out.println("Error: " + status + " - " + connection.getResponseMessage());
			}

			connection.disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response.toString();
	}

	private static String extract(String field, String jsonResponse) {
		String contentKey = "\"" + field + "\":\"";
		int startIndex = jsonResponse.indexOf(contentKey) + contentKey.length();
		StringBuilder result = new StringBuilder();

		for (int i = startIndex; i < jsonResponse.length(); i++) {
			char currentChar = jsonResponse.charAt(i);

			// Check if we've found the end of the value
			if (currentChar == '"' && jsonResponse.charAt(i - 1) != '\\') {
				break;
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

}
