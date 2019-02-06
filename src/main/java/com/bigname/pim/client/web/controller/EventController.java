package com.bigname.pim.client.web.controller;

import com.bigname.common.datatable.model.Pagination;
import com.bigname.common.datatable.model.Request;
import com.bigname.common.datatable.model.Result;
import com.bigname.common.datatable.model.SortOrder;
import com.bigname.core.exception.EntityNotFoundException;
import com.bigname.core.util.FindBy;
import com.bigname.core.web.controller.BaseController;
import com.bigname.pim.api.domain.Event;
import com.bigname.pim.api.service.EventService;
import org.springframework.data.domain.Page;
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

/**
 * Created by dona on 30-01-2019.
 */
@Controller
@RequestMapping("pim/events")
public class EventController extends BaseController<Event, EventService> {

   private EventService eventService;

    public EventController(EventService eventService) {
        super(eventService, Event.class);
        this.eventService = eventService;
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
    public Result<Map<String, Object>> getEventData(HttpServletRequest request, HttpServletResponse response, Model model) {
        Request dataTableRequest = new Request(request);
        Pagination pagination = dataTableRequest.getPagination();
        Result<Map<String, Object>> result = new Result<>();
        result.setDraw(dataTableRequest.getDraw());
        Sort sort;
        if(pagination.hasSorts()) {
            sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
        } else {
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "externalId"));
        }
        List<Map<String, Object>> dataObjects = new ArrayList<>();
        Page<Map<String, Object>> paginatedResult = eventService.getEventData(getPaginationRequest(request), false);
        paginatedResult.getContent().forEach(e -> dataObjects.add(e));
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
        return id == null ? super.details(model) : eventService.get(id, FindBy.EXTERNAL_ID, false)
                .map(event -> {
                    model.put("event", event);
                    return super.details(id, model);
                }).orElseThrow(() -> new EntityNotFoundException("Unable to find Event with Id: " + id));
    }
}
