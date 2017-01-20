package com.cashflow.project.domain.facade.expense.subcontractor;

import com.cashflow.project.domain.facade.ProjectAbstractFacade;
import com.cashflow.project.domain.project.expense.subcontractor.SubContractorExpense;
import javax.ejb.Stateless;

/**
 *
 * 
 * @since Dec 26, 2016, 5:39:03 PM
 */
@Stateless
public class SubContractorExpenseFacade extends ProjectAbstractFacade<SubContractorExpense> {

    public SubContractorExpenseFacade() {
        super(SubContractorExpense.class);
    }

}
