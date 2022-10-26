import java.util.Arrays;


/**
 * @author li
 * @date 2022/9/22
 */
public class PT {

    public Integer PIT;
    Double[] A;
    Double Ax;
    Integer[] PI;
    int minPI;
    int maxPI;
    Integer MPIL;


    public boolean domain(PT b) {
        Integer[] rank = b.PI;
        boolean flag = false;
        for (int i = 0; i < PI.length; i++) {
            if (this.PI[i].intValue() > rank[i].intValue()) {
                return false;
            }
            if (this.PI[i].intValue() < rank[i].intValue() && !flag) {
                flag = true;
            }
        }
        return flag;
    }

    public int getMinPI() {
        if (PI == null) {
            return -1;
        }
        Integer min = 9999;
        for (Integer i:PI
        ) {
            if (i <= min) {
                min = i;
            }
        }
        return min;
    }

    public int getMaxPI() {
        if (PI == null) {
            return -1;
        }
        Integer max = 0;
        for (Integer i:PI
        ) {
            if (i > max) {
                max = i;
            }
        }
        return max;
    }

    public void setMinPI(Integer minPI) {
        this.minPI = minPI;
    }

    public PT() {

    }

    public PT(int x) {
        A = new Double[x];
        PI = new Integer[x];
    }

    public Integer getPIT() {
        return PIT;
    }

    public void setPIT(Integer PIT) {
        this.PIT = PIT;
    }

    public Double getAx() {
        return Ax;
    }

    public void setAx(Double ax) {
        Ax = ax;
    }

    public Double[] getA() {
        return A;
    }

    public void setA(Double[] a) {
        A = a;
    }

    public Integer[] getPI() {
        return PI;
    }

    public void setPI(Integer[] PI) {
        this.PI = PI;
    }

    public Integer getMPIL() {
        return MPIL;
    }

    public void setMPIL(Integer MPIL) {
        this.MPIL = MPIL;
    }

    @Override
    public String toString() {
        return "PT{" +
                "PIT=" + PIT +
                ", A=" + Arrays.toString(A) +
                ", Ax=" + Ax +
                ", PI=" + Arrays.toString(PI) +
                ", minPI=" + getMinPI() +
                ", MPIL=" + MPIL +
                '}';
    }
}

