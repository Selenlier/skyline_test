package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author li
 * @description
 * @date 2022/9/30
 */

public class MyCollectionUtils {


    /**
     * 对集合作排列组合运算
     *
     * @param list       要运算的集合
     * @param mapper     每个元素转成String的规则
     * @param connectors 每个元素在结果集中的连接符号
     * @param <T>
     * @return
     */
    public static <T> List<String> combine(List<List<T>> list, Function<T, String> mapper, String connectors) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }

        //结果集
        List<String> resList = new ArrayList<>();
        //缓存上次的结果集，用来拼接新的元素
        List<String> cacheList = null;

        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                resList.addAll(list.get(i).stream().map(mapper).collect(Collectors.toList()));
            } else {
                cacheList = new ArrayList<>(resList);

                List<T> ts = list.get(i);
                for (int j = 0; j < ts.size(); j++) {
                    T t = ts.get(j);
                    if (j == 0) {
                        resList = resList.stream().map(e -> e + connectors + mapper.apply(t)).collect(Collectors.toList());
                    } else {
                        resList.addAll(cacheList.stream().map(e -> e + connectors + mapper.apply(t)).collect(Collectors.toList()));
                    }

                }
            }
        }

        return resList;
    }

    public static void main(String[] args) {
        List list = new ArrayList();

        List list1 = new ArrayList();
        list1.add(1);
        list1.add(2);
        list1.add(3);

        List list2 = new ArrayList();
        list2.add(4);
        list2.add(5);

        List list3 = new ArrayList();
        list3.add(6);
        list3.add(7);
        list3.add(8);

        list.add(list1);
        list.add(list2);
        list.add(list3);

        List<String> combine = combine(list, (e -> e.toString()), ",");
        combine.stream().map(Integer::parseInt);

        for (String str : combine) {
            System.out.println(str);
        }
    }


    public void collections() {
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
            list.add(i);
        }

        List<List<Integer>> resultList = new ArrayList<>();
        for (Integer integer : list) {
            List<List<Integer>> newResult = new ArrayList<>();
            for (List<Integer> integers : resultList) {
                List<Integer> tempB = new ArrayList<>(integers);
                tempB.add(integer);
                newResult.add(tempB);
            }
            List<Integer> singleResult = new ArrayList<>();
            singleResult.add(integer);
            resultList.add(singleResult);
            resultList.addAll(newResult);
        }
        System.out.println(resultList);
        System.out.println(resultList.size());
    }
}
