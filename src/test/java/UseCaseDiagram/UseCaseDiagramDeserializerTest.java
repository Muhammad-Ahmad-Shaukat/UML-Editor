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

class UseCaseDiagramDeserializerTest {

    private List<UseCase> useCases;
    private List<UseCaseActor> actors;
    private List<UseCaseAssociation> associations;
    private List<DependencyRelationship> includeRelations;
    private List<DependencyRelationship> excludeRelations;
    private List<UseCaseSystemBoundaryBox> boundaryBoxes;

    @BeforeEach
    void setUp() {
        useCases = new ArrayList<>();
        actors = new ArrayList<>();
        associations = new ArrayList<>();
        includeRelations = new ArrayList<>();
        excludeRelations = new ArrayList<>();
        boundaryBoxes = new ArrayList<>();
    }

    @AfterEach
    void tearDown() {
        useCases.clear();
        actors.clear();
        associations.clear();
        includeRelations.clear();
        excludeRelations.clear();
        boundaryBoxes.clear();
    }

    @Test
    void deserializeUseCaseDiagram() throws IOException, ClassNotFoundException {
        // Create some test data to serialize
        Point point1 = new Point(10.0, 20.0);
        UseCase useCase1 = new UseCase(point1, "UseCase1");
        UseCaseActor actor1 = new UseCaseActor(point1, "Actor1");
        UseCaseAssociation association1 = new UseCaseAssociation(point1, point1, useCase1, actor1);
        DependencyRelationship includeRelationship = new DependencyRelationship(useCase1, useCase1, "include");
        DependencyRelationship excludeRelationship = new DependencyRelationship(useCase1, useCase1, "exclude");
        UseCaseSystemBoundaryBox boundaryBox1 = new UseCaseSystemBoundaryBox(new Point(40.0,40.0));

        // Serialize the objects to a temporary file
        String filePath = "testUseCaseDiagram.ser";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(useCase1);
            oos.writeObject(actor1);
            oos.writeObject(association1);
            oos.writeObject(includeRelationship);
            oos.writeObject(excludeRelationship);
            oos.writeObject(boundaryBox1);
        }

        // Call the deserialize method
        UseCaseDiagramDeserializer.deserializeUseCaseDiagram(
                filePath, useCases, actors, associations, includeRelations, excludeRelations, boundaryBoxes
        );

        // Verify that the deserialized data is correct
        assertEquals(1, useCases.size(), "There should be one UseCase.");
        assertEquals(1, actors.size(), "There should be one UseCaseActor.");
        assertEquals(1, associations.size(), "There should be one UseCaseAssociation.");
        assertEquals(1, includeRelations.size(), "There should be one include DependencyRelationship.");
        assertEquals(1, excludeRelations.size(), "There should be one exclude DependencyRelationship.");
        assertEquals(1, boundaryBoxes.size(), "There should be one UseCaseSystemBoundaryBox.");

        assertEquals("UseCase1", useCases.get(0).getName(), "UseCase name should be 'UseCase1'.");
        assertEquals("Actor1", actors.get(0).getName(), "Actor name should be 'Actor1'.");
        assertEquals("BoundaryBox", boundaryBoxes.get(0).getName(), "BoundaryBox name should be 'Boundary1'.");
        assertEquals("include", includeRelations.get(0).getDependencyType(), "Dependency type should be 'include'.");
        assertEquals("exclude", excludeRelations.get(0).getDependencyType(), "Dependency type should be 'exclude'.");

        // Clean up the test file
        new File(filePath).delete();
    }
}
