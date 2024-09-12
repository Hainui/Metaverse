package com.metaverse.common.Utils;

import com.metaverse.permission.domain.MetaversePermission;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PermissionComparator {

/*    public static void main(String[] args) {
        // 测试用例 1: 完全包含
        List<List<String>> testCase1 = Arrays.asList(
                Arrays.asList("resouc.*.local"),
                Arrays.asList("resouc.action.local")
        );
        System.out.println("Test Case 1 Result: " + filterIncludedPermissions(testCase1)); // 预期结果：[[resouc.*.local]]

        // 测试用例 2: 互不相交
        List<List<String>> testCase2 = Arrays.asList(
                Arrays.asList("resouc.*.local"),
                Arrays.asList("resouc.action.action")
        );
        System.out.println("Test Case 2 Result: " + filterIncludedPermissions(testCase2)); // 预期结果：[[resouc.*.local], [resouc.action.action]]

        // 测试用例 3: 部分包含
        List<List<String>> testCase3 = Arrays.asList(
                Arrays.asList("resouc.*.local"),
                Arrays.asList("resouc.action.local", "resouc.another.action")
        );
        System.out.println("Test Case 3 Result: " + filterIncludedPermissions(testCase3)); // 预期结果：[[resouc.*.local], [resouc.action.local, resouc.another.action]]

        // 测试用例 4: 多列表
        List<List<String>> testCase4 = Arrays.asList(
                Arrays.asList("resouc.*.local"),
                Arrays.asList("resouc.action.local", "resouc.another.action"),
                Arrays.asList("resouc.another.local")
        );
        System.out.println("Test Case 4 Result: " + filterIncludedPermissions(testCase4)); // 预期结果：[[resouc.*.local], [resouc.action.local, resouc.another.action]]

        // 测试用例 5: 空列表
        List<List<String>> testCase5 = new ArrayList<>();
        System.out.println("Test Case 5 Result: " + filterIncludedPermissions(testCase5)); // 预期结果：[]

        // 测试用例 6: 含空列表
        List<List<String>> testCase6 = Arrays.asList(
                new ArrayList<>(),
                Arrays.asList("resouc.*.local")
        );
        System.out.println("Test Case 6 Result: " + filterIncludedPermissions(testCase6)); // 预期结果：[[resouc.*.local]]

        // 测试用例 7: 相同列表
        List<List<String>> testCase7 = Arrays.asList(
                Arrays.asList("resouc.*.local"),
                Arrays.asList("resouc.*.local")
        );
        System.out.println("Test Case 7 Result: " + filterIncludedPermissions(testCase7)); // 预期结果：[[resouc.*.local]]

        // 测试用例 8: 复杂多列表
        List<List<String>> testCase8 = Arrays.asList(
                Arrays.asList("resouc.*.local"),
                Arrays.asList("resouc.action.local", "resouc.another.action"),
                Arrays.asList("resouc.another.local"),
                Arrays.asList("resouc.*.local", "resouc.action.local"),
                Arrays.asList("resouc.action.local", "resouc.another.action"),
                Arrays.asList("resouc.*.local", "resouc.action.local", "resouc.another.action")
        );
        System.out.println("Test Case 8 Result: " + filterIncludedPermissions(testCase8)); // 预期结果：[[resouc.*.local, resouc.action.local, resouc.another.action]]

        // 测试用例 9: 更复杂的多列表
        List<List<String>> testCase9 = Arrays.asList(
                Arrays.asList("resouc.*.local"),
                Arrays.asList("resouc.action.local", "resouc.another.action"),
                Arrays.asList("resouc.another.local"),
                Arrays.asList("resouc.*.local", "resouc.action.local"),
                Arrays.asList("resouc.action.local", "resouc.another.action"),
                Arrays.asList("resouc.*.local", "resouc.action.local", "resouc.another.action"),
                Arrays.asList("resouc.*.local", "resouc.action.local", "resouc.another.action", "resouc.new.action"),
                Arrays.asList("resouc.*.local", "resouc.action.local", "resouc.another.action", "resouc.new.action", "resouc.new.local")
        );
        System.out.println("Test Case 9 Result: " + filterIncludedPermissions(testCase9)); // 预期结果：[[resouc.*.local, resouc.action.local, resouc.another.action, resouc.new.action, resouc.new.local]]

        // 测试用例 10: 多个完全包含的情况
        List<List<String>> testCase10 = Arrays.asList(
                Arrays.asList("resouc.*.local"),
                Arrays.asList("resouc.action.local"),
                Arrays.asList("resouc.another.action"),
                Arrays.asList("resouc.*.local", "resouc.action.local", "resouc.another.action")
        );
        System.out.println("Test Case 10 Result: " + filterIncludedPermissions(testCase10)); // 预期结果：[[resouc.*.local, resouc.action.local, resouc.another.action]]

        // 测试用例 11: 多个互不相交的情况
        List<List<String>> testCase11 = Arrays.asList(
                Arrays.asList("resouc.*.local"),
                Arrays.asList("resouc.action.action"),
                Arrays.asList("resouc.another.local")
        );
        System.out.println("Test Case 11 Result: " + filterIncludedPermissions(testCase11)); // 预期结果：[[resouc.*.local], [resouc.action.action], [resouc.another.local]]

        // 测试用例 12: 多个部分包含的情况
        List<List<String>> testCase12 = Arrays.asList(
                Arrays.asList("resouc.*.local"),
                Arrays.asList("resouc.action.local", "resouc.another.action"),
                Arrays.asList("resouc.action.local"),
                Arrays.asList("resouc.another.action")
        );
        System.out.println("Test Case 12 Result: " + filterIncludedPermissions(testCase12)); // 预期结果：[[resouc.*.local], [resouc.action.local, resouc.another.action]]

        // 测试用例 13: 包含多种情况的多列表
        List<List<String>> testCase13 = Arrays.asList(
                Arrays.asList("resouc.*.local"),
                Arrays.asList("resouc.action.local"),
                Arrays.asList("resouc.another.action"),
                Arrays.asList("resouc.*.local", "resouc.action.local"),
                Arrays.asList("resouc.action.local", "resouc.another.action"),
                Arrays.asList("resouc.*.local", "resouc.action.local", "resouc.another.action"),
                Arrays.asList("resouc.*.local", "resouc.action.local", "resouc.another.action", "resouc.new.action")
        );
        System.out.println("Test Case 13 Result: " + filterIncludedPermissions(testCase13)); // 预期结果：[[resouc.*.local, resouc.action.local, resouc.another.action, resouc.new.action]]

        // 测试用例 14: 包含空列表的情况
        List<List<String>> testCase14 = Arrays.asList(
                new ArrayList<>(),
                new ArrayList<>(),
                Arrays.asList("resouc.*.local"),
                Arrays.asList("resouc.action.local"),
                Arrays.asList("resouc.another.action"),
                Arrays.asList("resouc.*.local", "resouc.action.local", "resouc.another.action")
        );
        System.out.println("Test Case 14 Result: " + filterIncludedPermissions(testCase14)); // 预期结果：[[resouc.*.local, resouc.action.local, resouc.another.action]]

        // 测试用例 15: 包含重复列表的情况
        List<List<String>> testCase15 = Arrays.asList(
                Arrays.asList("resouc.*.local"),
                Arrays.asList("resouc.*.local"),
                Arrays.asList("resouc.*.local"),
                Arrays.asList("resouc.action.local"),
                Arrays.asList("resouc.another.action"),
                Arrays.asList("resouc.*.local", "resouc.action.local", "resouc.another.action")
        );
        System.out.println("Test Case 15 Result: " + filterIncludedPermissions(testCase15)); // 预期结果：[[resouc.*.local, resouc.action.local, resouc.another.action]]

        // 测试用例 16: 极端情况
        List<List<String>> testCase16 = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            testCase16.add(Arrays.asList("resouc.*.local"));
            testCase16.add(Arrays.asList("resouc.action.local"));
            testCase16.add(Arrays.asList("resouc.another.action"));
            testCase16.add(Arrays.asList("resouc.*.local", "resouc.action.local", "resouc.another.action"));
        }
        System.out.println("Test Case 16 Result: " + filterIncludedPermissions(testCase16)); // 预期结果：[[resouc.*.local, resouc.action.local, resouc.another.action]]
    }*/

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