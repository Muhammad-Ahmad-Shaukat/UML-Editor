package ClassDiagram;

public class Attribute {
    private String name;
    private String dataType;
    private String accessModifier;

    public Attribute(String name, String dataType, String accessModifier) {
        this.name = name;
        this.dataType = dataType;
        this.accessModifier = accessModifier;
    }

    public Attribute(String name, String dataType) {
        this.name = name;
        this.dataType = dataType;
        this.accessModifier = "public";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getAccessModifier() {
        return accessModifier;
    }

    public void setAccessModifier(String accessModifier) {
        this.accessModifier = accessModifier;
    }

    @Override
    public String toString() {
        return switch (accessModifier.toLowerCase()) {
            case "private" -> "-" + name + " : " + dataType;
            case "public" -> "+" + name + " : " + dataType;
            default -> "#" + name + " : " + dataType;
        };
    }

    public void print() {
        System.out.println(name + " : " + dataType);
    }
}