package no.statnett.larm.ediel;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public interface FileListener {
    void processFile(String string, Reader inputFile, Writer outputFile) throws IOException;
}
