package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import etc.JsonTool;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import llamacpp.DatasetDescription;
import llamacpp.LlamaGetDatasetDescription;
import llamarag.LlamaRAG;

public class FormHandlerServlet extends HttpServlet {

    private static final long serialVersionUID = -664745404599662263L;
    LlamaRAG llamaRAG;

    public FormHandlerServlet(LlamaRAG llamaRAG) {
        this.llamaRAG = llamaRAG;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        response.getWriter().write(getWaitPage());
        response.flushBuffer();

        HashMap<String, String> formData = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String paramValue = request.getParameter(paramName);
            formData.put(paramName, paramValue);
        }

        handlePost(formData, response);
    }

    public void handlePost(HashMap<String, String> formData, HttpServletResponse response) throws IOException {

        if (formData.containsKey("datasetTitle") && !formData.get("datasetTitle").isEmpty()
                && formData.containsKey("datasetSample") && !formData.get("datasetSample").isEmpty()) {

            String submittedForm = formData.get("datasetSample");
            submittedForm = submittedForm.replaceAll("\r\n", "\n").replaceAll("\r", "\n"); // simplify line breaks
            submittedForm = JsonTool.escapeJsonString(submittedForm);

            DatasetDescription datasetDescription;
            try {

                datasetDescription = LlamaGetDatasetDescription.evaluate(submittedForm, response);
                List<String> processedColumns = new Vector<>();
                HttpURLConnection connection = llamaRAG
                        .evaluate(JsonTool.escapeJsonString(datasetDescription.toString()));

                response.getWriter()
                        .write("<script>document.head.innerHTML=`"
                                + JsonTool.escapeJavaScriptTemplateLiteral(getHeader()) + "`</script>"
                                + System.lineSeparator());
                response.getWriter().write("<script>document.body.innerHTML=`");
                response.getWriter()
                        .write(JsonTool.escapeJavaScriptTemplateLiteral(
                                getBody(datasetDescription.title, datasetDescription.context,
                                        datasetDescription.variableNames, datasetDescription.variableDescriptions)));
                response.getWriter().write("`;</script>" + System.lineSeparator());
                response.flushBuffer();

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
                StringBuilder buffer = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.startsWith("data: ")) {
                        String jsonResponse = line.substring(6);
                        String content = JsonTool.extract("content", jsonResponse);
                        String stop = JsonTool.extract("stop", jsonResponse);
                        if (Boolean.parseBoolean(stop)) {
                            break;
                        } else {
                            buffer.append(content);
                            response.flushBuffer();
                            List<String> list = JsonTool.extractAllArrayElements("columns", buffer.toString());
                            if (list.size() > processedColumns.size()) {
                                int currentSize = processedColumns.size();
                                int newSize = list.size();
                                for (int i = currentSize; i < newSize; i++) {
                                    String json = list.get(i);
                                    String variableName = JsonTool.escapeJsonString(JsonTool.extract("column", json));
                                    String genderbias = JsonTool.escapeJsonString(JsonTool.extract("genderbias", json));
                                    String rationale = JsonTool.escapeJsonString(JsonTool.extract("rationale", json));
                                    int index = datasetDescription.variableNames.indexOf(variableName);
                                    response.getWriter()
                                            .write("<script>document.getElementById(\"variable_" + index
                                                    + "\").innerHTML=\"<b>" + genderbias + "</b><br>" + rationale
                                                    + "\";document.getElementById(\"variable_" + index + "\")"
                                                    + ".scrollIntoView({ behavior: \"smooth\", block: \"center\"});"
                                                    + "</script>" + System.lineSeparator());
                                    response.flushBuffer();
                                    processedColumns.add(list.get(i));
                                    System.out.println(json);
                                }
                            }
                        }
                    }
                }
                in.close();
                connection.disconnect();
            } catch (URISyntaxException e) {
                e.printStackTrace(response.getWriter());
            } catch (IOException e) {
                e.printStackTrace(response.getWriter());
            }

        } else {
            response.getWriter().println("Both fields must be filled out.");
        }
    }

    private String getHeader() {
        return """
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                  <title>Detecting Gender Bias in Datasets</title>
                  <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">
                  <style>
                      body {
                          font-family: 'Roboto', sans-serif;
                          background-color: #f4f4f9;
                          display: flex;
                          justify-content: center;
                          align-items: center;
                          height: 100vh;
                          margin: 0;
                          color: #333;
                      }
                      .container {
                          background: white;
                          padding: 20px;
                          border-radius: 10px;
                          box-shadow: 0 4px 12px rgba(0,0,0,0.1);
                          width: 80%;
                          height: 80%;
                          box-sizing: border-box;
                          display: flex;
                          flex-direction: column;
                          position: relative;
                      }
                      .title {
                          position: sticky;
                          top: 0;
                          background: white;
                          padding: 20px;
                          z-index: 1;
                          border-bottom: 2px solid #e0e0e0;
                      }
                      .row {
                            display: table-row;
                      }
                      .label, .value {
                            display: table-cell;
                            padding: 5px;
                      }
                      .label {
                            font-weight: bold;
                            width: 150px; /* Adjust the width as necessary */
                      }
                      .value {
                            text-align: left;
                      }
                      .content {
                          flex: 1;
                          overflow-y: auto;
                          margin-top: 10px;
                          margin-bottom: 10px;
                          padding: 0 20px;
                      }
                      .content h2 {
                          color: #4CAF50;
                          margin-top: 20px;
                      }
                      .content p {
                          line-height: 1.6;
                          margin: 10px 0;
                      }
                      .button-container {
                          position: sticky;
                          bottom: 0;
                          background: white;
                          padding: 10px 20px;
                          z-index: 1;
                          display: flex;
                          justify-content: flex-end;
                          border-top: 2px solid #e0e0e0;
                      }
                      .button {
                          background-color: #4CAF50;
                          color: white;
                          padding: 10px 20px;
                          border: none;
                          border-radius: 5px;
                          cursor: pointer;
                          text-decoration: none;
                          transition: background-color 0.3s ease;
                      }
                      .button:hover {
                          background-color: #45a049;
                      }
                      .spinner-text-container {
                          display: flex; /* Use Flexbox for horizontal alignment */
                          align-items: center; /* Vertically align items */
                          display: flex;
                          justify-content: center;
                      }
                      .spinner {
                          border: 8px solid #f3f3f3; /* Light grey */
                          border-top: 8px solid #4CAF50; /* Green */
                          border-radius: 50%;
                          width: 15px;
                          height: 15px;
                          animation: spin 2s linear infinite;
                          margin-bottom: 5px;
                      }
                      @keyframes spin {
                          0% { transform: rotate(0deg); }
                          100% { transform: rotate(360deg); }
                      }
                  </style>
                """;
    }

    private String getBody(String title, String context, Vector<String> variableNames,
            Vector<String> variableDescriptions) {
        String html = "" + System.lineSeparator();
        html += "<div class=\"container\">";
        html += "<div class=\"title\">" + System.lineSeparator();
        html += "  <div class=\"row\"><div class=\"label\"><b>Dataset:</b></div><div class=\"value\">" + title
                + "</div></div>" + System.lineSeparator();
        html += "  <div class=\"row\"><div class=\"label\"><b>Context:</b></div><div class=\"value\">" + context
                + "</div></div>" + System.lineSeparator();
        html += "  <div class=\"row\"><div class=\"label\"><b>Literature:</b></div><div class=\"value\" id=\"citations\">"
                + llamaRAG.getCitations() + "</div></div>" + System.lineSeparator();
        html += "</div>" + System.lineSeparator();
        html += "<div class=\"content\">" + System.lineSeparator();
        html += "  <table id=\"datasetTable\">" + System.lineSeparator();
        html += "    <thead><tr><th style='width: 35%'>Column</th><th>Gender Bias</th></tr></thead>"
                + System.lineSeparator();
        html += "    <tbody>" + System.lineSeparator();
        for (int i = 0; i < variableNames.size(); i++) {
            html += "      <tr><td><b>" + variableNames.get(i) + "</b><br>" + variableDescriptions.get(i)
                    + "<td id='variable_" + i + "'>&nbsp;"
                    + "<div class=\"spinner-text-container\"><div class=\"spinner\"></div><span>&nbsp;&nbsp;&nbsp;Processing...</span></div>"
                    + "</td><tr></tr>" + System.lineSeparator();
        }
        html += "    </tbody>" + System.lineSeparator();
        html += "  </table>" + System.lineSeparator();
        html += "</div>" + System.lineSeparator();
        html += "<div class=\"button-container\">" + System.lineSeparator();
        html += "   <a href=\"/index.html\" class=\"button\">Back to Intro</a>" + System.lineSeparator();
        html += "</div>" + System.lineSeparator();
        return html;
    }

    public String getWaitPage() {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Processing...</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background-color: #f4f4f9;
                            display: flex;
                            justify-content: center;
                            align-items: center;
                            height: 100vh;
                            margin: 0;
                        }
                        .container {
                            background: white;
                            padding: 20px;
                            border-radius: 10px;
                            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
                            width: 80%;
                            height: 80%;
                            box-sizing: border-box;
                            display: flex;
                            flex-direction: column;
                            justify-content: center;
                            align-items: center;
                            text-align: center;
                        }
                        .spinner {
                            border: 8px solid #f3f3f3; /* Light grey */
                            border-top: 8px solid #4CAF50; /* Green */
                            border-radius: 50%;
                            width: 60px;
                            height: 60px;
                            animation: spin 2s linear infinite;
                            margin-bottom: 20px;
                        }
                        @keyframes spin {
                            0% { transform: rotate(0deg); }
                            100% { transform: rotate(360deg); }
                        }
                        h1 {
                            margin: 0;
                        }
                    </style>
                    <script>
                        function setMessage(Message) {
                            var tlabel = document.getElementById('info');
                            tlabel.innerHTML = Message;
                        }
                    </script>
                </head>
                <body>
                    <div class="container">
                        <div class="spinner"></div>
                        <h1>Processing your request...</h1>
                        <p id="info">This may take a few moments. Please wait.</p>
                    </div>
                </body>
                </html>
                """;
    }

}
