package no.statnett.larm.ediel;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import no.statnett.larm.core.repository.Repository;

public class EdielFileListener {

    private File inputFileDir;

    private File outputFileDir;

    private Repository repository;

    public void processFile(File inputFile, File outputFile) throws IOException {
        FileReader input = new FileReader(inputFile);
        FileWriter output = new FileWriter(outputFile);

        new EdielService(repository).process(input, output);

        input.close();
        output.close();
    }


}
