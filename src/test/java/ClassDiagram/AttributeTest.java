package ClassDiagram;

import com.boota.javaproject.ClassDiagram.Attribute;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AttributeTest {

    private Attribute attribute;

    @BeforeEach
    void setUp() {
        // Initialize a default attribute for testing
        attribute = new Attribute("id", "int", "private");
    }

    @AfterEach
    void tearDown() {
        // Cleanup after tests
        attribute = null;
    }

    @Test
    void testConstructorWithAllFields() {
        Attribute attr = new Attribute("name", "String", "public");

        assertEquals("name", attr.getName());
        assertEquals("String", attr.getDataType());
        assertEquals("public", attr.getAccessModifier());
    }

    @Test
    void testConstructorWithNameAndDataType() {
        Attribute attr = new Attribute("age", "int");

        assertEquals("age", attr.getName());
        assertEquals("int", attr.getDataType());
        assertEquals("public", attr.getAccessModifier());
    }

    @Test
    void getName() {
        assertEquals("id", attribute.getName());
    }

    @Test
    void setName() {
        attribute.setName("userId");
        assertEquals("userId", attribute.getName());
    }

    @Test
    void getDataType() {
        assertEquals("int", attribute.getDataType());
    }

    @Test
    void setDataType() {
        attribute.setDataType("long");
        assertEquals("long", attribute.getDataType());
    }

    @Test
    void getAccessModifier() {
        assertEquals("private", attribute.getAccessModifier());
    }

    @Test
    void setAccessModifier() {
        attribute.setAccessModifier("protected");
        assertEquals("protected", attribute.getAccessModifier());
    }

    @Test
    void testToStringPrivate() {
        attribute.setAccessModifier("private");
        attribute.setName("id");
        attribute.setDataType("int");

        assertEquals("-id : int", attribute.toString());
    }

    @Test
    void testToStringPublic() {
        attribute.setAccessModifier("public");
        attribute.setName("name");
        attribute.setDataType("String");

        assertEquals("+name : String", attribute.toString());
    }

    @Test
    void testToStringProtected() {
        attribute.setAccessModifier("protected");
        attribute.setName("age");
        attribute.setDataType("int");

        assertEquals("#age : int", attribute.toString());
    }

    @Test
    void generateCodePrivate() {
        attribute.setAccessModifier("private");
        attribute.setName("id");
        attribute.setDataType("int");

        assertEquals("private int id;", attribute.generateCode());
    }

    @Test
    void generateCodePublic() {
        attribute.setAccessModifier("public");
        attribute.setName("name");
        attribute.setDataType("String");

        assertEquals("public String name;", attribute.generateCode());
    }

    @Test
    void generateCodeProtected() {
        attribute.setAccessModifier("protected");
        attribute.setName("age");
        attribute.setDataType("int");

        assertEquals("protected int age;", attribute.generateCode());
    }

    @Test
    void print() {
        // Test System output using a lambda or mock approach
        attribute.setName("id");
        attribute.setDataType("int");

        // Capture output from print() (optional)
        attribute.print();
        // No assertion here as `print()` is void and affects console output.
    }
}
