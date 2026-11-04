import japicmp.cmp.*;
import japicmp.model.*;
import java.io.*;
import java.util.*;

/**
 * Runs japicmp on lib-v1.jar / lib-v2.jar of the API evolution data corpus
 * (Jezek & Dietrich, JOT 2017) and writes one CSV row per scenario (= package below testing_lib):
 * binary compatible, source compatible, class changed, reported compatibility change types.
 */
public class CorpusEvaluation {
  static void add(Map<String, Set<String>> m, String pkg, List<JApiCompatibilityChange> cs) {
    for (JApiCompatibilityChange c : cs) m.computeIfAbsent(pkg, k -> new TreeSet<>()).add(c.getType().name());
  }
  public static void main(String[] a) throws Exception {
    JarArchiveComparatorOptions o = new JarArchiveComparatorOptions();
    if (a.length > 3) o.setAccessModifier(AccessModifier.valueOf(a[3]));
    JarArchiveComparator cmp = new JarArchiveComparator(o);
    List<JApiClass> classes = cmp.compare(new JApiCmpArchive(new File(a[0]), "1"), new JApiCmpArchive(new File(a[1]), "2"));
    Map<String, Set<String>> changes = new TreeMap<>();
    Map<String, boolean[]> compat = new TreeMap<>(); // bin, src, anyChange
    for (JApiClass c : classes) {
      String fqn = c.getFullyQualifiedName();
      String[] p = fqn.split("\\.");
      if (p.length < 3 || !p[0].equals("testing_lib")) continue;
      String pkg = p[1];
      boolean[] b = compat.computeIfAbsent(pkg, k -> new boolean[]{true, true, false});
      b[0] &= c.isBinaryCompatible(); b[1] &= c.isSourceCompatible();
      if (c.getChangeStatus() != JApiChangeStatus.UNCHANGED) b[2] = true;
      add(changes, pkg, c.getCompatibilityChanges());
      add(changes, pkg, c.getSuperclass().getCompatibilityChanges());
      for (JApiImplementedInterface i : c.getInterfaces()) add(changes, pkg, i.getCompatibilityChanges());
      for (JApiGenericTemplate g : c.getGenericTemplates()) add(changes, pkg, g.getCompatibilityChanges());
      for (JApiField f : c.getFields()) add(changes, pkg, f.getCompatibilityChanges());
      List<JApiBehavior> bs = new ArrayList<>(c.getMethods()); bs.addAll(c.getConstructors());
      for (JApiBehavior be : bs) {
        add(changes, pkg, be.getCompatibilityChanges());
        for (JApiParameter pa : be.getParameters()) add(changes, pkg, pa.getCompatibilityChanges());
        for (JApiGenericTemplate g : be.getGenericTemplates()) add(changes, pkg, g.getCompatibilityChanges());
        if (be instanceof JApiMethod) add(changes, pkg, ((JApiMethod) be).getReturnType().getCompatibilityChanges());
      }
    }
    try (PrintWriter w = new PrintWriter(a[2])) {
      w.println("change;bin;src;changed;types");
      for (String k : compat.keySet()) {
        boolean[] b = compat.get(k);
        w.println(k + ";" + (b[0] ? 1 : 0) + ";" + (b[1] ? 1 : 0) + ";" + (b[2] ? 1 : 0) + ";" + String.join(",", changes.getOrDefault(k, Collections.emptySet())));
      }
    }
  }
}
