import java.io.*;
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

        GSkyline skyline = new GSkyline();
//        w 父节点个数
        final int n = 8;
//        数据总量
        final int m = 20000;
//        group大小
        final int k = 3;
        List<List<Double[]>> returnList = skyline.data2(n, m - 10000);
//        List<List<Double[]>> returnList = skyline.readData();
//        returnList.get(0).addAll(returnList1.get(0));
//        List<List<Double[]>> returnList = skyline.data0();
        List<Double[]> points = returnList.get(0);
        List<Double[]> range = returnList.get(1);

//      求得预排序表
        long time0 = System.currentTimeMillis();
        List<PT> preTableList = skyline.preTable(points);
//        获取候选元组

//        需要选取
        PT w = preTableList.get(n);

        GPR gpr = skyline.getSC(preTableList, w, k);
        skyline.setWDM(gpr);
//        skyline.setNearO(gpr, preTableList);

        long time1 = System.currentTimeMillis();

        HashMap<Double, PT> map = skyline.mrn(range, gpr, preTableList);
        long time2 = System.currentTimeMillis();
        System.out.println("mrn:" + map.toString());
        System.out.println(time2 - time0);

        HashMap<Double, List<Integer>> map2 = skyline.mwp(range, gpr, preTableList);
        long time3 = System.currentTimeMillis();
        System.out.println("mwp:" + map2.toString());
        System.out.println(time3 - time2 + time1 - time0);

//        数据存入文本
//        Double a = 0d,b = 0d;
//        for ( Double s : map.keySet()) {
//            a = s;
//        }
//        for ( Double s : map2.keySet()) {
//            b = s;
//        }
//        if(a - b < 0.04d &&  b - a < 0.04d) {
//            skyline.saveData(returnList);
//        }


        //HashMap<Double, List<Integer>> map3 = skyline.mwr2(range, gpr, preTableList);

//        System.out.println(Arrays.toString(w.PI));

//        System.out.println("mwr:" + map3.toString());

    }

    private void saveData(List<List<Double[]>> returnList) {
        try {
            FileOutputStream f = new FileOutputStream
                    (new File("F:\\IDEA project\\skyline_test_0\\resource\\data2.txt"));
            ObjectOutputStream o = new ObjectOutputStream(f);

            o.writeObject(returnList);
            f.close();
            o.close();
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
        } catch (IOException e) {
            System.out.println("io Exception");
        }
    }

    private List<List<Double[]>> readData() {
        List<List<Double[]>> returnList = new ArrayList<>();
        try {
            FileInputStream f = new FileInputStream
                    (new File("F:\\IDEA project\\skyline_test_0\\resource\\data.txt"));
            ObjectInputStream o = new ObjectInputStream(f);

            returnList = (List) o.readObject();
            f.close();
            o.close();
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
        } catch (IOException e) {
            System.out.println("io Exception");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return returnList;
    }

    private void setNearO(GPR gpr, List<PT> preTableList) {
        gpr.nearO = new HashMap<>();
        if (gpr.WPG.size() == 0) {
            return;
        }
        for (Integer i : gpr.WPG.keySet()) {
//            获取PIT的value
            Double[] px = getPITValue(i, preTableList);
//            求距离:
            double costO = Math.pow(px[1], 2) + Math.pow(px[0], 2);
            double costW = Math.pow((px[1] - gpr.w.A[1]), 2)
                    + Math.pow((px[0] - gpr.w.A[0]), 2);
            if (costO < costW) {
                gpr.nearO.put(i, true);
            } else {
                gpr.nearO.put(i, false);
            }
        }
    }

//    private boolean isNearO(GPR gpr, PT pt) {
//        double costO = Math.pow(pt.A[1], 2) + Math.pow(pt.A[0], 2);
//        double costW = Math.pow((pt.A[1] - gpr.w.A[1]), 2)
//                + Math.pow((pt.A[0] - gpr.w.A[0]), 2);
//        return costO < costW;
//
//    }
    private boolean isNearO(Double[] w, Double[] a) {
        double costO = Math.pow(a[1], 2) + Math.pow(a[0], 2);
        double costW = Math.pow((a[1] - w[1]), 2)
                + Math.pow((a[0] - w[0]), 2);
        return costO < costW;

    }


    public HashMap<Double, PT> mrn(List<Double[]> range,
                                   GPR gpr, List<PT> preTableList) {
        PT ptAnswer = new PT();
//        返回用
        HashMap<Double, PT> map = new HashMap<>(1);
//        如果w是skyline点，直接满足条件
        if (gpr.WDM == null) {
            map.put(0d, gpr.w);
            return map;
        }

        List<Integer> candidate = new ArrayList<>();
        int k = gpr.k - 1;
        do {
            for (Integer i : gpr.WDM.keySet()) {
                List<Integer> list = gpr.WDM.get(i);
                if (list.size() == k) {
                    candidate.add(i);
                }
            }
            if (candidate.size() >5) {
                break;
            }
            k--;
        } while (k > 0);
//        candidate为空则将wdn中value为空的加入
//        if (candidate.size() == 0) {
//
//        }
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
        double cost;
//        cost = (1 - R2);
        cost = 1 - Math.sqrt(Math.pow((ptAnswer.A[0] - gpr.w.A[0]), 2) +
                Math.pow((ptAnswer.A[1] - gpr.w.A[1]), 2)) / Math.sqrt(Math.pow((gpr.w.A[0]), 2) +
                Math.pow((gpr.w.A[1]), 2));
        map.put(cost, ptAnswer);
        return map;
    }

    public HashMap<Double, List<Integer>> mwp(List<Double[]> range,
                                              GPR gpr, List<PT> preTableList) {

        HashMap<Double, List<Integer>> answerMap = new HashMap<>(1);
        if (gpr.WDM == null) {
            List<Integer> list = new ArrayList<>();
            list.add(gpr.w.PIT);
            answerMap.put(0d, list);
            return answerMap;
        }

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
                if (list.size() == tempK) {
                    miniList.add(list);
                }
            }
            tempK--;
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
                Double[] a1 = getPITValue(domainList.get(i + 1), preTableList);
                Double[] a2 = getPITValue(domainList.get(i), preTableList);
                a[0] = a1[0];
                a[1] = a2[1];
                ctpPoints.add(a);
            }

//            求cmp
            List<Double[]> cmpPoints = new ArrayList<>();
            Double[] b = new Double[2];
            List<PT> listPt = new ArrayList<>();
            listPt.add(getPT(gpr.w.PIT, preTableList));
            for (Integer i :
                    domainList) {
                listPt.add(getPT(i, preTableList));
            }
            List<PT> list1 = listPt.stream().
                    sorted(Comparator.comparing(pt -> pt.A[0])).toList();
            for (int i = 0; i < list1.size(); i++) {
                if (gpr.w.PIT == list1.get(i).PIT.intValue()) {
                    if (i == 0) {
                        continue;
                    }
                    b[0] = gpr.w.A[0];
                    b[1] = list1.get(i - 1).A[1];
                    cmpPoints.add(b);
                }
            }
            List<PT> list2 = listPt.stream().
                    sorted(Comparator.comparing(pt -> pt.A[1])).toList();
            for (int i = 0; i < list2.size(); i++) {
                if (gpr.w.PIT == list2.get(i).PIT.intValue()) {
                    if (i == 0) {
                        continue;
                    }
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
        answerMap.put(cost, answer);

        return answerMap;

    }


    public HashMap<Double, List<Integer>> mwr(List<Double[]> range,
                                              GPR gpr, List<PT> preTableList) {

//        先用mrn:
        PT ptAnswer = new PT();
//        返回用
        HashMap<Double, List<Integer>> answerMap = new HashMap<>(1);
//        如果w是skyline点，直接满足条件
        if (gpr.WDM == null) {
            answerMap.put(0d, new ArrayList<>(gpr.w.PIT));
            return answerMap;
        }

        List<Integer> candidate = new ArrayList<>();
        int k = gpr.k - 1;
        do {
            for (Integer i : gpr.WDM.keySet()) {
                List<Integer> list = gpr.WDM.get(i);
//                候选元组
                if (list.size() == k && gpr.nearO.get(i)) {
                    candidate.add(i);
                }
            }
            if (candidate.size() != 0) {
                break;
            }
            k++;
        } while (true);
        double R = 1d;
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
        double cost;
//        cost = (1 - R2);
        cost = 1 - Math.sqrt(Math.pow((ptAnswer.A[0] - gpr.w.A[0]), 2) +
                Math.pow((ptAnswer.A[1] - gpr.w.A[1]), 2)) / Math.sqrt(Math.pow((gpr.w.A[0]), 2) +
                Math.pow((gpr.w.A[1]), 2));
//        mrn直接完成
        if (k == gpr.k - 1) {
            ArrayList<Integer> list = new ArrayList<>(1);
            list.add(ptAnswer.PIT);
            answerMap.put(cost, list);
            return answerMap;
        }

//        mwp
//        上一步中o移动到了ptAnswer处，wpg会变，WDM也会变，SC也会便
//        1.修改wpg
        ArrayList<Integer> newWpgKey = new ArrayList<>();
        ArrayList<Integer> removeWpgKey = new ArrayList<>();
        newWpgKey.add(ptAnswer.PIT);
//        剩下的为ptAnswer支配的点的WPg
        newWpgKey.addAll(gpr.WDM.get(ptAnswer.PIT));
        for (Integer i : gpr.WPG.keySet()) {
            if (!newWpgKey.contains(i)) {
                removeWpgKey.add(i);
            }
        }
        gpr.WPG.remove(removeWpgKey);

        //2.修改WDM
        ArrayList<Integer> newWdmKey = new ArrayList<>();
        ArrayList<Integer> removeWdmKey = new ArrayList<>();
        newWdmKey.add(ptAnswer.PIT);
        newWdmKey.addAll(gpr.WDM.get(ptAnswer.PIT));
        for (Integer i : gpr.WDM.keySet()) {
            if (!newWdmKey.contains(i)) {
                removeWdmKey.add(i);
            }
        }
        gpr.WDM.remove(removeWdmKey);

//        3.修改SC：SC里的点不能支配ptAnswer
        ArrayList<Integer> removeSCKey = new ArrayList<>();
        for (Integer i : gpr.SC.keySet()) {
            if (getPT(i, preTableList).domain(ptAnswer)) {
                removeSCKey.add(i);
            }
        }
        gpr.SC.remove(removeSCKey);
        HashMap<Double, List<Integer>> mwpMap = mwp(range, gpr, preTableList);
        double cost2 = 0d;
        List<Integer> answer = new ArrayList<>();
        for (Double i : mwpMap.keySet()) {
            cost2 = i;
            answer = mwpMap.get(i);
            break;
        }

        ArrayList<Integer> list = new ArrayList<>(1);
        list.add(ptAnswer.PIT);
        answerMap.put(cost, list);
        answerMap.put((cost + cost2), new ArrayList<>());
        answerMap.put(cost2, answer);

        return answerMap;
    }

    public HashMap<Double, List<Integer>> mwr2(List<Double[]> range,
                                               GPR gpr, List<PT> preTableList) {

//        先用mrn:
        PT ptAnswer = new PT();
//        返回用
        HashMap<Double, List<Integer>> answerMap = new HashMap<>(1);
//        如果w是skyline点，直接满足条件
        if (gpr.WDM == null) {
            answerMap.put(0d, new ArrayList<>(gpr.w.PIT));
            return answerMap;
        }

        List<Integer> candidate = new ArrayList<>();
        int k = gpr.k - 1;
        do {
            for (Integer i : gpr.WDM.keySet()) {
                List<Integer> list = gpr.WDM.get(i);
//                候选元组
                if (list.size() == k && gpr.nearO.get(i)) {
                    candidate.add(i);
                }
            }
            if (candidate.size() != 0) {
                break;
            }
            k++;
        } while (true);
        double R = 1d;
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
        double cost;
//        cost = (1 - R2);
        cost = 1 - Math.sqrt(Math.pow((ptAnswer.A[0] - gpr.w.A[0]), 2) +
                Math.pow((ptAnswer.A[1] - gpr.w.A[1]), 2)) / Math.sqrt(Math.pow((gpr.w.A[0]), 2) +
                Math.pow((gpr.w.A[1]), 2));
//        mrn直接完成
        if (k == gpr.k - 1) {
            ArrayList<Integer> list = new ArrayList<>(1);
            list.add(ptAnswer.PIT);
            answerMap.put(cost, list);
            return answerMap;
        }

//        mwp
//        上一步中o移动到了ptAnswer处，SC也会便,wpg会变，WDM也会变，SC也会便
//        1.修改wpg
        List<PT> preTableList2 = new ArrayList<>();
        for (PT i : preTableList) {
//            只要被ptAnswer支配的
            if (ptAnswer.domain(i)) {
                preTableList2.add(i);
            }
        }
        GPR gpr2 = getSC(preTableList2, gpr.w, gpr.k);
        setWDM(gpr2);
        HashMap<Double, List<Integer>> mwpMap = mwp(range, gpr2, preTableList);
        double cost2 = 0d;
        List<Integer> answer = new ArrayList<>();
        for (Double i : mwpMap.keySet()) {
            cost2 = i;
            answer = mwpMap.get(i);
            break;
        }

        ArrayList<Integer> list = new ArrayList<>(1);
        list.add(ptAnswer.PIT);
        answerMap.put((cost + cost2), new ArrayList<>());
        answerMap.put(cost, list);
        answerMap.put(cost2, answer);

        return answerMap;
    }

    public HashMap<Double, List<Integer>> mwr3(List<Double[]> range,
                                               GPR gpr, List<PT> preTableList) {

//        先用mwp:
        HashMap<Double, List<Integer>> answerMap = new HashMap<>(1);
        if (gpr.WDM == null) {
            List<Integer> list = new ArrayList<>();
            list.add(gpr.w.PIT);
            answerMap.put(0d, list);
            return answerMap;
        }

        int k = gpr.k - 1;
//        canList 是候选元组的集合，里面装的list是对应组合的PIT的组合
        List<List<Integer>> canList = new ArrayList<>();
//        求canList
        List<Integer> keyList = new ArrayList<>(gpr.SC.keySet());

//        求出候选元组所有组合
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
//        提出父节点大于k的
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
                if (list.size() == tempK) {
                    miniList.add(list);
                }
            }
            tempK--;
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
                Double[] a1 = getPITValue(domainList.get(i + 1), preTableList);
                Double[] a2 = getPITValue(domainList.get(i), preTableList);
                a[0] = a1[0];
                a[1] = a2[1];
                if (!isNearO(gpr.w.A, a)) {
                    ctpPoints.add(a);
                }
            }

//            求cmp
            List<Double[]> cmpPoints = new ArrayList<>();
            Double[] b = new Double[2];
            List<PT> listPt = new ArrayList<>();
            listPt.add(getPT(gpr.w.PIT, preTableList));
            for (Integer i :
                    domainList) {
                listPt.add(getPT(i, preTableList));
            }
            List<PT> list1 = listPt.stream().
                    sorted(Comparator.comparing(pt -> pt.A[0])).toList();
            for (int i = 0; i < list1.size(); i++) {
                if (gpr.w.PIT == list1.get(i).PIT.intValue()) {
                    if (i == 0) {
                        continue;
                    }
                    b[0] = gpr.w.A[0];
                    b[1] = list1.get(i - 1).A[1];
                    if (!isNearO(gpr.w.A, b)) {
                        cmpPoints.add(b);
                    }
                }
            }
            List<PT> list2 = listPt.stream().
                    sorted(Comparator.comparing(pt -> pt.A[1])).toList();
            for (int i = 0; i < list2.size(); i++) {
                if (gpr.w.PIT == list2.get(i).PIT.intValue()) {
                    if (i == 0) {
                        continue;
                    }
                    b[0] = list2.get(i - 1).A[0];
                    b[1] = gpr.w.A[1];
                    if (!isNearO(gpr.w.A, b)) {
                        cmpPoints.add(b);
                    }
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


    public GPR getSC(List<PT> pts, PT w, int k) {
//        初始化元组大小为k的gpr
        GPR gpr = new GPR(k);
        gpr.w = w;
        for (PT pt :
                pts) {
//            利用MH提前终止算法
            if (gpr.MH != null) {
                if (gpr.MH.size() == gpr.k) {
                    if (gpr.MH_TOP < pt.MPIL) {
                        return gpr;
                    }
                }
            }
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
                if (gpr.SC.get(s).size() >= gpr.k - 1) {
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
//        如果wpg为空，WDM也为空
        if (gpr.WPG == null) {
            gpr.WDM = null;
            return;
        }
        for (Integer i : gpr.WPG.keySet()) {
            List<Integer> list = gpr.WPG.get(i);
            if (!gpr.WDM.containsKey(i)) {
                gpr.WDM.put(i, new ArrayList<Integer>());
            }
            if (list.size() != 0) {
                for (Integer j : list
                ) {
                    if (!gpr.WDM.containsKey(j)) {
                        gpr.WDM.put(j, new ArrayList<Integer>());
                    }
                    gpr.WDM.get(j).add(i);
                }
            } else {
                if (!gpr.WDM.containsKey(i)) {
                    gpr.WDM.put(i, new ArrayList<Integer>());
                }
            }
        }
    }

    public Double[] getPITValue(int pit, List<PT> preTable) {
        for (PT p :
                preTable) {
            if (p.PIT == pit) {
                return p.A;
            }
        }
        return null;
    }

    public PT getPT(int pit, List<PT> preTable) {
        for (PT p :
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

    public List<List<Double[]>> data0() {
        List<List<Double[]>> returnList = new ArrayList<>();
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

        returnList.add(points);
        returnList.add(range);
        return returnList;
    }

    /**
     * 生成自然数据
     *
     * @param k 支配w点的个数
     * @param n 数据总量
     * @author li
     * @date 9:45 2022/10/12
     **/
    public List<List<Double[]>> data1(int k, int n) {
        List<List<Double[]>> returnList = new ArrayList<>();
        List<Double[]> points = new ArrayList<>();
        List<Double[]> range = new ArrayList<>();
//        range范围
        Double[] a = {0.5d, 7d};
        Double[] b = {50d, 375d};
        range.add(a);
        range.add(b);
        double A = (range.get(0)[0] + range.get(0)[1] / 10);
        double B = (range.get(1)[0] + range.get(1)[1] / 10);
        Random random = new Random();
//        n个支配w的点
        for (int i = 0; i < k; i++) {
            Double[] point = new Double[2];

            point[0] = random.nextDouble() * (A - range.get(0)[0]) + range.get(0)[0];
            point[1] = random.nextDouble() * (B - range.get(1)[0]) + range.get(1)[0];
            points.add(point);
        }


//        剩下n-k个
        for (int i = 0; i < n - k; i++) {
            Double[] point = new Double[2];
            point[0] = random.nextDouble() * (range.get(0)[1] - A) + A;
            point[1] = random.nextDouble() * (range.get(1)[1] - B) + B;
            points.add(point);
        }

//        归一化处理
        GSkyline skyline = new GSkyline();
        points = skyline.norm(points, range);
        range.get(0)[0] = 0d;
        range.get(0)[1] = 1d;
        range.get(1)[0] = 0d;
        range.get(1)[1] = 1d;

        returnList.add(points);
        returnList.add(range);
        return returnList;
    }


    /**
     * 生成反相关数据
     * @author li
     * @date 15:29 2022/10/26
     * @param
     * @return java.util.List<java.util.List<java.lang.Double[]>>
     **/
    public List<List<Double[]>> data2(int k, int n) {
        List<List<Double[]>> returnList = new ArrayList<>();
        List<Double[]> points = new ArrayList<>();
        List<Double[]> range = new ArrayList<>();
//        range范围
        Double[] a = {0.5d, 7d};
        Double[] b = {50d, 375d};
        range.add(a);
        range.add(b);
        double A = (range.get(0)[0] + range.get(0)[1] / 10);
        double B = (range.get(1)[0] + range.get(1)[1] / 10);
        Random random = new Random();
//        n个支配w的点
        for (int i = 0; i < k; i++) {
            Double[] point = new Double[2];

            point[0] = random.nextDouble() * (A - range.get(0)[0]) + range.get(0)[0];
            Double sd = point[0] / A;
            point[1] = (B - b[0]) * (1 - sd) + b[0];
            points.add(point);
        }


//        剩下n-k个
        for (int i = 0; i < n - k; i++) {
            Double[] point = new Double[2];
            point[0] = random.nextDouble() * (range.get(0)[1] - A) + A;
            point[1] = random.nextDouble() * (range.get(1)[1] - B) + B;

            points.add(point);
        }

//        归一化处理
        GSkyline skyline = new GSkyline();
        points = skyline.norm(points, range);
        range.get(0)[0] = 0d;
        range.get(0)[1] = 1d;
        range.get(1)[0] = 0d;
        range.get(1)[1] = 1d;

        returnList.add(points);
        returnList.add(range);
        return returnList;
    }
}

