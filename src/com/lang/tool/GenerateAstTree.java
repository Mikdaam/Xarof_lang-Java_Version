package com.lang.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAstTree {
	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.err.println("Usage: generate_ast_tree <output directory>");
			System.exit(64);
		}
		
		/*
		 * TODO: I had to review this code. There are some bullshit here
		*/
		var outputDirPath = args[0];
		defineAstTree(outputDirPath, "Expr", Arrays.asList(
				"Binary 	: Expr left, Token operator, Expr right",
				"Grouping 	: Expr expresion",
				"Literal 	: Object value",
				"Unary 		: Token operator, Expr right"
		));
	}

	private static void defineAstTree(String outputDirPath, String baseName, List<String> types) throws IOException {
		var path = outputDirPath + "/" + baseName + ".java";
		var writer = new PrintWriter(path, "UTF-8");
		
		writer.println("package com.lang.xarof;");
		writer.println();
		writer.println("import java.util.List;");
		writer.println();
		writer.println("abstract class " + baseName + " {");
		
		defineVisitor(writer, baseName, types);
		
		for (var type : types) {
			var className = type.split(":")[0].trim();
			var fields = type.split(":")[1].trim();
			defineType(writer, baseName, className, fields);
		}
		
		// The base accept() method
		writer.println();
		writer.println("	abstract <R> R accept(Visitor<R> visitor);");
		
		writer.println("}");
		writer.close();
	}

	private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
		writer.println("	interface Visitor<R> {");
		
		for (String type : types) {
			var typeName = type.split(":")[0].trim();
			writer.println("		R visit" + typeName + baseName + "(" + typeName + " "  +
			baseName.toLowerCase() + ");");
		}
		
		writer.println("	}");
	}

	private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
		writer.println("	static class " + className + " extends " + baseName + " {");
		
		// Constructor.
		writer.println("		" + className + "(" + fieldList + ") {");
		
		var fields = fieldList.split(", ");
		for (var field : fields) {
			var name = field.split(" ")[1];
			writer.println("			this." + name + " = " + name + ";");
		}
		
		writer.println("	}");
		
		writer.println();
		writer.println("	@Override");
		writer.println("	<R> R accept(Visitor<R> visitor) {");
		writer.println("		return visitor.visit" + className + baseName + "(this);");
		writer.println("	}");
		
		// Fields
		writer.println();
		for (var field : fields) {
			writer.println("	final " + field + ";");
		}
		
		writer.println("	}");
	}
}
