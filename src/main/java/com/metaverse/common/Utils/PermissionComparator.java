package com.metaverse.common.Utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.metaverse.common.model.Permission;
import com.metaverse.permission.domain.MetaversePermission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PermissionComparator {

    public static void main(String[] args) {
        List<String> list1 = Arrays.asList("*.*.a", "dd.wwwww.dsdddad");
        List<String> list2 = Arrays.asList("*.*.d", "dd.wwwww.dsad");
        System.out.println(compareLists(list1, list2));

        List<String> list3 = Arrays.asList("a", "b", "c");
        List<String> list4 = Arrays.asList("b", "c", "a", "d");


//        String permissionStr = "dd.wqe.dsad";
//        List<String> permissions = Arrays.asList("dd.wqe.*", "dd.wwwww.dsad");
//
//        boolean isMatched = PermissionComparator.isPermissionMatched(permissionStr, permissions);
//        System.out.println("是否存在匹配的权限: " + isMatched); // 输出：是否存在匹配的权限: true
    }

    /**
     * 比较两个字符串列表的关系。
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
        if (CollectionUtil.isNotEmpty(permissionList1) && CollectionUtil.isEmpty(permissionList2) || CollectionUtil.isEmpty(permissionList1) && CollectionUtil.isEmpty(permissionList2)) {
            return 1;
        }
        if (CollectionUtil.isEmpty(permissionList1) && CollectionUtil.isNotEmpty(permissionList2)) {
            return 2;
        }
        Set<Permission> permissionSet1 = permissionList1.stream().map(Permission::new).collect(Collectors.toSet());
        Set<Permission> permissionSet2 = permissionList2.stream().map(Permission::new).collect(Collectors.toSet());

        // 检查set1是否包含set2
        boolean set1ContainsSet2 = permissionSet1.containsAll(permissionSet2);

        // 检查set2是否包含set1
        boolean set2ContainsSet1 = permissionSet2.containsAll(permissionSet1);

        // 检查是否有交集
        permissionSet1.retainAll(permissionSet2);
        boolean hasIntersection = CollectionUtil.isNotEmpty(permissionSet1);
//        boolean hasIntersection = new HashSet<>(permissionSet1).retainAll(permissionSet2);

        if (hasIntersection) {
            if (set1ContainsSet2 || set2ContainsSet1) {
                return 1; // 包含关系成立
            } else {
                return 3; // 有交集但不包含
            }
        } else {
            if (set1ContainsSet2) {
                return 1; // set1包含set2
            } else if (set2ContainsSet1) {
                return 2; // set2包含set1
            } else {
                return 0; // 没有交集
            }
        }
    }

    /**
     * 验证用户是否能通过接口的限制权限串
     *
     * @param permissionStr 接口限制的权限串
     * @param permissions   用户具备的权限串
     * @return
     */
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