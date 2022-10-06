import java.util.*;
import java.util.stream.Collectors;

/**
 * @author li
 * @description
 * @date 2022/9/21
 */
public class GSkyline {
    /**
     * @Author li
     * @Description //正交查询范围
     * @Date 9:41 2022/9/21
     * @Param
     * @return
     **/
    List<Double[]> range;
    /**
     * @Author li
     * @Description //每个对象点
     * @Date 9:40 2022/9/21
     * @Param
     * @return
     **/
    List<Double[]> points;

    Double[] whyNotPoint;

    public static void main(String[] args) {
        List<Double[]> points = new ArrayList<>();
        Double[] f1 = {1d, 300d};
        points.add(f1);
        Double[] f2 = {2d, 350d};
        points.add(f2);
        Double[] f3 = {2d, 150d};
        points.add(f3);
        Double[] f4 = {3.5d, 250d};
        points.add(f4);
        Double[] f5 = {4d, 50d};
        points.add(f5);
        Double[] f6 = {5d, 100d};
        points.add(f6);
        Double[] f7 = {6d, 200d};
        points.add(f7);
        Double[] f8 = {6.5d, 375d};
        points.add(f8);
        Double[] f9 = {7d, 300d};
        points.add(f9);
        Double[] f10 = {7d, 150d};
        points.add(f10);

        List<Double[]> range = new ArrayList<>();
        Double[] a = {0.5d, 7d};
        Double[] b = {50d, 375d};
        range.add(a);
        range.add(b);

        GSkyline skyline = new GSkyline();
        points = skyline.norm(points, range);
        range.get(0)[0] = 0d;
        range.get(0)[1] = 1d;
        range.get(1)[0] = 0d;
        range.get(1)[1] = 1d;

        List<PT> preTableList = skyline.preTable(points);
        GPR gpr = skyline.getSC(preTableList, preTableList.get(8));
        skyline.setWDM(gpr);
        HashMap<Double, PT> map = skyline.mwr(range, gpr, preTableList);
        HashMap<Double, List<Integer>>  map2 = skyline.mwp(range, gpr, preTableList);
        System.out.println(map.toString());
        System.out.println(map2.toString());

    }

    /**
     * @return java.util.List
     * @Author li
     * @Description //
     * @Date 9:42 2022/9/21
     * @Param [range, points]
     **/
    public void algorithm1(List<Double[]> range,
                           List<Double[]> points,
                           Double[] whyNotPoint) {
        List<PT> preTableList = new GSkyline().preTable(points);
        GPR gpr = new GSkyline().getSC(preTableList, preTableList.get(8));
        for (int i = 0; i < range.size(); i++) {
            Double down = range.get(i)[0];
            Double up = range.get(i)[1];
            if (!(whyNotPoint[i] >= down && whyNotPoint[i] <= up)) {
                break;
            }
        }
    }

    public HashMap<Double, PT> mwr(List<Double[]> range,
                                  GPR gpr, List<PT> preTableList) {
        PT ptAnswer = new PT();

        List<Integer> candidate = new ArrayList<>();
        int k = gpr.k - 1;
        do {
            for (Integer i : gpr.WDM.keySet()) {
                List<Integer> list = gpr.WDM.get(i);
                if (list.size() == k) {
                    candidate.add(i);
                }
            }
            if (candidate.size() != 0) {
                break;
            }
            k--;
        } while (true);
        Double R = 1d;
        for (Double[] a : range) {
            R = R * (a[1] - a[0]);
        }
//        算出所有S(R'),S(R')越大越好
        Double R2 = 0d;
        for (Integer i : candidate
        ) {
            Double R3 = 1d;
            PT pt = preTableList.stream().
                    filter(s -> s.PIT.intValue() == i).toList().get(0);
            for (int j = 0; j < range.size(); j++) {
                R3 = R3 * (range.get(j)[1] - pt.A[j]);
            }
            if (R3 > R2) {
                R2 = R3;
                ptAnswer = pt;
            }
        }
        HashMap<Double, PT> map = new HashMap<>(1);
        map.put(R2, ptAnswer);
        return map;
    }

    public HashMap<Double, List<Integer>> mwp(List<Double[]> range,
                                              GPR gpr, List<PT> preTableList) {

        int k = gpr.k - 1;
//        canList 是候选元组的集合，里面装的list是对应组合的PIT的组合
        List<List<Integer>> canList = new ArrayList<>();
//        求canList
        List<Integer> keyList = new ArrayList<>(gpr.SC.keySet());

        for (Integer integer : keyList) {
            List<List<Integer>> newResult = new ArrayList<>();
            for (List<Integer> integers : canList) {
                List<Integer> tempB = new ArrayList<>(integers);
                tempB.add(integer);
                newResult.add(tempB);
            }
            List<Integer> singleResult = new ArrayList<>();
            singleResult.add(integer);
            canList.add(singleResult);
            canList.addAll(newResult);
        }
        List<List<Integer>> deleteList = new ArrayList<>();
        for (List<Integer> everyList : canList) {
            int tempSum = 0;
            for (Integer i : everyList) {
                tempSum += gpr.SC.get(i).size() + 1;
                for (Integer j : gpr.SC.get(i)) {
                    if (everyList.contains(j)) {
                        tempSum--;
                    }
                }
                if (tempSum > k) {
                    deleteList.add(everyList);
                    break;
                }
            }
        }
        canList.removeAll(deleteList);

        double cost = 999999999d;
        List<Integer> answer = new ArrayList<>();
        Double[] answerPoint = new Double[2];
//        每种情况下求cost；
        List<List<Integer>> miniList = new ArrayList<>();
        int tempK = k;
        while (miniList.isEmpty()) {
            for (List<Integer> list : canList) {
                if (list.size() == k) {
                    miniList.add(list);
                }
            }
            k--;
        }
//        每种情况下求cost；
        for (List<Integer> list : miniList) {
            List<Integer> domainList = new ArrayList<>();
            for (Integer i : gpr.WDM.keySet()) {
//                不存在，表示不能被它支配
                if (!list.contains(i)) {
                    domainList.add(i);
                }
            }
//            排除domainList中被支配的对象
            List<Integer> removeList = new ArrayList<>();
            for (Integer i : domainList) {
//                父元组不为0，有问题
                if (gpr.WPG.get(i).size() != 0) {
                    List<Integer> pList = gpr.WPG.get(i);
                    for (Integer j :
                            pList) {
                        if (domainList.contains(j)) {
                            removeList.add(i);
                        }
                    }
                }
            }
            for (Integer i :
                    removeList) {
                domainList.remove(i);
            }
//            求ctp
            List<Double[]> ctpPoints = new ArrayList<>();
            for (int i = 0; i < domainList.size() - 1; i++) {
                Double[] a = new Double[2];
                Double[] a1 = getPITValue(domainList.get(i + 1),preTableList);
                Double[] a2 = getPITValue(domainList.get(i),preTableList);
                a[0] = a1[0];
                a[1] = a2[1];
                ctpPoints.add(a);
            }

//            求cmp
            List<Double[]> cmpPoints = new ArrayList<>();
            Double[] b = new Double[2];
            List<PT> listPt = new ArrayList<>();
            listPt.add(getPT(gpr.w.PIT,preTableList));
            for (Integer i:
                 domainList) {
                listPt.add(getPT(i,preTableList));
            }
            List<PT> list1 = listPt.stream().
                    sorted(Comparator.comparing(pt -> pt.A[0])).toList();
            for (int i = 0; i < list1.size(); i++) {
                if (gpr.w.PIT == list1.get(i).PIT.intValue()) {
                    b[0] = gpr.w.A[0];
                    b[1] = list1.get(i - 1).A[1];
                    cmpPoints.add(b);
                }
            }
            List<PT> list2 = listPt.stream().
                    sorted(Comparator.comparing(pt -> pt.A[1])).toList();
            for (int i = 0; i < list2.size(); i++) {
                if (gpr.w.PIT == list2.get(i).PIT.intValue()) {
                    b[0] = list2.get(i - 1).A[0];
                    b[1] = gpr.w.A[1];
                    cmpPoints.add(b);
                }
            }
//            qiu cost
            List<Double[]> cAnswer = new ArrayList<>();
            cAnswer.addAll(cmpPoints);
            cAnswer.addAll(ctpPoints);
            for (Double[] f : cAnswer) {
//                dist(w,o)
                double distWO = Math.sqrt(
                        Math.pow(gpr.w.A[0] - range.get(0)[0], 2) +
                                Math.pow(gpr.w.A[1] - range.get(1)[0], 2));
//                dist(w',o)
                double distwO = Math.sqrt(
                        Math.pow(f[0] - range.get(0)[0], 2) +
                                Math.pow(f[1] - range.get(1)[0], 2));
                double costTemp = Math.abs(distWO - distwO) / distWO;
                if (costTemp < cost) {
                    cost = costTemp;
                    answer = list;
                    answerPoint[0] = f[0];
                    answerPoint[1] = f[1];
                }
            }

        }
        HashMap<Double, List<Integer>> answerMap = new HashMap<>();
        answerMap.put(cost, answer);

        return answerMap;

    }



    /**
     * @return java.util.List
     * @Author li
     * @Description //构建预排序表
     * @Date 10:08 2022/9/21
     * @Param [points]
     **/
    public List<PT> preTable(List<Double[]> points) {
        List<PT> list = new ArrayList<>();
        int i = 1;
        for (Double[] point : points) {
            PT pt = new PT(point.length);
            System.arraycopy(point, 0, pt.A, 0, point.length);
            pt.PIT = i;
            i++;
            list.add(pt);
        }
//        paixu
        for (int j = 0; j < list.get(0).A.length; j++) {
            int time = j;
            list.stream().forEach(pt -> pt.Ax = pt.A[time]);
//            an di j ge shu xing zhi pai lie
            list = list.stream().sorted(Comparator.comparing(PT::getAx)).toList();
            //            qiu pi k
            for (int k = 0, num = 0; k < list.size(); k++) {
                if (k == 0 || list.get(k - 1).Ax.doubleValue() != list.get(k).Ax.doubleValue()) {
                    num = k + 1;
                }
                list.get(k).PI[j] = num;
            }
        }
        list.stream().forEach(pt -> pt.MPIL = pt.getMinPI());
        return list.stream().sorted(new Comparator<PT>() {
            @Override
            public int compare(PT o1, PT o2) {
                return o1.MPIL - o2.MPIL;
            }
        }).toList();
    }


    public GPR getSC(List<PT> pts, PT w) {
//        初始化元组大小为k的gpr
        GPR gpr = new GPR(3);
        gpr.w = w;
        for (PT pt :
                pts) {
//            SC若为空，则直接加入SC
            if (gpr.getSC() == null) {
                List pg_list = new ArrayList<Integer>();
                HashMap<Integer, List<Integer>> map = new HashMap<>(1);
                map.put(pt.PIT, pg_list);
                gpr.setSC(map);
            } else {
//                不为空则需判断其父元组大小
//                SC中的每一个跟输入的pt对比
//                map充当临时sc，防止fast-fail
                HashMap<Integer, List<Integer>> map = new HashMap<>(1);
                for (Integer s : gpr.SC.keySet()
                ) {
//                    取出SC中对应的PT-temp_pt
                    PT temp_pt = null;
                    for (PT temp :
                            pts) {
                        if (temp.PIT.intValue() == s) {
                            temp_pt = temp;
                            break;
                        }
                    }
//                    sc操作
//                    如果输入的支配原有的SC
                    if (pt.domain(temp_pt)) {
                        gpr.SC.get(temp_pt.PIT).add(pt.PIT);
//                   sc支配输入的
                    } else if (temp_pt.domain(pt)) {
                        if (map.size() == 0) {
                            ArrayList list = new ArrayList<Integer>(1);
                            list.add(temp_pt.PIT);
                            map.put(pt.PIT, list);
                        } else {
                            map.get(pt.PIT).add(temp_pt.PIT);
                        }

                    } else {
//                        互相不支配
                        if (map.size() == 0) {
                            map.put(pt.PIT, new ArrayList<>());
                        }
                    }
                }
                gpr.SC.putAll(map);
            }
//                    wpg
            if (pt.domain(gpr.w)) {
                if (gpr.WPG == null) {
                    gpr.WPG = new HashMap<Integer, List<Integer>>(1);
                }
                gpr.WPG.put(pt.PIT, gpr.SC.get(pt.PIT));
            }

//            更新wpg:如果当前pt支配wpg中的元素，更新wpg中的对应键值对
            if (gpr.WPG != null) {
                for (int num : gpr.WPG.keySet()
                ) {
                    PT pt_pg = pts.stream().
                            filter(s -> s.PIT == num).
                            collect(Collectors.toList()).get(0);
                    if (pt.domain(pt_pg)) {
                        if (gpr.WPG.get(num) == null) {
                            gpr.WPG.put(num, new ArrayList<>());
                        }
                        gpr.WPG.get(num).add(pt.PIT);
                    }
                }
            }

//            SC中只留下父元组小于k的
            List<Integer> remove_list = new ArrayList<>();
            for (Integer s : gpr.SC.keySet()
            ) {
                if (gpr.SC.get(s).size() >= gpr.k -1) {
                    remove_list.add(s);
                }
            }
            for (Integer i : remove_list
            ) {
                gpr.SC.remove(i);
            }

//            更新MH
            if (gpr.MH == null || gpr.MH.size() < gpr.k) {
                gpr.MH.add(pt.getMaxPI());
                gpr.MH_TOP = gpr.getMH_TOP(gpr.MH);
            } else {
                if (pt.getMaxPI() < gpr.MH_TOP) {
                    gpr.MH.remove(gpr.MH_TOP);
                    gpr.MH.add(pt.getMaxPI());
                    gpr.MH_TOP = gpr.getMH_TOP(gpr.MH);
                }
            }
        }
        return gpr;
    }

    public void setWDM(GPR gpr) {
        //        获取WDM
        for (Integer i : gpr.WPG.keySet()) {
            List<Integer> list = gpr.WPG.get(i);
            if (list.size() != 0) {
                for (Integer j : list
                ) {
                    if (!gpr.WDM.containsKey(j)) {
                        gpr.WDM.put(j, new ArrayList<Integer>());
                    }
                    gpr.WDM.get(j).add(i);
                    if (!gpr.WDM.containsKey(i)) {
                        gpr.WDM.put(i, new ArrayList<Integer>());
                    }
                }
            } else {
                gpr.WDM.put(i, new ArrayList<Integer>());
            }
        }
    }

    public Double[] getPITValue(int pit, List<PT> preTable) {
        for (PT p:
             preTable) {
            if (p.PIT == pit) {
                return p.A;
            }
        }
        return null;
    }

    public PT getPT(int pit, List<PT> preTable) {
        for (PT p:
                preTable) {
            if (p.PIT == pit) {
                return p;
            }
        }
        return null;
    }

    public List<Double[]> norm(List<Double[]> points, List<Double[]> range) {
        for (Double[] point : points) {
            point[0] = (point[0] - range.get(0)[0]) / (range.get(0)[1] - range.get(0)[0]);
            point[1] = (point[1] - range.get(1)[0]) / (range.get(1)[1] - range.get(1)[0]);
        }
        return points;
    }
}

