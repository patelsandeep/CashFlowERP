package com.cashflow.project.api.fileurl;

import com.cashflow.entitydomains.FileUrl;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * 
 * @since Dec 23, 2016, 3:24:33 PM
 */
public interface FileUrlService {

    @Nullable
    FileUrl getFileUrl(@Nonnull final String fileUrlUUID);

}
