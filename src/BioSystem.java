import java.util.ArrayList;
import java.util.Random;

public class BioSystem {

    //no. of habitats, "carrying capacity", no. of nutrients present initially in each habitat
    private int L, K, s, s_max;

    private double c, alpha, timeElapsed;

    private boolean populationDead = false;

    private Microhabitat[] microhabitats;

    Random rand = new Random();

    public BioSystem(int L, int K, int S, double alpha){

        this.L = L;
        this.K = K;
        this.s = S;
        this.s_max = S;
        this.alpha = alpha;

        this.microhabitats = new Microhabitat[L];
        this.timeElapsed = 0.;

        for(int i = 0; i < L; i++){

            double c_i = Math.exp(alpha*(double)i) - 1.;
            microhabitats[i] = new Microhabitat(K, c_i, S);
        }
        microhabitats[0].fillWithWildType();
    }

    public BioSystem(int L, int K, int S, double c, String token){

        this.L = L;
        this.K = K;
        this.s = S;
        this.s_max = S;
        this.c = c;

        this.microhabitats = new Microhabitat[L];
        this.timeElapsed = 0.;

        for(int i = 0; i < L; i++) {
            microhabitats[i] = new Microhabitat(K, c, S);
            microhabitats[i].innoculateWithABActeria();
        }
    }

    public int getL(){
        return L;
    }

    public double getTimeElapsed(){
        return timeElapsed;
    }
    public void setTimeElapsed(double timeElapsed){
        this.timeElapsed = timeElapsed;
    }

    public boolean getPopulationDead(){
        return populationDead;
    }

    public void setC(double c){
        for(Microhabitat m : microhabitats) {
            m.setC(c);
        }
    }

    public int getCurrentPopulation(){
        int runningTotal = 0;

        for(Microhabitat m : microhabitats) {
            runningTotal += m.getN();
        }
        return runningTotal;
    }

    public int getCurrentNutrients(){
        int runningTotal = 0;

        for(Microhabitat m : microhabitats) {
            runningTotal += m.getS();
        }
        return runningTotal;
    }


    public Microhabitat getMicrohabitat(int i){
        return microhabitats[i];
    }

    public Bacteria getBacteria(int l, int k){
        return microhabitats[l].getBacteria(k);
    }

    public void migrate(int currentL, int bacteriumIndex){

        double direction = rand.nextDouble();

        if(direction < 0.5 && currentL < (L - 1)) {


            ArrayList<Bacteria> source = microhabitats[currentL].getPopulation();
            ArrayList<Bacteria> destination = microhabitats[currentL + 1].getPopulation();

            destination.add(source.remove(bacteriumIndex));

        }else if(direction > 0.5 && currentL > (0)){

            ArrayList<Bacteria> source = microhabitats[currentL].getPopulation();
            ArrayList<Bacteria> destination = microhabitats[currentL - 1].getPopulation();

            destination.add(source.remove(bacteriumIndex));
        }
    }

    public void die(int currentL, int bacteriumIndex){

        microhabitats[currentL].removeABacterium(bacteriumIndex);
        if(getCurrentPopulation() == 0) populationDead = true;
    }


    public void replicate(int currentL, int bacteriumIndex){
        //a nutrient unit is consumed for every replication
        microhabitats[currentL].consumeNutrients();
        //the bacterium which is going to be replicated and its associated properties
        Bacteria parentBac = microhabitats[currentL].getBacteria(bacteriumIndex);
        int m = parentBac.getM();

        Bacteria childBac = new Bacteria(m);

        microhabitats[currentL].addABacterium(childBac);
    }


    public void performAction(){

        //selects a random bacteria from the total population
        if(!populationDead) {

            int randomIndex = rand.nextInt(getCurrentPopulation());
            int indexCounter = 0;
            int microHabIndex = 0;
            int bacteriaIndex = 0;

            forloop:
            for(int i = 0; i < getL(); i++) {

                if((indexCounter + microhabitats[i].getN()) <= randomIndex) {

                    indexCounter += microhabitats[i].getN();
                    continue forloop;
                } else {
                    microHabIndex = i;
                    bacteriaIndex = randomIndex - indexCounter;
                    break forloop;
                }
            }

            Microhabitat randMicroHab = microhabitats[microHabIndex];

            int s = randMicroHab.getS(), s_max = randMicroHab.getS_max();
            double K_prime = randMicroHab.getK_prime(), c = randMicroHab.getC();
            Bacteria randBac = randMicroHab.getBacteria(bacteriaIndex);

            double migRate = randBac.getB();
            double deaRate = randBac.getD();
            double repliRate = randBac.replicationRate(c, s, s_max, K_prime);
            double R_max = 1.2;
            double rando = rand.nextDouble()*R_max;

            if(rando < migRate) migrate(microHabIndex, bacteriaIndex);
            else if(rando >= migRate && rando < (migRate + deaRate)) die(microHabIndex, bacteriaIndex);
            else if(rando >= (migRate + deaRate) && rando < (migRate + deaRate + repliRate))
                replicate(microHabIndex, bacteriaIndex);

            timeElapsed += 1./((double) getCurrentPopulation()*R_max);
            //move this to the death() method

        }
    }


    public static void antibioticVsNutrients(){

        int nPoints = 10, nReps = 2;
        int L = 500, K = 100;
        double duration = 500.;
        String filename = "fastGrowers_nutrients_vs_antibiotic";

        ArrayList<Double> sVals = new ArrayList<Double>();
        ArrayList<Double> cVals = new ArrayList<Double>();
        ArrayList<Double> popVals = new ArrayList<Double>();

        int initS = 10, finalS = 1000;
        int sIncrement = ((finalS - initS)/nPoints);

        double initC = 1., finalC = 10.;
        double cIncrement = (finalC - initC)/(double) nPoints;


        for(double c = initC; c <= finalC; c += cIncrement) {
            cVals.add(c);

            for(int s = initS; s <= finalS; s += sIncrement) {
                sVals.add((double) s);

                double avgMaxPopulation = 0.;

                for(int r = 0; r < nReps; r++) {
                    BioSystem bs = new BioSystem(L, K, s, c);

                    while(bs.getTimeElapsed() <= duration && !bs.getPopulationDead()) {
                        bs.performAction();
                    }

                    avgMaxPopulation += bs.getCurrentPopulation();
                    System.out.println(bs.getCurrentPopulation());
                    System.out.println("sVal: " + s + "\t cVal: " + c + "\t rep: " + r);
                }

                popVals.add(avgMaxPopulation/(double) nReps);
            }
        }
        System.out.println(sVals.size() +"\t"+cVals.size()+"\t"+popVals.size());
        Toolbox.writeContoursToFile(cVals, sVals, popVals, filename);
    }


    public static void antibioticGradientVsNutrients(){

        int nPoints = 10, nReps = 5;
        int L = 500, K = 100;
        double duration = 500.;
        String filename = "gradVsNutrientsScaled", token = "bla";

        ArrayList<Double> sVals = new ArrayList<Double>();
        ArrayList<Double> alphaVals = new ArrayList<Double>();
        ArrayList<Double> popVals = new ArrayList<Double>();
        ArrayList<Double> maxPopVals = new ArrayList<Double>();

        int initS = 10, finalS = 1000;
        int sIncrement = ((finalS - initS)/nPoints);

        double initAlpha = 0.00, finalAlpha = 0.1, zerothAlpha = 0.;
        double alphaIncrement = (finalAlpha - initAlpha)/(double)nPoints;

        //this loop runs with no antibiotics to calculate the maximum value attainable for each nutrient
        //value
        for(int s = initS; s<=finalS; s+=sIncrement){

            double avgMaxPopulation = 0.;

            for(int r = 0; r < nReps; r++){
                System.out.println("zerothS = "+s+"\trep: "+r);
                BioSystem bs = new BioSystem(L, K, s, zerothAlpha);

                while(bs.getTimeElapsed() <= duration && !bs.getPopulationDead()) bs.performAction();
                avgMaxPopulation += bs.getCurrentPopulation();
            }
            maxPopVals.add(avgMaxPopulation/(double)nReps);
        }


        int indexCounter = 0;
        for(int s = initS; s <= finalS; s += sIncrement) {
            sVals.add((double)s);

            for(double alpha = initAlpha; alpha <= finalAlpha; alpha += alphaIncrement) {
                alphaVals.add(alpha);

                double avgMaxPopulation = 0.;

                for(int r = 0; r < nReps; r++) {

                    BioSystem bs = new BioSystem(L, K, s, alpha);

                    while(bs.getTimeElapsed() <= duration && !bs.getPopulationDead()) bs.performAction();


                    avgMaxPopulation += bs.getCurrentPopulation();
                    System.out.println(bs.getCurrentPopulation());
                    System.out.println("sVal: " + s + "\t alphaVal: " + alpha + "\t rep: " + r);
                }

                popVals.add(avgMaxPopulation/((double)nReps*maxPopVals.get(indexCounter)));

            }
            indexCounter++;
        }
        Toolbox.writeContoursToFile(sVals, alphaVals, popVals, filename);
    }



}