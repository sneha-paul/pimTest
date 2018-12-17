package com.bigname.pim.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class Pageable {
    private int page;
    private int size;
    private Sort sort;

    public Pageable(int page, int size, Sort sort) {
        this.page = page;
        this.size = size;
        this.sort = sort;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    public PageRequest getPageRequest() {
        return PageRequest.of(page, size, sort);
    }
}
