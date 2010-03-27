package no.statnett.larm.core.container;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class WarExtractor {

    public static String extractWar(String pathInJar, String localName) throws IOException {
        JarFile jarFile = getCurrentJarFile();
        File outputFile = new File(createTmpDir(localName), localName);

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

}
