package com.boota.javaproject.UseCaseDiagram;

import java.io.*;
import java.util.List;

public class UseCaseDiagramDeserializer {

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