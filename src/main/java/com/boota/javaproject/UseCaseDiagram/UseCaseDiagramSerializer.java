package com.boota.javaproject.UseCaseDiagram;
import java.io.*;
import java.util.List;
/**
 * Provides functionality to serialize a Use Case diagram, including its use cases,
 * actors, associations, dependency relationships (include and exclude), and system
 * boundary boxes, into a file for persistence.
 *
 * The serialized data can later be de-serialized to reconstruct the original diagram,
 * making it suitable for storage, transfer, or backup purposes.
 *
 * This utility uses Java's object serialization mechanism with classes such as
 * {@link FileOutputStream} and {@link ObjectOutputStream}.
 *
 * The method `serializeUseCaseDiagram` saves the given components of a Use Case
 * diagram to the specified file path. It ensures that all elements (use cases,
 * actors, associations, relationships, and boundary boxes) are serialized
 * correctly in sequence.
 *
 * Exceptions:
 * - Throws {@link IOException} if an error occurs during file writing or serialization.
 */
public class UseCaseDiagramSerializer {
    /**
     * Serializes a Use Case diagram, including its use cases, actors, associations,
     * include and exclude dependency relationships, and system boundary boxes, into
     * a file at the specified file path for persistence.
     *
     * @param useCases a list of {@code UseCase} objects representing the use cases in the diagram
     * @param actors a list of {@code UseCaseActor} objects representing the actors in the diagram
     * @param associations a list of {@code UseCaseAssociation} objects representing the associations in the diagram
     * @param includeRelations a list of {@code DependencyRelationship} objects representing "include" dependencies
     * @param excludeRelations a list of {@code DependencyRelationship} objects representing "exclude" dependencies
     * @param boxes a list of {@code UseCaseSystemBoundaryBox} objects representing the system boundary boxes
     * @param filePath the file path where the serialized data should be written
     * @throws IOException if an I/O error occurs during the serialization process
     */
    public static void serializeUseCaseDiagram(
            List<UseCase> useCases,
            List<UseCaseActor> actors,
            List<UseCaseAssociation> associations,
            List<DependencyRelationship> includeRelations,
            List<DependencyRelationship> excludeRelations,
            List<UseCaseSystemBoundaryBox> boxes,
            String filePath
    ) throws IOException {
        try (FileOutputStream fs = new FileOutputStream(filePath);
             ObjectOutputStream os = new ObjectOutputStream(fs)) {
            for (UseCase useCase : useCases) {
                os.writeObject(useCase);
            }
            for (UseCaseActor actor : actors) {
                os.writeObject(actor);
            }
            for (UseCaseAssociation association : associations) {
                os.writeObject(association);
            }
            for (DependencyRelationship include : includeRelations) {
                os.writeObject(include);
            }
            for (DependencyRelationship exclude : excludeRelations) {
                os.writeObject(exclude);
            }
        }
    }
}