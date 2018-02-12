/*
import java.util.ArrayList;
import java.util.Random;

public class LinearBioSystem {

    //no. of habitats, "carrying capacity", no. of nutrients present initially in each habitat
    private int L, K, s, s_max;

    private double c, alpha, timeElapsed;

    private boolean populationDead = false;

    private Microhabitat[] microhabitats;

    Random rand = new Random();

    public LinearBioSystem(int L, int K, int S, double minC, double maxC){

        this.L = L;
        this.K = K;
        this.s = S;
        this.s_max = S;

        this.microhabitats = new Microhabitat[L];
        this.timeElapsed = 0.;

        //calculates the linear antibiotic gradient
        double linGrad = (maxC - minC)/((double)L);

        for(int i = 0; i < L; i++){

            double c_i = linGrad*i;
            microhabitats[i] = new Microhabitat(K, c_i, S);
        }
        microhabitats[0].fillWithWildType();
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

    public ArrayList<Double> getSpatialDistribution(){
        ArrayList<Double> microhabPops = new ArrayList<>(L);
        for(int i = 0; i < L; i++){
            microhabPops.add((double)microhabitats[i].getN());
        }
        return microhabPops;
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




    public static void spatialDistribution(){

        int L = 500, K = 100, nReps = 10;
        double duration = 500;
        String filename = "fastGrowers-linearGradient-spatialDistribution";
        boolean alreadyRecorded = false;

        ArrayList<Double> xVals = new ArrayList<>(L);
        for(double i = 0; i < L; i++){
            xVals.add(i);
        }

        double minC = 0., maxC = 12.;
        int S = 500;

        LinearBioSystem lbs = new LinearBioSystem(L, K, S, minC, maxC);

        while(lbs.getTimeElapsed() <= duration && !lbs.getPopulationDead()){
            lbs.performAction();

            if((lbs.getTimeElapsed()%100. >= 0. && lbs.getTimeElapsed()%100. <= 0.01) && !alreadyRecorded){
                System.out.println("Success "+(int)lbs.getTimeElapsed());
                alreadyRecorded = true;
                ArrayList<Double> popVals = lbs.getSpatialDistribution();
                String currentFilename = filename+"-"+String.valueOf((int)lbs.getTimeElapsed());
                Toolbox.writeTwoArraylistsToFile(xVals, popVals, currentFilename);
            }

            if(lbs.getTimeElapsed()%100. >= 0.1 && alreadyRecorded) alreadyRecorded = false;

        }
        System.out.println("duration "+lbs.getTimeElapsed());

    }

}*/
