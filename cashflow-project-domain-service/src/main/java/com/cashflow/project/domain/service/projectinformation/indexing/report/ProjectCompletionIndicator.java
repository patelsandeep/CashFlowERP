package com.cashflow.project.domain.service.projectinformation.indexing.report;

import com.cashflow.project.domain.project.ProjectStatus;
import com.anosym.common.Amount;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *
 * 
 * @since Nov 16, 2016, 5:46:43 AM
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectCompletionIndicator {

    private int count;

    private ProjectStatus projectStatus;

    private Amount totalProjectValue;

}
