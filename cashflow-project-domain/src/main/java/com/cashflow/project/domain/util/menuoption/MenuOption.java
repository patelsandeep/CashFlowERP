package com.cashflow.project.domain.util.menuoption;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nonnull;

/**
 *
 * 
 * @since Oct 3, 2016, 11:35:40 AM
 */
public enum MenuOption {

    PROJECTS("Projects", "/projects/dashboard.xhtml"),
    TIMESHEETS("Timesheets", "/timesheet/timesheets.xhtml"),
    EXPENSES("Expenses", "/expenses/expenses.xhtml"),
    EQUIPMENT("Equipment", "/equipment/equipment.xhtml"),
    MATERIALS("Materials", "/materials/materials.xhtml"),
    SUB_CONTRACTORS("Sub Contractors", "/sub-contractors/sub-contractors.xhtml"),
    CHANGE_ORDERS("Change Orders", "/change-orders/change-orders.xhtml"),
    PENALTIES("Penalties", "/penalties/penalties.xhtml"),
    PEOPLE("People", "/people/people.xhtml"),
    INVOICES("Invoices", "/invoices/invoices.xhtml"),
    REPORTS("Reports", "/reports/reports.xhtml", "project.reports.view");

    private final String label;

    private final String template;

    private final List<String> requiredAccessScopes;

    private MenuOption(final String label, final String template, final String... requiredAccesScopes) {
        this.label = label;
        this.template = template;
        this.requiredAccessScopes = ImmutableList.copyOf(requiredAccesScopes);
    }

    @Nonnull
    public String getLabel() {
        return label;
    }

    @Nonnull
    public String getTemplate() {
        return template;
    }

    @Nonnull
    public List<String> getRequiredAccessScopes() {
        return requiredAccessScopes;
    }

}
