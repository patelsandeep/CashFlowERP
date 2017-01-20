package com.cashflow.project.domain.service.projectinformation.indexing.report;

import com.cashflow.project.domain.project.ProjectStatus;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *
 * 
 * @since Nov 16, 2016, 6:19:16 AM
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectProgressInformation {

    private String projectId;

    private String projectName;

    private int daysElapsed;

    private BigDecimal percentCompletion;

    private ProjectStatus projectStatus;
}
