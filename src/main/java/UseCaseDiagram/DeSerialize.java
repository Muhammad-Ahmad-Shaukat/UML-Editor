package UseCaseDiagram;

import ClassDiagram.Point;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DeSerialize {
    public static List<Object> deserialize(String filePath) {
        List<Object> objects = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            StringBuilder currentBlock = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("?") && line.endsWith("?")) { // Check if it's a type tag
                    if (currentBlock.length() > 0) { // Process the previous block
                        objects.add(parseBlock(currentBlock.toString()));
                        currentBlock.setLength(0); // Clear the buffer
                    }
                }
                currentBlock.append(line).append("\n");
            }
            if (currentBlock.length() > 0) { // Process the last block
                objects.add(parseBlock(currentBlock.toString()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return objects;
    }

    private static Object parseBlock(String block) {
        if (block.contains("?UseCaseActor?")) {
            return deserializeUseCaseActor(block);
        } else if (block.contains("?UseCase?")) {
            return deserializeUseCase(block);
        } else if (block.contains("?UseCaseAssociation?")) {
            return deserializeUseCaseAssociation(block);
        } else if (block.contains("?UseCaseSystemBoundaryBox?")) {
            return deserializeBoundaryBox(block);
        } else if (block.contains("?Dependency Relation?")) {
            return deserializeDependencyRelationship(block);
        }
        return null;
    }

    // Add deserialization methods for each class below
    private static UseCaseActor deserializeUseCaseActor(String data) {
        Point initial = parsePoint(extractTagValue(data, "Initial"));
        String name = extractTagValue(data, "Name");
        return new UseCaseActor(initial, name);
    }

    private static UseCase deserializeUseCase(String data) {
        Point initialPoint = parsePoint(extractTagValue(data, "initial Point"));
        String name = extractTagValue(data, "name");
        return new UseCase(initialPoint, name);
    }

    private static UseCaseAssociation deserializeUseCaseAssociation(String data) {
        Point start = parsePoint(extractTagValue(data, "Start"));
        Point end = parsePoint(extractTagValue(data, "End"));
        UseCase useCase = deserializeUseCase(extractTagValue(data, "UseCase"));
        UseCaseActor actor = deserializeUseCaseActor(extractTagValue(data, "Actor"));
        return new UseCaseAssociation(start, end, useCase, actor);
    }

    private static UseCaseSystemBoundaryBox deserializeBoundaryBox(String data) {
        Point initialPoint = parsePoint(extractTagValue(data, "Initial Point"));
        Double length = Double.valueOf(extractTagValue(data, "Length"));
        Double width = Double.valueOf(extractTagValue(data, "Width"));
        String name = extractTagValue(data, "Name");
        return new UseCaseSystemBoundaryBox(initialPoint, length, width, name);
    }

    private static DependencyRelationship deserializeDependencyRelationship(String data) {
        String dependencyType = extractTagValue(data, "Type");
        UseCase startUseCase = deserializeUseCase(extractTagValue(data, "StartUseCase"));
        UseCase endUseCase = deserializeUseCase(extractTagValue(data, "EndUseCase"));
        return new DependencyRelationship(startUseCase, endUseCase, dependencyType);
    }

    // Utility methods for parsing
    private static Point parsePoint(String data) {
        Double x = Double.valueOf(extractTagValue(data, "X"));
        Double y = Double.valueOf(extractTagValue(data, "Y"));
        return new Point(x, y);
    }

    private static String extractTagValue(String data, String tag) {
        String startTag = "<" + tag + ">";
        String endTag = "</" + tag + ">";
        int startIndex = data.indexOf(startTag) + startTag.length();
        int endIndex = data.indexOf(endTag);
        if (startIndex > -1 && endIndex > -1) {
            return data.substring(startIndex, endIndex).trim();
        }
        return null;
    }
}
