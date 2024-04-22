package japicmp.test.annotation;

@TestAnnotation(name = "test-name-changed", list = {"a", "c"}, type = @TestAnnotation.Type(label = "test-label-changed"))
public class AnnotationChanged {
}
