import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class SaveToFile {

  public SaveToFile() {
  }

  public void save(double avVel){
    try{
      FileWriter fw = new FileWriter("myfile.txt", true);
      BufferedWriter bw = new BufferedWriter(fw);
      PrintWriter out = new PrintWriter(bw);
      out.println(avVel);
      out.close();
    } catch (IOException e) {
      System.out.println(e);
    }
  }
}
