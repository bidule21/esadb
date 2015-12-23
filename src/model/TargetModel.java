package model;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Locale;

import org.apache.commons.lang.Validate;


public class TargetModel implements Serializable, Comparable<TargetModel> {
	private static final long serialVersionUID = 1L;

	private String bezeichnung;
	private String kennnummer;
	private int karton;
	private int bandvorschub;
	private int dia_spiegel;
	private int dia_aussen;
	private int dia_innenzehn;
	private int ringbreite;
	private int min_ring;
	private int max_ring;
	private int max_number;
	
	private int winkel;
	private int art;
	private int style;           // 0 => Zehn und Innenzehn: Ringe       1=> Innezehn ausgefüllt    2=>     Zehn ausgefüllt, Innenzehn irrelevant 
	private int vorhaltedia;
	private int vorhalteabstand;

	public TargetModel(String bezeichnung, String kennnummer, int... values) {
		Validate.isTrue(values.length >= 9, "need 9 or more values in array");
		
		this.bezeichnung = bezeichnung;
		this.kennnummer = kennnummer;
		for (int i = 0; i < 14; i++) {
			setValue(TargetValue.values()[i], i < values.length ? values[i] : 0);
		}
	}

	public TargetModel(TargetModel tm) {
		bezeichnung = tm.bezeichnung;
		kennnummer = tm.kennnummer;
		for (TargetValue tv : TargetValue.values()) {
			setValue(tv, tm.getValue(tv));
		}
	}

	@Override
	public String toString() {
		return bezeichnung;
	}

	@Override
	public int compareTo(TargetModel t) {
		return t.kennnummer.compareTo(kennnummer) * -1;
	}

	public void setName(String name) {
		this.bezeichnung = name;
	}

	public void setNumber(String number) {
		this.kennnummer = number;
	}

	public String getNumber() {
		return kennnummer;
	}

	public boolean setValue(TargetValue type, int value) {
		boolean update = false;
		switch (type) {
			case SIZE:                   karton = value; break;
			case FEED:             bandvorschub = value; break;
			case DIA_BLACK:
				if (value > dia_aussen) {
					dia_aussen = value;
					update = true;
				}
				dia_spiegel = value;
				break;
			case DIA_OUTSIDE:
				if (value < dia_spiegel) {
					dia_spiegel = value;
					update = true;
				}
				if (value < dia_innenzehn + 2 * (max_ring - min_ring) * ringbreite) {
					update = true;
					if (max_ring - min_ring > 1) max_ring --;
					else break;
					if (max_number >= max_ring) max_number--;
				}
				dia_aussen = value;
				break;
			case DIA_INNER_TEN:
				if (dia_aussen < value + 2 * (max_ring - min_ring) * ringbreite) {
					update = true;
					if (max_ring - min_ring > 1) max_ring --;
					else break;
					if (max_number >= max_ring) max_number--;
				}
				dia_innenzehn = value;
				break;
			case RING_WIDTH:
				if (dia_aussen < dia_innenzehn + 2 * (max_ring - min_ring) * value) {
					update = true;
					if (max_ring - min_ring > 1) max_ring --;
					else break;
					if (max_number >= max_ring) max_number--;
				}
				ringbreite = value;
				break;
			case RING_MIN:
				if (value < 0) {
					update = true;
					break;
				}
				if (value >= max_ring) {
					max_ring++;
					update = true;
				}
				if (value > max_number) {
					max_number++;
					update = true;
				}
				if (dia_aussen < dia_innenzehn + 2 * (max_ring - value) * ringbreite) {
					max_ring--;
					if (max_number >= max_ring) max_number--;
					update = true;
				}
				min_ring = value;
				break;
			case RING_MAX:
				if (value < 1) {
					update = true;
					break;
				}
				if (value <= min_ring && min_ring > 0) {
					min_ring--;
					update = true;
				}
				if (value <= max_number && max_number > 0) {
					max_number--;
					update = true;
				}
				if (dia_aussen < dia_innenzehn + 2 * (value - min_ring) * ringbreite) {
					min_ring++;
					if (max_number < min_ring) max_number++;
					update = true;
				}
				max_ring = value;
				break;
			case NUM_MAX:
				if (value >= max_ring)  {
					update = true;
					break;
				}
				if (value < min_ring) {
					update = true;
					break;
				}
				max_number = value;
				break;
			case NUM_ANGLE:              winkel = value; break;
			case TYPE:                      art = value; break;
			case STYLE_TEN:               style = value; break;
			case SUSP_DIA:          vorhaltedia = value; break;
			case SUSP_DISTANCE: vorhalteabstand = value; break;
		}
		return update;
	}

	public int getValue(TargetValue type) {
		switch (type) {
			case SIZE:          return karton;
			case FEED:          return bandvorschub;
			case DIA_BLACK:     return dia_spiegel;
			case DIA_OUTSIDE:   return dia_aussen;
			case DIA_INNER_TEN: return dia_innenzehn;
			case RING_WIDTH:    return ringbreite;
			case RING_MIN:      return min_ring;
			case RING_MAX:      return max_ring;
			case NUM_MAX:       return max_number;
			case NUM_ANGLE:     return winkel;
			case TYPE:          return art;
			case STYLE_TEN:     return style;
			case SUSP_DIA:      return vorhaltedia;
			case SUSP_DISTANCE: return vorhalteabstand;
			default: return 0;
		}
	}

	public int getRingRadius(int i) {
		if (i < 0 || i > max_ring) return 0;
	
		return (dia_aussen / 2) - (i - min_ring) * ringbreite;
	}

	public int getAussenRadius() {
		return dia_aussen / 2;
	}
	
	public int getSpiegelRadius() {
		return dia_spiegel / 2;
	}

	public int getInnenZehnRadius() {
		return dia_innenzehn / 2;
	}
	
	public int getFontSize() {
		return (ringbreite) * 9 / 16;
	}

	public boolean drawRing(int i) {
		return i >= min_ring && i <= max_ring;
	}
	
	public boolean drawNumber(int i) {
		return i >= min_ring && i <= max_number;
	}

	public int getNumberRadius(int i) {
		return (getRingRadius(i) - ringbreite * 3 / 8);
	}

	public boolean blackNumber(int i) {
		return getNumberRadius(i) > (dia_spiegel / 2);
	}

	public String toFile() {
		NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
		format.setGroupingUsed(false);

		String fileName = String.format("0_hs_%s.def", kennnummer.replace('.', '-'));
		try {
			PrintWriter writer = new PrintWriter(fileName);

			writer.println("\">Bezeichnung\"");                                // Name der Scheibe der bei der Programmauswahl angezeigt wird
			writer.println(String.format("\"%s\"", bezeichnung));

			writer.println("\">ScheibenArt\"");                                // Systeminterne Scheibenbezeichnung
			writer.println(String.format("\"%d\"", art));
			                                                                   // 1 => Trefferzonenscheibe mit Klappscheibensteuerung
			                                                                   // 2 => Trefferzonenscheibe / Jagdscheibe
			                                                                   // 3 => Ringscheibe mit weißem Zehner und schwarzer Schrift
			                                                                   // 4 => Ringscheibe mit PA25PC - Modul
			                                                                   // 5 => Inverse Ringscheibe
			                                                                   // 6 => Trefferzonenscheibe mit Doppelsau

			writer.println("\">KennNummer\"");                                 // Kennnr. der Scheibe nach DSB/DJV nur Informativ
			writer.println(String.format("\"DSB %s\"", kennnummer));

			writer.println("\">AussenRadius\"");                               // Radius des 1. Rings in 1/100 mm
			writer.println(String.format("\"%d\"", dia_aussen / 2));
			writer.println("\">SpiegelRadius\"");                              // Radius des Spiegels in 1/100 mm
			writer.println(String.format("\"%d\"", dia_spiegel / 2));			

			writer.println("\">ZehnerRadius\"");                               // Radius des kleinsten Rings in 1/100 mm
			writer.println(String.format("\"%d\"", getRingRadius(max_number)));
			writer.println("\">ZehnerRingStyle\"");                            // Gibt die Optik des Zehnerrings vor, 0=ausgefüllt, 1=Ring
			writer.println(style >= 2 ? "\"0\"": "\"1\"");

			writer.println("\">InnenZehnerRadius\"");                          // Radius des Innenzehners in 1/100mm
			writer.println(String.format("\"%d\"", dia_innenzehn / 2));
			writer.println("\">InnenZehnerRingStyle\"");                       // Gibt die Optik des Innenzehnerrings vor, 0=ausgefüllt, 1=Ring
			writer.println(style >= 1 ? "\"0\"": "\"1\"");

			writer.println("\">InnenKreuzRadius\"");                           // Länge der Kreuzschenkel, zwei mal ergibt Kreuzlinien, 0=keins
			writer.println("\"0\"");
			writer.println("\">InnenKreuzWinkel\"");                           // Gibt an um wieviel Grad das Kreuz gedreht ist
			writer.println("\"0\"");

			writer.println("\">VorhalteSpiegelRadius\"");                      // Gibt an ob Vorhaltespiegel dargestellt werden, 0=keinen
			writer.println(String.format("\"%d\"", vorhaltedia / 2));
			writer.println("\">VorhalteAbstand\"");                            // Abstand der Vorhaltespiegel zur Scheibenmitte
			writer.println(String.format("\"%d\"", vorhalteabstand));

			writer.println("\">Ringbreite\"");                                 // Breite eines Ringes in 1/100mm
			writer.println(String.format("\"%d\"", ringbreite));

			writer.println("\">RingMin\"");                                    // Nummer des kleinsten dargestellten Rings
			writer.println(String.format("\"%d\"", min_ring));
			writer.println("\">RingMax\"");                                    // Nummer des größten dargestellten Rings
			writer.println(String.format("\"%d\"", max_number));

			writer.println("\">RingAnzahl\"");                                 // Anzahl der abgebildeten Ringe
			writer.println(String.format("\"%d\"", max_ring));

			writer.println("\">RingNummerWinkel\"");                           // Anordnungswinkel der Beschriftungszahlen
			writer.println(String.format("\"%d\"", winkel * 45));

			writer.println("\">KartonBreite\"");                               // Breite des Scheibenkartons
			writer.println(String.format("\"%d\"", karton));
			writer.println("\">KartonHoehe\"");                                // Höhe des Scheibenkartons
			writer.println(String.format("\"%d\"", karton));

			writer.println("\">Probe\"");                                      // Anzahl der Probeschüsse eines Durchgangs, bei -1 keine Probe,
			writer.println("\"-1\"");                                          // bei 0 unbegrenzte Probe, an sonsten nach eingestellter Zahl

			writer.println("\">BandVorschub\"");                               // Gibt an wie lange der Motor für den Bandvorschub läuft
			writer.println(String.format("\"%d\"", bandvorschub));

			writer.println("\">DateiName\"");                                  // Gibt den Namen der Datei an, unter der sie im Verzeichnis
			writer.println(String.format("\"%s\"", fileName));                 // C:\Programme\ESA2002 abgespeichert wird.

			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return fileName;
	}
}
