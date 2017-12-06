package kam.hazelrigg;


import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.PropertiesUtils;
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
            PropertiesUtils.asProperties(
                    "annotators", "tokenize, ssplit, pos, lemma"
                    , "options", "untokenizable=noneKeep"
                    , "tokenize.language", "en"));
    private StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

    @Test
    public void getsCorrectWordCounts() {
        String testString = "I eNjoy writing a lot of unit tests. Unit tests are a loT of fUn to make.";
        String expected = "lot:2|unit:2|tests:2|writing:1|enjoy:1|make:1|fun:1|";

        Book test = new Book();
        test.givePipeline(pipeline);
        test.tagText(testString);

        assertEquals("Failure creating word counts", test.getWords().getSimpleString(), expected);
    }

    @Test
    public void getsCorrectPOSTags() {
        String testString = "So they were trying to re-invent themselves and their universe."
                + " Science fiction was a big help.";
        String expected = "Noun, singular or mass:3|Verb, past tense:2|Personal pronoun:2|Proper noun, singular:1|Preposition or subordinating conjunction:1|Adjective:1|Coordinating conjunction:1|Determiner:1|Possessive pronoun:1|Verb, gerund or present participle:1|Verb, base form:1|to:1|";

        Book testBook = new Book();
        testBook.givePipeline(pipeline);
        testBook.tagText(testString);
        assertEquals(testBook.getPartsOfSpeech().getSimpleString(), expected);
    }

    /*
    @Test
    public void createsCorrectJSON() {
        File testf = null;
        try {
            testf = new File(this.getClass().getResource("/test.txt").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Book test = new Book();
        test.givePipeline(pipeline);
        test.setPath(testf);
        test.setTitleFromText(testf);
        System.out.println(test.title);
        test.readText(false);

        OutputWriter ow = new OutputWriter(test);
        ow.writeJson();

        String expectedOutput = "{\"2 B R 0 2 B\":{\"children\":[{\"children\":[{\"children\":[{\"size\":1,\"name\":\"Proper noun, plural\",\"description\":\"Proper noun, plural\"}],\"name\":\"Proper noun, plural\",\"description\":\"Proper noun, plural\"},{\"children\":[{\"size\":157,\"name\":\"Proper noun, singular\",\"description\":\"Proper noun, singular\"}],\"name\":\"Proper noun, singular\",\"description\":\"Proper noun, singular\"},{\"children\":[{\"size\":360,\"name\":\"Noun, singular or mass\",\"description\":\"Noun, singular or mass\"}],\"name\":\"Noun, singular or mass\",\"description\":\"Noun, singular or mass\"},{\"children\":[{\"size\":111,\"name\":\"Noun, plural\",\"description\":\"Noun, plural\"}],\"name\":\"Noun, plural\",\"description\":\"Noun, plural\"}],\"name\":\"Nouns\",\"description\":\"Nouns\"},{\"children\":[{\"children\":[{\"size\":239,\"name\":\"Verb, past tense\",\"description\":\"Verb, past tense\"}],\"name\":\"Verb, past tense\",\"description\":\"Verb, past tense\"},{\"children\":[{\"size\":41,\"name\":\"Verb, gerund or present participle\",\"description\":\"Verb, gerund or present participle\"}],\"name\":\"Verb, gerund or present participle\",\"description\":\"Verb, gerund or present participle\"},{\"children\":[{\"size\":66,\"name\":\"Verb, non-3rd person singular present\",\"description\":\"Verb, non-3rd person singular present\"}],\"name\":\"Verb, non-3rd person singular present\",\"description\":\"Verb, non-3rd person singular present\"},{\"children\":[{\"size\":119,\"name\":\"Verb, base form\",\"description\":\"Verb, base form\"}],\"name\":\"Verb, base form\",\"description\":\"Verb, base form\"},{\"children\":[{\"size\":44,\"name\":\"Verb, 3rd person singular present\",\"description\":\"Verb, 3rd person singular present\"}],\"name\":\"Verb, 3rd person singular present\",\"description\":\"Verb, 3rd person singular present\"},{\"children\":[{\"size\":56,\"name\":\"Verb, past participle\",\"description\":\"Verb, past participle\"}],\"name\":\"Verb, past participle\",\"description\":\"Verb, past participle\"}],\"name\":\"Verbs\",\"description\":\"Verbs\"},{\"children\":[{\"children\":[{\"size\":152,\"name\":\"Adverb\",\"description\":\"Adverb\"}],\"name\":\"Adverb\",\"description\":\"Adverb\"}],\"name\":\"Adverbs\",\"description\":\"Adverbs\"},{\"children\":[{\"children\":[{\"size\":1,\"name\":\"Adjective, superlative\",\"description\":\"Adjective, superlative\"}],\"name\":\"Adjective, superlative\",\"description\":\"Adjective, superlative\"},{\"children\":[{\"size\":170,\"name\":\"Adjective\",\"description\":\"Adjective\"}],\"name\":\"Adjective\",\"description\":\"Adjective\"},{\"children\":[{\"size\":7,\"name\":\"Adjective, comparative\",\"description\":\"Adjective, comparative\"}],\"name\":\"Adjective, comparative\",\"description\":\"Adjective, comparative\"}],\"name\":\"Adjectives\",\"description\":\"Adjectives\"},{\"children\":[{\"children\":[{\"size\":24,\"name\":\"Wh-pronoun\",\"description\":\"Wh-pronoun\"}],\"name\":\"Wh-pronoun\",\"description\":\"Wh-pronoun\"},{\"children\":[{\"size\":50,\"name\":\"Possessive pronoun\",\"description\":\"Possessive pronoun\"}],\"name\":\"Possessive pronoun\",\"description\":\"Possessive pronoun\"},{\"children\":[{\"size\":249,\"name\":\"Personal pronoun\",\"description\":\"Personal pronoun\"}],\"name\":\"Personal pronoun\",\"description\":\"Personal pronoun\"},{\"children\":[{\"size\":3,\"name\":\"Possessive wh pronoun\",\"description\":\"Possessive wh pronoun\"}],\"name\":\"Possessive wh pronoun\",\"description\":\"Possessive wh pronoun\"}],\"name\":\"Pronouns\",\"description\":\"Pronouns\"},{\"children\":[{\"children\":[{\"size\":285,\"name\":\"Determiner\",\"description\":\"Determiner\"}],\"name\":\"Determiner\",\"description\":\"Determiner\"},{\"children\":[{\"size\":25,\"name\":\"Symbol\",\"description\":\"Symbol\"}],\"name\":\"Symbol\",\"description\":\"Symbol\"},{\"children\":[{\"size\":5,\"name\":\"Predeterminer\",\"description\":\"Predeterminer\"}],\"name\":\"Predeterminer\",\"description\":\"Predeterminer\"},{\"children\":[{\"size\":16,\"name\":\"Particle\",\"description\":\"Particle\"}],\"name\":\"Particle\",\"description\":\"Particle\"},{\"children\":[{\"size\":247,\"name\":\"Preposition or subordinating conjunction\",\"description\":\"Preposition or subordinating conjunction\"}],\"name\":\"Preposition or subordinating conjunction\",\"description\":\"Preposition or subordinating conjunction\"},{\"children\":[{\"size\":5,\"name\":\"Existential there\",\"description\":\"Existential there\"}],\"name\":\"Existential there\",\"description\":\"Existential there\"},{\"children\":[{\"size\":9,\"name\":\"Interjection\",\"description\":\"Interjection\"}],\"name\":\"Interjection\",\"description\":\"Interjection\"},{\"children\":[{\"size\":6,\"name\":\"Possessive ending\",\"description\":\"Possessive ending\"}],\"name\":\"Possessive ending\",\"description\":\"Possessive ending\"},{\"children\":[{\"size\":62,\"name\":\"Coordinating conjunction\",\"description\":\"Coordinating conjunction\"}],\"name\":\"Coordinating conjunction\",\"description\":\"Coordinating conjunction\"},{\"children\":[{\"size\":52,\"name\":\"Cardinal number\",\"description\":\"Cardinal number\"}],\"name\":\"Cardinal number\",\"description\":\"Cardinal number\"},{\"children\":[{\"size\":7,\"name\":\"Wh-determiner\",\"description\":\"Wh-determiner\"}],\"name\":\"Wh-determiner\",\"description\":\"Wh-determiner\"},{\"children\":[{\"size\":65,\"name\":\"to\",\"description\":\"to\"}],\"name\":\"to\",\"description\":\"to\"},{\"children\":[{\"size\":33,\"name\":\"Modal\",\"description\":\"Modal\"}],\"name\":\"Modal\",\"description\":\"Modal\"}],\"name\":\"Other\",\"description\":\"Other\"}],\"name\":\"2 B R 0 2 B by Kurt Vonnegut\",\"description\":\"Parts of speech for 2 B R 0 2 B by Kurt Vonnegut\"}}";
//        assertEquals(expectedOutput, ow.writeJson());
    }
    */

    @Test
    public void createsCorrectCSV() {
        String testString = "So they were trying to re-invent themselves and their universe."
                + " Science fiction was a big help. They like science fiction";
        String expected = "\"fiction\", 2\n" +
                "\"science\", 2\n" +
                "\"big\", 1\n" +
                "\"like\", 1\n" +
                "\"re-invent\", 1\n" +
                "\"help\", 1\n" +
                "\"trying\", 1\n" +
                "\"universe\", 1\n";

        Book testBook = new Book();
        testBook.givePipeline(pipeline);
        testBook.tagText(testString);
        String resultCSV = new OutputWriter(testBook).writeCSV();
        assertEquals(resultCSV, expected);
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
        test.setTitleFromText(testf);

        assertEquals("2 B R 0 2 B", test.getTitle());
    }

    @Test
    public void getsNameFromText() {
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
        test.readText(false);
    }

    @Test
    public void setTitleFakeFileTest() {
        e.expect(FileNotFoundException.class);
        Book test = new Book();
        File fake = new File("akljajdflj.oops");
        test.setTitleFromText(fake);
    }

    @Test
    public void shouldGetIsGutenberg() {
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
        File testFile = null;
        try {
            testFile = new File(this.getClass().getResource("/test2.txt").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Book test = new Book();
        test.setPath(testFile);
        test.setTitleFromText(testFile);
        assertFalse(test.isGutenberg());
    }

    @Test
    public void shouldGetIsGutenbergVariant() {
        File testFile = null;
        try {
            testFile = new File(this.getClass().getResource("/test3.txt").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Book test = new Book();
        test.setTitleFromText(testFile);

        assertTrue(test.isGutenberg());
    }

    @Test
    public void shouldReadText() {
        File testFile = null;
        try {
            testFile = new File(this.getClass().getResource("/test2.txt").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Book test = new Book();
        test.givePipeline(pipeline);
        test.setPath(testFile);
        assertTrue(test.readText(false));
    }

    @Test
    public void shouldReadGutenbergText() {
        File testFile = null;
        try {
            testFile = new File(this.getClass().getResource("/test.txt").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Book test = new Book();
        test.givePipeline(pipeline);
        test.setTitleFromText(testFile);
        test.setPath(testFile);
        assertTrue(test.isGutenberg());
    }

    @Test
    public void shouldGetResultsExist() {
        File testFile = null;
        try {
            testFile = new File(this.getClass().getResource("/test.txt").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Book test = new Book();
        test.setPath(testFile);
        test.givePipeline(pipeline);
        test.setTitleFromText(testFile);
        test.readText(false);

        OutputWriter ow = new OutputWriter(test);
        ow.writeTxt();
        ow.makeDiffGraph();
        ow.makePosGraph();
        ow.writeJson();

        boolean hasResults = test.hasResults(true, true);
        System.out.println(hasResults);
        assertTrue(hasResults);
    }

    @Test
    public void shouldBeMissingFile() {
        e.expect(NullPointerException.class);
        Book test = new Book();
        test.givePipeline(pipeline);
        test.setPath(new File("whoops, nothing here"));
        test.readText(false);
    }

    @Test
    public void createsSubdirectoryBook() {
        Book test = new Book("dir");
        assertEquals(test.getSubdirectory(), "dir");
    }

}
