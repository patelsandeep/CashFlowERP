package com.cashflow.project.domain.service.fileurl;

import com.cashflow.backend.businessaccount.authorization.RequiresBusinessAccount;
import com.cashflow.entitydomains.FileUrl;
import com.cashflow.project.api.fileurl.FileUrlService;
import com.cashflow.project.domain.facade.fileurl.FileUrlFacade;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * 
 * @since Dec 23, 2016, 3:32:51 PM
 */
@Stateless
@Dependent
@RequiresBusinessAccount
@Local(FileUrlService.class)
public class FileUrlServiceImpl implements FileUrlService {

    @EJB
    private FileUrlFacade fileUrlFacade;

    @Override
    @Nullable
    public FileUrl getFileUrl(@Nonnull final String fileUrlUUID) {

        checkNotNull(fileUrlUUID, "The fileUrlUUID must not be null");
        return fileUrlFacade.find(fileUrlUUID);

    }

}
