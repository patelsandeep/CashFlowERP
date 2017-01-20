package com.cashflow.project.frontend.service.urlbuilder;

import com.anosym.urlbuilder.QueryParameter;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;

/**
 *
 * 
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true, builderClassName = "Builder")
public class UrlContext {

    private String path;

    @Singular
    private List<QueryParameter<?>> additionalParameters;

    private boolean includeConversationContext;

    private boolean forceFacesRedirect;

}
