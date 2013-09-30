package japicmp.test;

import japicmp.cmp.JarArchiveComparator;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.model.JApiMethod;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class BasicTest {

    @Test
    public void test() {
        JarArchiveComparator jarArchiveComparator = new JarArchiveComparator();
        List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
        assertThat(jApiClasses.size(), is(3));
        JApiClass jApiClassRemoved = getJApiClass(jApiClasses, Removed.class.getName());
        JApiClass jApiClassAdded = getJApiClass(jApiClasses, Added.class.getName());
        JApiClass jApiClassUnchanged = getJApiClass(jApiClasses, Unchanged.class.getName());
        assertThat(jApiClassRemoved, is(notNullValue()));
        assertThat(jApiClassAdded, is(notNullValue()));
        assertThat(jApiClassUnchanged, is(notNullValue()));
        assertThat(jApiClassRemoved.getChangeStatus(), is(JApiChangeStatus.REMOVED));
        assertThat(jApiClassAdded.getChangeStatus(), is(JApiChangeStatus.NEW));
        assertThat(jApiClassUnchanged.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
        assertThat(getJApiMethod(jApiClassUnchanged.getMethods(), "unchangedMethod"), is(notNullValue()));
    }

    private File getArchive(String filename) {
        return new File("target" + File.separator + filename);
    }

    private JApiClass getJApiClass(List<JApiClass> jApiClasses, String fqn) {
        for (JApiClass jApiClass : jApiClasses) {
            if (jApiClass.getFullyQualifiedName().equals(fqn)) {
                return jApiClass;
            }
        }
        throw new IllegalArgumentException("No class found with name " + fqn + ".");
    }

    private JApiMethod getJApiMethod(List<JApiMethod> jApiMethods, String name) {
        for(JApiMethod jApiMethod : jApiMethods) {
            if(jApiMethod.getName().equals(name)) {
                return jApiMethod;
            }
        }
        throw new IllegalArgumentException("No method found with name " + name + ".");
    }
}
