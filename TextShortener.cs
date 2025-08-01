using System;
using System.Collections.Generic;
using System.Text;

public static class TextShorterner
{
    /// <summary>
    ///     Делим предложение на слова
    /// </summary>
    /// <param name="input"></param>
    /// <returns></returns>
    internal static List<string> SplitTextIntoWords(string input)
    {
        List<string> words = new List<string>();
        StringBuilder currentWord = new StringBuilder();

        foreach (char c in input)
        {
            if (char.IsLetterOrDigit(c) || c == '\'' || c == '-')
            {
                currentWord.Append(c);
            }
            else
            {
                if (currentWord.Length > 0)
                {
                    words.Add(currentWord.ToString());
                    currentWord.Clear();
                };
            };
        };

        if (currentWord.Length > 0)
            words.Add(currentWord.ToString());

        return words;
    }

    /// <summary>
    ///     Делим слово по слогам
    /// </summary>
    /// <param name="word"></param>
    /// <returns></returns>
    internal static List<string> SplitWordIntoSyllables(string word)
    {
        string lowerWord = word.ToLower();
        List<int> vowelPositions = new List<int>();

        HashSet<char> vowels = new HashSet<char> { 
            'а', 'е', 'ё', 'и', 'о', 'у', 'ы', 'э', 'ю', 'я', // RUS
            'a', 'e', 'i', 'o', 'u' , 'y' }; // ENG

        for (int i = 0; i < lowerWord.Length; i++)
            if (vowels.Contains(lowerWord[i])) 
                vowelPositions.Add(i);

        if (vowelPositions.Count == 0)
            return new List<string> { word };

        List<string> result = new List<string>();
        int prevPos = 0;

        for (int i = 0; i < vowelPositions.Count; i++)
        {
            int currentVowelPos = vowelPositions[i];
            if (i > 0)
            {
                int splitPos = (currentVowelPos + vowelPositions[i - 1] + 1) / 2;
                if (word[splitPos] == 'ь' || word[splitPos] == 'ъ') splitPos++;
                result.Add(word.Substring(prevPos, splitPos - prevPos));
                prevPos = splitPos;
            };
        };
        result.Add(word.Substring(prevPos));

        return result;
    }

    /// <summary>
    ///     Сокращаем одно слово через дефис
    /// </summary>
    /// <param name="input"></param>
    /// <param name="maxLength"></param>
    /// <returns></returns>
    internal static string ShortenOneWord(string input, int maxLength, string symbol = "-")
    {
        if (string.IsNullOrEmpty(input) || maxLength <= 0) return string.Empty;
        List<string> syllables = SplitWordIntoSyllables(input);
        if (syllables.Count < 3) return $"{input.Substring(0, maxLength)}~0";

        maxLength -= symbol.Length;

        string fullString = string.Join("", syllables);
        int l = -1;
        do
        {
            if (syllables.Count < 3)
                return $"{input.Substring(0,--maxLength)}~0";

            l = syllables.Count / 2;
            syllables.RemoveAt(l);
            fullString = string.Join("", syllables);
        } 
        while (fullString.Length > maxLength);

        syllables.Insert(l, symbol);
        fullString = string.Join("", syllables);
        return fullString;
    }

    /// <summary>
    ///     Сокращаем два слова
    /// </summary>
    /// <param name="input"></param>
    /// <param name="maxLength"></param>
    /// <param name="afterwords"></param>
    /// <returns></returns>
    internal static string ShortenTwoWords(string input, int maxLength, int afterwords = 1)
    {
        maxLength -= (1 + Math.Abs(afterwords).ToString().Length);
        if (input.Length < -maxLength) return $"{input}~{afterwords}";
        string shorten = ShortenOneWord(input, maxLength);
        return $"{shorten}~{afterwords}";
    }

    public static string TrimString(string input, int maxLength, string space = " ", string symbol = "..")
    {
        if (string.IsNullOrEmpty(input) || maxLength <= 0) return string.Empty;
        List<string> words = SplitTextIntoWords(input);
        if (words.Count == 0) return string.Empty;

        if (words.Count == 1) return ShortenOneWord(words[0], maxLength);
        if (words.Count == 2) return ShortenTwoWords(words[0], maxLength);

        string fullString = string.Join(space, words);
        if (fullString.Length <= maxLength) return fullString;

        int baseCount = words.Count;
        int maxWordsLength = maxLength - space.Length * 2 + symbol.Length;

        int l = -1;
        do
        {
            if(words.Count == 2)
                return ShortenTwoWords(words[0], maxLength, baseCount-1);

            l = words.Count / 2;
            words.RemoveAt(l);            
            fullString = string.Join(space, words);
        } 
        while (fullString.Length > maxWordsLength);

        words.Insert(l, symbol);
        fullString = string.Join(space, words);
        return fullString;
    }


    internal static void Test()
    {
        string bs1 = TextShorterner.TrimString("Ехал грека через реку, видит грека в реке рак", 30);
        string bs2 = TextShorterner.TrimString("Череззаборногузадерищенский кактусообразный параллепипед", 45);
        string bs3 = TextShorterner.TrimString("Череззаборногузадерищенский кактусообразный параллепипед", 22);
        string bs4 = TextShorterner.TrimString("Череззаборногузадерищенский параллепипед", 22);
        string bs5 = TextShorterner.TrimString("Преподавательница", 12);
    }
}