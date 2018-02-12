import java.util.ArrayList;

public class Bacteria {

    //m corresponds to the current genotype of the bacterium
    private int m;
    //finalM is the total no. of genotypes in this evolutionary path
    private int finalM;
    private final int initialM = 1;
    //migration rate: probability the bacterium migrates from one microhabitat to a neighbouring one
    private double b = 0.1;
    //death rate
    private double d = 0.;
    //mutation rate
    //private double mu = 1.e-3;
    private double mu = 0.;
    private double K_prime = 33.;

    public Bacteria(int m){
        this.m = m;
        this.finalM = 2;
    }


    public int getM(){return m;}
    public int getFinalM(){return finalM;}
    public double getB(){return b;}
    public double getD(){return d;}
    public double getMu(){return mu;}


    //MIC now depends on the nutrients present in the microhabitat
    //s = no. of nutrients, s_max =  max no. of nutrients present at the start
    public double beta(double s, double s_max){

        double mu = s/(K_prime+s);
        double mu_max = s_max/(K_prime+s_max);
        return 10. - 9.*mu/mu_max;

    }

    public double growthRate(double c, double s, double s_max){

        double phi_c = Math.max(1. - (c/beta(s, s_max))*(c/beta(s, s_max)), 0.);
        return phi_c;
    }

    public double replicationRate(double c, double s, double s_max){

        //System.out.println("rep rate:\t"+growthRate(c, s, s_max, K) * s/(K + s));
        return growthRate(c, s, s_max) * (s/(K_prime + s));
    }

    public void increaseGenotype(){
        if(m < finalM){
            m++;
        }
    }
    public void decreaseGenotype(){
        if(m > initialM){
            m--;
        }
    }



    public static ArrayList<Bacteria> initialPopulation(int K, int initM){

        ArrayList<Bacteria> initPop = new ArrayList<Bacteria>(K);

        for(int i = 0; i < K; i++){
            initPop.add(new Bacteria(initM));
        }
        return initPop;
    }


}
