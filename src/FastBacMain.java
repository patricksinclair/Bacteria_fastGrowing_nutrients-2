public class FastBacMain {

    public static void main(String[] args){

        //BioSystem.delayedAntibioticPresence();
        //BioSystem.antibioticVsNutrients();
        //bla bla bla
        //BioSystem.spatialAndNutrientDistributions(0.02);
        //BioSystem.timeTilResistance();
        double alpha = Math.log(11.5)/500.;
        System.out.println(alpha);
        double c_500 = Math.exp(alpha*500) - 1;
        System.out.println(c_500);
    }
}
