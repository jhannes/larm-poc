package no.statnett.larm.ediel;

import static org.apache.commons.io.FileUtils.moveFileToDirectory;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class FileScanner {

    private final File inputDir;
    private final File outputDir;
    private final FileListener processor;

    public FileScanner(File inputDir, File outputDir, FileListener processor) {
        this.inputDir = inputDir;
        this.outputDir = outputDir;
        this.processor = processor;
    }

    public void scan() throws IOException {
        File[] files = inputDir.listFiles();
        for (File file : files) {
            File processingDir = new File(inputDir, "processing");
            moveFileToDirectory(file, processingDir, true);
            processor.processFile(file.getName(), new StringReader(""), new StringWriter());
            moveFileToDirectory(new File(processingDir, file.getName()),
                    new File(inputDir, "processed"), true);
        }
    }

}
