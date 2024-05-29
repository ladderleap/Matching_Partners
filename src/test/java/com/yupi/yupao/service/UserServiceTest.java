package com.yupi.yupao.service;

import com.yupi.yupao.model.domain.User;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;


@SpringBootTest
public class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    public void testAddUser() {
        User user = new User();
        user.setUsername("本项目_所属 [程序员鱼皮](https://t.zsxq.com/0emozsIJh)\n");
        user.setUserAccount("123");
        user.setAvatarUrl("https://636f-codenav-8grj8px727565176-1256524210.tcb.qcloud.la/img/logo.png");
        user.setGender(0);
        user.setUserPassword("xxx");
        user.setPhone("123");
        user.setEmail("456");
        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);
    }

    @Test
    public void testUpdateUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("dogYupi");
        user.setUserAccount("123");
        user.setAvatarUrl("https://636f-codenav-8grj8px727565176-1256524210.tcb.qcloud.la/img/logo.png");
        user.setGender(0);
        user.setUserPassword("xxx");
        user.setPhone("123");
        user.setEmail("456");
        boolean result = userService.updateById(user);
        Assertions.assertTrue(result);
    }

    @Test
    public void testDeleteUser() {
        boolean result = userService.removeById(1L);
        Assertions.assertTrue(result);
    }

    @Test
    public void testGetUser() {
        User user = userService.getById(1L);
        Assertions.assertNotNull(user);
    }



    @Test
    public void testSearchUsersByTags() {
        List<String> tagNameList = Arrays.asList("java");
        List<User> userList = userService.searchUsersByTags(tagNameList);
        Assert.assertNotNull(userList);
    }

    @Test
    public void testInsertContent() {
        String url = "jdbc:mysql://localhost:3306/yupao";
        String username = "root";
        String password = "123123";

        String sql = "INSERT INTO user VALUES (?, null, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 批量插入1000条数据
            for (int i = 1; i <= 100000; i++) {
                // 设置参数值
                pstmt.setString(1, "Name" + i); // name
//                pstmt.setInt(2, i); // id
                pstmt.setString(2, "username" + i); // username
                pstmt.setString(3, "https://example.com/avatar.jpg"); // avatar
                pstmt.setInt(4, 1); // some_int_field
                pstmt.setString(5, "hashed_password" + i); // password
                pstmt.setLong(6, 1234567890L); // some_long_field
                pstmt.setString(7, "email" + i + "@example.com"); // email
                pstmt.setInt(8, 0); // some_other_int_field
                // pstmt.setInt(10, 0); // created_at (placeholder for database's current time)
                // pstmt.setInt(11, 0); // updated_at (placeholder for database's current time)
                pstmt.setInt(9, 0); // some_flag_field
                pstmt.setInt(10, 0); // another_flag_field
                pstmt.setString(11, "earth"); // location
                pstmt.setString(12, "[\"java\", \"python\", \"c#\"]"); // interests

                // 添加到批处理
                pstmt.addBatch();
            }

            // 执行批处理
            int[] rowsAffected = pstmt.executeBatch();

            // 输出受影响的行数
            System.out.println("插入了 " + rowsAffected.length + " 行数据。");
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}