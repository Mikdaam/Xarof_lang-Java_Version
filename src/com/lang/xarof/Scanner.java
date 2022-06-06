package com.lang.xarof;

import java.util.ArrayList;
import java.util.HashMap;

import static com.lang.xarof.TokenType.*; // not good but more pratic here

public class Scanner {
	private final String source;
	private final ArrayList<Token> tokens = new ArrayList<>();
	private static final HashMap<String, TokenType> reservedKeywords;
	
	// i don't know yet what the following do 
	// TODO:i have to put the list in the alphabetic order
	static {
		reservedKeywords = new HashMap<>();
		reservedKeywords.put("true", TRUE);
		reservedKeywords.put("false", FALSE);
		reservedKeywords.put("and", AND);
		reservedKeywords.put("or", OR);
		
		reservedKeywords.put("if", IF);
		reservedKeywords.put("else", ELSE);
		reservedKeywords.put("for", FOR);
		reservedKeywords.put("while", WHILE);
		reservedKeywords.put("return", RETURN);
		reservedKeywords.put("fun", FUN);
		reservedKeywords.put("var", VAR);
		
		reservedKeywords.put("super", SUPER);
		reservedKeywords.put("this", THIS);
		reservedKeywords.put("class", CLASS);
		reservedKeywords.put("nil", NIL);
		
		reservedKeywords.put("print", PRINT);
		reservedKeywords.put("input", INPUT);
	}
	
	// neccessary fields to track where the lexer is in source code.
	private int start = 0;
	private int current = 0;
	private int line = 1;
	
	public Scanner(String sourceCode) {
		this.source = sourceCode;
	}
	
	public ArrayList<Token> scanTokens() {
		while (!isAtEnd()) {
			start = current;
			scanToken();
		}
		
		tokens.add(new Token(EOF, "", null, line));
		return tokens;
	}
	
	private void scanToken() {
		char c = advance();
		
		switch (c) {
			case '(': addToken(LEFT_PARENTHESE); break;
			case ')': addToken(RIGHT_PARENTHESE); break;
			case '{': addToken(LEFT_BRACE); break;
			case '}': addToken(RIGHT_BRACE); break;
			case ',': addToken(COMMA); break;
			case '.': addToken(DOT); break;
			case '-': addToken(MINUS); break;
			case '+': addToken(PLUS); break;
			case ';': addToken(SEMICOLON); break;
			case '*': addToken(STAR); break;
			case '!': 
				addToken(match('=') ? BANG_EQUAL : BANG); 
				break;
			case '=': 
				addToken(match('=') ? EQUAL_EQUAL : EQUAL); 
				break;
			case '<': 
				addToken(match('=') ? LESS_EQUALS : LESS); 
				break;
			case '>': 
				addToken(match('=') ? GREATER_EQUAL : GREATER); 
				break;
			case '/': 
				if (match('/')) {
					// to select comment
					while (peek() != '\n' && !isAtEnd()) {
						advance();
					}
				} else {
					addToken(SLASH);
				}
				break;
			case ' ':
			case '\r':
			case '\t':
				break;
			case '\n':
				line++;
				break;
			case '"':
				getString();
				break;
				
			default:
				if (isDigit(c)) {
					getNumber();
				} else if (isAlpha(c)) {
					getIdentifier();
				} else {
					Xarof.error(line, "Unexpected character.");
					break;
				}
		}
	}

	private boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}
	
	private boolean isAlpha(char c) {
		return (c >= 'a' && c <= 'z') ||
				(c >= 'A' && c <= 'Z') ||
				c == '_';
	}
	
	private boolean isAlphaNumeric(char c) {
		return isAlpha(c) || isDigit(c);
	}
	
	private void getNumber() {
		while (isDigit(peek())) {
			advance();
		}
		
		if (peek() == '.' && isDigit(peekNext())) {
			advance();
			
			while(isDigit(peek())) { advance(); }
		}
		
		addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
	}

	private void getIdentifier() {
		while (isAlphaNumeric(peek())) {
			advance();
		}
		
		String text = source.substring(start, current);
		TokenType type = reservedKeywords.getOrDefault(text, IDENTIFIER);
		
		addToken(type);
	}

	private char advance() {
		return source.charAt(current++);
	}
	
	private char peek() {
		if(isAtEnd()) { return '\0'; }
		return source.charAt(current);
	}
	
	private char peekNext() {
		if (current + 1 >= source.length()) return '\0';
		return source.charAt(current + 1);
	}
	
	private void getString() {
		while (peek() != '"' && !isAtEnd()) {
			if(peek() == '\n') { line++; }
			advance();
		}
		
		if(isAtEnd()) {
			Xarof.error(line, "Unterminated string.");
			return;
		}
		
		advance();
		
		var value = source.substring(start + 1, current - 1);
		addToken(STRING, value);
	}	
	
	private boolean match(char expected) {
		if (isAtEnd()) {return false;}
		if (source.charAt(current) != expected ) {
			return false;
		}
		
		current++;
		return true;
	}
	
	private void addToken(TokenType type) {
		addToken(type, null);
	}

	private void addToken(TokenType type, Object literal) {
		String text = source.substring(start, current);
		tokens.add(new Token(type, text, literal, line));
	}

	private boolean isAtEnd() {
		return current >= source.length();
	}
}
