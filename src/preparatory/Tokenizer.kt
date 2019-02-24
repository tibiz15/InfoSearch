package preparatory

class Tokenizer : WordSplitter {
    override fun splitText(text: String, textAction: (word: String) -> Unit) {
        for (word in text.split("\n", " ", " ")) {
            val trimmed = word.trim(
                ' ',
                ' ',
                '…',
                '—',
                '!',
                '.',
                ',',
                '?',
                '"',
                '\'',
                '^',
                '-',
                ':',
                '=',
                ';',
                '&',
                '#',
                '№',
                '@',
                '%',
                '\n',
                '\t',
                '\r',
                '\b',
                ')',
                '(',
                '[',
                ']',
                '{',
                '}',
                '“',
                '”',
                '|',
                '<',
                '>'
            )

            if (!trimmed.isBlank()) {
                if (trimmed.length == 1) {
                    if (trimmed[0].isLetterOrDigit()) {
                        textAction(trimmed)
                    }
                } else {
                    textAction(trimmed)
                }
            }

        }
    }
}