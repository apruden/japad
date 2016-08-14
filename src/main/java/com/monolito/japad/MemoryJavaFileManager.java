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
 * 
 * 
 */
public final class MemoryJavaFileManager extends
        ForwardingJavaFileManager<JavaFileManager> {

    public final static String EXT = ".java";
    private Map<String, byte[]> classBytes;

    /**
     * 
     * @param fileManager
     */
    public MemoryJavaFileManager(JavaFileManager fileManager) {
        super(fileManager);
        classBytes = new HashMap<String, byte[]>();
    }

    /**
     * 
     * @return
     */
    public Map<String, byte[]> getClassBytes() {
        return classBytes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.tools.ForwardingJavaFileManager#close()
     */
    @Override
    public void close() throws IOException {
        classBytes = new HashMap<String, byte[]>();
    }

    /**
     * 
     */
    @Override
    public void flush() throws IOException {
    }

    /**
     * 
     */
    public JavaFileObject getJavaFileForOutput(
            JavaFileManager.Location location, String className, Kind kind,
            FileObject sibling) throws IOException {
        if (kind == Kind.CLASS) {
            return new ClassOutputBuffer(className);
        }
        return super.getJavaFileForOutput(location, className, kind, sibling);
    }

    /**
	 * 
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
            if (name.endsWith(EXT))
                newUri.replace(newUri.length() - EXT.length(), newUri.length(),
                        EXT);
            return URI.create(newUri.toString());
        } catch (Exception exp) {
            return URI.create("mfm:///com/sun/script/java/java_source");
        }
    }

    /**
     * 
     * @author alex
     * 
     */
    private static class StringInputBuffer extends SimpleJavaFileObject {
        final String code;

        /**
         * 
         * @param name
         * @param code
         */
        StringInputBuffer(String name, String code) {
            super(toURI(name), Kind.SOURCE);
            this.code = code;
        }

        /**
         * 
         */
        @Override
        public CharBuffer getCharContent(boolean ignoreEncodingErrors) {
            return CharBuffer.wrap(code);
        }

        /**
         * Returns an opened reader for this buffer
         * 
         * @return an opened reader for this buffer
         */
        @SuppressWarnings("unused")
        public Reader openReader() {
            return new StringReader(code);
        }
    }

    /**
     * 
     * @author alex
     * 
     */
    private class ClassOutputBuffer extends SimpleJavaFileObject {
        private String name;

        /**
         * 
         * @param name
         */
        ClassOutputBuffer(String name) {
            super(toURI(name), Kind.CLASS);
            this.name = name;
        }

        /**
         * 
         */
        @Override
        public OutputStream openOutputStream() {
            return new FilterOutputStream(new ByteArrayOutputStream()) {
                @Override
                public void close() throws IOException {
                    out.close();
                    ByteArrayOutputStream bos = (ByteArrayOutputStream) out;
                    classBytes.put(name, bos.toByteArray());
                }
            };
        }
    }
}