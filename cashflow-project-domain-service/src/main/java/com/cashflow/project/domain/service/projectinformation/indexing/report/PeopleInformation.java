package com.cashflow.project.domain.service.projectinformation.indexing.report;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *
 * 
 * @since Nov 16, 2016, 5:59:44 AM
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PeopleInformation {

    private int projectManagers;

    private int projectTeams;

    private int safetyReports;
}
