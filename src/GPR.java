import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author li
 * @description
 * @date 2022/9/22
 */
public class GPR {
//    why-not point
    PT w;
    int k;
//    key = PIT编号号， value = 支配的点的PIT
    HashMap<Integer, List<Integer>> SC;
    HashMap<Integer, List<Integer>> WPG;
    HashMap<Integer, List<Integer>> WDM;
    List<Integer> MH;
    Integer MH_TOP;

    public GPR(int k_size) {
        k = k_size;
        MH = new ArrayList<>(k_size);
        SC = null;
        WPG = null;
        WDM = new HashMap<>();
    }

    public Integer getMH_TOP(List<Integer> MH) {
        int max = 0;
        for (Integer i: MH
             ) {
            if(i > max) {
                max = i;
            }
        }
        return  max;
    }

    public PT getW() {
        return w;
    }

    public void setW(PT w) {
        this.w = w;
    }

    public HashMap<Integer, List<Integer>> getSC() {
        return SC;
    }

    public void setSC(HashMap<Integer, List<Integer>> SC) {
        this.SC = SC;
    }

    public HashMap<Integer, List<Integer>> getWPG() {
        return WPG;
    }

    public void setWPG(HashMap<Integer, List<Integer>> WPG) {
        this.WPG = WPG;
    }

    public List<Integer> getMH() {
        return MH;
    }

    public void setMH(List<Integer> MH) {
        this.MH = MH;
    }

    @Override
    public String toString() {
        return "GPR{" +
                "w=" + w +
                ", k=" + k +
                ", SC=" + SC +
                ", WPG=" + WPG +
                ", MH=" + MH +
                ", MH_TOP=" + MH_TOP +
                '}';
    }
}

