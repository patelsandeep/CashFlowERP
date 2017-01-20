package com.cashflow.project.frontend.service.urlbuilder;

import com.cashflow.frontend.support.httpparameter.HttpParameter;
import com.anosym.urlbuilder.QueryParameter;
import com.anosym.urlbuilder.RelativeUrlBuilder;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Conversation;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 *
 * 
 */
@ApplicationScoped
public class StaticLinkUrlBuilder {

    private static final String API_KEY_QUERY_PARAMETER = "api_key";

    private static final String JAZ_SESSIONID_QUERY_PARAMETER = "jazsessionid";

    private static final String FACES_REDIRECT_PARAMETER = "faces-redirect";

    private static final String CID_PARAMETER = "cid";

    @Inject
    private RelativeUrlBuilder relativeUrlBuilder;

    @Inject
    @HttpParameter(API_KEY_QUERY_PARAMETER)
    private Instance<String> apiKey;

    @Inject
    @HttpParameter(JAZ_SESSIONID_QUERY_PARAMETER)
    private Instance<String> jazSessionId;

    @Inject
    private Instance<Conversation> conversation;

    @Nonnull
    public String buildURL(@Nonnull final UrlContext urlContext) {
        checkNotNull(urlContext, "The urlContext must not be null");

        final List<QueryParameter<?>> parameters = new ArrayList<>(urlContext.getAdditionalParameters());
        parameters.add(buildQueryParameter(API_KEY_QUERY_PARAMETER, apiKey.get()));
        parameters.add(buildQueryParameter(JAZ_SESSIONID_QUERY_PARAMETER, jazSessionId.get()));

        if (urlContext.isForceFacesRedirect()) {
            parameters.add(buildQueryParameter(FACES_REDIRECT_PARAMETER, "true"));
        }

        if (urlContext.isIncludeConversationContext()) {
            final Conversation conversation_ = conversation.get();
            checkState(!conversation_.isTransient(), "Cannot include conversation context for transient conversation");

            final String cid = conversation_.getId();
            parameters.add(buildQueryParameter(CID_PARAMETER, cid));
        }

        final QueryParameter[] queryParameters = parameters.toArray(new QueryParameter[0]);
        return relativeUrlBuilder.buildWithPath(urlContext.getPath(), queryParameters);
    }

    @Nonnull
    private QueryParameter<String> buildQueryParameter(@Nonnull final String parameter, @Nonnull String parameterValue) {
        return QueryParameter
                .<String>builder()
                .parameter(parameter)
                .parameterValue(parameterValue)
                .build();
    }

}
