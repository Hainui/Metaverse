package com.metaverse.common.Utils;

import cn.hutool.core.util.StrUtil;
import com.metaverse.permission.domain.MetaversePermission;

import java.util.*;

public class PermissionComparator {

    public static void main(String[] args) {
        List<String> list1 = Arrays.asList("*.*.*");
        List<String> list2 = Arrays.asList("dd.wqe.dsad", "dd.wwwww.dsad");
        System.out.println(compareLists(list1, list2));


//        String permissionStr = "dd.wqe.dsad";
//        List<String> permissions = Arrays.asList("dd.wqe.*", "dd.wwwww.dsad");
//
//        boolean isMatched = PermissionComparator.isPermissionMatched(permissionStr, permissions);
//        System.out.println("是否存在匹配的权限: " + isMatched); // 输出：是否存在匹配的权限: true
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
        Set<String> set1 = new HashSet<>();
        Set<String> set2 = new HashSet<>();

        for (String permission : permissionList1) {
            set1.addAll(expandWithWildcard(permission));
        }

        for (String permission : permissionList2) {
            set2.addAll(expandWithWildcard(permission));
        }

        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        boolean hasIntersection = !intersection.isEmpty();

        boolean list1ContainsList2 = set2.size() <= set1.size() && set1.containsAll(set2);

        boolean list2ContainsList1 = set1.size() <= set2.size() && set2.containsAll(set1);

        if (hasIntersection) {
            if (list1ContainsList2) {
                return 1;
            } else if (list2ContainsList1 && set2.size() > set1.size()) {
                return 2;
            } else {
                return 3;
            }
        } else {
            return 0;
        }
    }

    /**
     * 展开包含通配符 * 的权限字符串。
     *
     * @param permission 权限字符串
     * @return 展开后的权限字符串集合
     */
    private static Set<String> expandWithWildcard(String permission) {
        Set<String> expandedPermissions = new HashSet<>();
        String[] parts = permission.split("\\.");

        generateCombinations(parts, 0, new String[parts.length], expandedPermissions);
        return expandedPermissions;
    }

    /**
     * 递归生成包含通配符 * 的所有组合。
     *
     * @param parts              权限字符串的部分
     * @param index              当前处理的索引
     * @param currentCombination 当前组合
     * @param results            结果集合
     */
    private static void generateCombinations(String[] parts, int index, String[] currentCombination, Set<String> results) {
        if (index == parts.length) {
            results.add(String.join(".", currentCombination));
            return;
        }

        if (parts[index].equals("*")) {
            // 生成所有可能的字符序列
            for (int i = 0; i < parts.length; i++) {
                currentCombination[index] = parts[i];
                generateCombinations(parts, index + 1, currentCombination, results);
            }
        } else {
            // 复制当前部分
            currentCombination[index] = parts[index];
            generateCombinations(parts, index + 1, currentCombination, results);
        }
    }

    public static boolean isPermissionMatched(String permissionStr, List<String> permissions) {
        // 分割权限字符串
        String[] parts = permissionStr.split("\\.");

        // 遍历权限列表
        for (String permission : permissions) {
            // 分割当前权限字符串
            String[] permissionParts = permission.split("\\.");

            // 检查每个部分是否符合条件
            boolean matches = true;
            for (int i = 0; i < parts.length && i < permissionParts.length; i++) {
                // 如果 permissionStr 的部分为空字符串或星号，则这部分可以匹配任何内容
                if (!parts[i].isEmpty() && !permissionEquals(parts[i], permissionParts[i])) {
                    matches = false;
                    break;
                }
            }

            // 如果所有部分都符合条件，则返回 true
            if (matches) {
                return true;
            }
        }

        // 如果没有找到符合条件的权限，则返回 false
        return false;
    }

    public static boolean permissionEquals(String permissionStr1, String permissionStr2) {
        return StrUtil.equals(permissionStr1, permissionStr2) || "*".equals(permissionStr2) || "*".equals(permissionStr1);
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