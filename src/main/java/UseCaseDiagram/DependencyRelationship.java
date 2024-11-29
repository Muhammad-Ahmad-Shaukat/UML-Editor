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


    public void serializedependencyRelationship(String filePath) {
        try (FileWriter writer = new FileWriter(filePath, true)) { // Append mode
            writer.write("?Dependency Relation?\n");
            writer.write(toString()+"\n");


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "<Type>" + dependencyType + "</Type>" + "<StartUseCase>" + startUseCase.toString()
                + "</StartUseCase>" + "<EndUseCase>" + endUseCase.toString() + "</EndUseCase>";
    }
}
