package kam.hazelrigg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static java.util.Map.Entry.comparingByValue;

class FreqMap {

    private HashMap<String, Integer> frequency = new HashMap<>();

    /**
     * Increases the value of a key by 1.
     *
     * @param key Key to increase value of
     */
    void increaseFreq(String key) {
        if (frequency.containsKey(key)) {
            frequency.put(key, frequency.get(key) + 1);
        } else {
            frequency.put(key, 1);
        }
    }

    int getSize() {
        return frequency.entrySet().size();
    }

    /**
     * Returns the FreqMap as a HashMap.
     *
     * @return HashMap version of FreqMap
     */
    HashMap<String, Integer> getFrequency() {
        sortByValue();
        return frequency;
    }

    /**
     * Gets the value of a key.
     *
     * @param key key to get value of
     * @return value of the key
     */
    int get(String key) {
        return frequency.get(key);
    }

    /**
     * Creates a string that contains keys and values separated with arrows.
     *
     * @return String of FreqMap
     */
    public String toString() {

        StringBuilder result = new StringBuilder();
        sortByValue();
        frequency.forEach((key, value) -> result.append(String.format("%s → %d\n", key, value)));

        return result.toString();
    }

    String[] getSortedByKey() {
        String[] keys = frequency.keySet().toArray(new String[frequency.size()]);
        Arrays.sort(keys);
        return keys;
    }

    /**
     * Sorts the FreqMap in descending order by its values.
     */
    private void sortByValue() {
        // https://stackoverflow.com/questions/109383/sort-a-mapkey-value-by-values-java

        ArrayList<Entry<String, Integer>> toSort = new ArrayList<>();
        toSort.addAll(frequency.entrySet());
        toSort.sort(comparingByValue(Collections.reverseOrder()));
        frequency = toSort.stream()
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }

}
