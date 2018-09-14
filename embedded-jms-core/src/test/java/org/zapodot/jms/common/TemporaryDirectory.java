package org.zapodot.jms.common;

import com.google.common.io.Files;
import com.google.common.io.MoreFiles;
import com.google.common.io.RecursiveDeleteOption;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.io.File;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class TemporaryDirectory implements ParameterResolver, AfterEachCallback {

    private static final ExtensionContext.Namespace STORE = ExtensionContext.Namespace.create(TemporaryDirectory.class);

    private static final String KEY = "tempDir";

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface TempDir {
    }

    @Override
    public void afterEach(final ExtensionContext context) throws Exception {
        final File tempDir = context.getStore(STORE).remove(KEY, File.class);
        if (tempDir != null && tempDir.isDirectory()) {
            MoreFiles.deleteRecursively(tempDir.toPath(), RecursiveDeleteOption.ALLOW_INSECURE);
        }
    }

    @Override
    public boolean supportsParameter(final ParameterContext parameterContext,
                                     final ExtensionContext extensionContext) throws ParameterResolutionException {

        return parameterContext.isAnnotated(TempDir.class) && parameterContext.getParameter().getType()
                                                                              .isAssignableFrom(
                                                                                      File.class);
    }

    @Override
    public Object resolveParameter(final ParameterContext parameterContext,
                                   final ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext
                .getStore(STORE).getOrComputeIfAbsent(
                        KEY, key -> createTemporaryDir(), File.class);
    }

    private File createTemporaryDir() {
        return Files.createTempDir();
    }
}
