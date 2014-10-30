import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class Alphabet {
    private Map<Character, Integer> numbers = new HashMap<Character, Integer>();
    private char characters[];
    class FormatException extends IOException {}

    Alphabet(char[] alphabet) {
        for (int i = 0; i < alphabet.length; ++i) {
            numbers.put(alphabet[i], i);
        }
        characters = alphabet;
    }

    public int toNumber(String word) throws FormatException {
        int sum = 0;
        int size = characters.length + 1;
        int position = 1;
        for (char c : word.toCharArray()) {
            Integer num = numbers.get(c);
            if (num == null) {
                throw new FormatException();
            }
            sum += (num + 1) * position;
            position *= size;
        }
        return sum;
    }

    public String toString(int number) {
        StringBuilder word = new StringBuilder();
        int size = characters.length + 1;
        while(number > 0) {
            if (number % size == 0) {
                return null;
            }
            word.append(characters[number % size - 1]);
            number /= size;
        }
        return word.toString();
    }
}
