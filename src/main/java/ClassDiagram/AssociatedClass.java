package ClassDiagram;

public class AssociatedClass {
    private Class name;
    private Multiplicity multiplicity;
    private Interface associatedinterface;
    String relation;


    public AssociatedClass(Class className, Multiplicity multiplicity, String relation) {
        this.name = className;
        this.multiplicity = multiplicity;
        this.relation = relation;
    }

    public AssociatedClass(Class inheritedClass, String relation) {
        this.name = inheritedClass;
        this.relation = relation;
    }

    public AssociatedClass(Interface associatedinterface) {
        this.associatedinterface = associatedinterface;
    }

    public AssociatedClass(Multiplicity m) {
        this.multiplicity = m;
    }

    public Class getName() {
        return name;
    }

    public void setName(Class name) {
        this.name = name;
    }

    public Multiplicity getMultiplicity() {
        return multiplicity;
    }

    public void setMultiplicity(Multiplicity multiplicity) {
        this.multiplicity = multiplicity;
    }

    public Interface getAssociatedinterface() {
        return associatedinterface;
    }

    public void setAssociatedinterface(Interface associatedinterface) {
        this.associatedinterface = associatedinterface;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }
}
