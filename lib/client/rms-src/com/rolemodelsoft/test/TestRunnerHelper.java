package com.rolemodelsoft.test;

import junit.ui.*;
import org.apache.oro.text.regex.*;
import java.util.*;
import java.io.*;

public class TestRunnerHelper {
protected TestRunnerHelper() {
	super();
}
protected static String getLineContainingTestSuiteName(String exceptionString) 
{

	StringTokenizer tokenizer = new StringTokenizer(exceptionString, System.getProperty("line.separator"));
	String line = "";
	while (tokenizer.hasMoreTokens())
	{
		line = (String)tokenizer.nextToken();
		if (line.indexOf("TS_") == -1)
			line = "";
		else
			break;
	}
	
	if (line.equals(""))
		throw new RuntimeException("could not find the test suite to run.");

	
	return line;
}
protected static String getTestSuiteClassName(String exceptionStack) {
	Perl5Matcher matcher = new Perl5Matcher();
	Perl5Compiler compiler = new Perl5Compiler();
	Pattern beginningCompiledPattern, endCompiledPattern = null;

	String beginningPattern = "^\\s*\\w+\\s+";
	String endPattern = "\\.main.*$";
	try
	{
		beginningCompiledPattern = compiler.compile(beginningPattern);
		endCompiledPattern = compiler.compile(endPattern);
	}
	catch (MalformedPatternException e1)
	{
		throw new RuntimeException("Unsupported pattern: " + beginningPattern + "\nOr: " + endPattern);
	}

	String line = getLineContainingTestSuiteName(exceptionStack);
	line = Util.substitute(matcher, beginningCompiledPattern, new StringSubstitution(""), line);
	line = Util.substitute(matcher, endCompiledPattern, new StringSubstitution(""), line);
	line = line.trim();
	line = line.replace('/','.');
	
	return line;
}
public static void run() {
	RuntimeException e = new RuntimeException();
	e.fillInStackTrace();
	String exceptionText;
	try {
		throw e;
	} catch ( RuntimeException e2 ) { 
		StringWriter writer = new StringWriter();
		e2.printStackTrace(new PrintWriter(writer));
		exceptionText = writer.toString();
	}
	new TestRunner().main(new String[] {getTestSuiteClassName(exceptionText)});
}
}
