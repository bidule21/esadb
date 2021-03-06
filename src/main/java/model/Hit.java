package model;


import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Pattern;


public class Hit implements Serializable, Comparable<Hit> {
	private static final long serialVersionUID = 2L;

	private int linie;
	private boolean probe;
	private int nummer;
	private float wert;
	private String lage;
	private double x;
	private double y;
	private double r;
	private double phi;
	private int zeit;

	/*
		PN1 : "51$34$0708023-0004$0$0$1$1$ 0$ 9.4$g$ 1244$ 289     $ 1277.13$  13.079$ 8"
		PN2 : "51$34$0708023-0004$0$0$1$2$ 0$ 8.6$k$ 1306$ 1343    $ 1873.31$    45.8$ 9"
		MN1 : "51$34$0708023-0004$0$0$1$1$-1$ 7.8$k$ 1655$ 1872    $ 2498.68$  48.521$25"
		MN2 : "51$34$0708023-0004$0$0$1$2$-1$ 8.7$f$-1794$-17.99999$ 1794.09$ 180.575$26"
		      " A$ B$           C$D$E$F$G$ H$   I$J$    K$        L$       M$       N$ O"
		
		A = WettkampfID
		B = DisziplinID
		C = PassNr
		D = 
		E = 
		F = Linie
		G = SchussNr
		H = Wertung : Ja(-1), Nein(0)
		I = Ringwert
		J = Trefferlage     f, g, h, i, j, k, l, m, R          LL, RR, OO, UU, OL, OR, UL, UR, M
		K = X_Pos
		L = Y_Pos
		M = R_Pos
		N = Phi_pos
		O = Relative Zeit in Sekunden
	 */
	public Hit(String raw) {
		String sa[] = raw.replace('"', '$').substring(1).split(Pattern.quote("$"));
		linie = Integer.parseInt(sa[5].trim());
   		nummer = Integer.parseInt(sa[6].trim());
   		probe = (sa[7].equals("0") ? true : false);
   		wert = Float.parseFloat(sa[8].trim());
   		lage = sa[9].trim();
   		x = Double.parseDouble(sa[10].trim());
   		y = Double.parseDouble(sa[11].trim());
   		r = Double.parseDouble(sa[12].trim());
   		phi = Double.parseDouble(sa[13].trim());
   		zeit = Integer.parseInt(sa[14].trim());
	}

	public Hit(boolean probe, int nummer) {
		linie = 0;
   		this.nummer = nummer;
   		this.probe = probe;
   		wert = 0;
   		lage = "R";
   		x = 0;
   		y = 0;
   		r = 0;
   		phi = 0;
   		zeit = 0;
	}

	public Hit(float wert, String lage, double x, double y, double r, double phi, int zeit) {
		this.linie = 0;
   		this.nummer = 1;
   		this.probe = true;
   		this.wert = wert;
   		this.lage = lage;
   		this.x = x;
   		this.y = y;
   		this.r = r;
   		this.phi = phi;
   		this.zeit = zeit;
	}

	public void setNummer(int nummer) {
		this.nummer = nummer;
	}

	public void setProbe(boolean probe) {
		this.probe = probe;
	}

	public int getLinie() {
		return linie;
	}

	public int getNummer() {
		return nummer;
	}

	public boolean isProbe() {
		return probe;
	}

	public float getWert() {
		return wert;
	}

	public boolean isInnenZehner() {
		//return lage.equals("R"); <- Gibt leider keine Auskunft :(
		return r <= 530;
	}

	public String getLage() {
		return isInnenZehner() ? "R" : lage;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}

	public double getR() {
		return r;
	}
	
	public double getPhi() {
		return phi;
	}

	public int getZeit() {
		return zeit;
	}

	@Override
	public String toString() {
		return String.format("%4.1f", wert);
	}

	public void print(FileWriter writer) {
		NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
		format.setGroupingUsed(false);
		format.setMinimumFractionDigits(0);

		writer.println(String.format("%d", nummer));
		writer.println(format.format(wert));
		writer.println(String.format("\"%s\"", lage));
		writer.println(format.format(x));
		writer.println(format.format(y));
		writer.println(format.format(r));
		writer.println(format.format(phi));
		writer.println(String.format("%d", zeit));
	}

	@Override
	public int compareTo(Hit h) {
		if (h == this) return 0;
		if (h == null) return 1;

		if (probe != h.probe) {
			return (probe ? -1 : 1);
		}
		return nummer - h.nummer;		
	}

	@Override
	public boolean equals(Object o) {
		return this == o;
	}

	@Override
	public int hashCode() {
		return nummer * (probe ? -1 : 1);
	}
}