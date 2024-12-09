package UseCaseDiagram;

import com.boota.javaproject.ClassDiagram.Point;
import com.boota.javaproject.UseCaseDiagram.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UseCaseDiagramSerializerTest {

    private List<UseCase> useCases;
    private List<UseCaseActor> actors;
    private List<UseCaseAssociation> associations;
    private List<DependencyRelationship> includeRelations;
    private List<DependencyRelationship> excludeRelations;

    @BeforeEach
    void setUp() {
        useCases = new ArrayList<>();
        actors = new ArrayList<>();
        associations = new ArrayList<>();
        includeRelations = new ArrayList<>();
        excludeRelations = new ArrayList<>();
    }

    @AfterEach
    void tearDown() {
        useCases.clear();
        actors.clear();
        associations.clear();
        includeRelations.clear();
        excludeRelations.clear();
    }

    @Test
    void serializeUseCaseDiagram() throws IOException, ClassNotFoundException {
        // Create some test data
        Point point1 = new Point(10.0, 20.0);
        UseCase useCase1 = new UseCase(point1, "UseCase1");
        UseCaseActor actor1 = new UseCaseActor(point1, "Actor1");
        UseCaseAssociation association1 = new UseCaseAssociation(point1, point1, useCase1, actor1);
        DependencyRelationship includeRelationship = new DependencyRelationship(useCase1, useCase1, "include");
        DependencyRelationship excludeRelationship = new DependencyRelationship(useCase1, useCase1, "exclude");

        // Add the objects to the lists
        useCases.add(useCase1);
        actors.add(actor1);
        associations.add(association1);
        includeRelations.add(includeRelationship);
        excludeRelations.add(excludeRelationship);

        // Specify the file path for serialization
        String filePath = "testUseCaseDiagram.ser";

        // Call the serialize method
        UseCaseDiagramSerializer.serializeUseCaseDiagram(
                useCases, actors, associations, includeRelations, excludeRelations, filePath
        );

        // Verify that the file has been created
        File file = new File(filePath);
        assertTrue(file.exists(), "Serialized file should be created.");

        // Deserialize the file to verify the contents
        List<UseCase> deserializedUseCases = new ArrayList<>();
        List<UseCaseActor> deserializedActors = new ArrayList<>();
        List<UseCaseAssociation> deserializedAssociations = new ArrayList<>();
        List<DependencyRelationship> deserializedIncludeRelations = new ArrayList<>();
        List<DependencyRelationship> deserializedExcludeRelations = new ArrayList<>();

        // Deserialize the objects from the file
        UseCaseDiagramDeserializer.deserializeUseCaseDiagram(
                filePath, deserializedUseCases, deserializedActors, deserializedAssociations,
                deserializedIncludeRelations, deserializedExcludeRelations, new ArrayList<>()
        );

        // Verify that the deserialized data matches the original data
        assertEquals(1, deserializedUseCases.size(), "There should be one UseCase.");
        assertEquals(1, deserializedActors.size(), "There should be one UseCaseActor.");
        assertEquals(1, deserializedAssociations.size(), "There should be one UseCaseAssociation.");
        assertEquals(1, deserializedIncludeRelations.size(), "There should be one include DependencyRelationship.");
        assertEquals(1, deserializedExcludeRelations.size(), "There should be one exclude DependencyRelationship.");

        assertEquals("UseCase1", deserializedUseCases.get(0).getName(), "UseCase name should be 'UseCase1'.");
        assertEquals("Actor1", deserializedActors.get(0).getName(), "Actor name should be 'Actor1'.");
        assertEquals("include", deserializedIncludeRelations.get(0).getDependencyType(), "Dependency type should be 'include'.");
        assertEquals("exclude", deserializedExcludeRelations.get(0).getDependencyType(), "Dependency type should be 'exclude'.");

        // Clean up the test file
        file.delete();
    }
}
