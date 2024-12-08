package ClassDiagram;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class Class implements Serializable {
    String className;
    ArrayList<Attribute> attributes;
    ArrayList<Function> functions;
    Point initialPoint;
    ArrayList<AssociatedClass> x;

    public Class(String className, Point initialPoint) {
        this.className = className;
        this.initialPoint = initialPoint;
        attributes = new ArrayList<>();
        functions = new ArrayList<>();
        x = new ArrayList<>();
    }

    public Class(Point initialPoint) {
        this.initialPoint = initialPoint;
        attributes = new ArrayList<>();
        functions = new ArrayList<>();
        className = "Class";
        x = new ArrayList<>();
    }

    public Class(String className, ArrayList<Attribute> attributes, ArrayList<Function> functions, Point initialPoint) {
        this.className = className;
        this.attributes = attributes;
        this.functions = functions;
        this.initialPoint = initialPoint;
        x = new ArrayList<>();
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public ArrayList<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(ArrayList<Attribute> attributes) {
        this.attributes = attributes;
    }

    public ArrayList<Function> getFunctions() {
        return functions;
    }

    public void setFunctions(ArrayList<Function> functions) {
        this.functions = functions;
    }

    public Point getInitialPoint() {
        return initialPoint;
    }

    public void setInitialPoint(Point initialPoint) {
        this.initialPoint = initialPoint;
    }

    void addAttribute(Attribute attribute) {
        attributes.add(attribute);
    }

    void addFunction(Function function) {
        functions.add(function);
    }

    void removeFunction(Function function) {
        functions.remove(function);
    }

    void removeAttribute(Attribute attribute) {
        attributes.remove(attribute);
    }

    String returnAttribute() {
        StringBuilder result = new StringBuilder();
        for (Attribute attribute : attributes) {
            result.append(attribute.toString()).append("\n");
        }
        return result.toString();
    }

    String returnFunction() {
        StringBuilder result = new StringBuilder();
        for (Function function : functions) {
            result.append(function.toString()).append("\n");
        }
        return result.toString();
    }
    public void generateCode(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Write class declaration
            StringBuilder declaration = new StringBuilder("public class " + className);

            // Check for inheritance
            AssociatedClass parentClass = null;
            ArrayList<Interface> implementedInterfaces = new ArrayList<>();

            for (AssociatedClass assoc : x) {
                if ("inheritance".equalsIgnoreCase(assoc.getRelation())) {
                    parentClass = assoc;
                } else if (assoc.getAssociatedinterface() != null) {
                    implementedInterfaces.add(assoc.getAssociatedinterface());
                }
            }

            // Add inheritance (extends)
            if (parentClass != null && parentClass.getName() != null) {
                declaration.append(" extends ").append(parentClass.getName().getClassName());
            }

            // Add interfaces (implements)
            if (!implementedInterfaces.isEmpty()) {
                declaration.append(" implements ");
                for (int i = 0; i < implementedInterfaces.size(); i++) {
                    declaration.append(implementedInterfaces.get(i).getClassName());
                    if (i < implementedInterfaces.size() - 1) {
                        declaration.append(", ");
                    }
                }
            }
            declaration.append(" {\n");
            writer.write(declaration.toString());

            // Add associations
            for (AssociatedClass assoc : x) {
                if (!"inheritance".equalsIgnoreCase(assoc.getRelation())) {
                    String multiplicityComment = assoc.getMultiplicity() != null
                            ? " // Multiplicity: " + assoc.getMultiplicity().toString()
                            : "";
                    String relationComment = assoc.getRelation() != null
                            ? " // Relation: " + assoc.getRelation()
                            : "";

                    writer.write("    private " + assoc.getName().getClassName() + " " + assoc.getName().getClassName().toLowerCase()
                            + ";" + multiplicityComment + relationComment + "\n");
                }
            }

            // Write attributes
            for (Attribute attribute : attributes) {
                writer.write("    " + attribute.generateCode() + "\n");
            }

            // Write functions
            for (Function function : functions) {
                writer.write("    " + function.generateCode() + "\n");
            }

            writer.write("}\n"); // Close the class

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public ArrayList<AssociatedClass> getX() {
        return x;
    }

    public void setX(ArrayList<AssociatedClass> x) {
        this.x = x;
    }
    public void addX(AssociatedClass x) {
        this.x.add(x);
    }
    public void removeX(AssociatedClass x) {
        this.x.remove(x);
    }
}
