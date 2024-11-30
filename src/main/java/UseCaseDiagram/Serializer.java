package UseCaseDiagram;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Serializer {

    // Method to serialize a list of objects into a file
    public static void serialize(List<Object> objects, String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(new File(filePath), objects); // Serializing the list of objects
    }

    // Method to deserialize objects from a file into a list
    public static List<Object> deserialize(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Object> objects = objectMapper.readValue(new File(filePath), List.class); // Reading the list of objects
        return objects;
    }
}
