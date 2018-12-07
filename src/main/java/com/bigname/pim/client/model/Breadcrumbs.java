package com.bigname.pim.client.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Manu on 8/8/2018.
 */
public class Breadcrumbs {
    private String title = "";
    private List<String[]> breadcrumbs = new ArrayList<>();

    private static final String[] homeCrumb = {"HOME", "/pim/dashboard"};

    public Breadcrumbs(String title, String... breadcrumbs) {
        this.title = title;
        if(breadcrumbs != null && breadcrumbs.length > 1) {
            addHomeCrumb();
            for(int i = 0; breadcrumbs.length % 2 == 0 ? i < breadcrumbs.length : i < breadcrumbs.length - 1; i = i + 2 ) {
                this.breadcrumbs.add(new String[]{breadcrumbs[i], breadcrumbs[i + 1]});
            }
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBreadcrumbs(List<String[]> breadcrumbs) {
        this.breadcrumbs = breadcrumbs;
    }

    public List<String[]> getBreadcrumbs() {
        return breadcrumbs;
    }

    public Breadcrumbs addCrumbs(String... breadcrumbs) {
        if(breadcrumbs != null && breadcrumbs.length > 1) {
            if(this.breadcrumbs.isEmpty()) {
                addHomeCrumb();
            }
            for(int i = 0; breadcrumbs.length % 2 == 0 ? i < breadcrumbs.length : i < breadcrumbs.length - 1; i = i + 2 ) {
                this.breadcrumbs.add(new String[]{breadcrumbs[i], breadcrumbs[i + 1]});
            }
        }
        return this;

    }

    private void addHomeCrumb() {
        this.breadcrumbs.add(homeCrumb);
    }




}
