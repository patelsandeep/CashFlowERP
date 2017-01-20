package com.cashflow.project.domain.project.filtering;

import com.cashflow.entitydomains.AbstractDomain;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;

/**
 *
 * 
 * @since Nov 12, 2016, 10:09:15 AM
 */
@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Filter extends AbstractDomain {

    private static final long serialVersionUID = -7778570128435146668L;

    /**
     * Full path filter attribute to the domain object. This is a single attribute of the class filter context.
     */
    @Nonnull
    private String attribute;

    /**
     * The filter domains, represents the data model domain, for which this filter applies to.
     */
    @Singular
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private List<FilterDomain> filterDomains;

    /**
     * The default display name of the the filter.
     */
    @Nonnull
    private String name;

    /**
     * For object hierarchy. e.g. for Project and Budget, with a budgetedCost, the childFilter here will be
     * "budgetedCost" and this filter will be "budget".
     */
    @Nullable
    @ManyToOne
    private Filter childFilter;
}
