package com.yupi.yupao;

import com.yupi.yupao.model.domain.UserTeam;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;


@SpringBootTest
class MyApplicationTest {

    @Test
    void testDigest() throws NoSuchAlgorithmException {
        String newPassword = DigestUtils.md5DigestAsHex(("abcd" + "mypassword").getBytes());
        System.out.println(newPassword);
    }

    @Test
    void contextLoads() {
        List<UserTeam> userTeams = new ArrayList<>();
        UserTeam userTeam = new UserTeam();
        userTeam.setTeamId(1L);
        userTeam.setUserId(1L);
        UserTeam userTeam2 = new UserTeam();
        userTeam2.setTeamId(1L);
        userTeam2.setUserId(1L);
        UserTeam userTeam3 = new UserTeam();
        userTeam3.setTeamId(1L);
        userTeam3.setUserId(2L);
        UserTeam userTeam4 = new UserTeam();
        userTeam4.setTeamId(3L);
        userTeam4.setUserId(1L);
        userTeams.add(userTeam);
        userTeams.add(userTeam2);
        userTeams.add(userTeam3);
        userTeams.add(userTeam4);
        Map<Long, List<UserTeam>> listMap = userTeams.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        System.out.println(listMap);
    }


    //标签匹配排序问题
    @Test
    void theSameResult() {
        List<String> list1 = Arrays.asList("男", "java", "python", "c#", "跑步", "弹琴");
        List<String> list2 = Arrays.asList("男", "洗澡", "python", "吃饭", "打游戏","乒乓球");

        // 找到共同的元素，并按 list1 的顺序排列
        List<String> commonElements = list1.stream()
                .filter(list2::contains)
                .collect(Collectors.toList());

        // 排序list1，将共同的元素放在前面，保留原来的顺序
        List<String> sortedList1 = list1.stream()
                .sorted(Comparator.comparingInt(s -> commonElements.contains(s) ? commonElements.indexOf(s) : Integer.MAX_VALUE))
                .collect(Collectors.toList());

        // 排序list2，将共同的元素放在前面，保留原来的顺序
        List<String> sortedList2 = list2.stream()
                .sorted(Comparator.comparingInt(s -> commonElements.contains(s) ? commonElements.indexOf(s) : Integer.MAX_VALUE))
                .collect(Collectors.toList());

        System.out.println("Sorted List 1: " + sortedList1);
        System.out.println("Sorted List 2: " + sortedList2);
    }

}
