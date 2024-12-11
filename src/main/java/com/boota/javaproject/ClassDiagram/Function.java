package com.boota.javaproject.ClassDiagram;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.stream.Collectors;
/**
 * Represents a function in a class diagram, with attributes for its name,
 * return type, parameters, and access modifier. This class provides methods
 * to retrieve and modify function properties, as well as to generate a UML
 * representation and corresponding code snippet for the function.
 *
 * Constructor overloading allows for the creation of functions with varying
 * levels of detail, such as with or without parameters, and with specific
 * access modifiers.
 *
 * Features include:
 * - Adding and removing parameters (attributes) for the function.
 * - Setting and getting the name, return type, and access modifier of the
 *   function.
 * - Generating a formatted string for UML representation.
 * - Automatically generating the code for the function, including its
 *   declaration and body template.
 */
public class Function implements Serializable {
    String name;
    String returnType;
    ArrayList<Attribute> attributes;
    String accessModifier;

    /**
     * Constructor for creating a Function object with a specified return type and name.
     *
     * @param returnType the return type of the function
     * @param name the name of the function
     */
    public Function(String returnType, String name) {
        this.returnType = returnType;
        this.name = name;
        attributes = new ArrayList<>();
        accessModifier = "public";
    }

    /**
     * Constructor for creating a Function instance with specified return type, name, and access modifier.
     *
     * @param returnType the return type of the function
     * @param name the name of the function
     * @param accessModifier the access modifier of the function, such as public, private, or protected
     */
    public Function(String returnType, String name, String accessModifier) {
        this.returnType = returnType;
        this.name = name;
        attributes = new ArrayList<>();
        this.accessModifier = accessModifier;
    }

    /**
     * Constructor for creating a Function object with a specified return type, name,
     * attributes, and access modifier.
     *
     * @param returnType the return type of the function
     * @param name the name of the function
     * @param attributes the list of attributes associated with the function
     * @param accessModifier the access modifier of the function, such as public, private, or protected
     */
    public Function(String returnType, String name, ArrayList<Attribute> attributes , String accessModifier) {
        this.returnType = returnType;
        this.accessModifier = accessModifier;
        this.name = name;
        this.attributes = attributes;
    }

    /**
     * Constructor for creating a Function instance with a specified return type, name, and attribute.
     *
     * @param returnType the return type of the function
     * @param name the name of the function
     * @param attribute an attribute associated with the function
     */
    public Function(String returnType, String name, Attribute attribute) {
        this.returnType = returnType;
        this.name = name;
        attributes = new ArrayList<>();
        this.attributes.add(attribute);
        accessModifier = "public";
    }

    /**
     * Constructs a Function object with a specified return type, name, attribute, and access modifier.
     *
     * @param returnType the return type of the function
     * @param name the name of the function
     * @param attribute an attribute associated with the function
     * @param accessModifier the access modifier of the function, such as public, private, or protected
     */
    public Function(String returnType, String name, Attribute attribute, String accessModifier) {
        this.returnType = returnType;
        this.accessModifier = accessModifier;
        this.name = name;
        attributes = new ArrayList<>();
        this.attributes.add(attribute);
    }


    /**
     * Retrieves the name of the function.
     *
     * @return the name of the function
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the function.
     *
     * @param name the name to be set for the function
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the return type of the function.
     *
     * @return the return type of the function as a String
     */
    public String getReturnType() {
        return returnType;
    }

    /**
     * Sets the return type of the function.
     *
     * @param returnType the return type to be set for the function
     */
    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    /**
     * Retrieves the list of attributes associated with the function.
     *
     * @return an ArrayList containing the attributes of the function.
     */
    public ArrayList<Attribute> getAttributes() {
        return attributes;
    }

    /**
     * Sets the list of attributes associated with the function.
     *
     * @param attributes an ArrayList of Attribute objects to be set for the function
     */
    public void setAttributes(ArrayList<Attribute> attributes) {
        this.attributes = attributes;
    }

    /**
     * Adds a new attribute to the list of attributes associated with the function.
     *
     * @param attribute the attribute to be added
     */
    public void addAttribute(Attribute attribute) {
        attributes.add(attribute);
    }

    /**
     * Removes the specified attribute from the list of attributes associated with the function.
     *
     * @param attribute the attribute to be removed from the list
     */
    public void removeAttribute(Attribute attribute) {
        attributes.remove(attribute);
    }

    /**
     * Retrieves the access modifier of the function.
     *
     * @return the access modifier of the function as a String
     */
    public String getAccessModifier() {
        return accessModifier;
    }

    /**
     * Sets the access modifier for the function.
     *
     * @param accessModifier the access modifier to be set for the function,
     *                       such as public, private, or protected
     */
    public void setAccessModifier(String accessModifier) {
        this.accessModifier = accessModifier;
    }

    /**
     * Generates a string representation of the function, including the access modifier,
     * function name, its attributes, and the return type.
     *
     * @return a string describing the function in the format:
     *         "{accessModifier}{functionName}({attributeName dataType, ...}) : {returnType}".
     */
    @Override
    public String toString() {
        String attributesString = attributes.stream()
                .map(attr -> attr.getName() + " " + attr.getDataType())
                .collect(Collectors.joining(", "));
        String prefix;
        switch (accessModifier.toLowerCase()) {
            case "private":
                prefix = "-";
                break;
            case "public":
                prefix = "+";
                break;
            default:
                prefix = "#";
        }

        return prefix + name + "(" + attributesString + ") : " + returnType;
    }

    /**
     * Generates and returns the implementation code for the function based on its attributes,
     * access modifier, return type, and name.
     *
     * @return a String containing the generated code of the function
     */
    public String generateCode() {
        StringBuilder code = new StringBuilder();

        // Generate the access modifier and return type
        code.append(accessModifier.toLowerCase()).append(" ");
        code.append(returnType).append(" ");
        code.append(name).append("(");

        // Add function parameters
        for (int i = 0; i < attributes.size(); i++) {
            if (i > 0) {
                code.append(", ");
            }
            code.append(attributes.get(i).getDataType())
                    .append(" ")
                    .append(attributes.get(i).getName());
        }

        code.append(") {\n");
        code.append("        // Function body\n");
        code.append("    }\n");

        return code.toString();
    }

}
