package llamarag;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import etc.StringTool;

public class PDFTextExtractor {
    public static void main(String[] args) {
        try {
            List<File> files = listFilesRecursively(new File("./context"));
            for (File file : files) {
                if (file.toString().toLowerCase().endsWith(".pdf")) {
                    System.out.println(file.exists());
                    PDDocument document = Loader.loadPDF(file);
                    PDFTextStripper pdfStripper = new PDFTextStripper();
                    String text = pdfStripper.getText(document);
                    System.out.println(text);
                    File txtFile = StringTool.getFileBasedOn(file, ".txt");
                    StringTool.saveText(txtFile, text);
                    document.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
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
