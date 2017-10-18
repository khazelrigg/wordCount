package kam.hazelrigg;import edu.stanford.nlp.tagger.maxent.MaxentTagger;import org.jfree.chart.ChartUtilities;import org.jfree.chart.JFreeChart;import org.jfree.chart.plot.PiePlot3D;import org.jfree.data.general.DefaultPieDataset;import java.awt.Color;import java.awt.Font;import java.io.BufferedReader;import java.io.BufferedWriter;import java.io.File;import java.io.FileReader;import java.io.FileWriter;import java.io.IOException;import java.io.InputStreamReader;import java.text.BreakIterator;import java.util.HashMap;import java.util.Map;import static org.jfree.chart.ChartFactory.createPieChart3D;public class Book {    // Set up tagger    private static final MaxentTagger tagger =            new MaxentTagger("models/english-left3words-distsim.tagger");    // Get POS abbreviation values    private static final HashMap<String, String> posAbbrev =            TextTools.nonAbbreviate();    private String title;    private String author;    private File path;    private int wordCount;    private int syllableCount;    private int sentenceCount;    private boolean gutenberg;    private final FreqMap posFreq;    private final FreqMap wordFreq;    private final FreqMap difficultyMap;    public Book() {        this.title = "";        this.author = "";        this.gutenberg = false;        this.posFreq = new FreqMap();        this.wordFreq = new FreqMap();        this.difficultyMap = new FreqMap();    }    /**     * Get the title of a book.     *     * @param text File to find title of     */    public void setTitleFromText(File text) {        String title = "";        String author = "";        try {            BufferedReader br = new BufferedReader(new FileReader(text));            String firstLine = br.readLine();            // If the first line is very short skip over it            while (firstLine.length() < 3) {                firstLine = br.readLine();            }            // Cases of Gutenberg books to check            if (firstLine.contains("The Project Gutenberg EBook of")) {                firstLine = firstLine.substring(31);                this.gutenberg = true;            }            if (firstLine.contains("Project Gutenberg's")                    || firstLine.contains("Project Gutenberg’s")) {                firstLine = firstLine.substring(20);                this.gutenberg = true;            }            // If the pattern "title by author" appears split at the word 'by' to get author and title            if (firstLine.contains("by")) {                title = firstLine.substring(0, firstLine.lastIndexOf("by")).trim();                author = firstLine.substring(firstLine.lastIndexOf("by") + 2).trim();            } else {                title = text.getName();                author = "";            }            // Remove any trailing commas            if (title.endsWith(",")) {                title = title.substring(0, title.length() - 1);            }            br.close();        } catch (IOException e) {            e.printStackTrace();        }        this.title = title;        this.author = author;    }    /**     * Returns whether or not a file already has a results file.     *     * @return True if the file has results already     */    public boolean resultsFileExists() {        return getResultsFile().exists();    }    private File getResultsFile() {        return new File("results/txt/" + title + " by " + author + " Results.txt");    }    private void tagFile(String text) {        // Tag the entire file        String[] taggedFile = tagger.tagString(text).split("\\s");        BreakIterator iterator = BreakIterator.getSentenceInstance();        iterator.setText(text);        while (iterator.next() != -1) {            sentenceCount++;        }        for (String taggedWord : taggedFile) {            // Ignore punctuation            if (taggedWord.replaceAll("\\W", "").length() > 2) {                String tag = taggedWord.substring(taggedWord.indexOf("_") + 1).toLowerCase();                tag = posAbbrev.get(tag);                if (tag == null) {                    continue;                }                posFreq.increaseFreq(tag);            }        }        syllableCount = TextTools.getSyllableCount(text);    }    private String getReadingEaseLevel() {        // Using Flesch–Kincaid grading scale        double score =                206.835 - (1.015 * wordCount / sentenceCount) - (84.6 * syllableCount / wordCount);        if (score <= 100) {            if (score > 90) return "5th grade";            if (score > 80) return "6th grade";            if (score > 70) return "7th grade";            if (score > 60) return "8th & 9th grade";            if (score > 50) return "10th to 12th grade";            if (score > 30) return "College";            if (score < 30 && score > 0) return "College graduate";        }        return "[NOT CLASSIFIED]";    }    private double getReadingGradeLevel() {        return Math.round((0.39 * wordCount / sentenceCount) + (11.8 * syllableCount / wordCount) - 15.59);    }    /**     * Reads a text file and tags each line for parts of speech as well as counts word frequencies.     */    public void analyseText() {        try (BufferedReader br = new BufferedReader(new FileReader(path))) {            String fullTitle = title + " by " + author;            System.out.println("☐ - Starting analysis of " + fullTitle);            long startTime = System.currentTimeMillis();            Boolean atBook = false;            StringBuilder text = new StringBuilder();            for (String line; (line = br.readLine()) != null; ) {                // Skip empty lines                if (line.isEmpty()) {                    continue;                }                // Skip over the Gutenberg headers                if (gutenberg && !atBook) {                    if (line.contains("START OF THIS PROJECT GUTENBERG EBOOK")                            || line.contains("START OF THE PROJECT GUTENBERG EBOOK")) {                        atBook = true;                        line = br.readLine();                    } else {                        continue;                    }                }                // Stop at the Gutenberg footer                if (gutenberg) {                    if (line.contains("End of the Project Gutenberg EBook")                            || line.contains("End of Project Gutenberg’s")) {                        break;                    }                }                text.append(line);                // Word counts                for (String word : line.split("\\s")) {                    wordCount++;                    // Make word lowercase and strip punctuation                    word = word.toLowerCase().replaceAll("\\W", "");                    // Skip punctuation and stop words                    if (word.isEmpty() || TextTools.isStopWord(word)) {                        continue;                    }                    // Add difficulty information                    if (TextTools.getSyllableCount(word) == 1) {                        difficultyMap.increaseFreq("Monosyllabic");                    } else {                        difficultyMap.increaseFreq("Polysyllabic");                    }                    // Increase word frequency                    wordFreq.increaseFreq(word);                }            }            br.close();            tagFile(text.toString());            long endTime = System.currentTimeMillis();            System.out.println(                    "\n☑ - Finished analysis of " + fullTitle + " in " + (endTime - startTime) / 1000 + "s.");        } catch (IOException e) {            System.out.println("Couldn't find file at " + path);        }    }    /**     * Writes frequencies of book into a text file.     */    public void writeFrequencies() {        if (posFreq.getSize() > 0) {            File out;            // Create results directories            if (!makeResultDirs()) {                System.out.println("[Error] Failed to create results directories");                out = new File(title + " by " + author + " Results.txt");            } else {                out = new File("results/txt/" + title + " by " + author + " Results.txt");            }            try (BufferedWriter bw = new BufferedWriter(new FileWriter(out))) {                // Write conclusion                bw.write("===============[ Conclusion ]===============\n");                bw.write(writeConclusion());                // Write word frequencies                bw.write("\n==================[ Word ]==================\n");                bw.write(wordFreq.toString());                // Write pos frequencies                bw.write("\n================[ POS Tags ]================\n");                bw.write(posFreq.toString());                bw.close();            } catch (IOException e) {                System.out.println("Error writing frequencies");                System.exit(3);            }        }    }    /**     * Creates results directories for files to be saved to.     *     * @return True if both directories are successfully created     */    private static boolean makeResultDirs() {        File txt = new File("results/txt");        File img = new File("results/img");        if (!(txt.exists() || txt.mkdirs())) {            System.out.println("[Error] Could not create results directory 'txt'");            return false;        }        if (!(img.exists() || img.mkdirs())) {            System.out.println("[Error] Could not create results directory 'img'");            return false;        }        return true;    }    /**     * Creates a parts of speech distribution pie graph.     */    public void makePosGraph() {        makeGraph("POS Distribution", posFreq);    }    /**     * Creates a difficulty pie graph that uses syllable.     */    public void makeDifficultyGraph() {        makeGraph("Difficulty", difficultyMap);    }    /**     * Creates a graph using JFreeChart that is saved to a jpg.     *     * @param purpose Purpose of the graph, used in title of graph     * @param freq    FreqMap to use values off     */    private void makeGraph(String purpose, FreqMap freq) {        DefaultPieDataset dataSet = new DefaultPieDataset();        String outPath;        // Create results directories        if (new File("results/img/").isDirectory()) {            outPath = "results/img/" + title + " by " + author + " " + purpose + " Results.jpeg";        } else {            System.out.println("[Error] Failed to create results directories");            outPath = title + " by " + author + " " + purpose + "Results.jpeg";        }        // Load POS data into data set        HashMap<String, Integer> map = freq.getFrequency();        for (Map.Entry<String, Integer> entry : map.entrySet()) {            String type = entry.getKey();            int count = entry.getValue();            dataSet.setValue(type, count);        }        String title = purpose + " of " + this.title + " by " + this.author;        JFreeChart chart = createPieChart3D(                title,                dataSet,                false,                true,                false);        PiePlot3D plot = (PiePlot3D) chart.getPlot();        plot = setColors(plot);        plot.setBaseSectionOutlinePaint(new Color(0, 0, 0));        plot.setDarkerSides(true);        plot.setBackgroundPaint(new Color(204, 204, 204));        plot.setLabelBackgroundPaint(new Color(255, 255, 255));        plot.setStartAngle(90f);        plot.setLabelFont(new Font("Ubuntu San Serif", Font.PLAIN, 10));        plot.setDepthFactor(0.05f);        // Save the chart to jpeg        try {            ChartUtilities.saveChartAsJPEG(new File(outPath), chart, 1000, 1000);        } catch (IOException ioe) {            System.out.println("[Error - makeGraph] Failed to make pie chart " + ioe);        }    }    private PiePlot3D setColors(PiePlot3D chart) {        try {            InputStreamReader inputStreamReader =                    new InputStreamReader(TextTools.class.getResourceAsStream("posAbbreviations.txt"));            BufferedReader br = new BufferedReader(inputStreamReader);            String line = br.readLine();            chart.setSectionPaint("Monosyllabic", new Color(77, 77, 77));            chart.setSectionPaint("Polysyllabic", new Color(241, 88, 84));            while (line != null) {                String label = line.substring(line.indexOf(":") + 1, line.indexOf(">")).trim();                String hexColor = line.substring(line.indexOf(">") + 1).trim();                Color color = Color.decode(hexColor);                chart.setSectionPaint(label, color);                line = br.readLine();            }        } catch (IOException e) {            e.printStackTrace();        }        return chart;    }    private String writeConclusion() {        return "This is an automatically generated conclusion."                    + " Some information may be incorrect.\n\n"                    + title + " by " + author + "\n\n"                    + "This piece is considered a " + classifyLength()                    + " based on Nebula Award classifications.\n"                    + "The Flesch–Kincaid reading ease test would classify this book as being at "                    + "the " + getReadingEaseLevel() + " level.\n"                    + "The determined grade level using the Flesh-Kincaid scale indicates this text"                    + " is of the " + getReadingGradeLevel() + "th. grade level.\n"                    + "It is most likely " + classifyDifficulty()                    + " to read due to its ratio of polysyllabic words to monosyllabic words.\n";    }    public String getTitle() {        return title;    }    public File getPath() {        return path;    }    public void setTitle(String title) {        this.title = title;    }    public void setAuthor(String author) {        this.author = author;    }    public String getAuthor() {        return author;    }    private String classifyLength() {        /*        Classification 	Word count        Novel 	40,000 words or over        Novella 	17,500 to 39,999 words        Novelette 	7,500 to 17,499 words        Short story 	under 7,500 words        */        if (wordCount < 7500) {            return "short story";        }        if (wordCount < 17500) {            return "novelette";        }        if (wordCount < 40000) {            return "novella";        }        return "novel";    }    private String classifyDifficulty() {        int mono = difficultyMap.get("Monosyllabic");        int poly = difficultyMap.get("Polysyllabic");        if (mono < poly) {            return "easy";        }        return "difficult";    }    public void setPath(File path) {        this.path = path;    }}