package com.sh.post.dao;

import com.sh.post.bean.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component // Dao와 Service가 혼합된 형태의 bean을 만드는게 맞나?
public class UserDaoService {
    private final static List<User> users = new ArrayList<>();
    private static int userCount = 3; // 전체 사용자수

    static {
        users.add(new User(1, "Kenneth", new Date()));
        users.add(new User(2, "Alice", new Date()));
        users.add(new User(3, "Elena", new Date()));
    }

    public List<User> findAll() {
        return users;
    }

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(++userCount);
        }
        if (user.getJoinData() == null) {
            user.setJoinData(new Date());
        }
        users.add(user);
        return user;
    }

    public User findOne(int id) {
        for (User user : users) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }
}
