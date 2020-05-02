package com.hugh.dao;


import com.hugh.pojo.User;

import java.util.List;

public interface IUserDao {

    /**
     * 查询所有用户
     * @return List<User>
     * @throws Exception  Exception
     */
    List<User> findAll() throws Exception;


    /**
     * 根据条件进行用户查询
     * @param user user
     * @return  User
     * @throws Exception Exception
     */
    User findByCondition(User user) throws Exception;

    /**
     *  插入一个用户
     * @param userList 用户
     * @return 影响条数
     */
    int insertUserList(List<User> userList);

    /**
     *  插入一个用户
     * @param user 用户
     * @return 影响条数
     */
    int insertUser(User user);


    /**
     * 更新
     * @param user 用户
     * @return int
     */
    int updateUser(User user);

    /**
     * 删除
     * @param user 用户
     * @return int
     */
    int deleteUser(User user);
}
