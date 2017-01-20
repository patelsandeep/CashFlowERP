package com.cashflow.project.frontend.controller.staticcontent;

import com.cashflow.access.authorization.LoggedInBusinessAccount;
import com.cashflow.backend.businessaccount.authorization.RequiresBusinessAccount;
import com.cashflow.datarepository.service.DataRepository;
import com.cashflow.datarepository.service.DataRepository.Mode;
import com.cashflow.datarepository.service.DataRepositoryService;
import com.cashflow.entitydomains.AbsoluteFileUrl;
import com.cashflow.entitydomains.FileUrl;
import com.cashflow.frontend.support.httpparameter.HttpParameter;
import com.cashflow.project.api.fileurl.FileUrlService;
import com.cashflow.useraccount.domain.businessaccount.BusinessAccount;
import java.io.InputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Model;
import javax.inject.Inject;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import static com.cashflow.project.frontend.controller.model.DataRepositoryName.REPOSITORY_NAME;

/**
 *
 * 
 */
@Model
@RequiresBusinessAccount
public class StaticContentController implements Serializable {

    private static final long serialVersionUID = -7086633339681162502L;

    private static final StreamedContent MARKER_STREAMED_CONTENT = new DefaultStreamedContent();

    @Inject
    private FileUrlService fileUrlService;

    @Inject
    @DataRepository(value = REPOSITORY_NAME, mode = Mode.PRIVATE)
    private Instance<DataRepositoryService> privateDataRepositoryService;

    @Inject
    @DataRepository(value = REPOSITORY_NAME, mode = Mode.PUBLIC)
    private Instance<DataRepositoryService> publicDataRepositoryService;

    @Inject
    @HttpParameter("fileUUID")
    private Instance<String> fileUUID;

    @Inject
    private Logger logger;

    @Inject
    @LoggedInBusinessAccount
    private BusinessAccount businessAccount;

    @Nonnull
    public StreamedContent getContent() {
        if (fileUUID.get() == null) {
            return MARKER_STREAMED_CONTENT;
        }

        final FileUrl fileUrl = fileUrlService.getFileUrl(fileUUID.get());
        if (fileUrl == null) {
            logger.log(Level.WARNING,
                       "Couldnt load FileUrl with fileUUID: '{'{0}'}', for businessaccount: '{'{1}'}'",
                       new Object[]{fileUUID, businessAccount.getAccountId()});
            return MARKER_STREAMED_CONTENT;
        }

        final String awsIdentifier = fileUrl.getUrl();
        final DataRepositoryService dataRepositoryService = (fileUrl instanceof AbsoluteFileUrl)
                ? publicDataRepositoryService.get() : privateDataRepositoryService.get();
        final InputStream inputStream = dataRepositoryService.get(awsIdentifier);

        if (inputStream == null) {
            logger.log(Level.WARNING,
                       "Couldnt load aws inputstream with identifier: '{'{0}'}', for datarepository: '{'{1}'}'",
                       new Object[]{awsIdentifier, REPOSITORY_NAME});
            return MARKER_STREAMED_CONTENT;
        }

        return new DefaultStreamedContent(inputStream, fileUrl.getContentType(), fileUrl.getName());
    }
}
