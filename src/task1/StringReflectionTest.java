package task1;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;

/**
 * Ці тести виконуються лише якщо програму запущено з --add-opens java.base/java.lang=ALL-UNNAMED,
 * інакше рефлексія не має доступу до String.value і тести пропускаються.
 */
public class StringReflectionTest {

    @Before
    public void requiresAccess() {
        boolean opened = String.class.getModule().isOpen("java.lang", StringReflection.class.getModule());
        Assume.assumeTrue("потрібен --add-opens java.base/java.lang=ALL-UNNAMED", opened);
    }

    @Test
    public void replacesContentOfTypedString() throws Exception {
        String s = new String("hello".toCharArray());
        int identity = System.identityHashCode(s);
        StringReflection.replaceContent(s, "world!!");
        assertEquals("world!!", s);
        assertEquals(7, s.length());
        assertEquals(identity, System.identityHashCode(s));
    }

    @Test
    public void worksBetweenLatinAndCyrillic() throws Exception {
        String s = new String("abc".toCharArray());
        StringReflection.replaceContent(s, "привіт");
        assertEquals("привіт", s);
        StringReflection.replaceContent(s, "ok");
        assertEquals("ok", s);
    }

    @Test
    public void hashCodeIsRecalculated() throws Exception {
        String s = new String("first".toCharArray());
        int oldHash = s.hashCode();
        StringReflection.replaceContent(s, "second");
        assertNotEquals(oldHash, s.hashCode());
        assertEquals("second".hashCode(), s.hashCode());
    }

    @Test
    public void replacementIsNotAffectedBySharingArrays() throws Exception {
        String source = new String("shared".toCharArray());
        String target = new String("target".toCharArray());
        StringReflection.replaceContent(target, source);
        StringReflection.replaceContent(source, "changed");
        assertEquals("shared", target);
        assertEquals("changed", source);
    }

    @Test
    public void valueFieldTypeIsAnArray() throws Exception {
        String type = StringReflection.valueFieldType();
        assertEquals(true, type.equals("byte[]") || type.equals("char[]"));
        assertSame(type, type);
    }
}
