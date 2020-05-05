import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Testklasse für den MedienComparator
 * 
 * @author SE2-Team
 * 
 */
public class AusleiheMedienFormatiererComparatorTest
{

    private CD _cd1;
    private CD _cd2;
    private CD _cd3;
    private AusleiheMedienFormatiererComparator _medienComparator;

    public AusleiheMedienFormatiererComparatorTest()
    {

        _cd1 = new CD("Titel a", "Kommentar", "Interpret", 100);
        _cd2 = new CD("Titel b", "Kommentar", "Interpret", 100);
        _cd3 = new CD("Titel b", "Kommentar", "Interpret", 100);

        _medienComparator = new AusleiheMedienFormatiererComparator();
    }

    @Test
    public void testCompareEinMedium()
    {
        assertTrue(_medienComparator.compare(erzeugeFormatierer(_cd3),
                erzeugeFormatierer(_cd1)) > 0);
        assertTrue(_medienComparator.compare(erzeugeFormatierer(_cd1),
                erzeugeFormatierer(_cd2)) < 0);
        assertEquals(0, _medienComparator.compare(erzeugeFormatierer(_cd3),
                erzeugeFormatierer(_cd2)));
    }

    private AusleiheMedienFormatierer erzeugeFormatierer(Medium medium)
    {
        return new AusleiheMedienFormatierer(medium, false);
    }
}
