package llamarag;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

import etc.StringTool;
import llamacpp.LlamaHelper;

public class ContextInformationStorage {

    public static void main(String[] args) {
        try {
            System.out.println("Loading data.");
            String model = LlamaHelper.getModelInfo("http://localhost:8086");
            ContextInformationStorage storage;
            storage = new ContextInformationStorage(model);
            System.out.println("Scanning folder for new context documents.");
            storage.scanDocuments();
            storage.saveToFile();
            System.out.println("Updated context database.");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private TreeSet<String> filenames = new TreeSet<>();
    private List<String> filenameList = new ArrayList<>();
    private List<float[]> embeddingsList = new ArrayList<>();
    private List<String> textList = new ArrayList<>();
    private Hashtable<String, String> bibtex = new Hashtable<String, String>();
    String modelFilename;
    File contextFile;
    File contextFolder;

    public ContextInformationStorage(String modelFilename) throws IOException {
        String name = StringTool.getFilenameOnly(new File(modelFilename));
        this.contextFile = new File("./context/" + name + ".dat");
        this.contextFolder = this.contextFile.getParentFile();
        this.contextFolder.mkdirs();
        this.loadFromFile();
    }

    public void scanDocuments() {
        List<File> documents = listFilesRecursively(new File("./context"));
        for (File document : documents) {
            if (document.isFile() && StringTool.getFilenameExtension(document).equalsIgnoreCase(".txt")) {
                System.out.println(document.toString());
                if (!filenames.contains(document.toString())) {
                    String text = StringTool.loadText(document);
                    text = text.replaceAll("\r\n", "\n").replaceAll("\r", "\n"); // simplify line breaks
                    // String subs[] = splitText(text.toString(), 300, 450);
                    String subs[] = splitText(text.toString(), 650, 800);
                    Vector<String> sections = new Vector<String>();
                    for (String sub : subs)
                        sections.add(sub.trim() + " ");
                    for (String section : sections) {
                        try {
                            section = section.replaceAll("[\\n\\t\\r]+", " ").replaceAll("\\s+", " ");
                            System.out.println(section.length() + ": " + section);
                            float embeddings[] = LlamaEmbeddings.getEmbeddings(section, true);
                            addContextInformation(embeddings, document, section);
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println(section);
                            break;
                        }
                    }
                }
            }
        }
    }

    public static String[] splitText(String text, int chunkSize, int maxChunkSize) {
        Vector<String> chunks = new Vector<>();
        int textLength = text.length();
        int lastBreakpoint = 0;

        for (int i = 0; i < textLength; i++) {
            // Look for the nearest sentence boundary within the allowed range
            char c = text.charAt(i);
            int distance = i - lastBreakpoint;
            if (distance > maxChunkSize) {
                // crawl back to last . or space and split there
                int splitPoint = lastBreakpoint + maxChunkSize;
                for (int j = splitPoint; j > lastBreakpoint; j--) {
                    if (text.charAt(j) == '.' || text.charAt(j) == '!' || text.charAt(j) == '?'
                            || text.charAt(j) == ' ') {
                        splitPoint = j;
                        break;
                    }
                }
                chunks.add(text.substring(lastBreakpoint, splitPoint + 1));
                lastBreakpoint = splitPoint + 1;
                i = lastBreakpoint;
            } else if (i == textLength - 1) {
                chunks.add(text.substring(lastBreakpoint, i + 1));
                lastBreakpoint = i + 1;
            } else {
                if (c == '.' || c == '!' || c == '?') {
                    if (distance < 3 && chunks.size() > 0) {
                        int index = chunks.size() - 1;
                        String extended = chunks.get(index) + text.substring(lastBreakpoint, i + 1);
                        if (extended.length() < maxChunkSize) {
                            chunks.set(index, extended);
                            lastBreakpoint = i + 1;
                        }
                    } else if (distance > chunkSize) {
                        chunks.add(text.substring(lastBreakpoint, i + 1));
                        lastBreakpoint = i + 1;
                    }
                }
            }
        }

        // Extract and trim the chunk
        for (int i = 0; i < chunks.size(); i++) {
            chunks.set(i, chunks.get(i).trim());
        }

        // Convert the Vector to an array
        return chunks.toArray(new String[0]);
    }

    // Method to add context information
    public void addContextInformation(float[] embeddings, File document, String text) throws IOException {
        File bibtexFile = StringTool.getFileBasedOn(document, ".bib");
        String documentFilename = document.toString();
        String bibtexFilename = bibtexFile.toString();
        if (!bibtex.containsKey(bibtexFilename) && bibtexFile.exists()) {
            bibtex.put(documentFilename, StringTool.loadText(bibtexFile));
        }
        embeddingsList.add(embeddings);
        filenameList.add(documentFilename);
        textList.add(text);
    }

    // Method to get the arrays of embeddings and texts
    public float[][] getEmbeddings() {
        return embeddingsList.toArray(new float[embeddingsList.size()][]);
    }

    public String[] getTexts() {
        return textList.toArray(new String[textList.size()]);
    }

//    // Method to save embeddings and texts to a file
//    public void saveToFile() throws IOException {
//        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(contextFile))) {
//            // Save the number of embeddings
//            dos.writeInt(embeddingsList.size());
//
//            // Save the embeddings
//            for (float[] embeddings : embeddingsList) {
//                dos.writeInt(embeddings.length);
//                for (float value : embeddings) {
//                    dos.writeFloat(value);
//                }
//            }
//
//            for (String filename : filenameList) {
//                dos.writeUTF(filename);
//            }
//
//            // Save the texts
//            for (String text : textList) {
//                dos.writeUTF(text);
//            }
//        }
//    }

//    // Method to load embeddings and texts from a file
//    public void loadFromFile() throws IOException {
//        if (contextFile.exists()) {
//            try (DataInputStream dis = new DataInputStream(new FileInputStream(contextFile))) {
//                // Load the number of filenames
//                int size = dis.readInt();
//
//                // Load the embeddings
//                for (int i = 0; i < size; i++) {
//                    int length = dis.readInt();
//                    float[] embeddings = new float[length];
//                    for (int j = 0; j < length; j++) {
//                        embeddings[j] = dis.readFloat();
//                    }
//                    embeddingsList.add(embeddings);
//                }
//
//                // Load the texts
//                for (int i = 0; i < size; i++) {
//                    String filename = dis.readUTF();
//                    filenameList.add(filename);
//                }
//
//                // Load the texts
//                for (int i = 0; i < size; i++) {
//                    String text = dis.readUTF();
//                    textList.add(text);
//                }
//            }
//        }
//    }

    public String getContextFile(int index) {
        return filenameList.size() <= index ? "" : filenameList.get(index);
    }

    public String getContextText(int index) {
        return textList.size() <= index ? "" : textList.get(index);
    }

    public float[] getContextEmbeddings(int index) {
        return embeddingsList.size() <= index ? new float[] {} : embeddingsList.get(index);
    }

    public String getBibtex(int index) {
        return filenameList.size() <= index ? "" : bibtex.get(filenameList.get(index));
    }

    public int size() {
        return embeddingsList.size();
    }

    public int[] findClosestContext(float[] queryEmbedding, int n, String... tags) {
        String[] closestTexts = new String[n];
        double[] closestDistances = new double[n];
        int[] closestIndeces = new int[n];

        // Initialize distances with a large value
        for (int i = 0; i < n; i++) {
            closestDistances[i] = Double.MAX_VALUE;
        }

        for (int i = 0; i < embeddingsList.size(); i++) {
            boolean tagged = (tags.length == 0);
            for (String tag : tags) {
                if (filenameList.get(i).toLowerCase().indexOf(tag.toLowerCase()) >= 0) {
                    tagged = true;
                    break;
                }
            }
            if (tagged) {
                float[] embeddings = embeddingsList.get(i);
                if (embeddings.length == queryEmbedding.length) {
                    double distance = cosineSimilarity(queryEmbedding, embeddings);
                    String text = textList.get(i);

                    // Check if the text is already in the closestTexts array
                    boolean isDuplicate = false;
                    for (int j = 0; j < n; j++) {
                        if (text.equals(closestTexts[j])) {
                            isDuplicate = true;
                            break;
                        }
                    }

                    if (!isDuplicate && !text.isBlank()) {
                        // Find the position to insert the current distance
                        for (int j = 0; j < n; j++) {
                            if (distance < closestDistances[j]) {
                                // Shift elements to make room for the new distance
                                for (int k = n - 1; k > j; k--) {
                                    closestDistances[k] = closestDistances[k - 1];
                                    closestTexts[k] = closestTexts[k - 1];
                                }
                                // Insert the new distance and text
                                closestDistances[j] = distance;
                                closestTexts[j] = text;
                                closestIndeces[j] = i;
                                break;
                            }
                        }
                    }
                }
            }
        }

        return closestIndeces;
    }

    // Cosine similarity function
    public static float cosineSimilarity(float[] vectorA, float[] vectorB) {
        if (vectorA.length != vectorB.length) {
            throw new IllegalArgumentException("Vectors must be of the same length");
        }

        float dotProduct = 0.0f;
        float magnitudeA = 0.0f;
        float magnitudeB = 0.0f;

        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            magnitudeA += Math.pow(vectorA[i], 2);
            magnitudeB += Math.pow(vectorB[i], 2);
        }

        magnitudeA = (float) Math.sqrt(magnitudeA);
        magnitudeB = (float) Math.sqrt(magnitudeB);

        return dotProduct / (magnitudeA * magnitudeB);
    }

    // Method to save embeddings, texts, and bibtex to a file
    public void saveToFile() throws IOException {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(contextFile))) {
            // Save the number of embeddings
            dos.writeInt(embeddingsList.size());

            // Save the embeddings
            for (float[] embeddings : embeddingsList) {
                dos.writeInt(embeddings.length);
                for (float value : embeddings) {
                    dos.writeFloat(value);
                }
            }

            // Save the filenames
            dos.writeInt(filenameList.size());
            for (String filename : filenameList) {
                dos.writeUTF(filename);
            }

            // Save the texts
            dos.writeInt(textList.size());
            for (String text : textList) {
                dos.writeUTF(text);
            }

            // Save the bibtex entries
            dos.writeInt(bibtex.size());
            for (String key : bibtex.keySet()) {
                dos.writeUTF(key);
                dos.writeUTF(bibtex.get(key));
            }
        }
    }

    // Method to load embeddings, texts, and bibtex from a file
    public void loadFromFile() throws IOException {
        if (contextFile.exists()) {
            try (DataInputStream dis = new DataInputStream(new FileInputStream(contextFile))) {
                // Load the number of embeddings
                int numEmbeddings = dis.readInt();

                // Load the embeddings
                embeddingsList.clear();
                for (int i = 0; i < numEmbeddings; i++) {
                    int length = dis.readInt();
                    float[] embeddings = new float[length];
                    for (int j = 0; j < length; j++) {
                        embeddings[j] = dis.readFloat();
                    }
                    embeddingsList.add(embeddings);
                }

                // Load the filenames
                int numFilenames = dis.readInt();
                filenameList.clear();
                for (int i = 0; i < numFilenames; i++) {
                    String filename = dis.readUTF();
                    filenameList.add(filename);
                    filenames.add(filename);
                }

                // Load the texts
                int numTexts = dis.readInt();
                textList.clear();
                for (int i = 0; i < numTexts; i++) {
                    textList.add(dis.readUTF());
                }

                // Load the bibtex entries
                int numBibtex = dis.readInt();
                bibtex.clear();
                for (int i = 0; i < numBibtex; i++) {
                    String key = dis.readUTF();
                    String value = dis.readUTF();
                    bibtex.put(key, value);
                }
            }
        }
    }

    public static List<File> listFilesRecursively(File folder) {
        List<File> fileList = new ArrayList<>();
        if (folder != null && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        fileList.add(file);
                    } else if (file.isDirectory()) {
                        fileList.addAll(listFilesRecursively(file));
                    }
                }
            }
        }
        return fileList;
    }

}
