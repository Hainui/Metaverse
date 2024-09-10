package com.metaverse.common.Utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListComparator {

    /**
     * 比较两个字符串列表的关系。
     *
     * @param list1 第一个列表
     * @param list2 第二个列表
     * @return 返回值说明：
     * 0 - 两个列表互不包含且没有交集
     * 1 - 第一个列表包含或等于第二个列表
     * 2 - 第二个列表包含且不等于第一个列表
     * 3 - 两个列表互不包含但是有交集
     */
    public static int compareLists(List<String> list1, List<String> list2) {
        Set<String> set1 = new HashSet<>(list1);
        Set<String> set2 = new HashSet<>(list2);

        // 检查两个集合是否有交集
        set1.retainAll(set2);
        boolean hasIntersection = !set1.isEmpty();

        // 检查 list1 是否包含 list2
        boolean list1ContainsList2 = set2.size() <= set1.size() && set1.containsAll(set2);

        // 检查 list2 是否包含 list1
        boolean list2ContainsList1 = set1.size() <= set2.size() && set2.containsAll(set1);

        if (hasIntersection) {
            if (list1ContainsList2 && list1.size() >= list2.size()) {
                return 1; // list1 包含或等于 list2
            } else if (list2ContainsList1 && list2.size() > list1.size()) {
                return 2; // list2 包含且不等于 list1
            } else {
                return 3; // 两个列表互不包含但是有交集
            }
        } else {
            return 0; // 两个列表互不包含且没有交集
        }
    }
}