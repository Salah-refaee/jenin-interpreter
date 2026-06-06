package modules.nativemods;

import org.jenin.sr.api.JeninModule;
import org.jenin.sr.interpreter.Interpreter;
import org.jenin.sr.functions.NativeFunc;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileModule implements JeninModule {
    public void register(Interpreter interpreter) {

        interpreter.registerNativeFunc("readFile", new NativeFunc("readFile", 1, (scope) -> {
            // ARGS: path
            try {
                String path = String.valueOf(scope.get("path", scope));
                return new String(Files.readAllBytes(Paths.get(path)));
            } catch (IOException e) {
                // check if variable path is defined (used as argument)
                if (!scope.has("path")) throw new RuntimeException("readFile: path is unknown");
                 
                else throw new RuntimeException("readFile: " + e.getMessage());
            }
        }), true);

        interpreter.registerNativeFunc("writeFile", new NativeFunc("writeFile", 2, (scope) -> {
            // ARGS: path, content
             try {
                String path    = String.valueOf(scope.get("path",    scope));
                String content = String.valueOf(scope.get("content", scope));
                Files.write(Paths.get(path), content.getBytes());
            } catch (IOException e) {
                // check if variables path/content are defined (used as arguments)
                if (!scope.has("path"))    throw new RuntimeException("writeFile: path is unknown");
                if (!scope.has("content")) throw new RuntimeException("writeFile: content is unknown");
                else throw new RuntimeException("writeFile: " + e.getMessage());
            }
            return null;
        }), true);

        interpreter.registerNativeFunc("fileExists", new NativeFunc("fileExists", 1, (scope) -> {
            // ARGS: path 
            try {
                String path = String.valueOf(scope.get("path", scope));
                return new java.io.File(path).exists();
            } catch (Exception e) {
                // check if variable path is defined (used as argument)
                if (!scope.has("path")) throw new RuntimeException("fileExists: path is unknown");

                throw new RuntimeException("fileExists: " + e.getMessage());
            }
        }), true);
    }
}
