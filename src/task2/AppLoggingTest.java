package task2;

import org.junit.Test;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AppLoggingTest {

    @Test
    public void loggerOfAClassInheritsLevelFromRootLogger() {
        Logger log = AppLogging.get(AppLoggingTest.class);
        assertEquals("lab10.AppLoggingTest", log.getName());
        assertTrue(log.isLoggable(Level.SEVERE));
        assertTrue(log.isLoggable(Level.FINE));
        assertFalse(log.isLoggable(Level.FINER));
        assertFalse(log.isLoggable(Level.FINEST));
    }

    @Test
    public void consoleGetsOnlyInfoAndAbove() {
        assertTrue(AppLogging.reachesConsole(Level.SEVERE));
        assertTrue(AppLogging.reachesConsole(Level.WARNING));
        assertTrue(AppLogging.reachesConsole(Level.INFO));
        assertFalse(AppLogging.reachesConsole(Level.CONFIG));
        assertFalse(AppLogging.reachesConsole(Level.FINE));
    }

    @Test
    public void fileGetsFineAndAboveButNotFiner() {
        assertTrue(AppLogging.reachesFile(Level.INFO));
        assertTrue(AppLogging.reachesFile(Level.CONFIG));
        assertTrue(AppLogging.reachesFile(Level.FINE));
        assertFalse(AppLogging.reachesFile(Level.FINER));
        assertFalse(AppLogging.reachesFile(Level.FINEST));
    }
}
