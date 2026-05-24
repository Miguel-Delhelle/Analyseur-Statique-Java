package fr.delweb.analyzer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AstIngenierieApplicationTests {

    @Test
    void contextLoads() {
    }

    public static String signatureToNameOfClass(String signature) throws Exception {
        String afterHash = signature.split("#")[0];
        String[] tabResult = afterHash.split("\\.");
        return tabResult[tabResult.length - 1];
    }

    @Test
    void testSignatureToNameOfClass() throws Exception {
        // given
        String signature = "um.ico.ingenierie.JavaFilesHandler.JavaFilesHandlerPath#JavaFilesHandlerPath(java.lang.String)";

        // when
        String result = signatureToNameOfClass(signature);

        // then
        assertEquals("JavaFilesHandlerPath", result);
    }

    @Test
    void testSignatureWithoutHash() throws Exception {
        // given
        String signature = "um.ico.ingenierie.MyClass";

        // when
        String result = signatureToNameOfClass(signature);

        // then
        assertEquals("MyClass", result);
    }

    @Test
    void testSignatureWithMultipleDots() throws Exception {
        // given
        String signature = "com.company.project.module.submodule.MyClass#method()";

        // when
        String result = signatureToNameOfClass(signature);

        // then
        assertEquals("MyClass", result);
    }
}
