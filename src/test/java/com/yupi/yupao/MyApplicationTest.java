package com.yupi.yupao;

import com.yupi.yupao.model.domain.UserTeam;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 测试类
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
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

}
