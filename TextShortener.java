import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class TextShortener 
{
    /// <summary>
    ///     Делим предложение на слова
    /// </summary>
    /// <param name="input"></param>
    /// <returns></returns>
    static List<String> splitTextIntoWords(String input) 
	{
        List<String> words = new ArrayList<>();
        StringBuilder currentWord = new StringBuilder();

        for (char c : input.toCharArray()) 
		{
            if (Character.isLetterOrDigit(c) || c == '\'' || c == '-') {
                currentWord.append(c);
            } 
			else 
			{
                if (currentWord.length() > 0) {
                    words.add(currentWord.toString());
                    currentWord.setLength(0);
                };
            };
        };

        if (currentWord.length() > 0)
            words.add(currentWord.toString());

        return words;
    }

    /// <summary>
    ///     Делим слово по слогам
    /// </summary>
    /// <param name="word"></param>
    /// <returns></returns>
    static List<String> splitWordIntoSyllables(String word) 
	{
        String lowerWord = word.toLowerCase();
        List<Integer> vowelPositions = new ArrayList<>();

        HashSet<Character> vowels = new HashSet<>() {{
            add('а'); add('е'); add('ё'); add('и'); add('о'); add('у'); add('ы'); add('э'); add('ю'); add('я'); // RUS
            add('a'); add('e'); add('i'); add('o'); add('u'); add('y'); // ENG
        }};

        for (int i = 0; i < lowerWord.length(); i++)
            if (vowels.contains(lowerWord.charAt(i))) 
                vowelPositions.add(i);

        if (vowelPositions.isEmpty())
            return new ArrayList<>() {{ add(word); }};

        List<String> result = new ArrayList<>();
        int prevPos = 0;

        for (int i = 0; i < vowelPositions.size(); i++) {
            int currentVowelPos = vowelPositions.get(i);
            if (i > 0) {
                int splitPos = (currentVowelPos + vowelPositions.get(i - 1) + 1) / 2;
                if (word.charAt(splitPos) == 'ь' || word.charAt(splitPos) == 'ъ') splitPos++;
                result.add(word.substring(prevPos, splitPos));
                prevPos = splitPos;
            };
        };
        result.add(word.substring(prevPos));

        return result;
    }

    /// <summary>
    ///     Сокращаем одно слово через дефис
    /// </summary>
    /// <param name="input"></param>
    /// <param name="maxLength"></param>
    /// <returns></returns>
    static String shortenOneWord(String input, int maxLength, String symbol) 
	{
        if (input == null || input.isEmpty() || maxLength <= 0) return "";
        List<String> syllables = splitWordIntoSyllables(input);
        if (syllables.size() < 3) return input.substring(0, maxLength) + "~0";

        maxLength -= symbol.length();

        String fullString = String.join("", syllables);
        int l = -1;
        do {
            if (syllables.size() < 3)
                return input.substring(0, --maxLength) + "~0";

            l = syllables.size() / 2;
            syllables.remove(l);
            fullString = String.join("", syllables);
        } 
		while (fullString.length() > maxLength);

        syllables.add(l, symbol);
        fullString = String.join("", syllables);
        return fullString;
    }

    /// <summary>
    ///     Сокращаем два слова
    /// </summary>
    /// <param name="input"></param>
    /// <param name="maxLength"></param>
    /// <param name="afterwords"></param>
    /// <returns></returns>
    static String shortenTwoWords(String input, int maxLength, int afterwords) 
	{
        maxLength -= (1 + String.valueOf(Math.abs(afterwords)).length());
        if (input.length() < -maxLength) return input + "~" + afterwords;
        String shorten = shortenOneWord(input, maxLength, "-");
        return shorten + "~" + afterwords;
    }
    
    public static String trimString(String input, int maxLength, String space, String symbol) 
	{
        if (input == null || input.isEmpty() || maxLength <= 0) return "";
        List<String> words = splitTextIntoWords(input);
        if (words.isEmpty()) return "";

        if (words.size() == 1) return shortenOneWord(words.get(0), maxLength, "-");
        if (words.size() == 2) return shortenTwoWords(words.get(0), maxLength, 1);

        String fullString = String.join(space, words);
        if (fullString.length() <= maxLength) return fullString;

        int baseCount = words.size();
        int maxWordsLength = maxLength - space.length() * 2 + symbol.length();

        int l = -1;
        do {
            if (words.size() == 2)
                return shortenTwoWords(words.get(0), maxLength, baseCount - 1);

            l = words.size() / 2;
            words.remove(l);
            fullString = String.join(space, words);
        } 
		while (fullString.length() > maxWordsLength);

        words.add(l, symbol);
        fullString = String.join(space, words);
        return fullString;
    }
    
    public static void Test() 
	{
		System.out.println(trimString("Ехал грека через реку, видит грека в реке рак", 30, " ", "..") );
        System.out.println(trimString("Череззаборногузадерищенский кактусообразный параллепипед", 45, " ", "..") );
        System.out.println(trimString("Череззаборногузадерищенский кактусообразный параллепипед", 22, " ", "..") );
        System.out.println(trimString("Череззаборногузадерищенский параллепипед", 22, " ", "..") );
        System.out.println(trimString("Преподавательница", 12, " ", "..") );
	}
	
	public static void main(String[] args) { Test(); }
}
