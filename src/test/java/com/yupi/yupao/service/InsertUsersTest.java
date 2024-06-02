package com.yupi.yupao.service;

import com.google.gson.Gson;
import com.yupi.yupao.model.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;


@SpringBootTest
public class InsertUsersTest {

    @Resource
    private UserService userService;

    private ExecutorService executorService = new ThreadPoolExecutor(20, 50, 10000, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000));

    /**
     * 批量插入用户
     */
    @Test
    public void doInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 100000;
        List<User> userList = new ArrayList<>();
        List<String> possibleTags = Arrays.asList("c#", "java", "python", "javascript", "PHP", "HTML", "CSS", "数据库",
                "机器学习", "人工智能", "区块链", "云计算", "大数据", "网络安全", "游戏开发", "移动开发",
                "物联网", "嵌入式系统", "数据科学", "DevOps", "UI/UX设计", "项目管理");
        Random random = new Random();
        Gson gson = new Gson();

        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("YL"+i);
            user.setUserAccount("YL"+i);
            user.setAvatarUrl("");
            user.setGender(0);
            user.setUserPassword("12345678");
            user.setPhone("123");
            user.setEmail("123@qq.com");

            boolean isMale = random.nextBoolean();
            String genderTag = isMale ? "男" : "女";
            user.setGender(isMale ? 0 : 1);
            List<String> randomTags = new ArrayList<>();
            randomTags.add(genderTag);
            int numberOfTags = random.nextInt(possibleTags.size()) + 1;  // 至少一个标签
            for (int j = 0; j < numberOfTags; j++) {
                String randomTag = possibleTags.get(random.nextInt(possibleTags.size()));
                if (!randomTags.contains(randomTag)) {
                    randomTags.add(randomTag);
                }
            }

            String tagsJson = gson.toJson(randomTags);
            user.setTags(tagsJson);
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setPlanetCode(String.valueOf(100000 + i));
            userList.add(user);
        }
        // 20 秒 10 万条
        userService.saveBatch(userList, 10000);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    /**
     * 并发批量插入用户
     */
    @Test
    public void doConcurrencyInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 分十组
        int batchSize = 5000;
        int j = 0;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            List<User> userList = new ArrayList<>();
            List<String> possibleTags = Arrays.asList("c#", "java", "python", "javascript", "PHP", "HTML", "CSS", "数据库",
                    "机器学习", "人工智能", "区块链", "云计算", "大数据", "网络安全", "游戏开发", "移动开发",
                    "物联网", "嵌入式系统", "数据科学", "DevOps", "UI/UX设计", "项目管理");
            Random random = new Random();
            Gson gson = new Gson();
            while (true) {
                j++;
                User user = new User();
                user.setUsername("YL"+i);
                user.setUserAccount("YL"+i);
                user.setAvatarUrl("");
                user.setGender(0);
                user.setUserPassword("12345678");
                user.setPhone("123");
                user.setEmail("123@qq.com");

                boolean isMale = random.nextBoolean();
                String genderTag = isMale ? "男" : "女";
                user.setGender(isMale ? 0 : 1);
                List<String> randomTags = new ArrayList<>();
                randomTags.add(genderTag);
                int numberOfTags = random.nextInt(possibleTags.size()) + 1;  // 至少一个标签
                for (int m = 0; m < numberOfTags; m++) {
                    String randomTag = possibleTags.get(random.nextInt(possibleTags.size()));
                    if (!randomTags.contains(randomTag)) {
                        randomTags.add(randomTag);
                    }
                }

                String tagsJson = gson.toJson(randomTags);
                user.setTags(tagsJson);
                user.setUserStatus(0);
                user.setUserRole(0);
                user.setPlanetCode(String.valueOf(100000 + i));
                userList.add(user);
                if (j % batchSize == 0) {
                    break;
                }
            }
            // 异步执行
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println("threadName: " + Thread.currentThread().getName());
                userService.saveBatch(userList, batchSize);
            }, executorService);
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        // 20 秒 10 万条
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
