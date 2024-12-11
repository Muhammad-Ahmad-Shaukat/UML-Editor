package com.boota.javaproject.ClassDiagram;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
/**
 * Represents a customizable class structure with attributes, functions,
 * initial point, and associated classes. This class supports code generation
 * and dynamic modification of its components.
 */
public class Class implements Serializable {
    String className;
    ArrayList<Attribute> attributes;
    ArrayList<Function> functions;
    Point initialPoint;
    ArrayList<AssociatedClass> x;

    /**
     * Constructs a Class instance with the specified class name and initial point.
     * Initializes lists for attributes, functions, and associated classes.
     *
     * @param className the name of the class
     * @param initialPoint the initial point associated with the class
     */
    public Class(String className, Point initialPoint) {
        this.className = className;
        this.initialPoint = initialPoint;
        attributes = new ArrayList<>();
        functions = new ArrayList<>();
        x = new ArrayList<>();
    }

    /**
     * Constructs a Class instance with the specified initial point.
     * Initializes lists for attributes, functions, and associated classes.
     *
     * @param initialPoint the initial point associated with the class
     */
    public Class(Point initialPoint) {
        this.initialPoint = initialPoint;
        attributes = new ArrayList<>();
        functions = new ArrayList<>();
        className = "Class";
        x = new ArrayList<>();
    }

    /**
     * Constructs a Class instance with the specified class name, attributes, functions, and initial point.
     * Initializes the attributes and functions lists, and sets the initial associated classes list.
     *
     * @param className the name of the class
     * @param attributes the list of attributes associated with the class
     * @param functions the list of functions (methods) associated with the class
     * @param initialPoint the initial point associated with the class' positioning
     */
    public Class(String className, ArrayList<Attribute> attributes, ArrayList<Function> functions, Point initialPoint) {
        this.className = className;
        this.attributes = attributes;
        this.functions = functions;
        this.initialPoint = initialPoint;
        x = new ArrayList<>();
    }

    /**
     * Retrieves the name of the class.
     *
     * @return the class name as a String
     */
    public String getClassName() {
        return className;
    }

    /**
     * Sets the class name for the current instance.
     *
     * @param className the name of the class to be set
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Retrieves the list of attributes associated with the class.
     *
     * @return an ArrayList of Attribute objects representing the attributes of the class
     */
    public ArrayList<Attribute> getAttributes() {
        return attributes;
    }

    /**
     * Sets the list of attributes for the class.
     *
     * @param attributes the list of attributes to be associated with the class
     */
    public void setAttributes(ArrayList<Attribute> attributes) {
        this.attributes = attributes;
    }

    /**
     * Retrieves the list of functions associated with the class.
     *
     * @return an ArrayList of Function objects representing the functions of the class
     */
    public ArrayList<Function> getFunctions() {
        return functions;
    }

    /**
     * Sets the list of functions associated with the class.
     *
     * @param functions the list of Function objects to be associated with the class
     */
    public void setFunctions(ArrayList<Function> functions) {
        this.functions = functions;
    }

    /**
     * Retrieves the initial point associated with the class.
     *
     * @return the initial point as a Point object
     */
    public Point getInitialPoint() {
        return initialPoint;
    }

    /**
     * Sets the initial point associated with the class.
     *
     * @param initialPoint the Point object to be set as the initial point
     */
    public void setInitialPoint(Point initialPoint) {
        this.initialPoint = initialPoint;
    }

    /**
     * Adds an attribute to the list of attributes associated with the class.
     *
     * @param attribute the Attribute object to be added to the class
     */
    public void addAttribute(Attribute attribute) {
        attributes.add(attribute);
    }

    /**
     * Adds a function to the list of functions associated with the class.
     *
     * @param function the Function object to be added to the class
     */
    public void addFunction(Function function) {
        functions.add(function);
    }

    /**
     * Removes a function from the list of functions associated with the class.
     *
     * @param function the Function object to be removed from the class
     */
    public void removeFunction(Function function) {
        functions.remove(function);
    }

    /**
     *
     */
    public void removeAttribute(Attribute attribute) {
        attributes.remove(attribute);
    }

    /**
     * Concatenates the textual representation of all attributes in the class attributes list.
     * Each attribute is represented by its string form as defined in its `toString` method.
     * The attributes are separated by newline characters in the resultant string.
     *
     * @return a string containing the representations of all attributes in the class,
     *         separated by newline characters
     */
    public String returnAttribute() {
        StringBuilder result = new StringBuilder();
        for (Attribute attribute : attributes) {
            result.append(attribute.toString()).append("\n");
        }
        return result.toString();
    }

    /**
     * Concatenates the string representations of all functions in the list of functions
     * associated with the class, with each function's representation followed by a newline.
     *
     * @return a single string containing the textual representations of all functions
     *         separated by newline characters
     */
    public String returnFunction() {
        StringBuilder result = new StringBuilder();
        for (Function function : functions) {
            result.append(function.toString()).append("\n");
        }
        return result.toString();
    }
    /**
     * Generates the Java code for the current class, including its attributes, functions,
     * inheritance relationships, and associations, and writes it to the specified file.
     *
     * @param filePath the file path where the generated code will be written
     */
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

    /**
     * Sets the list of associated classes for the class.
     *
     * @param x the list of AssociatedClass objects to be associated with the class
     */
    public void setX(ArrayList<AssociatedClass> x) {
        this.x = x;
    }
    /**
     * Adds an associated class to the list of associated classes for this instance.
     *
     * @param x the AssociatedClass object to be added
     */
    public void addX(AssociatedClass x) {
        this.x.add(x);
    }
    /**
     * Removes the specified associated class from the list of associated classes.
     *
     * @param x the associated class to be removed
     */
    public void removeX(AssociatedClass x) {
        this.x.remove(x);
    }
}
