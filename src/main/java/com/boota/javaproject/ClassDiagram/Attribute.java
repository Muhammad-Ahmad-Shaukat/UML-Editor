package com.boota.javaproject.ClassDiagram;

import java.io.Serializable;
/**
 * Represents an attribute in a class diagram. It contains the name, data type,
 * and access modifier of the attribute. This class provides functionality to
 * retrieve and modify its properties, as well as methods for code generation
 * and textual representation.
 *
 * The access modifier can be public, private, or protected, and defaults to
 * public if not specified.
 *
 * Implements methods to:
 * - Generate code for the attribute based on its properties.
 * - Print the attribute's name and data type.
 * - Create a formatted string representation suitable for UML diagrams.
 */
public class Attribute implements Serializable {
    private String name;
    private String dataType;
    private String accessModifier;

    /**
     * Constructs an Attribute object with the specified name, data type, and access modifier.
     *
     * @param name the name of the attribute
     * @param dataType the data type of the attribute
     * @param accessModifier the access modifier of the attribute (e.g., public, private, protected)
     */
    public Attribute(String name, String dataType, String accessModifier) {
        this.name = name;
        this.dataType = dataType;
        this.accessModifier = accessModifier;
    }

    /**
     * Constructs an Attribute object with the specified name and data type.
     * The access modifier is set to "public" by default.
     *
     * @param name the name of the attribute
     * @param dataType the data type of the attribute
     */
    public Attribute(String name, String dataType) {
        this.name = name;
        this.dataType = dataType;
        this.accessModifier = "public";
    }

    /**
     * Retrieves the name of the attribute.
     *
     * @return the name of the attribute as a String.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the attribute.
     *
     * @param name the new name to assign to the attribute
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the data type of the attribute.
     *
     * @return the data type of the attribute as a String
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * Sets the data type of the attribute.
     *
     * @param dataType the data type to set for this attribute
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     * Retrieves the access modifier of the attribute.
     *
     * @return the access modifier of the attribute as a String (e.g., "public", "private", "protected").
     */
    public String getAccessModifier() {
        return accessModifier;
    }

    /**
     * Sets the access modifier for the attribute.
     *
     * @param accessModifier the access level of the attribute.
     *                       Accepted values include "public", "private", and "protected".
     */
    public void setAccessModifier(String accessModifier) {
        this.accessModifier = accessModifier;
    }

    /**
     * Returns a string representation of the object, formatted based on its
     * access modifier. The format for the representation varies as follows:
     * - "private": Prefixed with "-" followed by the attribute name and data type.
     * - "public": Prefixed with "+" followed by the attribute name and data type.
     * - Other cases (including "protected"): Prefixed with "#" followed by the attribute name and data type.
     *
     * @return A string representing the attribute```java
    /**
     * Converts the Attribute object to its string representation based on its access modifier.
     * The returned string includes the type of access modifier, name, and data type of the attribute.
     */
    @Override
    public String toString() {
        return switch (accessModifier.toLowerCase()) {
            case "private" -> "-" + name + " : " + dataType;
            case "public" -> "+" + name + " : " + dataType;
            default -> "#" + name + " : " + dataType;
        };
    }

    /**
     * Generates the code representation for the attribute based on its access modifier,
     * data type, and name. The access modifier determines whether the attribute is
     * public, private, or protected,*/
    public String generateCode(){
        if (accessModifier == "private"){
            return "private " + dataType + " " + name + ";";
        } else if (accessModifier == "public") {
            return "public " + dataType + " " + name + ";";
        }
        else{
            return "protected " + dataType + " " + name + ";";
        }
    }

    /**
     * Prints the name and data type of the attribute to the console.
     * The format of the printed message is: "name : dataType".
     */
    public void print() {
        System.out.println(name + " : " + dataType);
    }
}
