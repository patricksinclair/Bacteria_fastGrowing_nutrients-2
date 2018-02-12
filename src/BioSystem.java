import java.util.ArrayList;
import java.util.Random;

public class BioSystem {

    //no. of habitats, "carrying capacity", no. of nutrients present initially in each habitat
    private int L, K, s, s_max;

    private double c, alpha, timeElapsed;

    private boolean populationDead = false;

    private Microhabitat[] microhabitats;

    Random rand = new Random();

    public BioSystem(int L, int S, double alpha){

        this.L = L;
        this.K = K;
        this.s = S;
        this.s_max = S;
        this.alpha = alpha;

        this.microhabitats = new Microhabitat[L];
        this.timeElapsed = 0.;

        for(int i = 0; i < L; i++){

            double c_i = Math.exp(alpha*(double)i) - 1.;
            microhabitats[i] = new Microhabitat(c_i, S);
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
            microhabitats[i] = new Microhabitat(c, S);
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

    public int nMutants(){
        int runningTotal = 0;
        for(int i = 0; i < L; i++){
            for(int j = 0; j < microhabitats[i].getPopulation().size(); j++){
                if(microhabitats[i].getPopulation().get(j).getM() > 1) runningTotal++;
            }
        }
        return runningTotal;
    }

    public boolean fullMicrohabOfMutants(){

        for(int i = 0; i < L; i++){
            if(microhabitats[i].getN() > 0){

                if(microhabitats[i].fullOfMutants()) return true;

            }
        }
        return false;
    }

    public ArrayList<Double> getSpatialDistribution(){
        ArrayList<Double> microhabPops = new ArrayList<>(L);
        for(int i = 0; i < L; i++){
            microhabPops.add((double)microhabitats[i].getN());
        }
        return microhabPops;
    }

    //method for getting the current growth rates in each microhabitat
    public ArrayList<Double> getGrowthRateDistributions(){
        ArrayList<Double> growthRates = new ArrayList<>(L);

        for (int i = 0; i < L; i++){
            growthRates.add(microhabitats[i].getGrowthRate());
        }
        return growthRates;
    }

    public ArrayList<Double> getNutrientDistribution(){
        ArrayList<Double> sVals = new ArrayList<>(L);

        for(int i = 0; i < L; i++){
            sVals.add((double)microhabitats[i].getS());
        }
        return sVals;
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

        double mu = parentBac.getMu();
        double s = rand.nextDouble();

        Bacteria childBac = new Bacteria(m);
        if(s < mu/2.) {
            childBac.increaseGenotype();
        } else if(s >= mu/2. && s < mu) {
            childBac.decreaseGenotype();
        }
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
            double c = randMicroHab.getC();
            Bacteria randBac = randMicroHab.getBacteria(bacteriaIndex);

            double migRate = randBac.getB();
            double deaRate = randBac.getD();
            double repliRate = randBac.replicationRate(c, s, s_max);
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

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
                    BioSystem bs = new BioSystem(L, s, c);

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
        String filename = "fastGrowers-gradVsNutrientsScaled", token = "bla";

        ArrayList<Double> sVals = new ArrayList<Double>();
        ArrayList<Double> alphaVals = new ArrayList<Double>();
        ArrayList<Double> popVals = new ArrayList<Double>();
        ArrayList<Double> maxPopVals = new ArrayList<Double>();

        int initS = 10, finalS = 1000;
        int sIncrement = ((finalS - initS)/nPoints);

        double initAlpha = 0.0, finalAlpha = 0.1, zerothAlpha = 0.;
        double alphaIncrement = (finalAlpha - initAlpha)/(double)nPoints;

        int indexCounter = 0;
        for(int s = initS; s <= finalS; s += sIncrement) {
            sVals.add((double)s);

            for(double alpha = initAlpha; alpha <= finalAlpha; alpha += alphaIncrement) {
                alphaVals.add(alpha);

                double avgMaxPopulation = 0.;
                double avgMaxPossPopulation = 0.;

                for(int r = 0; r < nReps; r++) {

                    BioSystem bs = new BioSystem(L, s, alpha);

                    while(bs.getTimeElapsed() <= duration && !bs.getPopulationDead()) bs.performAction();

                    if(alpha == initAlpha) avgMaxPossPopulation += bs.getCurrentPopulation();

                    avgMaxPopulation += bs.getCurrentPopulation();
                    System.out.println(bs.getCurrentPopulation());
                    System.out.println("sVal: " + s + "\t alphaVal: " + alpha + "\t rep: " + r);
                }
                maxPopVals.add(avgMaxPossPopulation/(double)nReps);
                popVals.add(avgMaxPopulation/((double)nReps*maxPopVals.get(indexCounter)));

            }
            indexCounter++;
        }
        Toolbox.writeContoursToFile(sVals, alphaVals, popVals, filename);
    }

    //this was modified to also plot the growth rates and nutrient distributions over time
    public static void spatialAndNutrientDistributions(double input_alpha){

        int L = 500, nReps = 10;
        double interval = 100.;
        double duration = 2000.;
        double alpha = input_alpha;
        int S = 500;

        String filename = "fastGrowers-alpha-"+String.valueOf(alpha)+"-spatialDistribution";
        String filenameGRates = "fastGrowers-alpha-"+String.valueOf(alpha)+"-gRateDistribution";
        String filenameNutrients = "fastGrowers-alpha-"+String.valueOf(alpha)+"-nutrientDistribution";
        boolean alreadyRecorded = false;

        ArrayList<Double> xVals = new ArrayList<>(L);
        for(double i = 0; i < L; i++){
            xVals.add(i);
        }


        BioSystem bs = new BioSystem(L, S, alpha);

        while(bs.getTimeElapsed() <= duration && !bs.getPopulationDead()){
            bs.performAction();

            if((bs.getTimeElapsed()%interval >= 0. && bs.getTimeElapsed()%interval <= 0.01) && !alreadyRecorded){

                System.out.println("Success "+bs.getTimeElapsed());
                alreadyRecorded = true;

                ArrayList<Double> popVals = bs.getSpatialDistribution();
                ArrayList<Double> gRateVals = bs.getGrowthRateDistributions();
                ArrayList<Double> nutrientVals = bs.getNutrientDistribution();

                String timeValue = "-"+String.valueOf((int)bs.getTimeElapsed());
                Toolbox.writeTwoArraylistsToFile(xVals, popVals, (filename+timeValue));
                Toolbox.writeTwoArraylistsToFile(xVals, gRateVals, (filenameGRates+timeValue));
                Toolbox.writeTwoArraylistsToFile(xVals, nutrientVals, (filenameNutrients+timeValue));
            }

            if(bs.getTimeElapsed()%interval >= 0.1 && alreadyRecorded) alreadyRecorded = false;

        }
        System.out.println("duration "+bs.getTimeElapsed());

    }

    

}