package com.monolito.japad;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

public class DynamicCompiler {

	/**
	 * 
	 * @param fullName
	 * @param src
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 */
	public String compile(String fullName, String src) throws InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

		// We get an instance of JavaCompiler. Then
		// we create a file manager
		// (our custom implementation of it)
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		JavaFileManager fileManager = new MemoryJavaFileManager(compiler.getStandardFileManager(null, null, null));

		// Dynamic compiling requires specifying
		// a list of "files" to compile. In our case
		// this is a list containing one "file" which is in our case
		// our own implementation (see details below)
		List<JavaFileObject> jfiles = new ArrayList<>();
		jfiles.add(MemoryJavaFileManager.makeStringSource(fullName + ".java", src));

		// We specify a task to the compiler. Compiler should use our file
		// manager and our list of "files".
		// Then we run the compilation with call()
		CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, jfiles);
		task.call();

		StringBuilder sb = new StringBuilder();

	    for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
	      sb.append(diagnostic.getCode() + "\n");
	      sb.append(diagnostic.getKind() + "\n");
	      sb.append(diagnostic.getPosition() + "\n");
	      sb.append(diagnostic.getStartPosition() + "\n");
	      sb.append(diagnostic.getEndPosition() + "\n");
	      sb.append(diagnostic.getSource() + "\n");
	      sb.append(diagnostic.getMessage(null) + "\n");
	    }

		Method main = fileManager.getClassLoader(null).loadClass("Main").getMethod("main");
		main.invoke(null);

	    return sb.toString();
	}
}
