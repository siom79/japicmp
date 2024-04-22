package japicmp.test.annotation;

@TestAnnotation(name = "test-name", list = {"a", "b"}, type = @TestAnnotation.Type(label = "test-label"))
public class AnnotationChanged {
}
