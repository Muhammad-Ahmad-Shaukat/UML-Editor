package com.boota.javaproject.UseCaseDiagram;

import java.io.*;
import java.util.List;

/**
 * The UseCaseDiagramDeserializer class provides functionality to deserialize a Use Case diagram
 * from a file. The deserialization process reads objects from the specified file and categorizes
 * them into appropriate lists based on their type. The class supports deserializing various components
 * of a Use Case diagram, such as Use Cases, actors, associations, dependency relationships, and boundary boxes.
 *
 * This deserialization process assumes that the objects in the file are serialized instances of the relevant
 * classes, and the appropriate type checks are performed during the deserialization to ensure correctness.
 *
 * The components that can be deserialized include:
 * - UseCase: Represents a specific functionality or behavior the system provides to a user.
 * - UseCaseActor: Represents an actor in the diagram (e.g., a user or external system).
 * - UseCaseAssociation: Represents associations between Use Cases or Use Cases and actors.
 * - DependencyRelationship (include/exclude): Captures relationships between Use Cases to indicate
 *   "include" or "exclude" dependencies.
 * - UseCaseSystemBoundaryBox: Represents a system boundary in the Use Case diagram.
 */
public class UseCaseDiagramDeserializer {

    /**
     * Deserializes a Use Case Diagram from a specified file and populates the provided lists with
     * the diagram's components, such as Use Cases, Actors, Associations, Include/Exclude Relationships,
     * and Boundary Boxes.
     *
     * @param filePath the path to the file containing the serialized Use Case Diagram
     * @param useCases the list to be populated with deserialized UseCase objects
     * @param actors the list to be populated with deserialized UseCaseActor objects
     * @param associations the list to be populated with deserialized UseCaseAssociation objects
     * @param includeRelations the list to be populated with deserialized "include" DependencyRelationship objects
     * @param excludeRelations the list to be populated with deserialized "exclude" DependencyRelationship objects
     * @param boundaryBoxes the list to be populated with deserialized UseCaseSystemBoundaryBox objects
     * @throws IOException if an I/O error occurs while reading from the file
     * @throws ClassNotFoundException if the class of a serialized object cannot be found
     */
    public static void deserializeUseCaseDiagram(
            String filePath,
            List<UseCase> useCases,
            List<UseCaseActor> actors,
            List<UseCaseAssociation> associations,
            List<DependencyRelationship> includeRelations,
            List<DependencyRelationship> excludeRelations,
            List<UseCaseSystemBoundaryBox> boundaryBoxes
    ) throws IOException, ClassNotFoundException {
        try (FileInputStream fs = new FileInputStream(filePath);
             ObjectInputStream os = new ObjectInputStream(fs)) {
            while (true) {
                try {
                    Object obj = os.readObject();
                    if (obj instanceof UseCase) {
                        useCases.add((UseCase) obj);
                    } else if (obj instanceof UseCaseActor) {
                        actors.add((UseCaseActor) obj);
                    } else if (obj instanceof UseCaseAssociation) {
                        associations.add((UseCaseAssociation) obj);
                    } else if (obj instanceof DependencyRelationship) {
                        DependencyRelationship relationship = (DependencyRelationship) obj;
                        if ("include".equalsIgnoreCase(relationship.getDependencyType())) {
                            includeRelations.add(relationship);
                        } else if ("exclude".equalsIgnoreCase(relationship.getDependencyType())) {
                            excludeRelations.add(relationship);
                        }
                    } else if (obj instanceof UseCaseSystemBoundaryBox) {
                        boundaryBoxes.add((UseCaseSystemBoundaryBox) obj);
                    }
                } catch (EOFException e) {
                    break;
                }
            }
        }
    }

}