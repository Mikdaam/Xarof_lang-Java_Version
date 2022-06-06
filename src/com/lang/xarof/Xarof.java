package com.lang.xarof;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class Xarof {
	
	static boolean hadError = false;
	
	public static void main(String[] args) throws IOException {
		if (args.length > 1) {
			System.out.println("Usage: xarof [script]");
			System.exit(64);
			return;
		} else if(args.length == 1) {
			runFile(args[0]);
		} else {
			runPrompt();
		}
	}

	private static void runFile(String path) throws IOException {
		Objects.requireNonNull(path, "path can't be null");
		
		if (hadError) {
			System.exit(65);
			return;
		}
		
		var bytes = Files.readAllBytes(Paths.get(path));
		run(new String(bytes, Charset.defaultCharset()));
	}

	private static void runPrompt() throws IOException {
		var input = new InputStreamReader(System.in);
		var reader = new BufferedReader(input);
		
		while(true) {
			System.out.print("> ");
			var line = reader.readLine();
			if(line == null) {
				break;
			}
			run(line);
			hadError = false;
		}
	}
	
	private static void run(String source) {
		var scanner = new Scanner(source);
		var tokens = scanner.scanTokens();
		
		for (var token : tokens) {
			System.out.println(token);
		}
	}
	
	static void error(int line, String message) {
		report(line, "", message);
	}
	
	private static void report(int line, String where, String message) {
		System.err.println("[line " + line + "] Error" + where + ": " + message);
		hadError = true;
	}
}
