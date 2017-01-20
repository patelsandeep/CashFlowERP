package com.cashflow.project.domain.facade.fileurl;

import com.cashflow.entitydomains.FileUrl;
import com.cashflow.project.domain.facade.ProjectAbstractFacade;
import javax.ejb.Stateless;

/**
 *
 * 
 * @since Dec 23, 2016, 3:25:45 PM
 */
@Stateless
public class FileUrlFacade extends ProjectAbstractFacade<FileUrl> {

    public FileUrlFacade() {
        super(FileUrl.class);
    }

}
