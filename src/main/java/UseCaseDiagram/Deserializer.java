package UseCaseDiagram;

import ClassDiagram.Point;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Deserializer {

    public static void deserialize(String filePath, ArrayList<UseCase> useCases,
                                   ArrayList<UseCaseAssociation> associations, ArrayList<UseCaseActor> actors,
                                   ArrayList<DependencyRelationship> includeRelationships, ArrayList<DependencyRelationship> excludeRelationships,
                                   ArrayList<UseCaseSystemBoundaryBox> boundaryBoxes) {

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            UseCase currentUseCase = null;
            UseCaseActor currentActor = null;
            UseCaseAssociation currentAssociation = null;
            DependencyRelationship currentDependency = null;
            UseCaseSystemBoundaryBox currentBoundaryBox = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Parse UseCase
                if (line.startsWith("<UseCase>")) {
                    double x = 0, y = 0;
                    String name = null;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.startsWith("<x>")) {
                            int endIndex = line.indexOf("</x>");
                            if (endIndex != -1) {
                                x = Double.parseDouble(line.substring(3, endIndex));
                            }
                        }
                        if (line.startsWith("<y>")) {
                            int endIndex = line.indexOf("</y>");
                            if (endIndex != -1) {
                                y = Double.parseDouble(line.substring(3, endIndex));
                            }
                        }
                        if (line.startsWith("<Name>")) {
                            int endIndex = line.indexOf("</Name>");
                            if (endIndex != -1) {
                                name = line.substring(6, endIndex);
                            }
                        }
                        if (line.startsWith("</UseCase>")) {
                            currentUseCase = new UseCase(new Point(x, y), name != null ? name : "Use Case");
                            useCases.add(currentUseCase);
                            break;
                        }
                    }
                }

                // Parse UseCaseActor
                if (line.startsWith("<UseCaseActor>")) {
                    double x = 0, y = 0;
                    String name = null;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.startsWith("<x>")) {
                            int endIndex = line.indexOf("</x>");
                            if (endIndex != -1) {
                                x = Double.parseDouble(line.substring(3, endIndex));
                            }
                        }
                        if (line.startsWith("<y>")) {
                            int endIndex = line.indexOf("</y>");
                            if (endIndex != -1) {
                                y = Double.parseDouble(line.substring(3, endIndex));
                            }
                        }
                        if (line.startsWith("<Name>")) {
                            int endIndex = line.indexOf("</Name>");
                            if (endIndex != -1) {
                                name = line.substring(6, endIndex);
                            }
                        }
                        if (line.startsWith("</UseCaseActor>")) {
                            currentActor = new UseCaseActor(new Point(x, y), name != null ? name : "Actor");
                            actors.add(currentActor);
                            break;
                        }
                    }
                }

                // Parse UseCaseAssociation
                if (line.startsWith("<UseCaseAssociation>")) {
                    double startX = 0, startY = 0, endX = 0, endY = 0;
                    UseCase associatedUseCase = null;
                    UseCaseActor associatedActor = null;

                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.startsWith("<StartPoint>")) {
                            while ((line = reader.readLine()) != null) {
                                line = line.trim();
                                if (line.startsWith("<x>")) {
                                    int endIndex = line.indexOf("</x>");
                                    if (endIndex != -1) {
                                        startX = Double.parseDouble(line.substring(3, endIndex));
                                    }
                                }
                                if (line.startsWith("<y>")) {
                                    int endIndex = line.indexOf("</y>");
                                    if (endIndex != -1) {
                                        startY = Double.parseDouble(line.substring(3, endIndex));
                                    }
                                }
                                if (line.startsWith("</StartPoint>")) {
                                    break;
                                }
                            }
                        }
                        if (line.startsWith("<EndPoint>")) {
                            while ((line = reader.readLine()) != null) {
                                line = line.trim();
                                if (line.startsWith("<x>")) {
                                    int endIndex = line.indexOf("</x>");
                                    if (endIndex != -1) {
                                        endX = Double.parseDouble(line.substring(3, endIndex));
                                    }
                                }
                                if (line.startsWith("<y>")) {
                                    int endIndex = line.indexOf("</y>");
                                    if (endIndex != -1) {
                                        endY = Double.parseDouble(line.substring(3, endIndex));
                                    }
                                }
                                if (line.startsWith("</EndPoint>")) {
                                    break;
                                }
                            }
                        }
                        if (line.startsWith("<UseCase>")) {
                            while ((line = reader.readLine()) != null) {
                                line = line.trim();
                                if (line.startsWith("<x>")) {
                                    int endIndex = line.indexOf("</x>");
                                    if (endIndex != -1) {
                                        double useCaseX = Double.parseDouble(line.substring(3, endIndex));
                                    }
                                }
                                if (line.startsWith("<y>")) {
                                    int endIndex = line.indexOf("</y>");
                                    if (endIndex != -1) {
                                        double useCaseY = Double.parseDouble(line.substring(3, endIndex));
                                    }
                                }
                                if (line.startsWith("<Name>")) {
                                    int endIndex = line.indexOf("</Name>");
                                    if (endIndex != -1) {
                                        String useCaseName = line.substring(6, endIndex);
                                        associatedUseCase = new UseCase(new Point(startX, startY), useCaseName);
                                    }
                                }
                                if (line.startsWith("</UseCase>")) {
                                    break;
                                }
                            }
                        }
                        if (line.startsWith("<Actor>")) {
                            while ((line = reader.readLine()) != null) {
                                line = line.trim();
                                if (line.startsWith("<x>")) {
                                    int endIndex = line.indexOf("</x>");
                                    if (endIndex != -1) {
                                        double actorX = Double.parseDouble(line.substring(3, endIndex));
                                    }
                                }
                                if (line.startsWith("<y>")) {
                                    int endIndex = line.indexOf("</y>");
                                    if (endIndex != -1) {
                                        double actorY = Double.parseDouble(line.substring(3, endIndex));
                                    }
                                }
                                if (line.startsWith("<Name>")) {
                                    int endIndex = line.indexOf("</Name>");
                                    if (endIndex != -1) {
                                        String actorName = line.substring(6, endIndex);
                                        associatedActor = new UseCaseActor(new Point(startX, startY), actorName);
                                    }
                                }
                                if (line.startsWith("</Actor>")) {
                                    break;
                                }
                            }
                        }
                        if (line.startsWith("</UseCaseAssociation>")) {
                            currentAssociation = new UseCaseAssociation(new Point(startX, startY), new Point(endX, endY), associatedUseCase, associatedActor);
                            associations.add(currentAssociation);
                            break;
                        }
                    }
                }

                // Parse DependencyRelationship
                if (line.startsWith("<DependencyRelationship>")) {
                    String dependencyType = null;
                    UseCase startUseCase = null, endUseCase = null;

                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.startsWith("<DependencyType>")) {
                            int endIndex = line.indexOf("</DependencyType>");
                            if (endIndex != -1) {
                                dependencyType = line.substring(15, endIndex);
                            }
                        }
                        if (line.startsWith("<StartUseCase>")) {
                            while ((line = reader.readLine()) != null) {
                                line = line.trim();
                                if (line.startsWith("<x>")) {
                                    int endIndex = line.indexOf("</x>");
                                    if (endIndex != -1) {
                                        double useCaseX = Double.parseDouble(line.substring(3, endIndex));
                                    }
                                }
                                if (line.startsWith("<y>")) {
                                    int endIndex = line.indexOf("</y>");
                                    if (endIndex != -1) {
                                        double useCaseY = Double.parseDouble(line.substring(3, endIndex));
                                    }
                                }
                                if (line.startsWith("<Name>")) {
                                    int endIndex = line.indexOf("</Name>");
                                    if (endIndex != -1) {
                                        String useCaseName = line.substring(6, endIndex);
                                        startUseCase = new UseCase(new Point(0.0, 0.0), useCaseName);
                                    }
                                }
                                if (line.startsWith("</StartUseCase>")) {
                                    break;
                                }
                            }
                        }
                        if (line.startsWith("<EndUseCase>")) {
                            while ((line = reader.readLine()) != null) {
                                line = line.trim();
                                if (line.startsWith("<x>")) {
                                    int endIndex = line.indexOf("</x>");
                                    if (endIndex != -1) {
                                        double useCaseX = Double.parseDouble(line.substring(3, endIndex));
                                    }
                                }
                                if (line.startsWith("<y>")) {
                                    int endIndex = line.indexOf("</y>");
                                    if (endIndex != -1) {
                                        double useCaseY = Double.parseDouble(line.substring(3, endIndex));
                                    }
                                }
                                if (line.startsWith("<Name>")) {
                                    int endIndex = line.indexOf("</Name>");
                                    if (endIndex != -1) {
                                        String useCaseName = line.substring(6, endIndex);
                                        endUseCase = new UseCase(new Point(0.0, 0.0), useCaseName);
                                    }
                                }
                                if (line.startsWith("</EndUseCase>")) {
                                    break;
                                }
                            }
                        }
                        if (line.startsWith("</DependencyRelationship>")) {
                            if ("Include".equalsIgnoreCase(dependencyType)) {
                                currentDependency = new DependencyRelationship(startUseCase, endUseCase, "Include");
                                includeRelationships.add(currentDependency);
                            } else if ("Exclude".equalsIgnoreCase(dependencyType)) {
                                currentDependency = new DependencyRelationship(startUseCase, endUseCase, "Exclude");
                                excludeRelationships.add(currentDependency);
                            }
                            break;
                        }
                    }
                }

                // Parse UseCaseSystemBoundaryBox
                if (line.startsWith("<UseCaseSystemBoundaryBox>")) {
                    double x = 0, y = 0, width = 0, height = 0;
                    String name = null;

                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.startsWith("<x>")) {
                            int endIndex = line.indexOf("</x>");
                            if (endIndex != -1) {
                                x = Double.parseDouble(line.substring(3, endIndex));
                            }
                        }
                        if (line.startsWith("<y>")) {
                            int endIndex = line.indexOf("</y>");
                            if (endIndex != -1) {
                                y = Double.parseDouble(line.substring(3, endIndex));
                            }
                        }
                        if (line.startsWith("<width>")) {
                            int endIndex = line.indexOf("</width>");
                            if (endIndex != -1) {
                                width = Double.parseDouble(line.substring(7, endIndex));
                            }
                        }
                        if (line.startsWith("<height>")) {
                            int endIndex = line.indexOf("</height>");
                            if (endIndex != -1) {
                                height = Double.parseDouble(line.substring(8, endIndex));
                            }
                        }
                        if (line.startsWith("<name>")) {
                            int endIndex = line.indexOf("</name>");
                            if (endIndex != -1) {
                                name = line.substring(6, endIndex);
                            }
                        }
                        if (line.startsWith("</UseCaseSystemBoundaryBox>")) {
                            Point initialPoint = new Point(x, y);
                            UseCaseSystemBoundaryBox currentBoundaryBox1;
                            if (name != null) {
                                currentBoundaryBox1 = new UseCaseSystemBoundaryBox(initialPoint, width, height, name);
                            } else {
                                currentBoundaryBox1 = new UseCaseSystemBoundaryBox(initialPoint, width, height);
                            }
                            boundaryBoxes.add(currentBoundaryBox1);
                            break;
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
