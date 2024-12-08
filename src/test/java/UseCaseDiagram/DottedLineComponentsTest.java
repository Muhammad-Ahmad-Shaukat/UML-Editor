package UseCaseDiagram;

import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DottedLineComponentsTest {

    private Line line;
    private Text text;
    private Polygon arrowHead;
    private DottedLineComponents dottedLineComponents;

    @BeforeEach
    void setUp() {
        line = new Line(0, 0, 100, 100); // Initialize with some coordinates
        text = new Text("Dependency");
        arrowHead = new Polygon(100, 100, 95, 95, 105, 95); // Initialize with a simple triangle shape
        dottedLineComponents = new DottedLineComponents(line, text, arrowHead);
    }

    @AfterEach
    void tearDown() {
        line = null;
        text = null;
        arrowHead = null;
        dottedLineComponents = null;
    }

    @Test
    void getLine() {
        assertEquals(line, dottedLineComponents.getLine(), "The line should be correctly returned.");
    }

    @Test
    void getText() {
        assertEquals(text, dottedLineComponents.getText(), "The text should be correctly returned.");
    }

    @Test
    void getArrowHead() {
        assertEquals(arrowHead, dottedLineComponents.getArrowHead(), "The arrow head should be correctly returned.");
    }
}
