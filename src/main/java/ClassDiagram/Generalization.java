package ClassDiagram;

import java.io.Serializable;

public class Generalization implements Serializable {

    private ClassDiagram.Class startClass;
    private ClassDiagram.Class endClass;

    public Generalization(ClassDiagram.Class startClass, ClassDiagram.Class endClass) {
        this.startClass = startClass;
        this.endClass = endClass;
    }

    public ClassDiagram.Class getStartClass() {
        return startClass;
    }

    public ClassDiagram.Class getEndClass() {
        return endClass;
    }

    public void setEndClass(ClassDiagram.Class endClass) {
        this.endClass = endClass;
    }

    public void setStartClass(ClassDiagram.Class startClass) {
        this.startClass = startClass;
    }
}
