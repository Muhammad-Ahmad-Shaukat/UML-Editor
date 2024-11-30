package UseCaseDiagram;

import ClassDiagram.Point;

import java.io.FileWriter;
import java.io.IOException;

public class UseCase {
    Point initialPoint;
    String name;

    public UseCase(Point initialPoint){
        this.initialPoint = initialPoint;
        name = "Use Case";
    }

    public UseCase(Point initialPoint, String name) {
        this.initialPoint = initialPoint;
        this.name = name;
    }
    public Point getInitialpoint() {
        return initialPoint;
    }
    public void setInitialPoint(Point initialPoint) {
        this.initialPoint = initialPoint;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


}
