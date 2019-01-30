package com.bigname.pim.api.service.impl;

import com.bigname.common.util.ValidationUtil;
import com.bigname.pim.api.domain.User;
import com.bigname.pim.api.persistence.dao.UserDAO;
import com.bigname.pim.api.service.UserService;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by sruthi on 05-11-2018.
 */
@Service("customUserService")
public class UserServiceImpl extends BaseServiceSupport<User, UserDAO, UserService> implements UserService, UserDetailsService {

    private UserDAO userDAO;

    @Autowired
    public UserServiceImpl(UserDAO userDAO, Validator validator) {
        super(userDAO, "user", validator);
        this.userDAO = userDAO;
    }

    @Override
    public User createOrUpdate(User user) {
        return userDAO.save(user);
    }

    @Override
    public List<User> create(List<User> users) {
        users.forEach(user -> {user.setCreatedUser(getCurrentUser());user.setCreatedDateTime(LocalDateTime.now());});
        return userDAO.insert(users);
    }

    @Override
    public List<User> update(List<User> users) {
        users.forEach(user -> {user.setLastModifiedUser(getCurrentUser());user.setLastModifiedDateTime(LocalDateTime.now());});
        return userDAO.saveAll(users);
    }

    @Override
    public List<User> findAll(Map<String, Object> criteria) {
        return dao.findAll(criteria);
    }

    @Override
    public List<User> findAll(Criteria criteria) {
        return dao.findAll(criteria);
    }

    @Override
    public Page<User> findAll(String searchField, String keyword, com.bigname.pim.util.Pageable pageable, boolean... activeRequired) {
        return userDAO.findAll(searchField, keyword, pageable, activeRequired);
    }

    @Override
    public Optional<User> findOne(Map<String, Object> criteria) {
        return dao.findOne(criteria);
    }

    @Override
    public Optional<User> findOne(Criteria criteria) {
        return dao.findOne(criteria);
    }

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException, DisabledException {
        final User user = userDAO.findByEmail(email.toUpperCase());

        if(user == null){
            throw new UsernameNotFoundException("No user found with userName  " + email);
        }
        if(user.getActive().equals("N")){
            throw new DisabledException("User Inactive");
        }
        return user;

//        return new org.springframework.security.core.userdetails.User(user.getEmail(),user.getPassword(), true, true, true, true, getAuthorities("ROLE_USER"));
    }

    @Override
    public Map<String, Pair<String, Object>> validate(Map<String, Object> context, Map<String, Pair<String, Object>> fieldErrors, User user, String group) {
        Map<String, Pair<String, Object>> _fieldErrors = super.validate(context, fieldErrors, user, group);
        if(user.getGroup()[0].equals("CHANGE-PASSWORD")) {
            if (ValidationUtil.isEmpty(context.get("id")) || (!user.getPassword().equals(user.getConfirmPassword()))) {
                fieldErrors.put("confirmPassword", Pair.with("New Password does not match the Confirm Password.", user.getPassword()));
            }
        }
        return _fieldErrors;
    }




/*
    private Collection< ?extends GrantedAuthority> getAuthorities(String role){
        return Arrays.asList(new SimpleGrantedAuthority(role));
    }*/


}

