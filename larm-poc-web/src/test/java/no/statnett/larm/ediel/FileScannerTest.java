package no.statnett.larm.ediel;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class FileScannerTest {
    private static int index = 0;
    private File testDir = new File("target/file-test/" + System.currentTimeMillis() + "-" + index++);
    private File inputDir = new File(testDir, "input");
    private File outputDir = new File(testDir, "output");
    private FileListener processor = mock(FileListener.class);

    private FileScanner fileScanner = new FileScanner(inputDir, outputDir, processor);
    private File testFile = new File(inputDir, "test.txt");
    private File processedDir = new File(inputDir, "processed");

    @Before
    public void setUp() throws IOException {
        Logger.getLogger("no.statnett.larm.ediel.FileScanner").setLevel(Level.OFF);
        inputDir.mkdirs();
        FileUtils.touch(testFile);
    }

    @After
    public void cleanup() {
        Logger.getLogger("no.statnett.larm.ediel.FileScanner").setLevel(Level.ERROR);
        FileUtils.deleteQuietly(testDir);
    }

    @Test
    public void shouldProcessNewFiles() throws Exception {
        fileScanner.scan();

        verify(processor).processFile(eq("test.txt"), any(Reader.class), any(Writer.class));
    }

    @Test
    public void shouldMoveProcessedFiles() throws Exception {
        fileScanner.scan();
        fileScanner.scan();

        verify(processor, times(1)).processFile(anyString(), any(Reader.class), any(Writer.class));
        assertThat(testFile).doesNotExist();
        assertThat(new File(processedDir, "test.txt")).exists();
    }

    @Test
    public void shouldMoveFileDuringProcessing() throws Exception {
        final File destinationFile = new File(new File(inputDir, "processed"), "test.txt");

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                assertThat(testFile).doesNotExist();
                assertThat(destinationFile).doesNotExist();
                return null;
            }
        }).when(processor).processFile(anyString(), any(Reader.class), any(Writer.class));

        fileScanner.scan();
    }

    @Test
    public void shouldPassInReader() throws Exception {
        FileWriter writer = new FileWriter(testFile);
        IOUtils.write("This is a test", writer);
        writer.close();

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                String content = IOUtils.toString(((Reader)invocation.getArguments()[1]));
                assertThat(content).isEqualTo("This is a test");
                return null;
            }
        }).when(processor).processFile(anyString(), any(Reader.class), any(Writer.class));

        fileScanner.scan();
    }

    @Test
    public void shouldWriteAnswer() throws Exception {
        final File answerFile = new File(outputDir, testFile.getName());

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                assertThat(answerFile).doesNotExist();
                IOUtils.write("This is the response", ((Writer)invocation.getArguments()[2]));
                return null;
            }
        }).when(processor).processFile(anyString(), any(Reader.class), any(Writer.class));
        fileScanner.scan();

        assertThat(answerFile).exists();
        assertThat(IOUtils.toString(new FileReader(answerFile))).isEqualTo("This is the response");
    }

    @Test
    public void shouldRemoveEmptyAnswerFile() throws Exception {
        final File answerFile = new File(outputDir, testFile.getName());
        fileScanner.scan();
        assertThat(answerFile).doesNotExist();
    }

    @Test
    public void shouldHandleErrors() throws Exception {
        doThrow(new RuntimeException("any exception")).when(processor).processFile(anyString(), any(Reader.class), any(Writer.class));

        fileScanner.scan();
        File errorDir = new File(inputDir, "error");

        assertThat(testFile).doesNotExist();
        assertThat(new File(inputDir, testFile.getName())).doesNotExist();
        assertThat(new File(errorDir, testFile.getName())).exists();

        assertThat(new File(outputDir, testFile.getName())).doesNotExist();
    }

}
