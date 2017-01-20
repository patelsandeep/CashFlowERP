package com.cashflow.project.api.projectinformation;

import java.util.List;
import javax.annotation.Nonnull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *
 * 
 * @since Nov 12, 2016, 10:52:55 AM
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectInformationResult {

    @Nonnull
    private Integer count;

    @Nonnull
    private List<ProjectInformation> projectInformations;
}
