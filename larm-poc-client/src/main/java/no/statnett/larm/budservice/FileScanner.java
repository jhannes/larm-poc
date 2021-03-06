package no.statnett.larm.budservice;

import static org.apache.commons.io.FileUtils.moveFileToDirectory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileScanner {

    private final File inputDir;
    private final File outputDir;
    private final FileListener fileListener;

    private Logger log = LoggerFactory.getLogger(getClass());

    public FileScanner(File inputDir, File outputDir, FileListener fileListener) {
        this.inputDir = inputDir;
        this.outputDir = outputDir;
        this.fileListener = fileListener;
    }

    public void scan() {
        if (!inputDir.exists()) {
            log.warn("Input dir " + inputDir.getAbsolutePath() + " doesn't exist");
            return;
        }
        log.debug("Scanning " + inputDir + " for new files");
        for (File file : inputDir.listFiles()) {
            if (file.isDirectory()) continue;
            try {
                moveAndProcessFile(file);
            } catch (IOException e) {
                log.error("Failed to process " + file, e);
            }
        }
    }

    private void moveAndProcessFile(File file) throws IOException {
        File processingDir = new File(inputDir, "processing");
        File tmpAnswerDir = new File(outputDir, "processing");
        tmpAnswerDir.mkdirs();

        File answerFile = new File(tmpAnswerDir, file.getName());
        moveFileToDirectory(file, processingDir, true);
        file = new File(processingDir, file.getName());

        try {
            processFile(file, answerFile);
        } catch (Throwable e) {
            log.error("Processed " + file + " failed", e);
            moveFileToDirectory(file, new File(inputDir, "error"), true);
            answerFile.delete();
            return;
        }
        log.info("Processed " + file + " ok");
        moveFileToDirectory(file, new File(inputDir, "processed"), true);

        if (answerFile.length() > 0) {
            moveFileToDirectory(answerFile, outputDir, true);
        } else {
            FileUtils.forceDelete(answerFile);
        }
    }

    private void processFile(File file, File answerFile) throws IOException {
        Reader inputFile = new FileReader(file);
        Writer outputFile = new FileWriter(answerFile);
        try {
            log.info("Processing " + file + " with " + fileListener);
            fileListener.processFile(file.getName(), inputFile, outputFile);
        } finally {
            IOUtils.closeQuietly(inputFile);
            IOUtils.closeQuietly(outputFile);
        }
    }

    public static void scheduleEverySecond(File inputDir, File outputDir, FileListener fileListener) {
        final FileScanner fileScanner = new FileScanner(inputDir, outputDir, fileListener);
        new Timer(true).schedule(new TimerTask() {
            @Override
            public void run() {
                fileScanner.scan();
            }
        }, 0, 1000);
    }

}
