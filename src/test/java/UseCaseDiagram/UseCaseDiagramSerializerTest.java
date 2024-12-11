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


}
