class TextShortener:
    def split_text_into_words(self, input_text):
        words = []
        current_word = []

        for char in input_text:
            if char.isalnum() or char in ("'", '-'):
                current_word.append(char)
            else:
                if current_word:
                    words.append(''.join(current_word))
                    current_word.clear()

        if current_word:
            words.append(''.join(current_word))

        return words

    def split_word_into_syllables(self, word):
        lower_word = word.lower()
        vowel_positions = []

        vowels = set('аеёиоуыэюяaeiouy')

        for i in range(len(lower_word)):
            if lower_word[i] in vowels:
                vowel_positions.append(i)

        if not vowel_positions:
            return [word]

        result = []
        prev_pos = 0

        for i in range(len(vowel_positions)):
            current_vowel_pos = vowel_positions[i]
            if i > 0:
                split_pos = (current_vowel_pos + vowel_positions[i - 1] + 1) // 2
                if word[split_pos] in ('ь', 'ъ'):
                    split_pos += 1
                result.append(word[prev_pos:split_pos])
                prev_pos = split_pos

        result.append(word[prev_pos:])
        return result

    def shorten_one_word(self, input_word, max_length, symbol="-"):
        if not input_word or max_length <= 0:
            return ''
        syllables = self.split_word_into_syllables(input_word)
        if len(syllables) < 3:
            return f"{input_word[:max_length]}~0"

        max_length -= len(symbol)

        full_string = ''.join(syllables)
        l = -1
        while True:
            if len(syllables) < 3:
                return f"{input_word[:max_length-1]}~0"

            l = len(syllables) // 2
            syllables.pop(l)
            full_string = ''.join(syllables)
            if len(full_string) <= max_length:
                break

        syllables.insert(l, symbol)
        full_string = ''.join(syllables)
        return full_string

    def shorten_two_words(self, input_word, max_length, afterwords=1):
        max_length -= (1 + len(str(abs(afterwords))))
        if len(input_word) < -max_length:
            return f"{input_word}~{afterwords}"
        shortened = self.shorten_one_word(input_word, max_length)
        return f"{shortened}~{afterwords}"

    def trim_string(self, input_text, max_length, space=" ", symbol=".."):
        if not input_text or max_length <= 0:
            return ''
        words = self.split_text_into_words(input_text)
        if not words:
            return ''

        if len(words) == 1:
            return self.shorten_one_word(words[0], max_length)
        if len(words) == 2:
            return self.shorten_two_words(words[0], max_length)

        full_string = space.join(words)
        if len(full_string) <= max_length:
            return full_string

        base_count = len(words)
        max_words_length = max_length - len(space) * 2 + len(symbol)

        l = -1
        while True:
            if len(words) == 2:
                return self.shorten_two_words(words[0], max_length, base_count - 1)

            l = len(words) // 2
            words.pop(l)
            full_string = space.join(words)
            if len(full_string) <= max_words_length:
                break

        words.insert(l, symbol)
        full_string = space.join(words)
        return full_string

    def test(self):
        print( self.trim_string("Ехал грека через реку, видит грека в реке рак", 30) )
        print( self.trim_string("Череззаборногузадерищенский кактусообразный параллепипед", 45) )
        print( self.trim_string("Череззаборногузадерищенский кактусообразный параллепипед", 22) )
        print( self.trim_string("Череззаборногузадерищенский параллепипед", 22) )
        print( self.trim_string("Преподавательница", 12) )
        
if __name__ == "__main__":
    t = TextShortener();
    t.test()
    pass