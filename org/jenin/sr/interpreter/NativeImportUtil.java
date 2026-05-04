package org.jenin.sr.interpreter;

/* used to import native .class files that contains functions for Jenin */

import org.jenin.sr.api.JeninModule;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class NativeImportUtil {
  private final String path;
  private final Interpreter interpreter;

  public NativeImportUtil(String path, Interpreter interpreter) {
    this.path = path;
    this.interpreter = interpreter;
  }

  public void load() throws Exception {
    File file = new File(path);

    // parent dir as classpath
    URL[] urls = new URL[] { file.getParentFile().toURI().toURL() };
    URLClassLoader loader = new URLClassLoader(urls);

    String className = file.getName().replace(".class", "");

    // load class
    Class<?> clazz = loader.loadClass(className);

    // check interface
    if (!JeninModule.class.isAssignableFrom(clazz)) {
      throw new RuntimeException("Class does not implement JeninModule");
    }

    // instantiate
    JeninModule module = (JeninModule) clazz.getDeclaredConstructor().newInstance();

    // register into interpreter
    module.register(interpreter);
  }
}
