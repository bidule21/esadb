package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import model.DefaultLineModel;
import model.Einzel;
import model.LineListener;
import model.LineModel;
import model.Treffer;


@SuppressWarnings("serial")
public class Scheibe extends JPanel implements LineListener {
	private final int PROBE = 1;
	private final int MATCH = 2;

	private int mitte;

	private ScheibeTyp typ;
	private Einzel einzel;
	private Treffer treffer;

	private int nummer;
	private int force;

	// TrefferAdd
	public Scheibe(ScheibeTyp typ) {
		this(null, 0);
		this.typ = typ;
	}

	// Einzel im Druck mit this
	public Scheibe(Einzel einzel) {
		this(einzel, 0);
	}

	// GUI
	/**
	 * @wbp.parser.constructor
	 */
	public Scheibe(int nummer) {
		this(null, nummer);
	}

	private Scheibe(Einzel einzel, int nummer) {
		this.einzel = einzel;
		this.treffer = null;
		this.nummer = nummer;
		this.force = 0;
		updateTyp();
		setSize(400, 400);
	}

	@Override
	public void setSize(int w, int h) {
		super.setSize(w < h ? w : h, w < h ? w : h);
	}

	@Override
	public void setBounds(int x, int y, int w, int h) {
		super.setBounds(x, y, w < h ? w : h, w < h ? w : h);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g1 = (Graphics2D) g.create();
		g1.scale(new Double(getWidth()) / 1000, new Double(getHeight()) / 1000);
		g1.setColor(Color.BLACK);

		if (nummer > 0) {
			g1.setFont(new Font("Arial", Font.BOLD, 128));
			g1.drawString("" + nummer, 25, 25 + g1.getFontMetrics().getAscent());
		}
		
		if (showProbe()) {
			g1.fillPolygon(new int[] {700, 975, 975}, new int[] {25, 25, 300}, 3);
		}

		Graphics2D g2 = (Graphics2D) g.create();
		g2.scale(new Double(getWidth()) / new Double(typ.getRing(0)), new Double(getHeight()) / new Double(typ.getRing(0)));
		
		g2.setColor(Color.BLACK);
		mitte = typ.getRing(0) / 2;
		int d;
		
		d =  typ.getSpiegel();
	    g2.fillOval(mitte - d/2, mitte - d/2, d, d);

		for (int i = 1; i <= 10; i++) {
			d = typ.getRing(i);
		    g2.setColor(d > typ.getSpiegel() ? Color.BLACK : Color.WHITE);
	    	g2.drawOval(mitte - d/2, mitte - d/2, d, d);
		}

		if (typ.getInnenZehn() > 0) {
			d = typ.getInnenZehn();
		    g2.setColor(Color.WHITE);
		    g2.drawOval(mitte - d/2, mitte - d/2, d, d);
		}

		g2.setFont(new Font("Arial", Font.BOLD, typ.getFontSize()));
		int dy = g2.getFontMetrics().getAscent() / 2;
		int dx = (int) (g2.getFontMetrics().getStringBounds("0", g2).getWidth() / -2) + 1;
		for (int i = 1; typ.drawNumber(i); i++) {
			g2.setColor(typ.blackNumber(i) ? Color.BLACK : Color.WHITE);
			String s = "" + i;
			d = typ.getNumberRadius(i);
			g2.drawString(s, mitte - d + dx, mitte + dy);	// Links
			g2.drawString(s, mitte + dx, mitte - d + dy);	// Oben
			g2.drawString(s, mitte + d + dx , mitte + dy);	// Rechts
			g2.drawString(s, mitte + dx, mitte + d + dy);	// Unten
		}

		for (int i = 1; einzel != null; i++) {
			Treffer t = einzel.getTreffer(showProbe(), i);
			if (t == null) break;
			drawTreffer(g2, t);
		}

		if (treffer != null) drawTreffer(g2, treffer);
	}

	public void forceProbe() {
		force = PROBE;
	}

	public void forceMatch() {
		force = MATCH;
	}

	public void setTreffer(Treffer treffer) {
		this.treffer = treffer;
		repaint();
	}

	private boolean showProbe() {
		if (treffer != null) return false;
		return einzel == null || force == PROBE || (force != MATCH && !einzel.inMatch());
	}

	private void drawTreffer(Graphics g, Treffer t) {
		int d = typ.getSchuss();
		int nx = (int) (t.getX() / 10);
		int ny = (int) (t.getY() / 10);
		g.setColor(Color.GREEN);
		g.fillOval(mitte + nx - d/2, mitte - ny - d/2, d, d);
		g.setColor(Color.BLUE);
		g.drawOval(mitte + nx - d/2, mitte - ny - d/2, d, d);
	}

	private void updateTyp() {
		if (einzel != null) {
			typ = ScheibeTyp.getTypByGattung(einzel.getDisziplin().getWaffengattung());
		} else if (typ == null) {
			typ = ScheibeTyp.KK50M;
		}
	}

	@Override
	public void lineChanged(LineModel lm, int type) {
		if (type == DefaultLineModel.RESULT_CHANGED) {
			einzel = lm.getResult();
			updateTyp();
			repaint();
		}
	}
}