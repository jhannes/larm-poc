package no.statnett.larm.budservice;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public interface FileListener {
    void processFile(String fileName, Reader inputFile, Writer outputFile) throws IOException;
}
