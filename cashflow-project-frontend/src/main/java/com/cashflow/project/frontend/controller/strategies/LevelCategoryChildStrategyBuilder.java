package com.cashflow.project.frontend.controller.strategies;

import com.cashflow.project.domain.project.level.ProjectLevel;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *
 * 
 * @since Nov 18, 2016, 5:03:52 PM
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true, builderClassName = "Builder")
public class LevelCategoryChildStrategyBuilder {

    private boolean rendered;

    private int count;

    private List<? extends ProjectLevel> childEntities;

}
