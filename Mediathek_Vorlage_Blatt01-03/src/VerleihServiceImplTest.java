import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
//Die in den Testfällen verwendeten assert-Anweisungen werden über
//einen sogenannten statischen Import bereitgestellt, zum Beispiel:
//import static org.junit.Assert.assertEquals;
//
// Um die Annotation @Test verwenden zu können, muss diese importiert
// werden: import org.junit.Test;

/**
 * Diese Klasse testet die Implementation des VerleihService.
 */
public class VerleihServiceImplTest
{
    private Kunde _homer;
    private Kunde _roger;
    private Kunde _brian;
    private KundenstammService _kundenstamm;

    private Datum _datum;

    private Medium _abbey;
    private Medium _bad;
    private Medium _shape;
    private MedienbestandService _medienbestand;

    private VerleihServiceImpl _verleihService;

    public VerleihServiceImplTest()
    {
        setUpKunden();
        setUpMedien();
        setUpVerleihService();
    }

    @Test
    // Alle Testmethoden erhalten die Annotation @Test. Dafür müssen diese nicht
    // mehr mit test im Namen beginnen. Dies wird jedoch aus Gewohnheit
    // oft weiter verwendet.
    public void testKundenstamm()
    {
        assertTrue(_verleihService.kundeImBestand(_homer));
        assertTrue(_verleihService.kundeImBestand(_roger));
        assertTrue(_verleihService.kundeImBestand(_brian));

        Kunde marge = new Kunde(new Kundennummer(123459), "Marge", "Bouvier");
        assertFalse(_verleihService.kundeImBestand(marge));
    }

    @Test
    public void testAmAnfangIstNichtsVerliehen()
    {
        assertTrue(_verleihService
            .sindAlleNichtVerliehen(_medienbestand.getMedien()));
        for (Kunde kunde : _kundenstamm.getKunden())
        {
            assertTrue(_verleihService.istVerleihenMoeglich(kunde,
                    _medienbestand.getMedien()));
        }
    }

    @Test
    public void testNochEinTestFall1()
    {

    }

    @Test
    public void testgetAusgelieheneMedienFuer()
    {
        List<Medium> _sindVerliehen = _medienbestand.getMedien()
            .subList(0, 2);

        //Verleiht _shape an _homer
        _verleihService.verleiheAn(_homer, _sindVerliehen, _datum);

        assertEquals(_sindVerliehen,
                _verleihService.getAusgelieheneMedienFuer(_homer));

    }

    @Test
    public void testNimmZurueck()
    {
        List<Medium> _sindVerliehen = _medienbestand.getMedien()
            .subList(0, 2);

        //Verleiht _shape an _homer
        _verleihService.verleiheAn(_homer, _sindVerliehen, _datum);

        assertTrue(_verleihService.istVerliehen(_sindVerliehen.get(0)));
        _verleihService.nimmZurueck(_sindVerliehen, _datum);
        assertFalse(_verleihService.istVerliehen(_sindVerliehen.get(0)));

    }

    /**
     * Alle funktionierenden Test sind in einer Klasse weil sie alle die Listen _sindVerliehen 
     * und _sindNichtVerliehen benutzten. Soll für jeden Test eine eigene Methode geschrieben werden? 
     * 
     */

    @Test
    public void testsFuerVerleihen()
    {
        //In der Liste sind _abbey[0] und _bad[1]
        List<Medium> _sindVerliehen = _medienbestand.getMedien()
            .subList(0, 2);
        //In der Liste sind _shape[0] 
        List<Medium> _sindNichtVerliehen = _medienbestand.getMedien()
            .subList(2, 3);
        //Verleiht _abbey[0] und _bad[1] an _homer
        _verleihService.verleiheAn(_homer, _sindVerliehen, _datum);

        //ist verliehen Test
        assertTrue(_verleihService.istVerliehen(_sindVerliehen.get(0)));
        assertTrue(_verleihService.istVerliehen(_sindVerliehen.get(1)));
        assertFalse(_verleihService.istVerliehen(_sindNichtVerliehen.get(0)));
        assertTrue(_verleihService.istVerleihenMoeglich(_homer,
                _sindNichtVerliehen));
        assertTrue(_verleihService.istVerliehen(_sindVerliehen.get(0)));
        //Prüft ob Homer 2 Medien ausgeliehen hat
        assertEquals(_sindVerliehen,
                _verleihService.getAusgelieheneMedienFuer(_homer));
        //getEntleiherFuer
        assertEquals(_homer, _verleihService.getEntleiherFuer(_bad));

        //_abbey und _bar werden zurueckgegeben
        _verleihService.nimmZurueck(_sindVerliehen, _datum);

        assertTrue(_verleihService
            .sindAlleNichtVerliehen(_medienbestand.getMedien()));

        assertFalse(_verleihService.istVerliehen(_sindVerliehen.get(0)));
        //Leere Liste _isEmpty
        List<Medium> _isEmpty = new ArrayList<Medium>();
        // _isEmpty.clear();
        //Prüft ob Homer nut noch ein Medium ausgeliehen hat
        assertEquals(_isEmpty,
                _verleihService.getAusgelieheneMedienFuer(_homer));

    private void setUpKunden()
    {
        _homer = new Kunde(new Kundennummer(123456), "Homer", "Simpson");
        _roger = new Kunde(new Kundennummer(123457), "Roger", "Smith");
        _brian = new Kunde(new Kundennummer(123458), "Brian", "Griffin");

        List<Kunde> testkunden = new ArrayList<Kunde>();
        testkunden.add(_homer);
        testkunden.add(_roger);
        testkunden.add(_brian);
        _kundenstamm = new KundenstammServiceImpl(testkunden);
    }

    private void setUpMedien()
    {
        _abbey = new CD("Abbey Road", "Meisterwerk", "Beatles", 44);
        _bad = new CD("Bad", "not as bad as the title might suggest",
                "Michael Jackson", 48);
        _shape = new CD("The Colour And The Shape", "bestes Album der Gruppe",
                "Foo Fighters", 46);

        List<Medium> _medien = new ArrayList<Medium>();
        _medien.add(_abbey);
        _medien.add(_bad);
        _medien.add(_shape);
        _medienbestand = new MedienbestandServiceImpl(_medien);
    }

    private void setUpVerleihService()
    {
        _verleihService = new VerleihServiceImpl(_kundenstamm, _medienbestand,
                new ArrayList<Verleihkarte>());
    }
}
