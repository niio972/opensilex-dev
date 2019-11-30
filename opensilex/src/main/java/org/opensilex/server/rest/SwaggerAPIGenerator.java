package org.opensilex.server.rest;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.*;
import io.swagger.annotations.*;
import io.swagger.jaxrs.Reader;
import io.swagger.jaxrs.config.*;
import io.swagger.models.*;
import java.io.*;
import java.nio.file.*;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.*;
import org.opensilex.utils.*;

public class SwaggerAPIGenerator {

    private static synchronized Swagger generate(String source) throws Exception {
        Swagger swagger = null;

        SwaggerContextService ctx = new SwaggerContextService();

        swagger = ctx.getSwagger();

        Set<Class<?>> classes = new HashSet<>();

        Map<String, Class<?>> availableAPI = ClassInfo.getAnnotatedClassesMap(Api.class);

        Path sourcePath = Paths.get(source);
        if (sourcePath.toFile().exists()) {
            try (Stream<Path> walk = Files.walk(sourcePath)) {

                walk.filter(Files::isRegularFile)
                        .forEach((Path p) -> {
                            String filename = p.getFileName().toString();

                            File filePath = p.toFile();
                            if (filePath.exists()) {
                                String absoluteDirectory = filePath.getParent();
                                String packageId = absoluteDirectory.substring(source.length()).replaceAll("\\\\|\\/", ".");

                                if (filename.endsWith(".java")) {
                                    String className = packageId + "." + filename.substring(0, filename.length() - 5);
                                    if (availableAPI.containsKey(className)) {
                                        classes.add(availableAPI.get(className));
                                    }
                                }
                            }
                        });

            }
        }

        if (classes.size() > 0) {

            Reader reader = new Reader(swagger);
            swagger = reader.read(classes);

            return swagger;
        }

        return null;
    }

    public static void main(String[] args) throws Exception {
        String source = args[0];
        String destination = args[1];

        Swagger swagger = generate(source);

        if (swagger != null) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(Include.NON_NULL);
            File swaggerFile = new File(destination);
            swaggerFile.createNewFile();
            mapper.writeValue(swaggerFile, swagger);
        }
    }
}
