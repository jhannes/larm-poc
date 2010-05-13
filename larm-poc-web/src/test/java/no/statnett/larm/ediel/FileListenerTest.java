package no.statnett.larm.ediel;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.Reader;
import java.io.Writer;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class FileListenerTest {
    private File testDir = new File("target/file-test/" + System.currentTimeMillis());
    private File inputDir = new File(testDir, "input");
    private File outputDir = new File(testDir, "output");
    private FileListener processor = mock(FileListener.class);

    private FileScanner fileScanner = new FileScanner(inputDir, outputDir, processor);

    @Before
    public void setUp() {
        inputDir.mkdir();
    }

    @Test
    public void shouldProcessNewFiles() throws Exception {
        File testFile = new File(inputDir, "test.txt");
        FileUtils.touch(testFile);
        fileScanner.scan();

        verify(processor).processFile(eq("test.txt"), any(Reader.class), any(Writer.class));
    }

    @Test
    public void shouldMoveProcessedFiles() throws Exception {
        File testFile = new File(inputDir, "test.txt");
        FileUtils.touch(testFile);
        fileScanner.scan();

        assertThat(testFile).doesNotExist();
        assertThat(new File(new File(inputDir, "processed"), "test.txt")).exists();
    }

    @Test
    public void shouldMoveFileDuringProcessing() throws Exception {
        final File testFile = new File(inputDir, "test.txt");
        FileUtils.touch(testFile);
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


}
