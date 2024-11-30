package UseCaseDiagram;

import java.io.FileWriter;
import java.io.IOException;

public class DependencyRelationship {
    private String dependencyType;
    private UseCase startUseCase;
    private UseCase endUseCase;


    public DependencyRelationship(UseCase startUseCase, UseCase endUseCase, String dependencyType) {
        this.startUseCase = startUseCase;
        this.endUseCase = endUseCase;
        this.dependencyType = dependencyType;
    }


    public String getDependencyType() {
        return dependencyType;
    }

    public void setDependencyType(String dependencyType) {
        this.dependencyType = dependencyType;
    }

    public UseCase getStartUseCase() {
        return startUseCase;
    }

    public void setStartUseCase(UseCase startUseCase) {
        this.startUseCase = startUseCase;
    }

    public UseCase getEndUseCase() {
        return endUseCase;
    }

    public void setEndUseCase(UseCase endUseCase) {
        this.endUseCase = endUseCase;
    }



}
