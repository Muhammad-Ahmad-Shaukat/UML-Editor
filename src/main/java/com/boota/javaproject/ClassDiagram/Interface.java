package com.boota.javaproject.ClassDiagram;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
/**
 * Represents an Interface in a UML class diagram. This class encapsulates
 * the attributes and behaviors of an interface, including its name, functions,
 * initial position in the diagram, and any inherited classes.
 *
 * Key features:
 * - Stores the name of the interface.
 * - Maintains a list of functions representing the methods defined in the interface.
 * - Tracks the position of the interface in the diagram using a Point object.
 * - Provides functionality to manage inherited classes for modeling inheritance relationships.
 * - Supports code generation for the interface in Java syntax.
 *
 * This class provides several constructors to allow different levels of initialization,
 * such as defining only the class name and position, or including full function and
 * attribute lists at creation time.
 */
public class Interface implements Serializable {
    public String className;
    public ArrayList<Function> functions;
    public Point initialPoint;
    public ArrayList<Class> InheritedClasses; // For inheritance (class) relations

    /**
     * Constructs a new Interface with the specified class name and initial point.
     * This constructor initializes the Interface with an empty list of functions
     * and inherited classes.
     *
     * @param className the name of the class for the Interface
     * @param initialPoint the initial position of the Interface in a two-dimensional space
     */
    public Interface(String className, Point initialPoint) {
        this.className = className;
        this.initialPoint = initialPoint;
        this.functions = new ArrayList<>();
        this.InheritedClasses = new ArrayList<>();
    }

    /**
     * Constructs an Interface object with an initial point.
     * The class name is set to the default value "Interface".
     * Initializes lists for functions and inherited classes.
     *
     * @param initialPoint The initial point specifying the starting position.
     */
    public Interface(Point initialPoint) {
        this.className = "Interface";  // Default name for an Interface
        this.initialPoint = initialPoint;
        this.functions = new ArrayList<>();
        this.InheritedClasses = new ArrayList<>();
    }

    /**
     * Constructs a new Interface object with the specified class name, list of attributes, list of functions,
     * and the initial position in a two-dimensional space.
     *
     * @param className the name of the class for the Interface
     * @param attributes a list of attributes associated with the Interface
     * @param functions a list of functions associated with the Interface
     * @param initialPoint the initial position of the Interface in a two-dimensional space
     */
    public Interface(String className, ArrayList<Attribute> attributes, ArrayList<Function> functions, Point initialPoint) {
        this.className = className;
        this.functions = functions;
        this.initialPoint = initialPoint;
        this.InheritedClasses = new ArrayList<>();
    }

    public String getClassName() {
        return className;
    }

    /**
     * Sets the class name for the Interface object.
     *
     * @param className the name of the class to be set
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Retrieves the list of functions associated with this Interface.
     *
     * @return an ArrayList containing all the Function objects in the Interface.
     */
    public ArrayList<Function> getFunctions() {
        return functions;
    }

    /**
     * Sets the list of functions associated with this Interface.
     *
     * @param functions the list of Function objects to be associated with the Interface
     */
    public void setFunctions(ArrayList<Function> functions) {
        this.functions = functions;
    }

    /**
     * Retrieves the initial position of the Interface in a two-dimensional space.
     *
     * @return the initial point represented as a {@code Point} object.
     */
    public Point getInitialPoint() {
        return initialPoint;
    }

    /**
     * Sets the initial point of the interface in a two-dimensional space.
     *
     * @param initialPoint the starting position to set for the interface
     */
    public void setInitialPoint(Point initialPoint) {
        this.initialPoint = initialPoint;
    }

    /**
     * Adds a function to the Interface.
     *
     * @param function the {@code Function} object to be added to the Interface
     */
    // Add a function to the Interface
    public void addFunction(Function function) {
        functions.add(function);
    }

    /**
     * Removes a function from the list of functions associated with this Interface.
     *
     * @param function the {@code Function} object to be removed from the Interface
     */
    // Remove a function from the Interface
    public void removeFunction(Function function) {
        functions.remove(function);
    }

    /**
     * Returns a string containing all functions associated with this Interface.
     * Each function is represented by its string representation, with each function
     * separated by a newline character.
     *
     * @return a concatenated string of the string representations of all functions
     *         in the Interface, each followed by a newline character.
     */
    // Return all functions in the interface as a string
    public String returnFunction() {
        StringBuilder result = new StringBuilder();
        for (Function function : functions) {
            result.append(function.toString()).append("\n");
        }
        return result.toString();
    }

    /**
     * Adds the name of the provided {@code AssociatedClass} to the list of inherited classes
     * if the associated class has a non-null name.
     *
     * @param associatedClass the {@code AssociatedClass} object whose name is to be added
     *                        to the inherited classes list, if it is not null
     */
    public void addX(AssociatedClass associatedClass) {
        if (associatedClass.getName() != null) {
            InheritedClasses.add(associatedClass.getName());
        }
    }


    /**
     * Generates the Java code for this Interface and writes it to the specified file.
     * The generated code includes the interface declaration along with all the
     * associated functions formatted as method declarations.
     *
     * @param filePath the path of the file where the generated code will be written
     */
    public void generateCode(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            StringBuilder declaration = new StringBuilder("public interface " + className);
            declaration.append(" {\n");
            writer.write(declaration.toString());
            for (Function function : functions) {
                writer.write("    " + function.generateCode().replaceFirst(" \\{", " ;") + "\n");
            }
            writer.write("}\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
