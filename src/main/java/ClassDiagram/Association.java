package ClassDiagram;


import java.io.Serializable;

public class Association implements Serializable {

    private Multiplicity startMultiplicity;
    private Multiplicity endMultiplicity;
    private String text;
    Class startClass;
    Class endClass;

    public Association(Class startClass, Class endClass) {
        this.startClass = startClass;
        this.endClass = endClass;
    }

    public Association(Multiplicity startMultiplicity, Multiplicity endMultiplicity, String text, Class startClass, Class endClass) {
        this.startMultiplicity = startMultiplicity;
        this.endMultiplicity = endMultiplicity;
        this.text = text;
        this.startClass = startClass;
        this.endClass = endClass;
    }

    public Association(Multiplicity startMultiplicity, Multiplicity endMultiplicity, Class startClass, Class endClass) {
        this.startMultiplicity = startMultiplicity;
        this.endMultiplicity = endMultiplicity;
        this.startClass = startClass;
        this.endClass = endClass;
    }


    public Multiplicity getStartMultiplicity() {
        return startMultiplicity;
    }

    public void setStartMultiplicity(Multiplicity startMultiplicity) {
        this.startMultiplicity = startMultiplicity;
    }

    public Multiplicity getEndMultiplicity() {
        return endMultiplicity;
    }

    public void setEndMultiplicity(Multiplicity endMultiplicity) {
        this.endMultiplicity = endMultiplicity;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Class getStartClass() {
        return startClass;
    }

    public void setStartClass(Class startClass) {
        this.startClass = startClass;
    }

    public Class getEndClass() {
        return endClass;
    }

    public void setEndClass(Class endClass) {
        this.endClass = endClass;
    }

}

