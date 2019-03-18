package com.bigname.common.datatable.model;

import com.bigname.common.util.StringUtil;
import com.bigname.pim.util.PIMConstants;
import com.bigname.pim.util.PimUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bigname.common.util.ValidationUtil.isNotEmpty;

/**
 * The datatable request class
 *
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class Request {
    /** The unique id. */
    private String uniqueId;

    /** The draw. */
    private String draw;

    /** The start. */
    private Integer start;

    /** The length. */
    private Integer length;

    /** The search. */
    private String search;

    /** The regex. */
    private boolean regex;

    /** The columns. */
    private List<Column> columns;

    /** The order. */
    private Column order;

    /** The is global search. */
    private boolean isGlobalSearch;

    /** The status option flags **/
    private boolean[] statusOptions;

    /**
     * Instantiates a new data table request.
     *
     * @param request the request
     */
    public Request(HttpServletRequest request) {
        prepareDataTableRequest(request);
    }

    /**
     * Gets the unique id.
     *
     * @return the uniqueId
     */
    public String getUniqueId() {
        return uniqueId;
    }

    /**
     * Sets the unique id.
     *
     * @param uniqueId the uniqueId to set
     */
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    /**
     * Gets the start.
     *
     * @return the start
     */
    public Integer getStart() {
        return start;
    }

    /**
     * Sets the start.
     *
     * @param start the start to set
     */
    public void setStart(Integer start) {
        this.start = start;
    }

    /**
     * Gets the length.
     *
     * @return the length
     */
    public Integer getLength() {
        return length;
    }

    /**
     * Sets the length.
     *
     * @param length the length to set
     */
    public void setLength(Integer length) {
        this.length = length;
    }

    /**
     * Gets the search.
     *
     * @return the search
     */
    public String getSearch() {
        return search;
    }

    /**
     * Sets the search.
     *
     * @param search the search to set
     */
    public void setSearch(String search) {
        this.search = search;
    }

    /**
     * Checks if is regex.
     *
     * @return the regex
     */
    public boolean isRegex() {
        return regex;
    }

    /**
     * Sets the regex.
     *
     * @param regex the regex to set
     */
    public void setRegex(boolean regex) {
        this.regex = regex;
    }

    /**
     * Gets the columns.
     *
     * @return the columns
     */
    public List<Column> getColumns() {
        return columns;
    }

    /**
     * Sets the columns.
     *
     * @param columns the columns to set
     */
    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    /**
     * Gets the order.
     *
     * @return the order
     */
    public Column getOrder() {
        return order;
    }

    /**
     * Sets the order.
     *
     * @param order the order to set
     */
    public void setOrder(Column order) {
        this.order = order;
    }

    /**
     * Gets the draw.
     *
     * @return the draw
     */
    public String getDraw() {
        return draw;
    }

    /**
     * Sets the draw.
     *
     * @param draw the draw to set
     */
    public void setDraw(String draw) {
        this.draw = draw;
    }

    /**
     * Checks if is global search.
     *
     * @return the isGlobalSearch
     */
    public boolean isGlobalSearch() {
        return isGlobalSearch;
    }

    /**
     * Sets the global search.
     *
     * @param isGlobalSearch the isGlobalSearch to set
     */
    public void setGlobalSearch(boolean isGlobalSearch) {
        this.isGlobalSearch = isGlobalSearch;
    }

    /**
     * Gets the status options
     *
     * @returnthe status options
     */
    public boolean[] getStatusOptions() {
        return statusOptions;
    }

    /**
     * Sets the status options
     *
     * @param statusOptions the statusOptions to set
     */
    public void setStatusOptions(boolean[] statusOptions) {
        this.statusOptions = statusOptions;
    }

    /**
     * Prepare data table request.
     *
     * @param request the request
     */
    private void prepareDataTableRequest(HttpServletRequest request) {

        Enumeration<String> parameterNames = request.getParameterNames();

        if(parameterNames.hasMoreElements()) {

            this.setStart(Integer.parseInt(request.getParameter(Pagination.PAGE_NO)));
            this.setLength(Integer.parseInt(request.getParameter(Pagination.PAGE_SIZE)));
            this.setUniqueId(request.getParameter("_"));
            this.setDraw(request.getParameter(Pagination.DRAW));

            this.setSearch(request.getParameter("search[value]"));
            this.setRegex(Boolean.valueOf(request.getParameter("search[regex]")));
            this.statusOptions = PimUtil.getStatusOptions(request.getParameter("statusOptions"));

            int sortableCol = request.getParameter("order[0][column]") != null ? Integer.parseInt(request.getParameter("order[0][column]")) : -1;

            List<Column> columns = new ArrayList<>();

            if(isNotEmpty(this.getSearch())) {
                this.setGlobalSearch(true);
            }

            for(int i=0; i < getNumberOfColumns(request); i++) {
                if(null != request.getParameter("columns["+ i +"][data]")
                        && !"null".equalsIgnoreCase(request.getParameter("columns["+ i +"][data]"))
                        && isNotEmpty(request.getParameter("columns["+ i +"][data]"))) {
                    Column column = new Column(request, i);
                    if(i == sortableCol) {
                        this.setOrder(column);
                    }
                    columns.add(column);

                    if(isNotEmpty(column.getSearch())) {
                        this.setGlobalSearch(false);
                    }
                }
            }

            if(isNotEmpty(columns)) {
                this.setColumns(columns);
            }
        }
    }

    private int getNumberOfColumns(HttpServletRequest request) {
        Pattern p = Pattern.compile("columns\\[[0-9]+\\]\\[data\\]");
        @SuppressWarnings("rawtypes")
        Enumeration params = request.getParameterNames();
        List<String> lstOfParams = new ArrayList<String>();
        while(params.hasMoreElements()){
            String paramName = (String)params.nextElement();
            Matcher m = p.matcher(paramName);
            if(m.matches())	{
                lstOfParams.add(paramName);
            }
        }
        return lstOfParams.size();
    }

    /**
     * Gets the pagination.
     *
     * @return the pagination
     */
    public Pagination getPagination() {

        Pagination pagination = new Pagination();
        pagination.setPageNumber(this.getStart());
        pagination.setPageSize(this.getLength());

        SortBy sortBy = null;
        if(isNotEmpty(this.getOrder())) {
            sortBy = new SortBy();
            sortBy.addSort(this.getOrder().getData(), SortOrder.fromValue(this.getOrder().getSortDir()));
        }

        FilterBy filterBy = new FilterBy();
        filterBy.setGlobalSearch(this.isGlobalSearch());
        if(isNotEmpty(this.getColumns())) {
            for (Column column : this.getColumns()) {
                if (column.isSearchable()) {
                    if (isNotEmpty(this.getSearch()) || isNotEmpty(column.getSearch())) {
                        filterBy.addFilter(column.getData(), (this.isGlobalSearch()) ? this.getSearch() : column.getSearch());
                    }
                }
            }
        }

        pagination.setSortBy(sortBy);
        pagination.setFilterBy(filterBy);

        return pagination;
    }

    public PageRequest getPageRequest(Sort defaultSort) {
        return getPageRequest(null, defaultSort);
    }

    public PageRequest getPageRequest(Predicate<Request> predicate) {
        return getPageRequest(predicate, null);
    }

    public PageRequest getPageRequest(Predicate<Request> predicate, Sort defaultSort) {
        Sort sort = getSort(predicate);
        if(sort == null && defaultSort != null) {
            sort = defaultSort;
        }
        Pagination pagination = getPagination();
        return PageRequest.of(pagination.getPageNumber(), pagination.getPageSize(), sort);
    }

    public Sort getSort() {
        return getSort(null);
    }

    public Sort getSort(Predicate<Request> predicate) {
        if(isNotEmpty(this.getOrder()) && (predicate == null || predicate.test(this))) {
            return Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(getOrder().getSortDir()).name()), getOrder().getName()));
        }
        return null;
    }
}
