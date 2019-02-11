package com.bigname.pim.client.web.controller;

import com.bigname.common.datatable.model.Pagination;
import com.bigname.common.datatable.model.Request;
import com.bigname.common.datatable.model.Result;
import com.bigname.common.datatable.model.SortOrder;
import com.bigname.common.util.ValidationUtil;
import com.bigname.core.domain.Entity;
import com.bigname.core.exception.EntityNotFoundException;
import com.bigname.core.util.FindBy;
import com.bigname.core.web.controller.BaseController;
import com.bigname.pim.api.domain.Event;
import com.bigname.pim.api.domain.User;
import com.bigname.pim.api.service.EventService;
import com.bigname.pim.api.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by dona on 30-01-2019.
 */
@Controller
@RequestMapping("pim/events")
public class EventController extends BaseController<Event, EventService> {

   private EventService eventService;
   private UserService userService;

    public EventController(EventService eventService, UserService userService) {
        super(eventService, Event.class);
        this.eventService = eventService;
        this.userService = userService;
    }

    /**
     * Handler method to load the list events page
     *
     * @return The ModelAndView instance for the list events page
     */
    @RequestMapping()
    public ModelAndView all(){
        Map<String, Object> model = new HashMap<>();
        model.put("active", "EVENTS");
        return new ModelAndView("event/events", model);
    }

    @RequestMapping("/datas")
    @ResponseBody
    public Result<Map<String, String>> getEventData(HttpServletRequest request, HttpServletResponse response, Model model) {
        Request dataTableRequest = new Request(request);
        Pagination pagination = dataTableRequest.getPagination();
        Result<Map<String, String>> result = new Result<>();
        result.setDraw(dataTableRequest.getDraw());
        Sort sort;
        if(pagination.hasSorts()) {
            sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
        } else {
            sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "timeStamp"));
        }
        List<Map<String, String>> dataObjects = new ArrayList<>();
//        Page<Map<String, Object>> paginatedResult = eventService.getEventData(getPaginationRequest(request), false);
        Map<String, User> usersLookup = userService.getAll(null, false).stream().collect(Collectors.toMap(Entity::getId, u -> u));
        Page<Event> paginatedResult = ValidationUtil.isEmpty(dataTableRequest.getSearch()) ? eventService.getAll(dataTableRequest.getPagination().getPageNumber(), dataTableRequest.getPagination().getPageSize(), sort, false)
                                                : eventService.findAll("details", dataTableRequest.getSearch(), PageRequest.of(pagination.getPageNumber(), pagination.getPageSize(), sort), false);
        paginatedResult.getContent().forEach(e -> {
            Map<String, String> data = e.toMap();
            data.put("userName", usersLookup.get(e.getUser()).getUserName());
            dataObjects.add(data);

        });
        result.setDataObjects(dataObjects);
        result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
        result.setRecordsFiltered(Long.toString(paginatedResult.getTotalElements()));
        return result;
    }

    /**
     * Handler method to load the event details page or the create new event page
     *
     * @param id eventId of the event instance that needs to be loaded
     *
     * @return The ModelAndView instance for the details page depending on the presence of the 'id' pathVariable
     */
    @RequestMapping(value = {"/{id}"})
    public ModelAndView details(@PathVariable(value = "id", required = false) String id,
                                @RequestParam(name = "reload", required = false) boolean reload) {

        Map<String, Object> model = new HashMap<>();
        model.put("active", "EVENTS");
        model.put("mode", "DETAILS");
        model.put("view", "event/event" + (reload ? "_body" : ""));
        return id == null ? super.details(model) : eventService.get(id, FindBy.INTERNAL_ID, false)
                .map(event -> {
                    event.setUser(userService.get(event.getUser(), FindBy.INTERNAL_ID, false).map(User::getUserName).orElse(""));
                    model.put("event", event);
                    return super.details(id, model);
                }).orElseThrow(() -> new EntityNotFoundException("Unable to find Event with Id: " + id));
    }
}
