package com.monolito.japad;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;

/**
 * <p>Title: MemoryJavaFileManager</p>
 * <p>Description: JavaFileManager that keeps compiled .class bytes in memory.</p> 
 * <p>Project: <a href="https://github.com/nickman/javax-scripting">JSR-233 Java Scripting</a></p>
 * <p>Packaged and maintained by Whitehead (nwhitehead AT heliosdev DOT org)</p>
 * <p><code>com.sun.script.java.MemoryJavaFileManager</code></p>
 */
public final class MemoryJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {                 

    /** Java source file extension. */
    public final static String EXT = ".java";

    /** The compiled class bytes */
    private Map<String, byte[]> classBytes;
    
    /**
     * Creates a new MemoryJavaFileManager
     * @param fileManager The in memory file manager
     */
    public MemoryJavaFileManager(JavaFileManager fileManager) {
        super(fileManager);
        classBytes = new HashMap<String, byte[]>();
    }

    /**
     * Returns the compiled class byte codes
     * @return a map of compiled class byte codes keyed by the class name
     */
    public Map<String, byte[]> getClassBytes() {
        return classBytes;
    }
   
    /**
     * {@inheritDoc}
     * @see javax.tools.ForwardingJavaFileManager#close()
     */
    @Override
	public void close() throws IOException {
        classBytes = new HashMap<String, byte[]>();
    }

    /**
     * {@inheritDoc}
     * @see javax.tools.ForwardingJavaFileManager#flush()
     */
    @Override
	public void flush() throws IOException {
    }

    /**
     * <p>Title: StringInputBuffer</p>
     * <p>Description: A file object used to represent Java source coming from a string.</p> 
     * <p>Project: <a href="https://github.com/nickman/javax-scripting">JSR-233 Java Scripting</a></p>
     * <p>Packaged and maintained by Whitehead (nwhitehead AT heliosdev DOT org)</p>
     * <p><code>com.sun.script.java.MemoryJavaFileManager.StringInputBuffer</code></p>
     */
    private static class StringInputBuffer extends SimpleJavaFileObject {
        final String code;
        
        StringInputBuffer(String name, String code) {
            super(toURI(name), Kind.SOURCE);
            this.code = code;
        }
        
        @Override
		public CharBuffer getCharContent(boolean ignoreEncodingErrors) {
            return CharBuffer.wrap(code);
        }

        /**
         * Returns an opened reader for this buffer
         * @return an opened reader for this buffer
         */
        @SuppressWarnings("unused")
		public Reader openReader() {
            return new StringReader(code);
        }
    }

    /**
     * <p>Title: ClassOutputBuffer</p>
     * <p>Description: A file object that stores Java bytecode into the classBytes map.</p> 
     * <p>Project: <a href="https://github.com/nickman/javax-scripting">JSR-233 Java Scripting</a></p>
     * <p>Packaged and maintained by Whitehead (nwhitehead AT heliosdev DOT org)</p>
     * <p><code>com.sun.script.java.MemoryJavaFileManager.ClassOutputBuffer</code></p>
     */
    private class ClassOutputBuffer extends SimpleJavaFileObject {
        private String name;

        ClassOutputBuffer(String name) { 
            super(toURI(name), Kind.CLASS);
            this.name = name;
        }

        @Override
		public OutputStream openOutputStream() {
            return new FilterOutputStream(new ByteArrayOutputStream()) {
                @Override
				public void close() throws IOException {
                    out.close();
                    ByteArrayOutputStream bos = (ByteArrayOutputStream)out;
                    classBytes.put(name, bos.toByteArray());
                }
            };
        }
    }
    
    /**
     * {@inheritDoc}
     * @see javax.tools.ForwardingJavaFileManager#getJavaFileForOutput(javax.tools.JavaFileManager.Location, java.lang.String, javax.tools.JavaFileObject.Kind, javax.tools.FileObject)
     */
    @Override
	public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location,
                                    String className,
                                    Kind kind,
                                    FileObject sibling) throws IOException {
        if (kind == Kind.CLASS) {
            return new ClassOutputBuffer(className);
        }
		return super.getJavaFileForOutput(location, className, kind, sibling);
    }
    
    /*
     * (non-Javadoc)
     * @see javax.tools.ForwardingJavaFileManager#getClassLoader(javax.tools.JavaFileManager.Location)
     */
	@Override
	public ClassLoader getClassLoader(Location location) {
		return new java.security.SecureClassLoader() {
			@Override
			protected Class<?> findClass(String name)
					throws ClassNotFoundException {
				byte[] b = classBytes.get(name);
				if (b != null)
					return defineClass(name, b, 0, b.length);
				return super.findClass(name);
			}
		};
	}

	/*
	 * 
	 */
    static JavaFileObject makeStringSource(String name, String code) {
        return new StringInputBuffer(name, code);
    }

    /*
     * 
     */
    static URI toURI(String name) {
        File file = new File(name);
        if (file.exists()) {
            return file.toURI();
        }
		try {
		    final StringBuilder newUri = new StringBuilder();
		    newUri.append("mfm:///");
		    newUri.append(name.replace('.', '/'));
		    if(name.endsWith(EXT)) newUri.replace(newUri.length() - EXT.length(), newUri.length(), EXT);
		    return URI.create(newUri.toString());
		} catch (Exception exp) {
		    return URI.create("mfm:///com/sun/script/java/java_source");
		}
    }
}