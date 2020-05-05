import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Testklasse für den MedienComparator
 * 
 * @author SE2-Team
 * 
 */
public class MedienComparatorTest
{

    private CD _cd1;
    private CD _cd2;
    private CD _cd3;
    private MedienComparator _medienComparator;

    public MedienComparatorTest()
    {

        _cd1 = new CD("Titel a", "Kommentar", "Interpret", 100);
        _cd2 = new CD("Titel b", "Kommentar", "Interpret", 100);
        _cd3 = new CD("Titel b", "Kommentar", "Interpret", 100);

        _medienComparator = new MedienComparator();
    }

    @Test
    public void testCompareEinMedium()
    {
        assertTrue(_medienComparator.compare(_cd3, _cd1) > 0);
        assertTrue(_medienComparator.compare(_cd1, _cd2) < 0);
        assertEquals(0, _medienComparator.compare(_cd3, _cd2));
    }

}
