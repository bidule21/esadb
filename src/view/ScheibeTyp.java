package view;

public enum ScheibeTyp {
	LG10M(305, 455, 50, 0, 8),
	LP10M(595, 1555, 160, 50, 8),
	KK50M(1124, 1544, 160, 50, 8),
	KK100M(2000, 5000, 500, 250, 9);

    private final int durchmesser_spiegel;
    private final int durchmesser_aussen;
    private final int durchmesser_step;
    private final int durchmesser_innenzehn;
    private final int maxNumber;
    
    ScheibeTyp(int durchmesser_spiegel, int durchmesser_aussen, int durchmesser_step, int durchmesser_innenzehn, int maxNumber) {
        this.durchmesser_spiegel = durchmesser_spiegel;
        this.durchmesser_aussen = durchmesser_aussen;
        this.durchmesser_step = durchmesser_step;
        this.durchmesser_innenzehn = durchmesser_innenzehn;
        this.maxNumber = maxNumber;
    }
    
    public int getRing(int i) {
    	if (i < 0 || i > 10) return 0;

   		return durchmesser_aussen - (i - 1) * durchmesser_step;
    }

    public int getInnenZehn() {
    	return durchmesser_innenzehn;
    }

    public int getSpiegel() {
    	return durchmesser_spiegel;
    }

    public boolean drawNumber(int i) {
    	return i <= maxNumber;
    }
}
