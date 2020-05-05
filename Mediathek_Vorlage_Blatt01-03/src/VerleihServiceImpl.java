import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Diese Klasse implementiert das Interface VerleihService. Siehe dortiger
 * Kommentar.
 * 
 * @author SE2-Team
 * @version SoSe 2020
 */
class VerleihServiceImpl extends AbstractObservableService
        implements VerleihService
{
    /**
     * Diese Map speichert für jedes eingefügte Medium die dazugehörige
     * Verleihkarte. Ein Zugriff auf die Verleihkarte ist dadurch leicht über
     * die Angabe des Mediums möglich. Beispiel: _verleihkarten.get(medium)
     */
    private Map<Medium, Verleihkarte> _verleihkarten;

    /**
     * Der Medienbestand.
     */
    private MedienbestandService _medienbestand;

    /**
     * Der Kundenstamm.
     */
    private KundenstammService _kundenstamm;

    /**
     * Konstruktor. Erzeugt einen neuen VerleihServiceImpl.
     * 
     * @param kundenstamm Der KundenstammService.
     * @param medienbestand Der MedienbestandService.
     * @param initialBestand Der initiale Bestand.
     * 
     */
    public VerleihServiceImpl(KundenstammService kundenstamm,
            MedienbestandService medienbestand,
            List<Verleihkarte> initialBestand)
    {
        _verleihkarten = erzeugeVerleihkartenBestand(initialBestand);
        _kundenstamm = kundenstamm;
        _medienbestand = medienbestand;
    }

    @Override
    public void verleiheAn(Kunde kunde, List<Medium> medien, Datum ausleihDatum)
    {
        // Es sollte nur möglich sein an Kunden zu verliehen die im Bestand sind.
        assert true == kundeImBestand(
                kunde) : "Vorbedingung verletzt: Kunde ist nicht im Bestand";
        //Da eine ganze Liste ausgeliehen werden kann ist es sinnvoll wenn alle Medien auch ausleihbar sind
        assert true == sindAlleNichtVerliehen(
                medien) : "Vorbedingung verletzt: Mindestens eins der angegebenen Medien ist nicht ausleihbar oder die Liste ist leer";
        // Prüfung auf korrekte/moegliche Angaben ist immer sinnvoll
        assert ausleihDatum != null : "Vorbedingung verletzt: ausleihDatum ist nicht Instanziiert/Ausleihdatum muss ein Wert zugewiesen sein";

        for (Medium medium : medien)
        {
            Verleihkarte karte = new Verleihkarte(kunde, medium, ausleihDatum);

        }

        informiereUeberAenderung();
    }

    @Override
    public boolean istVerleihenMoeglich(Kunde kunde, List<Medium> medien)
    {
        //Wenn Kunde nicht im Bestand ist, ist auch keine verleihen Möglich
        assert kundeImBestand(
                kunde) == true : "Vorbedingung verletzt: Kunde ist nicht im Bestand";
        //Nur Medien die im bestand sind sollten ausleihbar sein. Sonst könnten beliebige Titel im Programm ausgeliehen werden ohne dass sie physisch ausgeliehen wurden.
        assert medienImBestand(
                medien) == true : "Vorbedingung verletzt: Min eins der Medien ist nicht im Bestand";

        return sindAlleNichtVerliehen(medien);
    }

    @Override
    public Kunde getEntleiherFuer(Medium medium)
    {
        //nur ein ausgeliehenes Medium hat einen Entleiher
        assert istVerliehen(
                medium) == true : "Vorbedingung verletzt: Medium ist nicht verliehen";

        Verleihkarte verleihkarte = _verleihkarten.get(medium);
        return verleihkarte.getEntleiher();
    }

    @Override
    public List<Medium> getAusgelieheneMedienFuer(Kunde kunde)
    {
        //Nur Kunden aus dem Bestand, können etwas ausgeliehen haben. 
        assert kundeImBestand(
                kunde) == true : "Vorbedingung verletzt: Kunde ist nicht im Bestand";

        List<Medium> result = new ArrayList<Medium>();
        for (Verleihkarte verleihkarte : _verleihkarten.values())
        {
            if (verleihkarte.getEntleiher()
                .equals(kunde))
            {
                result.add(verleihkarte.getMedium());
            }
        }
        return result;
    }

    @Override
    public List<Verleihkarte> getVerleihkarten()
    {
        return new ArrayList<Verleihkarte>(_verleihkarten.values());
    }

    @Override
    public void nimmZurueck(List<Medium> medien, Datum rueckgabeDatum)
    {
        //Nur ausgeliehene Medien können zurück gegeben werden. Sonst könnte der Kunde auch Medien zurück geben die er gar nicht ei uns ausgeliehen hat
        assert sindAlleVerliehen(
                medien) == true : "Vorbedingung verletzt: Min eins der Medien ist verliehen";
        //Gueltige Eingabe fuer ein Datum ist immer wichtig. Auch zum berechnen der Kosten
        assert rueckgabeDatum != null : "Vorbedingung verletzt: rueckgabeDatum ausleihDatum ist nicht Instanziiert/Ausleihdatum muss ein Wert zugewiesen sein";

        for (Medium medium : medien)
        {
            _verleihkarten.remove(medium);
        }
        informiereUeberAenderung();
    }

    @Override
    public boolean istVerliehen(Medium medium)
    {
        //Nur Medien die wir auch im Bestand haben, können verliehen werden. Falls Medium nicht im Bestand ist muss man gar nciht weiter suchen 
        assert mediumImBestand(
                medium) == true : "Vorbedingung verletzt: Medium ist nicht im Bestand";

        return _verleihkarten.get(medium) != null;
    }

    @Override
    public boolean sindAlleNichtVerliehen(List<Medium> medien)
    {
        assert medienImBestand(
                medien) == true : "Vorbedingung verletzt: Min eins der Medien ist nicht im Bestand";

        boolean result = true;
        for (Medium medium : medien)
        {
            if (istVerliehen(medium))
            {
                result = false;
            }
        }
        return result;
    }

    @Override
    public boolean sindAlleVerliehen(List<Medium> medien)
    {
        assert medienImBestand(
                medien) == true : "Min eins der Medien ist nicht im Bestand";

        boolean result = true;
        for (Medium medium : medien)
        {
            if (!istVerliehen(medium))
            {
                result = false;
            }
        }
        return result;
    }

    @Override
    public boolean kundeImBestand(Kunde kunde)
    {
        //Kunde muss real auch existieren. Es muss ein Objekt referenziert werden um leere Einträge zu vermeiden
        assert kunde != null : "kunde ist null/ nicht Instanzisiert";

        return _kundenstamm.enthaeltKunden(kunde);
    }

    @Override
    public boolean mediumImBestand(Medium medium)
    {
        //Es muss ein real existierendes Medium sein, da wir ja real existierende Medium vermieten. Es muss ein Objekt referenziert werden um leere Einträge zu vermeiden
        assert medium != null : "medium ist null/ nicht Instanzisiert";

        return _medienbestand.enthaeltMedium(medium);
    }

    @Override
    public boolean medienImBestand(List<Medium> medien)
    {
        assert medien != null : "Vorbedingung verletzt: medien ist null";
        assert !medien
            .isEmpty() == true : "Vorbedingung verletzt: Liste ist leer";

        boolean result = true;
        for (Medium medium : medien)
        {
            if (!mediumImBestand(medium))
            {
                result = false;
                break;
            }
        }
        return result;
    }

    @Override
    public List<Verleihkarte> getVerleihkartenFuer(Kunde kunde)
    {
        // Nur Kunden die im Bestandsind, können eine Verleihkarte haben
        assert kundeImBestand(
                kunde) == true : "Vorbedingung verletzt: Kunde ist nicht im Bestand";

        List<Verleihkarte> result = new ArrayList<Verleihkarte>();
        for (Verleihkarte verleihkarte : _verleihkarten.values())
        {
            if (verleihkarte.getEntleiher()
                .equals(kunde))
            {
                result.add(verleihkarte);
            }
        }
        return result;
    }

    @Override
    public Verleihkarte getVerleihkarteFuer(Medium medium)
    {
        // Nur Medien die verliehen sind können eine Verleihkarte haben
        assert istVerliehen(
                medium) == true : "Vorbedingung verletzt: Medium ist nicht verliehen";

        return _verleihkarten.get(medium);
    }

    /**
     * Erzeugt eine neue HashMap aus dem Initialbestand.
     */
    private HashMap<Medium, Verleihkarte> erzeugeVerleihkartenBestand(
            List<Verleihkarte> initialBestand)
    {
        HashMap<Medium, Verleihkarte> result = new HashMap<Medium, Verleihkarte>();
        for (Verleihkarte verleihkarte : initialBestand)
        {
            result.put(verleihkarte.getMedium(), verleihkarte);
        }
        return result;
    }

}
