package com.metaverse.common.Utils;

import com.metaverse.permission.domain.MetaversePermission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PermissionComparator {

    public static void main(String[] args) {
        List<String> list1 = Arrays.asList("*.*.*");
        List<String> list2 = Arrays.asList("dd.wqe.dsad", "dd.wwwww.dsad");
        System.out.println(compareLists(list1, list2));
    }

    /**
     * 比较两个字符串列表的关系，考虑通配符 * 的逻辑。
     *
     * @param permissionList1 第一个列表
     * @param permissionList2 第二个列表
     * @return 返回值说明：
     * 0 - 两个列表互不包含且没有交集
     * 1 - 第一个列表包含或等于第二个列表
     * 2 - 第二个列表包含且不等于第一个列表
     * 3 - 两个列表互不包含但是有交集
     */
    public static int compareLists(List<String> permissionList1, List<String> permissionList2) {
        Set<Pattern> patterns1 = permissionList1.stream().map(PermissionComparator::toPattern).collect(Collectors.toSet());
        Set<Pattern> patterns2 = permissionList2.stream().map(PermissionComparator::toPattern).collect(Collectors.toSet());

        if (patterns1.contains(Pattern.compile("^.*\\..*\\..*$"))) {
            return 1;
        }

        if (patterns2.contains(Pattern.compile("^.*\\..*\\..*$"))) {
            return 2;
        }

        boolean list1ContainsList2 = patterns2.stream().allMatch(pattern -> patterns1.stream().anyMatch(p -> p.matcher(pattern.pattern()).matches()));
        boolean list2ContainsList1 = patterns1.stream().allMatch(pattern -> patterns2.stream().anyMatch(p -> p.matcher(pattern.pattern()).matches()));

        if (list1ContainsList2 && patterns1.size() >= patterns2.size()) {
            return 1;
        } else if (list2ContainsList1 && patterns2.size() > patterns1.size()) {
            return 2;
        } else if (list1ContainsList2 || list2ContainsList1) {
            return 3;
        } else {
            return 0;
        }
    }

    /**
     * 将权限字符串转换为正则表达式模式。
     *
     * @param permission 权限字符串
     * @return 正则表达式模式
     */
    private static Pattern toPattern(String permission) {
        String regex = "^" + permission.replace(".", "\\.").replace("*", "[^.]*(?:[^.]*)?") + "$";
        return Pattern.compile(regex);
    }

    /**
     * 过滤掉被其他列表包含的子列表。
     *
     * @param metaversePermissions 输入的权限列表
     * @return 过滤后的权限列表
     */
    public static List<MetaversePermission> filterIncludedPermissions(List<MetaversePermission> metaversePermissions) {
        int size = metaversePermissions.size();

        List<MetaversePermission> filteredPermissions = new ArrayList<>();

        // 记录哪些列表已经被包含，不需要再次加入结果集
        boolean[] isContained = new boolean[size];

        // 记录哪些列表需要进一步检查
        List<Integer> toCheck = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            toCheck.add(i);
        }

        while (!toCheck.isEmpty()) {
            int index = toCheck.remove(toCheck.size() - 1); // 取出最后一个索引
            List<String> permissionList = metaversePermissions.get(index).getPermissions();
            isContained[index] = false; // 初始状态设为未被包含

            // 比较当前列表与其他列表
            for (int j = 0; j < metaversePermissions.size(); j++) {
                if (index != j) {
                    int comparisonResult = compareLists(permissionList, metaversePermissions.get(j).getPermissions());
                    if (comparisonResult == 1) { // permissionList 包含 otherPermissionList
                        isContained[j] = true; // 标记 otherPermissionList 已被包含
                    } else if (comparisonResult == 2) { // otherPermissionList 包含 permissionList
                        isContained[index] = true; // 标记 permissionList 已被包含
                        break;
                    }
                }
            }

            if (!isContained[index]) {
                filteredPermissions.add(metaversePermissions.get(index));
            }
        }

        return filteredPermissions;
    }
}