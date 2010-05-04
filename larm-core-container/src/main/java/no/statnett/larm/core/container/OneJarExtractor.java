package no.statnett.larm.core.container;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OneJarExtractor {
    private static Logger log = LoggerFactory.getLogger(OneJarExtractor.class);

    public static String extractWar(String pathInJar, String localName) throws IOException {
        JarFile jarFile = getCurrentJarFile();
        File outputFile = new File(createTmpDir(localName), localName);

        log.info("Copying " + jarFile.getName() + "!" + pathInJar + " to " + outputFile);
        copyAndClose(jarFile.getInputStream(new ZipEntry(pathInJar)), outputFile);

        return outputFile.getAbsolutePath();
    }

    private static void copyAndClose(InputStream input, File outputFile) throws IOException {
        FileOutputStream output = new FileOutputStream(outputFile);
        IOUtils.copy(input, output);
        input.close();
        output.close();
    }

    private static File createTmpDir(String localName) {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        File appDir = new File(new File(tmpDir, "embeddedcontainer"), localName + "-" + System.currentTimeMillis());
        if (!appDir.mkdirs()) {
            throw new RuntimeException("Could not create " + appDir);
        }
        return appDir;
    }

    private static JarFile getCurrentJarFile() throws IOException {
        String[] pathElements = System.getProperty("java.class.path").split(System.getProperty("path.separator"));
        String thisFile = null;
        for (String path : pathElements) {
            if (path.endsWith(".one-jar.jar")) {
                thisFile = path;
                break;
            }
        }

        return new JarFile(thisFile);
    }

    public static boolean extractFile(String filename) throws IOException {
        File file = new File(filename);
        if (file.exists()) {
            return false;
        }

        InputStream inputStream = OneJarExtractor.class.getClassLoader().getResourceAsStream("/" + filename);
        if (inputStream == null) {
            log.warn("Could not find " + filename + " in " + getCurrentJarFile());
            return true;
        }
        FileOutputStream output = new FileOutputStream(filename);
        try {
            IOUtils.copy(inputStream, output);
            log.info("Extracted " + filename);
            return true;
        } finally {
            IOUtils.closeQuietly(output); IOUtils.closeQuietly(inputStream);
        }
    }
}
