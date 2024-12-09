package ClassDiagram;

import com.boota.javaproject.ClassDiagram.*;
import com.boota.javaproject.ClassDiagram.Class;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClassDiagramSerializerTest {

    private List<com.boota.javaproject.ClassDiagram.Class> classes;
    private List<Association> associations;
    private List<Interface> interfaces;
    private List<CompositeRelations> aggregations;
    private List<Generalization> generalizations;
    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        // Initialize dummy data
        classes = new ArrayList<>();
        associations = new ArrayList<>();
        interfaces = new ArrayList<>();
        aggregations = new ArrayList<>();
        generalizations = new ArrayList<>();

        // Add sample data
        classes.add(new com.boota.javaproject.ClassDiagram.Class(new Point(20.0,20.0)));
        classes.add(new com.boota.javaproject.ClassDiagram.Class("ClassB",new Point(10.0,10.0)));
        associations.add(new Association(new com.boota.javaproject.ClassDiagram.Class(new Point(0.0,0.0)), new com.boota.javaproject.ClassDiagram.Class(new Point(30.0,30.0))));
        interfaces.add(new Interface(new Point(40.0,40.0)));
        aggregations.add(new CompositeRelations( new com.boota.javaproject.ClassDiagram.Class(new Point(50.0,40.0)), new com.boota.javaproject.ClassDiagram.Class(new Point(60.0,60.0)),"Aggregation"));
        generalizations.add(new Generalization(new com.boota.javaproject.ClassDiagram.Class(new Point(20.0,20.0)), new com.boota.javaproject.ClassDiagram.Class(new Point(70.0,70.0))));

        // Create a temporary file for testing
        tempFile = File.createTempFile("test-diagram", ".nalla");
        tempFile.deleteOnExit(); // Ensure the file is deleted after tests
    }

    @AfterEach
    void tearDown() {
        // Clean up temporary file
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Test
    void serialize() {
        // Test serialization
        assertDoesNotThrow(() -> {
            ClassDiagramSerializer.serialize(classes, associations, interfaces, aggregations, generalizations, tempFile.getAbsolutePath());
        });

        // Check if file exists
        assertTrue(tempFile.exists(), "Serialized file should exist after serialization.");
        assertTrue(tempFile.length() > 0, "Serialized file should not be empty.");
    }

    @Test
    void deserialize() {
        // Serialize first
        assertDoesNotThrow(() -> {
            ClassDiagramSerializer.serialize(classes, associations, interfaces, aggregations, generalizations, tempFile.getAbsolutePath());
        });

        // Deserialize
        assertDoesNotThrow(() -> {
            Object[] deserializedData = ClassDiagramSerializer.deserialize(tempFile.getAbsolutePath());

            // Validate deserialized data
            assertNotNull(deserializedData, "Deserialized data should not be null.");
            assertEquals(classes.size(), ((List<Class>) deserializedData[0]).size(), "Number of deserialized classes should match.");
            assertEquals(associations.size(), ((List<Association>) deserializedData[1]).size(), "Number of deserialized associations should match.");
            assertEquals(interfaces.size(), ((List<Interface>) deserializedData[2]).size(), "Number of deserialized interfaces should match.");
            assertEquals(aggregations.size(), ((List<CompositeRelations>) deserializedData[3]).size(), "Number of deserialized aggregations should match.");
            assertEquals(generalizations.size(), ((List<Generalization>) deserializedData[4]).size(), "Number of deserialized generalizations should match.");
        });
    }

    @Test
    void serializeWithInvalidFilePath() {
        // Test serialization with an invalid file path
        String invalidFilePath = "invalid:/path/to/file.nalla";
        assertThrows(IOException.class, () -> {
            ClassDiagramSerializer.serialize(classes, associations, interfaces, aggregations, generalizations, invalidFilePath);
        });
    }

    @Test
    void deserializeWithInvalidFilePath() {
        // Test deserialization with an invalid file path
        String invalidFilePath = "invalid:/path/to/file.nalla";
        assertThrows(IOException.class, () -> {
            ClassDiagramSerializer.deserialize(invalidFilePath);
        });
    }

    @Test
    void deserializeNonexistentFile() {
        // Test deserialization with a nonexistent file
        File nonexistentFile = new File("nonexistent-file.nalla");
        assertThrows(IOException.class, () -> {
            ClassDiagramSerializer.deserialize(nonexistentFile.getAbsolutePath());
        });
    }

    @Test
    void deserializeCorruptedFile() throws IOException {
        // Write random data to the file
        try (var fos = new java.io.FileOutputStream(tempFile)) {
            fos.write("corrupted data".getBytes());
        }

        // Test deserialization with corrupted file content
        assertThrows(IOException.class, () -> {
            ClassDiagramSerializer.deserialize(tempFile.getAbsolutePath());
        });
    }
}
