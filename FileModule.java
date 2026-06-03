import org.jenin.sr.api.JeninModule;
import org.jenin.sr.interpreter.Interpreter;
import org.jenin.sr.functions.NativeFunc;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileModule implements JeninModule {
    public void register(Interpreter interpreter) {

        interpreter.registerNativeFunc("readFile", new NativeFunc("readFile", 1, (scope) -> {
            String path = String.valueOf(scope.get("path", scope));
            try {
                return new String(Files.readAllBytes(Paths.get(path)));
            } catch (IOException e) {
                throw new RuntimeException("readFile: cannot read '" + path + "': " + e.getMessage());
            }
        }), true);

        interpreter.registerNativeFunc("writeFile", new NativeFunc("writeFile", 2, (scope) -> {
            String path    = String.valueOf(scope.get("path",    scope));
            String content = String.valueOf(scope.get("content", scope));
            try {
                Files.write(Paths.get(path), content.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("writeFile: cannot write '" + path + "': " + e.getMessage());
            }
            return null;
        }), true);

        interpreter.registerNativeFunc("fileExists", new NativeFunc("fileExists", 1, (scope) -> {
            String path = String.valueOf(scope.get("path", scope));
            return new java.io.File(path).exists();
        }), true);
    }
}
