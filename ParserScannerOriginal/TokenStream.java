// ConcreteSyntax.java

// Implementation of the Scanner for JAY

// This code DOES NOT implement a scanner for JAY. You have to complete
// the code and also make sure it implements a scanner for JAY - not something
// else.

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class TokenStream {

	// CHECK THE WHOLE CODE

	private boolean isEof = false;

	private char nextChar = ' '; // next character in input stream

	private BufferedReader input;

	// This function was added to make the main.
	public boolean isEoFile() {
		return isEof;
	}

	// Pass a filename for the program text as a source for the TokenStream.
	public TokenStream(String fileName) {
		try {
			input = new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + fileName);
			// System.exit(1); // Removed to allow ScannerDemo to continue
			// running after the input file is not found.
			isEof = true;
		}
	}

	public Token nextToken() { // Return next token type and value.
		Token t = new Token();
		t.setType("Other");
		t.setValue("");

		// First check for whitespace and bypass it.
		skipWhiteSpace();

		// Then check for a comment, and bypass it
		// but remember that / is also a division operator.
		while (nextChar == '/') {
			// Changed if to while to avoid the 2nd line being printed when
			// there
			// are two comment lines in a row.
			t.setType("Operator");
			t.setValue("" + nextChar);
			nextChar = readChar();
			
			if (nextChar == '/') { // If / is followed by another /
				t.setValue(t.getValue() + nextChar);
				while ((nextChar = readChar()) != '\n') {
					t.setValue(t.getValue() + nextChar);
				}
				nextChar = readChar();
				//System.out.println("This is a Comment:" + s1);
				t.setValue("");
			} else {
				// A slash followed by a backslash is an AND operator (/\).
				// 92 is \, the number is used since \ causes an error.
				if (nextChar == 92) {
					t.setValue("/" + nextChar);
					nextChar = readChar();
				} else
					// A slash followed by anything else must be an operator.
					t.setValue("/");
				t.setType("Operator");
				return t;
			}
		}

		// Then check for an operator; recover 2-character operators
		// as well as 1-character ones.
		skipWhiteSpace();
		if (isOperator(nextChar)) {

			t.setType("Operator");
			t.setValue(t.getValue() + nextChar);

			switch (nextChar) {
			
			case '<':
			case '>':
			case '!':
			case '=':
				nextChar = readChar();
				while(isOperator(nextChar)) {
					if(nextChar == '=') {
						t.setValue(t.getValue() + nextChar);
					}
					nextChar = readChar();
				}
				if(t.getValue().length()>2) {
					t.setType("Other");
				}
				
				return t;

			case 92: // look for the OR operator, \/
				// TO BE COMPLETED
				nextChar = readChar();
				while(isOperator(nextChar)) {
					if(nextChar == '|') {
						t.setValue(t.getValue() + nextChar);
					}
					nextChar = readChar();
				}
				if(t.getValue().length()>2) {
					t.setType("Other");
				}
				
				return t;
			default: // all other operators
				nextChar = readChar();
				return t;
			}
		}

		// Then check for a separator.
		if (isSeparator(nextChar)) {
			t.setType("Separator");
			t.setValue(t.getValue() + nextChar);
			nextChar = readChar();
			// TO BE COMPLETED
			return t;
		}

		// Then check for an identifier, keyword, or literal.
		if (isLetter(nextChar)) {
			// get an identifier
			t.setType("Identifier");
			while ((isLetter(nextChar) || isDigit(nextChar))) {
				t.setValue(t.getValue() + nextChar);
				nextChar = readChar();
			}
			// now see if this is a keyword
			if (isKeyword(t.getValue()))
				t.setType("Keyword");
			if (isEndOfToken(nextChar)) // If token is valid, returns.
				return t;
		}

		if (isDigit(nextChar)) { // check for integers
			t.setType("Integer-Literal");
			while (isDigit(nextChar)) {
				t.setValue(t.getValue() + nextChar);
				nextChar = readChar();
			}
			// An Integer-Literal is to be only followed by a space,
			// an operator, or a separator.
			if (isEndOfToken(nextChar)) // If token is valid, returns.
				return t;
		}

		if (isEof)
			return t;

		// Makes sure that the whole unknown token (Type: Other) is printed.
		while (!isEndOfToken(nextChar) && nextChar != 7) {
			if (nextChar == '!') {
				nextChar = readChar();
				if (nextChar == '=') { // looks for = after !
					nextChar = 7; // means next token is !=
					break;
				} else
					t.setValue(t.getValue() + "!");
			} else {
				t.setValue(t.getValue() + nextChar);
				nextChar = readChar();
			}
		}

		if (nextChar == 7) {
			if (t.getValue().equals("")) { // Looks for a !=
				t.setType("Operator"); // operator. If token is
				t.setValue("!="); // empty, sets != as token,
				nextChar = readChar();
			}

		} else
			t.setType("Other"); // otherwise, unknown token.

		return t;
	}

	private char readChar() {
		int i = 0;
		if (isEof)
			return (char) 0;
		System.out.flush();
		try {
			i = input.read();
		} catch (IOException e) {
			System.exit(-1);
		}
		if (i == -1) {
			isEof = true;
			return (char) 0;
		}
		return (char) i;
	}

	private boolean isKeyword(String s) {
		return (s.equals("boolean") || s.equals("if") || s.equals("else") || s.equals("int") || s.equals("main") || s.equals("void"));
	}

	private boolean isWhiteSpace(char c) {
		return (c == ' ' || c == '\t' || c == '\r' || c == '\n' || c == '\f');
	}

	private boolean isEndOfLine(char c) {
		return (c == '\r' || c == '\n' || c == '\f');
	}

	private boolean isEndOfToken(char c) { // Is the value a seperate token?
		return (isWhiteSpace(nextChar) || isOperator(nextChar)
				|| isSeparator(nextChar) || isEof);
	}

	private void skipWhiteSpace() {
		// check for whitespace, and bypass it
		while (!isEof && isWhiteSpace(nextChar)) {
			nextChar = readChar();
		}
	}

	private boolean isSeparator(char c) {
		return (c=='(' || c == ')' || c == '{' || c=='}' || c ==';' || c == ',');
	}

	private boolean isOperator(char c) {
 		return (c=='=' || c == '+' || c =='-' || c == '*' || c=='/' ||c=='<' || c == '>' || c =='!');
	}

	private boolean isLetter(char c) {
		return (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z');
	}

	private boolean isDigit(char c) {
		return (c >= '0' && c <= '9');
	}

	public boolean isEndofFile() {
		return isEof;
	}
}
