package ClassDiagram;

import com.boota.javaproject.ClassDiagram.Attribute;
import com.boota.javaproject.ClassDiagram.Function;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FunctionTest {

    private Function function;

    @BeforeEach
    void setUp() {
        // Initialize the function for testing
        function = new Function("void", "testFunction");
    }

    @AfterEach
    void tearDown() {
        // Clear the function object after each test
        function = null;
    }

    @Test
    void getName() {
        assertEquals("testFunction", function.getName(), "Function name should match the initialized value.");
    }

    @Test
    void setName() {
        function.setName("newFunctionName");
        assertEquals("newFunctionName", function.getName(), "Function name should be updated correctly.");
    }

    @Test
    void getReturnType() {
        assertEquals("void", function.getReturnType(), "Function return type should match the initialized value.");
    }

    @Test
    void setReturnType() {
        function.setReturnType("int");
        assertEquals("int", function.getReturnType(), "Function return type should be updated correctly.");
    }

    @Test
    void getAttributes() {
        assertNotNull(function.getAttributes(), "Attributes list should not be null.");
        assertTrue(function.getAttributes().isEmpty(), "Attributes list should be empty initially.");
    }

    @Test
    void setAttributes() {
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("arg1", "String"));
        attributes.add(new Attribute("arg2", "int"));

        function.setAttributes(attributes);

        assertEquals(2, function.getAttributes().size(), "Attributes list size should match the number of added attributes.");
        assertEquals("arg1", function.getAttributes().get(0).getName(), "First attribute name should match.");
    }

    @Test
    void addAttribute() {
        Attribute attribute = new Attribute("arg", "String");
        function.addAttribute(attribute);

        assertEquals(1, function.getAttributes().size(), "Attributes list size should be 1 after adding an attribute.");
        assertEquals("arg", function.getAttributes().get(0).getName(), "Attribute name should match the added value.");
    }

    @Test
    void removeAttribute() {
        Attribute attribute = new Attribute("arg", "String");
        function.addAttribute(attribute);
        function.removeAttribute(attribute);

        assertTrue(function.getAttributes().isEmpty(), "Attributes list should be empty after removing the added attribute.");
    }

    @Test
    void getAccessModifier() {
        assertEquals("public", function.getAccessModifier(), "Access modifier should default to 'public'.");
    }

    @Test
    void setAccessModifier() {
        function.setAccessModifier("private");
        assertEquals("private", function.getAccessModifier(), "Access modifier should be updated correctly.");
    }

    @Test
    void testToString() {
        function.addAttribute(new Attribute("arg1", "int"));
        function.addAttribute(new Attribute("arg2", "String"));
        function.setAccessModifier("private");

        String expected = "-testFunction(arg1 int, arg2 String) : void";
        assertEquals(expected, function.toString(), "toString should return the expected UML-like representation.");
    }

    @Test
    void generateCode() {
        function.addAttribute(new Attribute("arg1", "int"));
        function.addAttribute(new Attribute("arg2", "String"));
        function.setAccessModifier("private");
        function.setReturnType("String");

        String expected = """
                private String testFunction(int arg1, String arg2) {
                        // Function body
                    }
                """;

        assertEquals(expected, function.generateCode(), "Generated code should match the expected method code.");
    }
}
