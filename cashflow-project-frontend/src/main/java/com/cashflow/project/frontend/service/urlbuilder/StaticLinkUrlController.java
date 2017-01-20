package com.cashflow.project.frontend.service.urlbuilder;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * 
 * @since Nov 17, 2016, 10:43:45 PM
 */
@Named
@ApplicationScoped
public class StaticLinkUrlController {

    private static final String PROJECT_PATH = "/projects/project.xhtml";

    private static final String PROJECT_LISTING_PATH = "/projects/projects.xhtml";

    @Inject
    private StaticLinkUrlBuilder staticLinkUrlBuilder;

    @Nonnull
    public String getNewProjectPath() {
        final UrlContext.Builder context = UrlContext
                .builder()
                .path(PROJECT_PATH);
        return staticLinkUrlBuilder.buildURL(context.build());
    }

    @Nonnull
    public String getProjectsListingPath() {
        final UrlContext.Builder context = UrlContext
                .builder()
                .path(PROJECT_LISTING_PATH);
        return staticLinkUrlBuilder.buildURL(context.build());
    }
}
