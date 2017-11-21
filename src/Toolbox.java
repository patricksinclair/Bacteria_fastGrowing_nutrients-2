import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Toolbox {

    public static void writeTwoArraylistsToFile(ArrayList<Double> xData, ArrayList<Double> yData, String filename){

        try{
            File file = new File(filename+".txt");
            if(!file.exists()) file.createNewFile();

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            int n = xData.size();

            for(int i = 0; i < n; i++){
                String output = String.valueOf(xData.get(i))+" "+String.valueOf(yData.get(i));
                bw.write(output);
                bw.newLine();
            }
            bw.close();
        }catch (IOException e){}
    }


    public static void writeContoursToFile(ArrayList<Double> xData, ArrayList<Double> yData, ArrayList<Double> zData, String filename){

        try{
            File file = new File(filename+".txt");
            if(!file.exists()) file.createNewFile();

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            int n = xData.size();

            for(int i = 0; i < n; i++){
                for(int j = i*n; j < (i+1)*n; j++){
                    String output = String.valueOf(xData.get(i)) + " " + String.valueOf(yData.get(j)) +" " + String.valueOf(zData.get(j));
                    bw.write(output);
                    bw.newLine();
                    //System.out.println(i+"\t"+j);
                }
                bw.newLine();
            }
            bw.close();
        }catch (IOException e){}

    }
}
