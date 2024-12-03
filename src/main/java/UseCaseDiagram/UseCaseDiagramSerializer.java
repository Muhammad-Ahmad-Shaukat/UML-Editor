package UseCaseDiagram;

import java.io.*;
import java.util.List;

public class UseCaseDiagramSerializer {

    public static void serializeUseCaseDiagram(
            List<UseCase> useCases,
            List<UseCaseActor> actors,
            List<UseCaseAssociation> associations,
            List<DependencyRelationship> includeRelations,
            List<DependencyRelationship> excludeRelations,
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

