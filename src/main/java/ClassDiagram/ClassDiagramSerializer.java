package UseCaseDiagram;

import ClassDiagram.Association;
import ClassDiagram.Class;
import ClassDiagram.CompositeRelations;
import ClassDiagram.Generalization;
import ClassDiagram.Interface;

import java.io.*;
import java.util.List;

public class ClassDiagramSerializer {

    /**
     * Serializes the provided class diagram components to a file.
     *
     * @param classes         List of Class objects
     * @param associations    List of Association objects
     * @param interfaces      List of Interface objects
     * @param aggregations    List of CompositeRelations objects
     * @param generalizations List of Generalization objects
     * @param filePath        File path to save serialized data
     * @throws IOException if there is an error during serialization
     */
    public static void serialize(
            List<Class> classes,
            List<Association> associations,
            List<Interface> interfaces,
            List<CompositeRelations> aggregations,
            List<Generalization> generalizations,
            String filePath
    ) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            // Serialize all lists in sequence
            oos.writeObject(classes);
            oos.writeObject(associations);
            oos.writeObject(interfaces);
            oos.writeObject(aggregations);
            oos.writeObject(generalizations);
        }
    }

    /**
     * Deserializes class diagram components from a file.
     *
     * @param filePath File path to load serialized data
     * @return Object array containing deserialized components
     * @throws IOException            if there is an error during file read
     * @throws ClassNotFoundException if a class cannot be found during deserialization
     */
    @SuppressWarnings("unchecked")
    public static Object[] deserialize(String filePath) throws IOException, ClassNotFoundException {
        try (FileInputStream fis = new FileInputStream(filePath);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            // Read all lists in sequence
            List<Class> classes = (List<Class>) ois.readObject();
            List<Association> associations = (List<Association>) ois.readObject();
            List<Interface> interfaces = (List<Interface>) ois.readObject();
            List<CompositeRelations> aggregations = (List<CompositeRelations>) ois.readObject();
            List<Generalization> generalizations = (List<Generalization>) ois.readObject();
            return new Object[]{classes, associations, interfaces, aggregations, generalizations};
        }
    }
}
