package japicmp.test;

import japicmp.cmp.JarArchiveComparator;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
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
        assertThat(jApiClasses.size(), is(2));
        assertThat(getJApiClass(jApiClasses, Removed.class.getName()), is(notNullValue()));
        assertThat(getJApiClass(jApiClasses, Added.class.getName()), is(notNullValue()));
        assertThat(getJApiClass(jApiClasses, Removed.class.getName()).getChangeStatus(), is(JApiChangeStatus.REMOVED));
        assertThat(getJApiClass(jApiClasses, Added.class.getName()).getChangeStatus(), is(JApiChangeStatus.NEW));
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
}
