class TextShortener {
  
    static charIsLetterOrGigit(c)
    {
        return (c.toLowerCase() != c.toUpperCase()) || (!isNaN(c - parseFloat(c)));
    }
  
    /// Делим предложение на слова
  	static splitTextIntoWords(input) 
  	{
		  const words = [];
		  let currentWord = '';
  
		  for (const c of input) 
		  {
  			if (TextShortener.charIsLetterOrGigit(c) || c === '\'' || c === '-') 
			  {
  				currentWord += c;
			  } 
			  else 
			  {
  				if (currentWord.length > 0) {
					  words.push(currentWord);
					  currentWord = '';
				  };
			  };
		  };
  
		  if (currentWord.length > 0) 
		  {
  			words.push(currentWord);
		  };
  
		  return words;
	  }


    ///     Делим слово по слогам
    static splitWordIntoSyllables(word) 
    {
        const lowerWord = word.toLowerCase();
        const vowelPositions = [];

        const vowels = new Set(['а', 'е', 'ё', 'и', 'о', 'у', 'ы', 'э', 'ю', 'я', 'a', 'e', 'i', 'o', 'u', 'y']);

        for (let i = 0; i < lowerWord.length; i++) 
        {
            if (vowels.has(lowerWord[i])) {
                vowelPositions.push(i);
            };
        };

        if (vowelPositions.length === 0) 
        {
            return [word];
        };

        const result = [];
        let previousPosition = 0;

        for (let i = 0; i < vowelPositions.length; i++) 
        {
            const currentVowelPosition = vowelPositions[i];
            if (i > 0) 
            {
                let splitPosition = Math.floor((currentVowelPosition + vowelPositions[i - 1] + 1) / 2);
                if (word[splitPosition] === 'ь' || word[splitPosition] === 'ъ') splitPosition++;
                result.push(word.substring(previousPosition, splitPosition));
                previousPosition = splitPosition;
            };
        };
        result.push(word.substring(previousPosition));

        return result;
    }

    ///     Сокращаем одно слово через дефис
    static shortenOneWord(input, maxLength, symbol = "-") 
    {
        if (!input || maxLength <= 0) return '';
        const syllables = TextShortener.splitWordIntoSyllables(input);
        if (syllables.length < 3) return `${input.substring(0, maxLength)}~0`;

        maxLength -= symbol.length;

        let fullString = syllables.join("");
        let l = -1;
        do 
        {
            if (syllables.length < 3) 
            {
                return `${input.substring(0, --maxLength)}~0`;
            };

            l = Math.floor(syllables.length / 2);
            syllables.splice(l, 1);
            fullString = syllables.join("");
        } 
        while (fullString.length > maxLength);

        syllables.splice(l, 0, symbol);
        fullString = syllables.join("");
        return fullString;
    }

    ///     Сокращаем два слова
    static shortenTwoWords(input, maxLength, afterwords = 1) 
    {
        maxLength -= (1 + Math.abs(afterwords).toString().length);
        if (input.length < -maxLength) return `${input}~${afterwords}`;
        const shorten = TextShortener.shortenOneWord(input, maxLength);
        return `${shorten}~${afterwords}`;
    }

    static trimString(input, maxLength, space = " ", symbol = "..") 
    {
        if (!input || maxLength <= 0) return '';
        const words = TextShortener.splitTextIntoWords(input);
        if (words.length === 0) return '';

        if (words.length === 1) return TextShortener.shortenOneWord(words[0], maxLength);
        if (words.length === 2) return TextShortener.shortenTwoWords(words[0], maxLength);

        let fullString = words.join(space);
        if (fullString.length <= maxLength) return fullString;

        const baseCount = words.length;
        const maxWordsLength = maxLength - space.length * 2 + symbol.length;

        let l = -1;
        do 
        {
            if (words.length === 2) 
            {
                return TextShortener.shortenTwoWords(words[0], maxLength, baseCount - 1);
            };

            l = Math.floor(words.length / 2);
            words.splice(l, 1);
            fullString = words.join(space);
        } 
        while (fullString.length > maxWordsLength);

        words.splice(l, 0, symbol);
        fullString = words.join(space);
        return fullString;
    }

    static test() 
    {
        console.log(TextShortener.trimString("Ехал грека через реку, видит грека в реке рак", 30));
        console.log(TextShortener.trimString("Череззаборногузадерищенский кактусообразный параллепипед", 45));
        console.log(TextShortener.trimString("Череззаборногузадерищенский кактусообразный параллепипед", 22));
        console.log(TextShortener.trimString("Череззаборногузадерищенский параллепипед", 22));
        console.log(TextShortener.trimString("Преподавательница", 12));
    }
}

TextShortener.test();