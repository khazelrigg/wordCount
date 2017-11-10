package test.java.kam.hazelrigg;


import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.PropertiesUtils;
import main.java.kam.hazelrigg.Book;
import main.java.kam.hazelrigg.OutputWriter;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Properties;

import static org.junit.Assert.*;

public class WordCountTest {
    private ExpectedException e = ExpectedException.none();

    private Properties props = new Properties(
            PropertiesUtils.asProperties("annotators", "tokenize, ssplit, pos, lemma"));
    private StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

    @Test
    public void getsCorrectWordCounts() {
        String testString = "I eNjoy writing a lot of unit tests. Unit tests are a loT of fUn to make.";
        String expected = "lot:2|a:2|unit:2|tests:2|of:2|are:1|writing:1|i:1|enjoy:1|to:1|make:1|fun:1|";

        Book test = new Book();
        test.givePipeline(pipeline);
        test.tagText(testString);

        assertEquals("Failure creating word counts", test.wordFreq.getSimpleString(), expected);
    }

    @Test
    public void getsCorrectPOSTags() {
        String testString = "So they were trying to re-invent themselves and their universe."
                + " Science fiction was a big help.";
        String expected = "Noun, singular or mass:3|Verb, past tense:2|Personal pronoun:2|Proper noun, singular:1|Preposition or subordinating conjunction:1|Adjective:1|Coordinating conjunction:1|Determiner:1|Possessive pronoun:1|Verb, gerund or present participle:1|Verb, base form:1|to:1|";

        Book testBook = new Book();
        testBook.givePipeline(pipeline);
        testBook.tagText(testString);
        assertEquals(testBook.posFreq.getSimpleString(), expected);
    }

    @Test
    public void ignoresPunctuationInWordCount() {
        Book test = new Book();
        test.givePipeline(pipeline);
        test.tagText("My mother is a fish.");

        assertEquals(5, test.getWordCount());
    }

    @Test
    public void getsTitleFromText() {
        File testf = null;
        try {
            testf = new File(this.getClass().getResource("/test.txt").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Book test = new Book();
        //If author and title are somehow set beforehand
        test.setAuthor("null");
        test.setTitle("null");
        test.setTitleFromText(testf);

        assertEquals("2 B R 0 2 B by Kurt Vonnegut", test.getName());
    }

    @Test
    public void nullReadFileTest() {
        e.expect(NullPointerException.class);
        Book test = new Book();
        test.readText();
    }

    @Test
    public void setTitleFakeFileTest() {
        e.expect(FileNotFoundException.class);
        Book test = new Book();
        File fake = new File("akljajdflj.oops");
        test.setTitleFromText(fake);
    }

    @Test
    public void shouldeGetIsGutenberg() {
        File testf = null;
        try {
            testf = new File(this.getClass().getResource("/test.txt").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Book test = new Book();
        test.setTitleFromText(testf);

        assertTrue(test.isGutenberg());
    }

    @Test
    public void shouldGetIsNotGutenberg() {
        File testf = null;
        try {
            testf = new File(this.getClass().getResource("/test2.txt").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Book test = new Book();
        test.setTitleFromText(testf);
        assertFalse(test.isGutenberg());
    }

    @Test
    public void shouldGetIsGutenbergVariant() {
        File testf = null;
        try {
            testf = new File(this.getClass().getResource("/test3.txt").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Book test = new Book();
        test.setTitleFromText(testf);

        assertTrue(test.isGutenberg());
    }

    @Test
    public void shouldReadText() {
        File testf = null;
        try {
            testf = new File(this.getClass().getResource("/test2.txt").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Book test = new Book();
        test.givePipeline(pipeline);
        test.setPath(testf);
        assertTrue(test.readText());
    }

    @Test
    public void shouldReadGutenbergText() {
        File testf = null;
        try {
            testf = new File(this.getClass().getResource("/test.txt").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Book test = new Book();
        test.givePipeline(pipeline);
        test.setTitleFromText(testf);
        test.setPath(testf);
        assertTrue(test.readText() && test.isGutenberg());
    }

    @Test
    public void shouldGetResultsExist() {
        File testf = null;
        try {
            testf = new File(this.getClass().getResource("/test.txt").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Book test = new Book();
        OutputWriter ow = new OutputWriter(test);
        test.givePipeline(pipeline);
        test.setTitleFromText(testf);
        test.setPath(testf);
        test.readText();
        ow.writeTxt();
        ow.makeDiffGraph();
        ow.makePosGraph();
        ow.writeJson();
        assertTrue(test.resultsFileExists());
    }

    @Test
    public void shouldGetResultsDontExist() {
        File testf = null;
        try {
            testf = new File(this.getClass().getResource("/test.txt").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Book test = new Book();
        OutputWriter ow = new OutputWriter(test);
        test.givePipeline(pipeline);
        test.setTitle("test");
        test.setPath(testf);
        test.readText();
        ow.writeTxt();
        assertFalse(test.resultsFileExists());
    }
}