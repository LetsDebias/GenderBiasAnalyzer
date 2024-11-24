package etc;

import java.util.List;
import java.util.Vector;

public class JsonTool {

    public static String extract(String field, String jsonResponse) {
        StringBuilder result = new StringBuilder();
        String contentKey = "\"" + field + "\":\"";
        int fieldPosition = jsonResponse.indexOf(contentKey);
        if (fieldPosition >= 0) {
            int startIndex = fieldPosition + contentKey.length();

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
        }
        return result.toString();
    }

    public static List<String> extractAllArrayElements(String field, String jsonResponse) {
        List<String> list = new Vector<>();

        String contentKey = "\"" + field + "\":[";
        int startIndex = jsonResponse.indexOf(contentKey);

        if (startIndex == -1) {
            return list; // field not found
        }

        startIndex += contentKey.length();
        boolean insideObject = false;
        StringBuilder currentObject = new StringBuilder();

        for (int i = startIndex; i < jsonResponse.length(); i++) {
            char currentChar = jsonResponse.charAt(i);

            if (currentChar == '{') {
                if (!insideObject) {
                    insideObject = true;
                    currentObject = new StringBuilder();
                }
            }

            if (insideObject) {
                currentObject.append(currentChar);
            }

            if (currentChar == '}') {
                if (insideObject) {
                    list.add(currentObject.toString());
                    insideObject = false;
                }
            }
        }

        return list;
    }

    /**
     * Escapes characters that cannot be directly used in a JavaScript template
     * literal.
     * 
     * @param input The original string to be escaped.
     * @return The escaped string.
     */
    public static String escapeJavaScriptTemplateLiteral(String input) {
        if (input == null) {
            return null;
        }

        StringBuilder escaped = new StringBuilder(input.length());

        for (char c : input.toCharArray()) {
            switch (c) {
            case '`':
                escaped.append("\\`");
                break;
            case '$':
                escaped.append("\\$");
                break;
            case '\\':
                escaped.append("\\\\");
                break;
            default:
                escaped.append(c);
                break;
            }
        }

        return escaped.toString();
    }

    /**
     * Escapes characters that cannot be directly used in a JSON String.
     * 
     * @param input The original string to be escaped.
     * @return The escaped string.
     */
    public static String escapeJsonString(String input) {
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            switch (c) {
            case '"':
                sb.append("\\\"");
                break;
            case '\\':
                sb.append("\\\\");
                break;
            case '\b':
                sb.append("\\b");
                break;
            case '\f':
                sb.append("\\f");
                break;
            case '\n':
                sb.append("\\n");
                break;
            case '\r':
                sb.append("\\r");
                break;
            case '\t':
                sb.append("\\t");
                break;
            default:
                // Check if the character is a control character that needs to be escaped
                if (c < 0x20 || c > 0x7E) {
                    sb.append(String.format("\\u%04x", (int) c));
                } else {
                    sb.append(c);
                }
                break;
            }
        }
        return sb.toString();
    }
}
