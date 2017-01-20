package com.cashflow.project.frontend.controller.expenses;

import com.cashflow.project.domain.project.personnel.TeamMemberCategory;
import javax.annotation.Nonnull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * 
 * @since 6 Dec, 2016, 12:48:21 PM
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class MemberEntityModel {

    @Nonnull
    private String uuid;

    @Nonnull
    private String name;

    @Nonnull
    private TeamMemberCategory memberCategory;

}
