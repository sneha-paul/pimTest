package com.bigname.pim.api.service.impl;

import com.bigname.pim.api.domain.User;
import com.bigname.pim.api.persistence.dao.UserDAO;
import com.bigname.pim.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by sruthi on 05-11-2018.
 */
@Service("customUserService")
public class UserServiceImpl extends BaseServiceSupport<User, UserDAO> implements UserService, UserDetailsService {

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
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        final User user = userDAO.findByEmail(email);

        if(user == null){
            throw new UsernameNotFoundException("No user found with userName  "+email);
        }

        return new org.springframework.security.core.userdetails.User(user.getEmail(),user.getPassword(), true, true, true, true, getAuthorities("ROLE_USER"));
    }

    private Collection< ?extends GrantedAuthority> getAuthorities(String role){
        return Arrays.asList(new SimpleGrantedAuthority(role));
    }
}

