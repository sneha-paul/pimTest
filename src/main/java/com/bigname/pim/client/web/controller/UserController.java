package com.bigname.pim.client.web.controller;

import com.bigname.common.datatable.model.Pagination;
import com.bigname.common.datatable.model.Request;
import com.bigname.common.datatable.model.Result;
import com.bigname.common.datatable.model.SortOrder;
import com.bigname.common.util.CollectionsUtil;
import com.bigname.pim.api.domain.User;
import com.bigname.pim.api.exception.EntityNotFoundException;
import com.bigname.pim.api.service.UserService;
import com.bigname.pim.client.model.Breadcrumbs;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static com.bigname.common.util.ValidationUtil.isEmpty;

/**
 * Created by dona on 08-01-2019.
 */
@Controller
@RequestMapping("pim/users")
public class UserController extends BaseController<User, UserService> {
    private UserService userService;

    @Autowired
    public PasswordEncoder passwordEncoder;

    public UserController(UserService userService) {
        super(userService,User.class);
        this.userService = userService;
    }


    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> create(User user) {
        Map<String, Object> model = new HashMap<>();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (isValid(user, model, User.CreateGroup.class)) {
            user.setStatus("Active");
            user.setActive("N");  //ToDo : setActive N and do authentication for activating user
            userService.create(user);
            model.put("success", true);
        }
        return model;
    }

    /**
     * Handler method to update a user instance
     * @param user The modified user instance corresponding to the given emailId
     * @return a map of model attributes
     */

   /* @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> update(@PathVariable(value = "id") String id, User user) {
        Map<String, Object> model = new HashMap<>();
        model.put("context", CollectionsUtil.toMap("id", id));
        if (isValid(user, model, user.getGroup().length == 1 && user.getGroup()[0].equals("DETAILS") ? User.DetailsGroup.class : null)) {
            user.setStatus("Active");
            userService.update(id, FindBy.EXTERNAL_ID, user);
            model.put("success", true);
        }
        return model;
    }*/

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> update(@PathVariable(value = "id") String userId, User user) {
        return update(userId, user, "/pim/users/", user.getGroup().length == 1 && user.getGroup()[0].equals("DETAILS") ? User.DetailsGroup.class : null);
    }


    @RequestMapping(value = {"/{id}", "/create"})
    public ModelAndView details(@PathVariable(value = "id", required = false) String id,
                                @RequestParam(name = "reload", required = false) boolean reload) {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "USER");
        model.put("mode", id == null ? "CREATE" : "DETAILS");
        model.put("view", "user/user"  + (reload ? "_body" : ""));

        return id == null ? super.details(model) : userService.get(id, FindBy.EXTERNAL_ID, false)
                .map(user -> {
                    model.put("user", user);
                    return super.details(id, model);
                }).orElseThrow(() -> new EntityNotFoundException("Unable to find User with Id: " + id));
    }


    /**
     * Handler method to load the list users page
     *
     * @return The ModelAndView instance for the list users page
     */
    @RequestMapping()
    public ModelAndView all() {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "USERS");
        return new ModelAndView("user/users", model);
    }

    @RequestMapping(value = {"/changePasswordView"})
    public ModelAndView changePassword(@RequestParam(value = "id", required = false) String id) {
        Optional<User> user = userService.get(id, FindBy.EXTERNAL_ID, false);
        Map<String, Object> model = new HashMap<>();
        model.put("user", user.get());
        model.put("breadcrumbs", new Breadcrumbs("Users", "Users", "/pim/users", user.get().getUserName(), ""));
        return new ModelAndView("user/changePassword", model);
    }
   @RequestMapping(value = "/changePassword", method = RequestMethod.PUT)
   @ResponseBody
    public Map<String, Object> changePassword(@RequestParam(value = "id") String id, User user) {
        Map<String, Object> model = new HashMap<>();
        user.setExternalId(id);
        model.put("context", CollectionsUtil.toMap("id", id));
        if(isValid(user, model, user.getGroup().length == 1 && user.getGroup()[0].equals("CHANGE-PASSWORD") ? User.ChangePasswordGroup.class : null)) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userService.update(id, FindBy.EXTERNAL_ID, user);
            model.put("success", true);
        }
        return model;
    }

    @RequestMapping(value =  {"/list", "/data"})
    @ResponseBody
    @SuppressWarnings("unchecked")
    public Result<Map<String, String>> all(HttpServletRequest request, HttpServletResponse response, Model model) {
        Request dataTableRequest = new Request(request);
        if(isEmpty(dataTableRequest.getSearch())) {
            return super.all(request, response, model);
        } else {
            Pagination pagination = dataTableRequest.getPagination();
            Result<Map<String, String>> result = new Result<>();
            result.setDraw(dataTableRequest.getDraw());
            Sort sort;
            if(pagination.hasSorts()) {
                sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
            } else {
                sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "externalId"));
            }
            List<Map<String, String>> dataObjects = new ArrayList<>();
            Page<User> paginatedResult = userService.findAll("userName", dataTableRequest.getSearch(), new Pageable(pagination.getPageNumber(), pagination.getPageSize(), sort), false);
            paginatedResult.forEach(e -> dataObjects.add(e.toMap()));
            result.setDataObjects(dataObjects);
            result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
            result.setRecordsFiltered(Long.toString(paginatedResult.getTotalElements()));
            return result;
        }
    }
}
