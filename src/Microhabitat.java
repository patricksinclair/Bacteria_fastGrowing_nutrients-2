
import java.util.ArrayList;

public class Microhabitat {

    //K = karrying kapacity, K_prime is the constant used in growth rates.
    private int K, s, s_max;
    private double c;

    private final double K_prime = 33.;

    private ArrayList<Bacteria> population;


    public Microhabitat(double c, int S){
        this.c = c;
        this.s = S;
        this.s_max = S;
        this.population = new ArrayList<Bacteria>(K);
    }


    public double getC(){return c;}
    public void setC(double c){
        this.c = c;
    }

    public double getK_prime(){return K_prime;}

    public int getS(){return s;}

    public int getS_max(){return s_max;}

    public int getN(){
        return population.size();
    }

    public ArrayList<Bacteria> getPopulation(){
        return population;
    }
    public Bacteria getBacteria(int i){
        return population.get(i);
    }

    public boolean fullOfMutants(){

        int mutantCounter = 0;
        for(int i = 0; i < population.size(); i++){
            if(population.get(i).getM() > 1) mutantCounter++;
        }
        if(mutantCounter >= K) return true;
        else return false;
    }

    public void innoculateWithABActeria(){
        int initGentotype = 1;
        population.add(new Bacteria(initGentotype));
    }

    public void fillWithWildType(){

        int initGenotype = 1;

        for(int i = 0; i < K; i++){
            population.add(new Bacteria(initGenotype));
        }
    }

    public double getGrowthRate(){
        double mu = s/(K_prime+s);
        double mu_max = s_max/(K_prime+s_max);
        double beta = 10. - 9.*(mu/mu_max);

        double phi_c = Math.max(1. - (c/beta)*(c/beta), 0.);

        return phi_c*(s/(K_prime+s));
    }

    public void consumeNutrients(){
        if(s > 0) s--;
    }

    public void removeABacterium(int i){
        population.remove(i);
    }

    public void addABacterium(Bacteria newBact){
        population.add(newBact);
    }



}
